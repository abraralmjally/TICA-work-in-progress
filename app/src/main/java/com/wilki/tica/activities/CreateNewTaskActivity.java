package com.wilki.tica.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wilki.tica.dragAndTouchListeners.BoardTileDragListener;
import com.wilki.tica.dragAndTouchListeners.DragTouchListener;
import com.wilki.tica.exceptions.InstructionsRequiredException;
import com.wilki.tica.fragments.MessageFragment;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.SquareTypes;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.exceptions.TaskLayoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

/**
 * Created by John Wilkie on 15/11/2016.
 * Activity used to create new tasks to add to the system.
 */

public class CreateNewTaskActivity extends AppCompatActivity {

    private Spinner[] spinners;
    private int[] spinnerId = {R.id.forwardSpinner, R.id.leftSpinner, R.id.rightSpinner, R.id.noiseSpinner,
        R.id.repeatSpinner, R.id.ifSpinner, R.id.elseIfSpinner};
    private TaskLayout newTaskLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);

        newTaskLayout = new TaskLayout(5); // new task that will be built

        // set drag listeners
        RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.activity_create_new_task);
        ImageView blankBoard = (ImageView) findViewById(R.id.imageViewBoard);
        BoardTileDragListener listener = new BoardTileDragListener(screenLayout,
                getApplicationContext(), newTaskLayout, blankBoard);
        screenLayout.setOnDragListener(listener);
        setTileDragListeners();

        // set font
        TextView title = (TextView)findViewById(R.id.textViewCreateTaskTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        title.setTypeface(customFont);

        setUpSpinners();
    }

    /*
     * Load and set drag listeners to layout tiles.
     */
    private void setTileDragListeners(){
        ImageView startTile = (ImageView) findViewById(R.id.imageViewStartTile);
        startTile.setTag(SquareTypes.START.toString());
        startTile.setOnTouchListener(new DragTouchListener());

        ImageView finishTile = (ImageView) findViewById(R.id.imageViewFinishTile);
        finishTile.setTag(SquareTypes.FINISH.toString());
        finishTile.setOnTouchListener(new DragTouchListener());

        ImageView noiseTile = (ImageView) findViewById(R.id.imageViewNoiseTile);
        noiseTile.setTag(SquareTypes.NOISE.toString());
        noiseTile.setOnTouchListener(new DragTouchListener());

        ImageView blockedTile = (ImageView) findViewById(R.id.imageViewBlockedTile);
        blockedTile.setTag(SquareTypes.BLOCKED.toString());
        blockedTile.setOnTouchListener(new DragTouchListener());
    }

    /*
     * Load and set instruction spinner values.
     */
    private void setUpSpinners(){
        spinners = new Spinner[spinnerId.length];
        for(int i = 0; i < spinners.length; i++){
            spinners[i] = (Spinner) findViewById(spinnerId[i]);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.instruction_qty, R.layout.spinner_item_inst);
            adapter.setDropDownViewResource(R.layout.spinner_list_dropdown_inst);
            spinners[i].setAdapter(adapter);
            spinners[i].setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * Return to the task options menu.
     * @param view button pressed.
     */
    public void returnToTaskSettings(View view) {
        startActivity(new Intent(getApplicationContext(), TaskOptionsActivity.class));
    }

    /**
     * Creates a new task by validating the tiles currently selected and instruction quantities
     * currently set.
     * @param view button pressed.
     */
    public void createNewTask(View view) {
        try{
            newTaskLayout.checkTaskLayoutIsValid();
            ImageView taskImage = (ImageView) findViewById(R.id.imageViewBoard);
            String layoutImagePath = takeScreenshot(taskImage);
            newTaskLayout.setImagePath(layoutImagePath);

            // save new task
            HashMap<String, Integer> instructions = convertSpinnersToHashMap();
            Task newTask = new Task(newTaskLayout, instructions);
            Task.saveTask(getApplicationContext(), newTask);

            // alert user task created and return to task menu.
            Toast.makeText(this, "new task created", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), TaskOptionsActivity.class));
        } catch (TaskLayoutException | InstructionsRequiredException e) {
            // layout was not valid on insufficient instructions set.
            makeMessageDialogue(e.getMessage());
        }
    }

    /*
     * Shows a fragment with the message provided as an argument.
     */
    private void makeMessageDialogue(String message){
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        messageFragment.setArguments(bundle);
        messageFragment.show(getFragmentManager(), "message dialogue");
    }

    /*
     * Creates instruction inventory quantities from spinners. In the event that no direction
     * instructions are set throws InstructionsRequiredException.
     */
    private HashMap<String, Integer> convertSpinnersToHashMap() throws InstructionsRequiredException {
        HashMap<String, Integer> instructionQty = new HashMap<>();
        int forwardCount = convertSpinnerValueToInt(spinners[0].getSelectedItem());
        int leftCount = convertSpinnerValueToInt(spinners[1].getSelectedItem());
        int rightCount = convertSpinnerValueToInt(spinners[2].getSelectedItem());
        int noiseCount = convertSpinnerValueToInt(spinners[3].getSelectedItem());
        int repeatCount = convertSpinnerValueToInt(spinners[4].getSelectedItem());
        int ifCount = convertSpinnerValueToInt(spinners[5].getSelectedItem());
        int ifElseCount = convertSpinnerValueToInt(spinners[6].getSelectedItem());

        if(forwardCount == 0 && leftCount == 0 && rightCount == 0 && noiseCount == 0){
            throw new InstructionsRequiredException("a task requires at least one forward, left, " +
                    "right or noise instruction");
        }
        instructionQty.put(Forward.TAG, forwardCount);
        instructionQty.put(TurnLeft.TAG, leftCount);
        instructionQty.put(TurnRight.TAG, rightCount);
        instructionQty.put(Noise.TAG, noiseCount);
        instructionQty.put(Repeat.TAG, repeatCount);
        instructionQty.put(If.TAG, ifCount);
        instructionQty.put(IfElse.TAG, ifElseCount);
        return instructionQty;
    }

    /*
     * Converts spinner values into int.
     */
    private Integer convertSpinnerValueToInt(Object spinnerValue){
        String spinnerAsString = (String) spinnerValue;
        return Integer.parseInt(spinnerAsString);
    }

    /*
     * Takes a screenshot of the task layout portion of the board and saves it. Based on code from:
     * http://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-in-android
     */
    private String takeScreenshot(View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache(), (int)view.getX()+10,
                    (int)view.getY()+35, view.getWidth()-22, view.getHeight()-33);
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            return mPath;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

}
