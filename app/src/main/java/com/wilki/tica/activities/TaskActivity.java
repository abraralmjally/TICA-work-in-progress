package com.wilki.tica.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wilki.tica.exceptions.BluetoothDisabledException;
import com.wilki.tica.exceptions.NoBluetoothException;
import com.wilki.tica.logicLayer.BluetoothConnectionManager;
import com.wilki.tica.dragAndTouchListeners.DialogClickListener;
import com.wilki.tica.fragments.ExitDialogueFragment;
import com.wilki.tica.fragments.MessageFragment;
import com.wilki.tica.fragments.BusyDialogFragment;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.TICA;
import com.wilki.tica.logicLayer.Session;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskEvaluation;
import com.wilki.tica.logicLayer.TaskPerformance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 3/1/2017.
 * Abstract class used to reduce duplicate code between the TangibleTaskActivity and
 * ScreenTaskActivity.
 */

public abstract class TaskActivity extends AppCompatActivity implements DialogClickListener {

    private Task currentTask;
    private TaskPerformance taskPerformance;
    private String SelectedGroupName;
    private Session session;
    private int sessionId;
    private  List <Session> AllSessions;
    private BluetoothConnectionManager btConManager;
    private Context cont;
    private DialogFragment robotRunning;
    private TaskEvaluation taskEval;
    private DialogFragment exitDialogueFragment;

    //=================================================================================/
    /*
     * Initial values for posX, posY and direction
     * These could be set when grid is selected but, for now,
     * set for middle square of first row, facing forward.
     */

    // X and Y co-ods of current robot position (1-5)
    private int posX = 3;
    private int posY = 1;

    // current robot direction (0, 90, 180, 270)
    private int direction = 0;

    // proposed co-ods and direction, based  on current robot command
    private int newPosX;
    private int newPosY;
    private int newDirection;

    private String checkInstructionSequence(String sequence)
    {
        String checkedSequence = "";
        if (canRobotMove())
        {
            for (int i = 0; i < sequence.length(); i++)
            {
                char c = sequence.charAt(i);
                if (setNewPostion(c))
                {
                    checkedSequence += c;
                }
                else
                {
                    checkedSequence += 'b';
                    return checkedSequence;
                }
            }
        }
        return checkedSequence;
    }

    private boolean setNewPostion(char command)
    {
        /*
         * check robot is able to move from current position and, if so, that
         * command is executable given grid boundaries and
         * any other obstacles/barriers.
         * If OK, send command to robot and update position.
         * If not OK, do not move robot and send alternative command.
         */
        boolean newPositionOK = true;
        if (calculateNewPosition(command))
        {
            // Check if new position is executable

            // check robot will stay on grid
            if (newPosY > 5 || newPosY < 1 || newPosX > 5 || newPosX < 1)
                newPositionOK = false;

            // Add checks here for any obstacles or barriers on grid
            // ...

            if (newPositionOK)
            {
                // send command and update robot position and direction
                posX = newPosX;
                posY = newPosY;
                direction = newDirection;
            }
            else
            {
                return false;
            }
        }
        else
        {
            // command not recognised - print error message
            return false;
        }
        return newPositionOK;
    }

    private boolean canRobotMove()
    {
        // check if robot is able to move from current position.
        // e.g. check against blacklist of x, y positions.
        return true;
    }

    private boolean calculateNewPosition(char command)
    {
        // calculate newPosX, newPosY and direction based on command
        newPosX = posX;
        newPosY = posY;
        if (command =='f')
        {
            switch (direction) {
                case 0:
                    newPosY++;
                    break;
                case 90:
                    newPosX++;
                    break;
                case 180:
                    newPosY--;
                case 270:
                    newPosX--;
            }
        }
        else if (command =='r')
        {
            newDirection += 90;
            newDirection = (newDirection == 360) ?  0 : newDirection;
        }
        else if (command =='l')
        {
            newDirection -= 90;
            newDirection = (newDirection == -90) ?  270 : newDirection;
        }
        else
            return false;
        return true;
    }
    //======================================================================================/

    protected void onCreate(Bundle savedInstanceState, Intent receivedIntent, InterfaceType interfaceType,
                            Context cont) {
        super.onCreate(savedInstanceState);

        this.cont = cont;
        currentTask = (Task) receivedIntent.getSerializableExtra("SELECTED_TASK");
        SelectedGroupName = receivedIntent.getStringExtra("SelectedGroup");
        session= new Session(SelectedGroupName);
        //searching in the DB
        session=session.findSession(cont,SelectedGroupName);
        if (session == null)
        {
            session= new Session(SelectedGroupName);
           // session.saveSession(cont,session);
           // sessionId=session.getSessionID();

        }
        taskPerformance = new TaskPerformance(session, currentTask, interfaceType);
        sessionId=session.getSessionID();


        btConManager = ((TICA) getApplication()).getBtConManager();


        // register local broadcast to listen for when the robot completes.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("bluetooth_robot_complete"));
    }

    /**
     * Abstract method for reading the instructions selected.
     * @return a list of selected instructions.
     */
    protected abstract List<Instruction> readInstructions();

    /**
     * Processes an attempt. Checks if the provided instructions complete the task and sends
     * instructions to the robot.
     * @param instructionsToProcess list of instructions to process.
     */
    protected void processInstructions(List<Instruction> instructionsToProcess){
        taskEval = currentTask.checkForTaskCompletion(instructionsToProcess);

        // save attempt
        taskPerformance.stopTaskAttempt(instructionsToProcess);
        sendInstructionsToRobot(taskEval.getInstructionsOnBoard());

        // start fragment to show the robot is running
        robotRunning = new BusyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", "robot running");
        robotRunning.setArguments(bundle);
        robotRunning.show(getFragmentManager(), "robot running");
    }

    /*
     * Converts the list of instructions given as a parameter into robot instruction equivalents
     * and sends them to the robot.
     */
    private void sendInstructionsToRobot(List<Instruction> instructionsToSend){
        String instructionString = "";
        for(Instruction inst: instructionsToSend){
            instructionString += inst.getBluetoothInstruction();
        }
        // Check instruction sequence is valid
        //String checkedInstructionString = checkInstructionSequence(instructionString);
        //btConManager.writeToRobot(checkedInstructionString+"e");

        try {
            btConManager.writeToRobot(instructionString+"e");
        } catch (IOException e) {
            // Bluetooth connection is no longer connected
            try {
                btConManager.resetConnection();
                btConManager.connectToRobot(); // Reconnect to robot
                btConManager.writeToRobot(instructionString+"e");
            } catch (Exception e1) {
                e1.printStackTrace();
                // Need to add code to end task and session gracefully so DB is not corrupted and notify user
            }
        }
    }

    /*
     * Called when a task is completed successfully. Saves the task performance and transfers to
     * the task complete activity.
     */
    private void taskCompleted(){

        session.AddTaskPerformance(taskPerformance);

        if(!session.isAvailable()){
            // new session
            Session.saveSession(getApplicationContext(),session);
            //to get the session ID from database
            session=session.findSession(cont,SelectedGroupName);
            //ippdate the session to the TP object
            taskPerformance.setSession(session);

        }
        //save the TP with the sessionID
        TaskPerformance.saveTaskPerformance(getApplicationContext(), taskPerformance);

        LocalBroadcastManager.getInstance(cont).unregisterReceiver(mMessageReceiver);
        Intent taskCompletedIntent = new Intent(getApplicationContext(), TaskCompleteActivity.class);
        taskCompletedIntent.putExtra("INTERFACE_TYPE", taskPerformance.getInterfaceType());
        startActivity(taskCompletedIntent);
    }

    /**
     * @return the task being attempted.
     */
    protected Task getCurrentTask(){ return currentTask; }

    /**
     * Display fragment checking user wants to exit.
     */
    void displayExitPopup(){
        exitDialogueFragment = new ExitDialogueFragment();
        exitDialogueFragment.show(getFragmentManager(), "exit dialog");
    }

    /**
     * Called when user decides to exit. Stops the current task attempt and saves it. Then returns
     * to the main menu activity.
     */
    @Override
    public void onYesClick() {
        taskPerformance.stopTaskAttempt(new ArrayList<Instruction>());
        session.AddTaskPerformance(taskPerformance);
        if(!session.isAvailable()){
            // new session
            Session.saveSession(getApplicationContext(),session);
            session=session.findSession(cont,SelectedGroupName);
            taskPerformance.setSession(session);
        }


        TaskPerformance.saveTaskPerformance(getApplicationContext(), taskPerformance);

        // unregister broadcast so this class is not called if the robot replies.
        LocalBroadcastManager.getInstance(cont).unregisterReceiver(mMessageReceiver);

        Intent toLaunch = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(toLaunch);
    }

    /**
     * Closes the exit fragment.
     */
    @Override
    public void onNoClick() {
        if(exitDialogueFragment != null) {
            exitDialogueFragment.dismiss();
        }
    }

    /*
     * Method is called when a broadcast is received from the thread communicating with the robot.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            robotRunning.dismiss(); // hide fragment saying robot running
            if (taskEval.taskCompleted()) {
                taskPerformance.setTaskAsCompleted();
                LocalBroadcastManager.getInstance(cont).unregisterReceiver(mMessageReceiver);
                taskCompleted();
            } else {
                taskPerformance.startNewTaskAttempt();
                displayFailedTaskAttemptDialog();
            }
        }
    };

    /*
     * Displays a fragment informing the user that the task attempt was not successful.
     */
    private void displayFailedTaskAttemptDialog(){
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", "that is not quite right, try again");
        messageFragment.setArguments(bundle);
        messageFragment.show(getFragmentManager(), "message dialogue");
    }

}
