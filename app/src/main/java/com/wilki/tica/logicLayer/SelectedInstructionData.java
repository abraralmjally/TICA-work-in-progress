package com.wilki.tica.logicLayer;

import com.wilki.tica.exceptions.NoLocationException;

import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Repeat;

/**
 * Created by John Wilkie on 01/02/2017.
 * Stores and manipulates data regarding the set of instructions that are currently selected. Splits
 * an area into a set of slots that instructions can be allocated to.
 */

public class SelectedInstructionData {

    private final int maxSlots = 11;    // max number of instructions+1
    private final InstructionSlot[] instructionSlots;
    private boolean first;
    private int slotHeight;

    /**
     * Constructor that sets all instruction slots to empty.
     */
    public SelectedInstructionData(){
        instructionSlots = new InstructionSlot[maxSlots];
        clearAllSlots();
        first = true;
    }

    /*
    * Sets all instruction slots to be empty.
     */
    private void clearAllSlots(){
        for(int i = 0; i < instructionSlots.length; i++){
            instructionSlots[i] = new InstructionSlot();
        }
        instructionSlots[0].setSlot(null);
    }

    /**
     * Attempts to allocate a slot to place an instruction.
     * @param droppedViewTag the tag of the instruction to allocate a slot to.
     * @param posY the yPosition of the instruction.
     * @param viewHeight The total height of the area the slots represent.
     * @return an array of the slots allocated.
     * @throws NoLocationException thrown if no slot can be allocated.
     */
    public int[] findFreeSlots(String droppedViewTag, float posY, int viewHeight) throws
            NoLocationException {
        if (first) {
            setParams(viewHeight);
            first = false;
        }
        int location = (int) posY / slotHeight;
        if(location > maxSlots){
            location = maxSlots-1;
        }
        int[] slots;
        if(Instruction.matchTagToInstruction(droppedViewTag).isABasicInstruction()){
            slots = new int[1];
            slots[0] = findSlotForBasicInstruction(posY, location);
            setBasicInstructionInSlot(droppedViewTag, slots[0]);
        }else {
            slots = findSlotsForComplexInstruction(droppedViewTag, location);
            setComplexInstructionInSlots(droppedViewTag, slots);
        }
        return slots;
    }

    /*
     * Allocates slots for instructions without bodies.
     */
    private int findSlotForBasicInstruction(float posY, int location) throws NoLocationException {
        int freeSlot;
        if(location < maxSlots && instructionSlots[location].isEmpty()){
            freeSlot = location;
        } else if(location < maxSlots && emptyNeighbouringSlot(location)) {
            freeSlot = findNearestFreeLocation(location, posY);
        } else {
            throw new NoLocationException("Location " + location + " is full.");
        }
        return freeSlot;
    }

    /*
     * Allocates slots for instructions with bodies.
     */
    private int[] findSlotsForComplexInstruction(String droppedViewTag, int droppedLocation) throws
            NoLocationException {
        int[] freeSlots;
        if(droppedViewTag.equals(Repeat.TAG)){
            freeSlots = getFreeSlotsForRepeat(droppedLocation);
        } else if(droppedViewTag.equals(If.TAG)){
            freeSlots = getFreeSlotsForIf(droppedLocation);
        } else {
            freeSlots = getFreeSlotsForIfElse(droppedLocation);
        }
    
        if(freeSlots == null){
            throw new NoLocationException("Location " + droppedLocation + " is full.");
        }
        return freeSlots;
    }



    /*
    * Finds slots for a repeat instruction.
     */
    private int[] getFreeSlotsForRepeat(int location){
        int[] freeSlots = null;
        if((location == 0 || location == 1 || location == 2) && instructionSlots[1].isEmpty() &&
                instructionSlots[4].isEmpty()){
            freeSlots = new int[]{1, 4};
        } else if((location == maxSlots -1 || location == maxSlots -2) &&
                instructionSlots[maxSlots-1].isEmpty() &&
                instructionSlots[maxSlots-4].isEmpty()){
            freeSlots = new int[]{maxSlots -4, maxSlots -1};
        } else if (instructionSlots[location-2].isEmpty() &&
                instructionSlots[location+1].isEmpty()){
            freeSlots = new int[]{location-2, location+1};
        }
        return freeSlots;
    }

     /*
      * Finds slots for an If instruction.
      */
    private int[] getFreeSlotsForIf(int location){
        int[] freeSlots = null;
        if((location == 0 || location == 1 || location == 2) && instructionSlots[1].isEmpty() &&
                instructionSlots[3].isEmpty()){
            freeSlots = new int[]{1, 3};
        } else if((location == maxSlots -1 || location == maxSlots -2) &&
                instructionSlots[maxSlots-3].isEmpty() &&
                instructionSlots[maxSlots-1].isEmpty()){
            freeSlots = new int[]{maxSlots -3, maxSlots -1};
        } else if (instructionSlots[location-1].isEmpty() && instructionSlots[location+1].isEmpty()){
            freeSlots = new int[]{location-1, location+1};
        }
        return freeSlots;
    }

    /*
     * Finds slots for if else instruction.
     */
    private int[] getFreeSlotsForIfElse(int location){
        int[] freeSlots = null;
        if((location == 0 || location == 1 || location == 2) && instructionSlots[1].isEmpty() &&
                instructionSlots[3].isEmpty() &&
                instructionSlots[5].isEmpty()){
            freeSlots = new int[]{1, 3, 5};
        } else if((location == maxSlots-1 || location == maxSlots-2 || location == maxSlots-3) &&
                instructionSlots[maxSlots-1].isEmpty() &&
                instructionSlots[maxSlots-3].isEmpty() &&
                instructionSlots[maxSlots-5].isEmpty()){
            freeSlots = new int[]{maxSlots-5, maxSlots-3, maxSlots-1};
        } else if (instructionSlots[location-2].isEmpty() && instructionSlots[location].isEmpty()
                && instructionSlots[location+2].isEmpty()){
            freeSlots = new int[]{location-2, location, location+2};
        }
        return freeSlots;
    }

    /*
     * Sets the height of a single instruction based on the screen height and the number of slots.
     */
    private void setParams(int viewHeight){
        slotHeight = viewHeight / (maxSlots +1);
    }

    /*
     * Set a slot without a body to a slot.
     */
    private void setBasicInstructionInSlot(String droppedViewTag, int freeSlot){
        Instruction instructionToSet = Instruction.matchTagToInstruction(droppedViewTag);
        instructionSlots[freeSlot].setSlot(instructionToSet);
        if(inABlock(freeSlot)){ // slot is in the body of another
            instructionSlots[freeSlot].setInAnInstructionBody(true);
        }
    }

    /*
     * Set an instruction with a body into multiple slots.
     */
    private void setComplexInstructionInSlots(String droppedViewTag, int[] freeSlot) {
        Instruction inst = Instruction.matchTagToInstruction(droppedViewTag);
        int j = 0;
        for(int i = freeSlot[0]; i <= freeSlot[freeSlot.length-1]; i++){
            if(i == freeSlot[j]){
                instructionSlots[freeSlot[j]].setSlot(inst);
                j++;
            } else { // update instructions potentially in the body
                if((!instructionSlots[i].isEmpty())){
                    instructionSlots[i].setInAnInstructionBody(true);
                }
            }
        }
    }


    /*
     * Checks if the provided location is within another instruction body.
     */
    private boolean inABlock(int location){
        int i = 1;
        boolean inRepeat = false;
        boolean inIf = false;
        boolean inIfElse = false;
        while(i < instructionSlots.length && !inRepeat && !inIf && !inIfElse){
            InstructionSlot currentSlot = instructionSlots[i];
            if(!currentSlot.isEmpty() && currentSlot.getInstruction().getTag().equals(Repeat.TAG)){
                inRepeat = location == i+1 || location == i+2;
                i += 3;
            }else if(!currentSlot.isEmpty() && currentSlot.getInstruction().getTag().equals(If.TAG)){
                inIf = location == i+1;
                i += 2;
            }else if(!currentSlot.isEmpty() && currentSlot.getInstruction().getTag().equals(IfElse.TAG)){
                inIfElse = location == i+1 || location == i+3;
                i += 4;
            }
            i++;
        }

        return inRepeat || inIf || inIfElse;
    }

    /*
     * Returns true if a slot is empty either side of the once indicated by the parameter given.
     */
    private boolean emptyNeighbouringSlot(int location) {
        boolean freeNeighbour;
        if(location == 0){
            freeNeighbour = instructionSlots[1].isEmpty();
        } else if (location == 1){
            freeNeighbour = instructionSlots[2].isEmpty();
        } else if (location == maxSlots -1){
            freeNeighbour = instructionSlots[maxSlots -2].isEmpty();
        } else {
            freeNeighbour = instructionSlots[location-1].isEmpty() ||
                    instructionSlots[location+1].isEmpty();
        }
        return freeNeighbour;
    }

    /*
     * Finds the nearest free slot to the index of the location provided as an argument.
     */
    private int findNearestFreeLocation(int location, float posY) {
        int freeNeighbour;
        if(location == 0 && instructionSlots[1].isEmpty()){
            freeNeighbour = 1;
        } else if (location == 1 && instructionSlots[2].isEmpty()){
            freeNeighbour = 2;
        } else if (location == maxSlots -1 && instructionSlots[maxSlots -2].isEmpty()){
            freeNeighbour = maxSlots -2;
        } else {
            if(!instructionSlots[location-1].isEmpty()){
                freeNeighbour = location + 1;
            } else if(!instructionSlots[location+1].isEmpty()){
                freeNeighbour = location -1;
            } else {
                float diff = ((location * slotHeight) - ((location+1) * slotHeight)) / 2;
                float remainder = (location * slotHeight + diff) - posY;
                if(remainder >=0){
                    freeNeighbour = location + 1;
                } else {
                    freeNeighbour = location - 1;
                }
            }
        }
        return freeNeighbour;
    }

    /**
     * Removes the instruction from the slot indicated by the index provided as a parameter.
     * @param slotIndex the index of the instruction to remove.
     */
    public void removeInstructionFromSlot(int slotIndex){
        instructionSlots[slotIndex].clearSlot();
    }

    /**
     * @return a list of all the currently selected instructions as a list.
     */
    public List<Instruction> getAllInstructionsInSlots(){
        List<Instruction> instructions = new ArrayList<>();
        for(int i = 1; i < instructionSlots.length; i++){
            InstructionSlot currentSlot = instructionSlots[i];
            if(!currentSlot.isEmpty()) {
                instructions.add(currentSlot.getInstruction());
            }
        }
        return instructions;
    }

    /**
     * Returns the InstructionSlot in the slot indicated by the index.
     * @param index the index of the InstructionSlot to return.
     * @return the InstructionSlot in the index.
     */
    public InstructionSlot getInstructionSlot(int index){
        return instructionSlots[index];
    }
}
