package com.wilki.tica.logicLayer;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 02/02/2017.
 * The InstructionSlot class represents a slot an instruction can be in while using the screen interface.
 * Information is stored on whether the slot is empty and if it is not the type of instruction
 * the slot contains.
 */

public class InstructionSlot {
    private boolean empty;
    private boolean inAnInstructionBody;
    private Instruction instructionType;

    /**
     * Default constructor, sets the slot to be empty and the current instruction to be null.
     */
    public InstructionSlot(){
        empty = true;
        instructionType = null;
        inAnInstructionBody = false;
    }

    /**
     * Sets the Instruction to the one provided as a parameter and sets the slot to not be empty.
     * @param instructionType the instruction to set in the slot.
     */
    public void setSlot(Instruction instructionType){
        this.instructionType = instructionType;
        empty = false;
    }

    /**
     * Clears the instruction slot by setting the instruction type to null and to be empty.
     */
    public void clearSlot(){
        instructionType = null;
        empty = true;
        inAnInstructionBody = false;
    }

    /**
     * Checks if the slot is empty.
     * @return true if the slot is empty and false otherwise.
     */
    public boolean isEmpty(){
        return empty;
    }

    /**
     * Returns the instruction currently in the slot. Will return null if no instruction is set.
     * @return the instruction in the slot.
     */
    public Instruction getInstruction(){
        return instructionType;
    }

    /**
     * @return true if the instruction in the slot is in the body of another instruction and false
     * otherwise.
     */
    public boolean isInAnInstructionBody() {
        return inAnInstructionBody;
    }

    /**
     * @param inAnInstructionBody sets the parameter that indicates the institution in the slot is in
     *                            the body of another instruction.
     */
    public void setInAnInstructionBody(boolean inAnInstructionBody) {
        this.inAnInstructionBody = inAnInstructionBody;
    }
}
