package com.wilki.tica.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wilki.tica.logicLayer.BluetoothConnectionManager;
import com.wilki.tica.dragAndTouchListeners.DialogClickListener;
import com.wilki.tica.fragments.ExitDialogueFragment;
import com.wilki.tica.fragments.MessageFragment;
import com.wilki.tica.fragments.BusyDialogFragment;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.TICA;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskEvaluation;
import com.wilki.tica.logicLayer.TaskPerformance;

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
    private BluetoothConnectionManager btConManager;
    private Context cont;
    private DialogFragment robotRunning;
    private TaskEvaluation taskEval;
    private DialogFragment exitDialogueFragment;

    protected void onCreate(Bundle savedInstanceState, Intent receivedIntent, InterfaceType interfaceType,
                            Context cont) {
        super.onCreate(savedInstanceState);

        this.cont = cont;
        currentTask = (Task) receivedIntent.getSerializableExtra("SELECTED_TASK");
        taskPerformance = new TaskPerformance(currentTask, interfaceType);
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
        btConManager.writeToRobot(instructionString+"e");
    }

    /*
     * Called when a task is completed successfully. Saves the task performance and transfers to
     * the task complete activity.
     */
    private void taskCompleted(){
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
