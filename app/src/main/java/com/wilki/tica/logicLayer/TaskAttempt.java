package com.wilki.tica.logicLayer;

import android.content.Context;
import android.os.SystemClock;

import com.wilki.tica.dataLayer.DbHelper;

import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 19/02/2017.
 * The TaskAttempt class represents an attempt at some task. It is used to record the duration of
 * the task attempt and the instructions used in that task attempt.
 */

public class TaskAttempt {

    private int attemptId;
    private long timeStarted;
    private long timeFinished;
    private long attemptTime;
    private List<Instruction> instructionsUsed;

    /**
     * Constructor used to create a new task attempt.
     */
    TaskAttempt(){
        timeStarted = SystemClock.elapsedRealtime();
        timeFinished = 0;
    }

    /**
     * Constructor used when re-creating an object from a database record.
     * @param attemptId id used to identify the task attempt.
     * @param attemptTime the time taken on this attempt.
     * @param instructionsUsed the instructions used on this attempt.
     */
    public TaskAttempt(int attemptId, long attemptTime, String instructionsUsed) {
        this.attemptId = attemptId;
        this.attemptTime = attemptTime;
        this.instructionsUsed = convertInstructionStringToList(instructionsUsed);
    }

    /**
     * Returns a list of all TaskAttempts that have the specified performance ID.
     * @param cont the application context.
     * @param perfId the performance ID to match against.
     * @return a list of TaskAttempts that match the performance ID.
     */
    public static List<TaskAttempt> getAllTaskAttemptsWithPerformanceId(Context cont, int perfId){
        DbHelper dbHelper = new DbHelper(cont);
        List<TaskAttempt> attemptsWithPerfId = dbHelper.readSelectedFromTaskAttemptsTable(perfId);
        dbHelper.close();
        return attemptsWithPerfId;
    }

    /*
    * Converts a string representing a list of instructions to a list of instructions.
     */
    private List<Instruction> convertInstructionStringToList(String instructionsUsed) {
        String[] splitInstructionString  = instructionsUsed.split(", ");
        List<Instruction> newInstructionList = new ArrayList<>();
        for (String aSplitInstructionString : splitInstructionString) {
            newInstructionList.add(Instruction.matchTagToInstruction(aSplitInstructionString));
        }
        return newInstructionList;
    }

    /**
     * Stops the timer on the task attempt and records the attempt duration on the attemptTime
     * field.
     */
    void stopAttemptTimer(){
        timeFinished = SystemClock.elapsedRealtime();
        attemptTime = timeFinished - timeStarted;
    }

    /**
     * @return the duration of the task attempt.
     */
    public long getAttemptDuration(){
        return attemptTime;
    }

    /**
     * @param instruction_list the instructions used for this attempt.
     */
    public void setInstructionsUsed(List<Instruction> instruction_list){
        this.instructionsUsed = instruction_list;
    }

    /**
     * @return a list of the instructions used for this attempt.
     */
    public List<Instruction> getInstructionsUsed(){ return instructionsUsed;}

    /**
     * @return a string representation of the instructions used for this attempt.
     */
    public String getInstructionsUsedAsString(){
        String result = "";
        for(Instruction instruction: instructionsUsed){
            result += instruction.getTag() + ", ";
        }
        return result;
    }

    /**
     * @return the id of this task attempt.
     */
    public int getAttemptId(){ return attemptId; }
}
