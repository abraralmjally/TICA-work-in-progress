package com.wilki.tica.dataLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskAttempt;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.logicLayer.TaskPerformance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John Wilkie on 29/11/2016.
 * Class acts as the interface between the logic and data layers. Controls access to the database.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTaskTable(db);
        createPerformanceTable(db);
        createAttemptTable(db);
    }

    /*
     * Create a table to store task data.
     */
    private void createTaskTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbContract.TaskEntry.TABLE_NAME + "  (" +
                DbContract.TaskEntry.COLUMN_NAME_NUMBER  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.TaskEntry.COLUMN_NAME_BOARD + " TEXT, " +
                DbContract.TaskEntry.COLUMN_NAME_INSTRUCTIONS + " TEXT," +
                DbContract.TaskEntry.COLUMN_NAME_FILEPATH + " TEXT );");
    }

    /*
     * Create table to store task performance data.
     */
    private void createPerformanceTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbContract.TaskPerformanceEntry.TABLE_NAME + " (" +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_TASK_NUM + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_ATTEMPTS + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_COMPLETED + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE + " TEXT );");
    }

    /*
     * Create table to store task attempts.
     */
    private void createAttemptTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbContract.TaskAttemptEntry.TABLE_NAME + " (" +
                DbContract.TaskAttemptEntry.COLUMN_NAME_PERFORMANCE_NUM + " INTEGER, " +
                DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_NUM + " INTEGER, " +
                DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_TIME + " REAL, " +
                DbContract.TaskAttemptEntry.COLUMN_NAME_PIECES_USED + " TEXT, " +
                "PRIMARY KEY( " + DbContract.TaskAttemptEntry.COLUMN_NAME_PERFORMANCE_NUM + ", " +
                DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_NUM + " ));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Clears all data from the task performance and task attempt tables.
     * @param db database to clear performance data from.
     */
    public void resetPerformanceTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
        createPerformanceTable(db);
        createAttemptTable(db);
    }

    /**
     * Adds an entry to the task table.
     * @param taskToAdd task to add to the table.
     * @return true if task added and false otherwise.
     */
    public boolean addEntryToTaskTable(Task taskToAdd){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.TaskEntry.COLUMN_NAME_BOARD, taskToAdd.getTaskLayout().toString());
        values.put(DbContract.TaskEntry.COLUMN_NAME_INSTRUCTIONS,
                taskToAdd.getInstructionsAsString());
        values.put(DbContract.TaskEntry.COLUMN_NAME_FILEPATH,
                taskToAdd.getTaskLayout().getLayoutImageFilePath());
        long newRowId = db.insert(DbContract.TaskEntry.TABLE_NAME, null, values);
        return newRowId > 0;
    }

    /**
     * Reads all tasks from the task table.
     * @return a list of all tasks in the task table.
     */
    public List<Task> readAllFromTaskTable(){
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("select * from " + DbContract.TaskEntry.TABLE_NAME, null);
        while(resultCursor.moveToNext()){
            int taskNo = resultCursor.getInt(0);
            String taskLayout = resultCursor.getString(1);
            String taskInstructions = resultCursor.getString(2);
            String taskFilePath = resultCursor.getString(3);
            tasks.add(new Task(new TaskLayout(taskLayout, taskFilePath), taskNo, taskInstructions));
        }
        resultCursor.close();
        return tasks;
    }

    /**
     * Adds a task performance to the task performance table.
     * @param taskPerformance the task performance to store data on.
     * @return true if the data was successfully added and false otherwise.
     */
    public boolean addEntryToTaskPerformance(TaskPerformance taskPerformance){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_TASK_NUM,
                taskPerformance.getTask().getTaskNumber() );
        values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_ATTEMPTS,
                taskPerformance.getNumAttempts() );
        values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_COMPLETED,
                taskPerformance.getTaskCompleted());
        values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE,
                taskPerformance.getInterfaceType().toString());
        long newRowId = db.insert(DbContract.TaskPerformanceEntry.TABLE_NAME, null, values);

        List<TaskAttempt> taskAttempts = taskPerformance.getTaskAttempts();
        for(int i = 0; i < taskAttempts.size(); i++){
            TaskAttempt run = taskAttempts.get(i);
            ContentValues runValues = new ContentValues();
            runValues.put(DbContract.TaskAttemptEntry.COLUMN_NAME_PERFORMANCE_NUM, newRowId);
            runValues.put(DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_NUM, i);
            runValues.put(DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_TIME,
                    run.getAttemptDuration());
            runValues.put(DbContract.TaskAttemptEntry.COLUMN_NAME_PIECES_USED,
                    run.getInstructionsUsedAsString());
            long attemptRowId = db.insert(DbContract.TaskAttemptEntry.TABLE_NAME, null, runValues);
            if(attemptRowId < 1){
                return false;
            }
        }
        return newRowId > 0;
    }

    /**
     * Reads all task attempts with the provided performance id from the task attempt table.
     * @param performanceID the performance ID to match against.
     * @return list of task attempts that match the task ID.
     */
    public List<TaskAttempt> readSelectedFromTaskAttemptsTable(int performanceID){
        List<TaskAttempt> attemptData = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("SELECT * FROM " + DbContract.TaskAttemptEntry.TABLE_NAME +
                " WHERE " + DbContract.TaskAttemptEntry.COLUMN_NAME_PERFORMANCE_NUM + " = " +
                performanceID , null);
        while(resultCursor.moveToNext()){
            int attemptId = resultCursor.getInt(1);
            long time = resultCursor.getLong(2);
            String InstructionsUsed = resultCursor.getString(3);
            attemptData.add(new TaskAttempt(attemptId, time, InstructionsUsed));
        }
        resultCursor.close();
        return attemptData;
    }

    /**
     * Reads all data from the task performance table.
     * @return a list of all task performances in the able.
     */
    public List<TaskPerformance> readAllFormPerformanceTable(){
        List<Task> tasks = readAllFromTaskTable();
        List<TaskPerformance> taskPerf = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("select * from " +
                DbContract.TaskPerformanceEntry.TABLE_NAME, null);
        while(resultCursor.moveToNext()){
            int perfNum = resultCursor.getInt(0);
            int taskNum = resultCursor.getInt(1);
            boolean completed = resultCursor.getInt(3) == 1;
            String inter = resultCursor.getString(4);
            InterfaceType interfaceType;
            if(inter.equals(InterfaceType.SCREEN.toString())){
                interfaceType = InterfaceType.SCREEN;
            } else {
                interfaceType = InterfaceType.TANGIBLE;
            }
            List<TaskAttempt> taskAttempts = readSelectedFromTaskAttemptsTable(perfNum);
            taskPerf.add(new TaskPerformance(perfNum, tasks.get(taskNum-1), completed,
                    interfaceType, taskAttempts));
        }
        resultCursor.close();
        return taskPerf;
    }
}
