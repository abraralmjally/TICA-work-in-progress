package com.wilki.tica.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wilki.tica.dragAndTouchListeners.DragTouchListener;
import com.wilki.tica.dragAndTouchListeners.InstructionDragListener;
import com.wilki.tica.logicLayer.SelectedInstructionData;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;

import java.util.HashMap;
import java.util.List;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

/**
 * Created by John Wilkie on 7/1/2017.
 * Activity for screen interface. Used while attempting a task with the screen interface.
 */

public class ScreenTaskActivity extends TaskActivity {

    private int[] imageIds = {R.id.instImage0, R.id.instImage1, R.id.instImage2, R.id.instImage3,
            R.id.instImage4, R.id.instImage5, R.id.instImage6};
    private int[] qtyLabelIds = {R.id.instTV0, R.id.instTV1, R.id.instTV2, R.id.instTV3,
            R.id.instTV4, R.id.instTV5, R.id.instTV6};
    private ImageView[] images;
    private TextView[] qtyLabels;
    private SelectedInstructionData instructionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, getIntent(), InterfaceType.SCREEN,
                getApplicationContext());
        setContentView(R.layout.activity_screen_task);
        Context cont = getApplicationContext();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.tile_layout);
        RelativeLayout instructionSection = (RelativeLayout) findViewById(R.id.instruction_layout);

        // load instruction inventory
        loadImageViews();
        loadTextViews();
        displayTaskInstructions();

        // set drag listeners
        instructionData = new SelectedInstructionData();
        InstructionDragListener instructionDragListener = new InstructionDragListener(layout,
                instructionSection, cont, instructionData);
        layout.setOnDragListener(instructionDragListener);
        instructionSection.setOnDragListener(instructionDragListener);
    }

    /*
     * Loads image views to place instruction inventory in
     */
    private void loadImageViews(){
        images = new ImageView[imageIds.length];
        for(int i = 0; i < imageIds.length; i++){
            images[i] = (ImageView) findViewById(imageIds[i]);
        }
    }

    /*
     * Loads text views for instruction inventory.
     */
    private void loadTextViews(){
        qtyLabels = new TextView[qtyLabelIds.length];
        for(int i = 0; i < qtyLabelIds.length; i++){
            qtyLabels[i] = (TextView) findViewById(qtyLabelIds[i]);
            qtyLabels[i].setTextSize(40);
        }
    }

    /**
     * Gets selected instructions and processes them.
     * @param view play button pressed.
     */
    public void runInstructions(View view){
        // play noise
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        mp.start();

        // get selected instructions and process them.
        List<Instruction> instructions = readInstructions();
        processInstructions(instructions);
    }

    /**
     * Show exit attempt fragment.
     */
    public void exitTask(View view){
        displayExitPopup();
    }

    @Override
    protected List<Instruction> readInstructions(){
        return instructionData.getAllInstructionsInSlots();
    }

    /*
     * Sets up and display task inventory section of screen.
     */
    private void displayTaskInstructions(){
        HashMap<String, Integer> instructionQty = getCurrentTask().getInstructions();
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
     * Place instructions without bodies into task inventory.
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
     * Place instructions with bodies into task inventory.
     */
    private void placeInstruction(int slot, Instruction inst, Integer instructionQty, int width,
                                  int height) {
        ImageView view = images[slot];
        view.setImageResource(inst.getImgResourceLocation());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
        view.setTag(inst.toString());
        view.setOnTouchListener(new DragTouchListener());
        TextView tv = qtyLabels[slot];
        tv.setText(Integer.toString(instructionQty));
        tv.setTag(inst.toString() + "_count");
    }
}
