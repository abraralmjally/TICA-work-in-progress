package com.wilki.tica.logicLayer;

import java.util.List;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 27/02/2017.
 * the TaskEvaluation task holds data on if a task attempt was successful and the instructions that
 * are on the TaskLayout.
 */

public class TaskEvaluation {

    private final boolean completed;
    private final List<Instruction> instructionsOnBoard;

    /**
     * Constructor for the TaskEvaluation class.
     * @param completed if the task attempt was successful.
     * @param instructionsOnBoard the instructions from the task attempt that are on the task layout.
     */
    public TaskEvaluation(boolean completed, List<Instruction> instructionsOnBoard){
        this.completed = completed;
        this.instructionsOnBoard = instructionsOnBoard;
    }

    /**
     * @return if the task attempt was successful.
     */
    public boolean taskCompleted(){ return completed; }

    /**
     * @return the instructions that were one the task layout.
     */
    public List<Instruction> getInstructionsOnBoard(){ return instructionsOnBoard; }
}
