package com.wilki.tica.logicLayer;

import android.content.Context;

import com.wilki.tica.dataLayer.DbHelper;

import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 30/11/2016.
 * The TaskPerformance class represents each time a user begins a new task. It is possible that the
 * user may make multiple attempts at a task during a single task performance and each of these
 * attempts is represented by the TaskAttempt class.
 */

public class TaskPerformance {

    private int performanceId;
    private final Task taskToAttempt;
    private final InterfaceType interfaceType;
    private final List<TaskAttempt> taskAttempts;
    private boolean taskCompleted;
    private TaskAttempt currentAttempt;

    /**
     * Constructor used to create a fresh task performance.
     * @param taskToAttempt the task the performance is on.
     * @param interfaceType the type of the interface being used for the performance.
     */
    public TaskPerformance(Task taskToAttempt, InterfaceType interfaceType){
        this.taskToAttempt = taskToAttempt;
        this.interfaceType = interfaceType;
        taskCompleted = false;
        currentAttempt = new TaskAttempt();
        taskAttempts = new ArrayList<>();
    }

    /**
     * Constructor used to re-create a task performance that has previously been saved to a
     * database.
     * @param performanceId unique identification number.
     * @param taskToAttempt the task that the performance was on.
     * @param completed true if the task was successfully completed false otherwise.
     * @param interfaceType the type of interface used for the performance.
     * @param taskAttempts a list of task attempts that made up the performance.
     */
    public TaskPerformance(int performanceId, Task taskToAttempt, boolean completed, InterfaceType interfaceType, List<TaskAttempt> taskAttempts) {
        this.performanceId = performanceId;
        this.taskToAttempt = taskToAttempt;
        this.taskCompleted = completed;
        this.interfaceType = interfaceType;
        this.taskAttempts = taskAttempts;
    }

    /**
     * Saves the TaskPerformance given as an argument to database.
     * @param cont the application context.
     * @param taskPerformance the TaskPerformance to be saved.
     */
    public static void saveTaskPerformance(Context cont, TaskPerformance taskPerformance) {
        DbHelper dbHelper = new DbHelper(cont);
        dbHelper.addEntryToTaskPerformance(taskPerformance);
        dbHelper.close();
    }

    /**
     * Returns a list of all TaskPerformances in the database.
     * @param cont the application context.
     * @return a list of all task performances in the database.
     */
    public static List<TaskPerformance> getAllTaskPerformances(Context cont){
        DbHelper dbHelper = new DbHelper(cont);
        List<TaskPerformance> performances = dbHelper.readAllFormPerformanceTable();
        dbHelper.close();
        return performances;
    }

    /**
     * Deletes all TaskPerformance data from the database.
     * @param cont the application context.
     */
    public static void deleteAllTaskPerformanceData(Context cont){
        DbHelper dbHelper = new DbHelper(cont);
        dbHelper.resetPerformanceTable(dbHelper.getWritableDatabase());
        dbHelper.close();
    }

    /**
     * Returns the number of task attempts that made up the performance.
     * @return the number of attempts in this performance.
     */
    public int getNumAttempts(){ return taskAttempts.size(); }

    /**
     * Returns the total time spent attempting the task during this performance.
     * @return the total time of all task attempts of this performance.
     */
    public long getTaskPerformanceDuration(){
        long totalTime = 0;
        for(TaskAttempt attempt: taskAttempts){
            totalTime += attempt.getAttemptDuration();
        }
        return totalTime;
    }

    /**
     * Sets the task performance to be completed.
     */
    public void setTaskAsCompleted(){
        taskCompleted = true;
    }

    /**
     * Returns weather the task for the task performance was successfully completed.
     * @return true if the task was completed and false otherwise.
     */
    public boolean getTaskCompleted(){
        return taskCompleted;
    }

    /**
     * Stops the current task attempt.
     * @param pieces_used a list of instructions used for the task attempt.
     */
    public void stopTaskAttempt(List<Instruction> pieces_used){
        currentAttempt.stopAttemptTimer();
        currentAttempt.setInstructionsUsed(pieces_used);
        taskAttempts.add(currentAttempt);
    }

    /**
     * Starts a new task attempt, which becomes the current task attempt of the task performance.
     */
    public void startNewTaskAttempt(){
        currentAttempt = new TaskAttempt();
    }

    /**
     * @return the task that is set for this task performance.
     */
    public Task getTask(){
        return taskToAttempt;
    }

    /**
     * @return the interface used for this task performance.
     */
    public InterfaceType getInterfaceType(){ return interfaceType; }

    /**
     * @return the list of task attempts that made up this task performance.
     */
    public List<TaskAttempt> getTaskAttempts(){ return taskAttempts; }

    /**
     * @return the ID of the task performance.
     */
    public int getPerformanceId(){ return performanceId; }
}
