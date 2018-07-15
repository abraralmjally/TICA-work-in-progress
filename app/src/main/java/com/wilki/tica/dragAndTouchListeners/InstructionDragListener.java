package com.wilki.tica.dragAndTouchListeners;

import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wilki.tica.exceptions.NoLocationException;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.SelectedInstructionData;

import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Repeat;


/**
 * Created by John Wilkie on 22/01/2017.
 * Drag listener for dropped instructions in the screen interface.
 */
public class InstructionDragListener implements View.OnDragListener {

    private SelectedInstructionData slotData;
    private ViewGroup selectedInstructionsLayout;
    private ViewGroup instructionSection;
    private Context context;
    private ImageView[] placedInstructions;
    private int imageHeight;
    private final double heightToWidthRatio = 2.5;
    private boolean firstTime = true;


    /**
     * Initializes class variables.
     * @param selectedInstructionsLayout layout that instructions are placed on to select them.
     * @param instructionsSection layout that the instructions are dragged from.
     * @param context the application context.
     * @param slotData used to store data on instructions that are being used.
     */
    public InstructionDragListener(ViewGroup selectedInstructionsLayout,
                                   ViewGroup instructionsSection, Context context,
                                   SelectedInstructionData slotData){
        this.selectedInstructionsLayout = selectedInstructionsLayout;
        this.context = context;
        this.instructionSection = instructionsSection;
        this.slotData = slotData;
        placedInstructions = new ImageView[11];
    }

    /*
     * Used to recognise the stages of a view being dragged and react to them.
     */
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DROP:
                float y = dragEvent.getY();
                ClipData clipData = dragEvent.getClipData();
                String droppedViewTag = clipData.getItemAt(0).getText().toString();
                int viewId = view.getId();
                if(viewId == R.id.tile_layout){ // view dropped on the selected instruction section
                    instructionDroppedOnSelected(droppedViewTag, y, view.getHeight());
                } else { // view dropped on the instruction inventory section.
                    instructionDroppedOnInventory(droppedViewTag);
                }
                return true;
        }
        return false;
    }

    /*
     * Processes instructions dropped on the selected instruction section.
     * @param droppedViewTag the tag of the instruction that was dropped.
     * @param yPos the y position of the dropped instruction.
     * @param the height of the selected instruction view.
     */
    private void instructionDroppedOnSelected(String droppedViewTag, float yPos, int viewHeight){
        if(firstTime){
            imageHeight = viewHeight/12;
            firstTime= false;
        }
        if(droppedViewTag.contains("-")){
            // instruction was already selected and has been moved to a new position.
            updateInstructionPos(droppedViewTag, yPos, viewHeight);
        } else {
            // instruction was dragged from the inventory section.
            placeNewInstruction(droppedViewTag, yPos, viewHeight);
        }
    }

    /*
     * Processes instructions that were already selected and have been moved.
     */
    private void updateInstructionPos(String droppedViewTag, float y, int height){
        String tag = droppedViewTag.split("-")[0];
        try {
            int[] freeSlots = slotData.findFreeSlots(tag, y, height);
            placeNewImage(tag, freeSlots);
            removeImageFromSelection(droppedViewTag);
        } catch (NoLocationException e){
            // A location can't be found for this instruction so it isn't placed.
        }

    }

    /*
     * Processes instructions that have been dragged from the instruction inventory section.
     */
    private void placeNewInstruction(String droppedViewTag, float y, int height) {
        try {
            if(instructionAvailable(droppedViewTag)) {
                int[] freeSlots = slotData.findFreeSlots(droppedViewTag, y, height);
                placeNewImage(droppedViewTag, freeSlots);
            }
        } catch (NoLocationException e){
            // A location can't be found for this instruction so it isn't placed.
        }
    }

    /*
     * Places the instruction indicated by the instruction tag into the slots provided by the
     * free slots parameter.
     */
    private void placeNewImage(String instructionTag, int[] freeSlots){
        ImageView newImage = new ImageView(context);
        newImage.setImageResource(slotData.getInstructionSlot(freeSlots[0]).getInstruction().
                getImgResourceLocation());
        int height = calculateHeight(instructionTag);
        int width = calculateWidth(height);
        LayoutParams params = new LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        newImage.setLayoutParams(params);
        selectedInstructionsLayout.addView(newImage);
        newImage.setX(calculateXPos(freeSlots[0]));
        newImage.setY(calculateYPos(freeSlots[0]));
        newImage.setTag(instructionTag + "-" + freeSlots[0]);
        newImage.setOnTouchListener(new DragTouchListener());
        placedInstructions[freeSlots[0]] = newImage;
        checkIfAnyInstructionsNowInBody(instructionTag, freeSlots[0]);
    }

    /*
     * Calculate the image height depending on the instruction type indicated by the tag.
     */
    private int calculateHeight(String instructionTag) {
        if(instructionTag.equals(Repeat.TAG)){
            return Double.valueOf(imageHeight * 3 * 1.46).intValue();
        } else if(instructionTag.equals(If.TAG)){
            return 226;
        } else if(instructionTag.equals(IfElse.TAG)){
            return 360;
        } else {
            return Double.valueOf(imageHeight * 1.8).intValue();
        }
    }

    /*
     * Calculate the image width on an instruction image.
     */
    private int calculateWidth(int height) {
        return Double.valueOf(height*heightToWidthRatio).intValue();
    }

    /*
     * Calculates the x position for a new instruction image.
     */
    private float calculateXPos(int slotIndex) {
        if(slotData.getInstructionSlot(slotIndex).isInAnInstructionBody()){
            return 25;
        }
        return 0;
    }

    /*
     * Calculates the y position for a new instruction image.
     */
    private float calculateYPos(int slotIndex) {
        if(slotData.getInstructionSlot(slotIndex).getInstruction().isABasicInstruction()){
            return slotIndex * imageHeight;
        } else {
            return slotIndex * imageHeight + 15;
        }
    }

    /*
     * Depending on the type of instruction being places calculates if any instruction already
     * selected is now in the body of the new instruction and if it is updates its position.
     */
    private void checkIfAnyInstructionsNowInBody(String instructionTag, int startIndex) {
        switch (instructionTag) {
            case Repeat.TAG:
                moveInstructionIfInBodyPosition(startIndex + 1, 25);
                moveInstructionIfInBodyPosition(startIndex + 2, 25);
                break;
            case If.TAG:
                moveInstructionIfInBodyPosition(startIndex + 1, 25);
                break;
            case IfElse.TAG:
                moveInstructionIfInBodyPosition(startIndex + 1, 25);
                moveInstructionIfInBodyPosition(startIndex + 3, 25);
                break;
        }
    }

    /*
     * If an instruction is in the body of another indent it.
     */
    private void moveInstructionIfInBodyPosition(int slotIndex, int offset) {
        if(!(slotData.getInstructionSlot(slotIndex).isEmpty()) &&
                slotData.getInstructionSlot(slotIndex).getInstruction().isABasicInstruction()){
            ImageView instructionInBody = placedInstructions[slotIndex];
            instructionInBody.setX(instructionInBody.getX()+offset);
        }
    }

    /*
     * Checks if an instruction dropped has sufficient number in the inventory.
     */
    private boolean instructionAvailable(String droppedViewTag){
        TextView instructionQty = (TextView) instructionSection.findViewWithTag(
                droppedViewTag + "_count");
        String text = instructionQty.getText().toString();
        int count = Integer.parseInt(text);
        if(count > 0){
            count--;
            instructionQty.setText(""+count);
            return true;
        }
        return false;

    }

    /*
     * Removes an instruction from the selection. If the instruction being removed has a body any
     * instruction in the body has indentation removed.
     */
    private void removeImageFromSelection(String tagToRemove){
        String instructionTag = tagToRemove.split("-")[0];
        int slotNumber = Integer.parseInt(tagToRemove.split("-")[1]);
        removeInstructionImage(tagToRemove);
        switch (instructionTag) {
            case Repeat.TAG:
                slotData.removeInstructionFromSlot(slotNumber);
                slotData.removeInstructionFromSlot(slotNumber + 3);
                placedInstructions[slotNumber] = null;
                placedInstructions[slotNumber + 3] = null;
                checkIfAnyInstructionsInBody(slotNumber + 1);
                checkIfAnyInstructionsInBody(slotNumber + 2);
                break;
            case If.TAG:
                slotData.removeInstructionFromSlot(slotNumber);
                slotData.removeInstructionFromSlot(slotNumber + 2);
                placedInstructions[slotNumber] = null;
                placedInstructions[slotNumber + 2] = null;
                checkIfAnyInstructionsInBody(slotNumber + 1);
                break;
            case IfElse.TAG:
                slotData.removeInstructionFromSlot(slotNumber);
                slotData.removeInstructionFromSlot(slotNumber + 2);
                slotData.removeInstructionFromSlot(slotNumber + 4);
                placedInstructions[slotNumber] = null;
                placedInstructions[slotNumber + 2] = null;
                placedInstructions[slotNumber + 4] = null;
                checkIfAnyInstructionsInBody(slotNumber + 1);
                checkIfAnyInstructionsInBody(slotNumber + 3);
                break;
            default:
                slotData.removeInstructionFromSlot(slotNumber);
                break;
        }
    }

    /*
     * If an instruction is in the index provided remove indentation.
     */
    private void checkIfAnyInstructionsInBody(int indexToCheck){
        if(!(slotData.getInstructionSlot(indexToCheck).isEmpty())){
            slotData.getInstructionSlot(indexToCheck).setInAnInstructionBody(false);
            moveInstructionIfInBodyPosition(indexToCheck, -25);
        }
    }

    /*
     * Checks if an instruction that has been dropped on the inventory section was previously
     * selected and if removes it from the selected area.
     */
    private void instructionDroppedOnInventory(String droppedViewTag){
        if(droppedViewTag.contains("-")){
            returnInstructionToInventory(droppedViewTag);
        }
    }

    /*
     * Processes selected instructions being returned to the inventory.
     */
    private void returnInstructionToInventory(String droppedViewTag) {
        incrementInstructionCount(droppedViewTag);
        removeImageFromSelection(droppedViewTag);
    }

    /*
     * Increase the instruction qty in the inventory for the type of instruction dropped on the
     * inventory.
     */
    private void incrementInstructionCount(String droppedViewTag){
        String instruction_type = droppedViewTag.split("-")[0];
        TextView instructionQty = (TextView) instructionSection.findViewWithTag(instruction_type +
                "_count");
        String text = instructionQty.getText().toString();
        int count = Integer.parseInt(text);
        instructionQty.setText("" + Integer.toString(count+1));
    }

    /*
     * Remove the image from the selected image view.
     */
    private void removeInstructionImage(String droppedViewTag) {
        ImageView instructionImageToRemove = (ImageView)
                selectedInstructionsLayout.findViewWithTag(droppedViewTag);
        selectedInstructionsLayout.removeView(instructionImageToRemove);
    }

}
