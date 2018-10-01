package com.wilki.tica.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.Task;

import java.io.File;
import java.util.HashMap;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

/**
 * Created by John Wilkie on 14/12/2016.
 * Activity displays the board and instructions of a specific task.
 */

public class TaskDetailsActivity extends AppCompatActivity {

    private Task currentTask;
    private InterfaceType interfaceType;
    private int[] imageIds = {R.id.instImage0, R.id.instImage1, R.id.instImage2, R.id.instImage3,
            R.id.instImage4, R.id.instImage5, R.id.instImage6};
    private int[] qtyLabelIds = {R.id.instTV0, R.id.instTV1, R.id.instTV2, R.id.instTV3,
            R.id.instTV4, R.id.instTV5, R.id.instTV6};
    private ImageView[] images;
    private TextView[] qtyLabels;
    private   String SelectedGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Intent receivedIntent = getIntent();
        currentTask = (Task) receivedIntent.getSerializableExtra("SELECTED_TASK");
        interfaceType = (InterfaceType) receivedIntent.getSerializableExtra("MODE");
        SelectedGroupName = receivedIntent.getStringExtra("SelectedGroup");
        setTaskLayoutImage(); // set the image of the task layout.
        TextView taskNum = (TextView) findViewById(R.id.taskNumber);
        taskNum.setText("task " + currentTask.getTaskNumber());
        setFont();
        loadImageViews(); // get references to image views for instructions.
        loadTextViews(); // get references for text views for instructions.
        displayTaskInstructions();
    }

    /*
     * Set font for text views
     */
    private void setFont(){
        TextView instructions = (TextView) findViewById(R.id.textViewInstructions);
        TextView number = (TextView) findViewById(R.id.taskNumber);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        instructions.setTypeface(customFont);
        number.setTypeface(customFont);
    }

    /*
     * Loads image views from the layout to display images of instructions.
     */
    private void loadImageViews(){
        images = new ImageView[imageIds.length];
        for(int i = 0; i < imageIds.length; i++){
            images[i] = (ImageView) findViewById(imageIds[i]);
        }
    }

    /*
     * Loads text views from layout to display instruction quantities.
     */
    private void loadTextViews(){
        qtyLabels = new TextView[qtyLabelIds.length];
        for(int i = 0; i < qtyLabelIds.length; i++){
            qtyLabels[i] = (TextView) findViewById(qtyLabelIds[i]);
            qtyLabels[i].setTextSize(40);
        }
    }

    /**
     * Starts tangible or screen interface for the task that is currently being displayed.
     * @param view is the view pressed to call this method.
     */
    public void startTask(View view){
        Intent toStart;
        if(interfaceType == InterfaceType.SCREEN){
            toStart = new Intent(getApplicationContext(), ScreenTaskActivity.class);
        } else {
            toStart = new Intent(getApplicationContext(), TangibleTaskActivity.class);
        }
        toStart.putExtra("SELECTED_TASK", currentTask);
        toStart.putExtra("SelectedGroup", SelectedGroupName);
        startActivity(toStart);
    }

    /*
     * Loads the image of the task layout and displays it.
     */
    private void setTaskLayoutImage(){
        ImageView taskImage = (ImageView) findViewById(R.id.taskLayout);
        String layoutPath = currentTask.getTaskLayout().getLayoutImageFilePath();
        File image = new File(layoutPath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        taskImage.setImageBitmap(bitmap);
    }

    /*
     * Displays task instructions with quantities for each. Done in a dynamic manner depending on
     * the instructions available.
     */
    private void displayTaskInstructions(){
        HashMap<String, Integer> instructionQty = currentTask.getInstructions();
        if(instructionQty.get(Repeat.TAG) > 0 || instructionQty.get(If.TAG) > 0 ||
                instructionQty.get(IfElse.TAG) > 0){
            int placed = 0;
            if(instructionQty.get(Repeat.TAG) > 0){
                placeInstruction(5, new Repeat(), instructionQty.get(Repeat.TAG), 280, 300);
                placed++;
            }
            if(instructionQty.get(If.TAG) > 0 ){
                if(placed == 0){
                    placeInstruction(5, new If(), instructionQty.get(If.TAG), 280, 220);
                } else {
                    placeInstruction(4, new If(), instructionQty.get(If.TAG), 280, 220);
                }
                placed++;
            }
            if(instructionQty.get(IfElse.TAG) > 0){
                if(placed == 0){
                    placeInstruction(5, new IfElse(), instructionQty.get(IfElse.TAG), 280, 380);
                } else if(placed == 1){
                    placeInstruction(4, new IfElse(), instructionQty.get(IfElse.TAG), 280, 380);
                } else {
                    placeInstruction(6, new IfElse(), instructionQty.get(IfElse.TAG), 280, 380);
                }

            }
            placeOtherInstructions(instructionQty);
        }else{
            placeOtherInstructions(instructionQty);
        }
    }

    /*
     * Displays data on instructions without bodies.
     */
    private void placeOtherInstructions(HashMap<String, Integer> instructionQty) {
        int placed = 0;
        String[] otherInstructions = {Forward.TAG, TurnLeft.TAG, TurnRight.TAG, Noise.TAG};
        for(String inst: otherInstructions){
            int count = instructionQty.get(inst);
            if (count != 0) {
                placeInstruction(placed, Instruction.matchTagToInstruction(inst), count, 300, 80);
                placed++;
            }
        }
    }

    /*
     * Displays data on instructions that have bodies.
     */
    private void placeInstruction(int slot, Instruction inst, Integer instructionQty, int width, int height) {
        ImageView view = images[slot];
        view.setImageResource(inst.getImgResourceLocation());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
        TextView tv = qtyLabels[slot];
        tv.setText(""+instructionQty);
    }
}
