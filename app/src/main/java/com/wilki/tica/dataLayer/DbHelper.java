package com.wilki.tica.dataLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wilki.tica.R;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskAttempt;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.logicLayer.TaskPerformance;
import com.wilki.tica.logicLayer.CStudents;
import com.wilki.tica.logicLayer.Session;

import com.wilki.tica.dataLayer.DbContract.TaskEntry;
import com.wilki.tica.dataLayer.DbContract.TaskPerformanceEntry;
import com.wilki.tica.dataLayer.DbContract.TaskAttemptEntry;
import com.wilki.tica.dataLayer.DbContract.Sessions;
import com.wilki.tica.dataLayer.DbContract.Students;







import java.util.ArrayList;
import java.util.LinkedList;
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
       createStudentsTable(db);
       createSessionTable(db);

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
    //TODO addtime
    private void createPerformanceTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbContract.TaskPerformanceEntry.TABLE_NAME + " (" +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_TASK_NUM + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_ATTEMPTS + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_COMPLETED + " INTEGER, " +
                TaskPerformanceEntry.COLUMN_NAME_SESSION + " INTEGER, " +
                DbContract.TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE + " TEXT,"+
                DbContract.TaskPerformanceEntry.COLUMN_NAME_TP_TIME + " REAL);");
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
                TaskAttemptEntry.COLUMN_NAME_HELP_COUNT + " INTEGER, " +
                "PRIMARY KEY( " + DbContract.TaskAttemptEntry.COLUMN_NAME_PERFORMANCE_NUM + ", " +
                DbContract.TaskAttemptEntry.COLUMN_NAME_ATTEMPT_NUM + " ));");
    }

    /* Create table for Sessions */

    private void createSessionTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbContract.Sessions.TABLE_NAME + " (" +
                DbContract.Sessions._SessionID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Sessions.COLUMN_NAME_GROUP_NAME + " TEXT not null unique , "+
                Sessions.COLUMN_NAME_PERFORMANCE_NUM + " INTEGER );");
    }





    private void createStudentsTable (SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Students.TABLE_NAME + "(" + Students._StudentID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Students.COLUMN_NAME_STUDENT + " TEXT NOT NULL,"+ Students.COLUMN_NAME_GENDER + " INTEGER," +
                Students.COLUMN_NAME_SCHOOL+ "  TEXT," +
                Students.COLUMN_NAME_GROUP_NAME + " TEXT );" );
    }



        @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
         //   db.execSQL("DROP TABLE IF EXISTS " + DbContract.Groups.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Students.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Sessions.TABLE_NAME);
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
     * Clears all data from the  Session and the task performance and task attempt tables.
     * @param db database to clear performance data from.
     */

    //TODO might I need to delete the Students table
    public void resetSessionTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Sessions.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
        createSessionTable(db);
        createPerformanceTable(db);
        createAttemptTable(db);
    }

    //TODO not tested

    public void resetTaskTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
        createTaskTable(db);
        createPerformanceTable(db);
        createAttemptTable(db);
    }
    //TODO not tested
    public void resetStudentsTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Students.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Sessions.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskPerformanceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskAttemptEntry.TABLE_NAME);
        createStudentsTable(db);
        createTaskTable(db);
        createSessionTable(db);
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


    //TODO  add session number

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
                values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_SESSION,
                        taskPerformance.getSession().getSessionID());
                values.put(DbContract.TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE,
                taskPerformance.getInterfaceType().toString());
        values.put(TaskPerformanceEntry.COLUMN_NAME_TP_TIME,taskPerformance.getTaskPerformanceDuration());
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


    /*
    Add a Session to the Session table.
    @parm session is the session to store data on.
    @return true if the datat was successfully added and false otherwise.
    * */
//TODO test add new Session
    //not tested
    public boolean addEntryToSession (Session session){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Sessions.COLUMN_NAME_GROUP_NAME,
                session.getGroupID() );
        values.put(Sessions.COLUMN_NAME_PERFORMANCE_NUM,
                session.getNumPerformance());
        long newRowId = db.insert(DbContract.Sessions.TABLE_NAME, null, values);
        /*
        List<TaskPerformance> taskPerformance =session.getTaskPerformanceList ();
        for(int i = 0; i < taskPerformance.size(); i++){
            TaskPerformance run = taskPerformance.get(i);
            ContentValues runValues = new ContentValues();
            runValues.put(TaskPerformanceEntry.COLUMN_NAME_SESSION, newRowId);
            runValues.put(TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM, i);
            long attemptRowId = db.insert(DbContract.TaskPerformanceEntry.TABLE_NAME, null, runValues);

            if(attemptRowId < 1){
                return false;
            }
        }*/
        return newRowId > 0;

    }
/*
    //to create group from the EditText
    public void CreateNewGroupDB(){
        String groupnameString = GroupNametEditText.getText().toString().trim();

        ContentValues UpdateStudent1 = new ContentValues();
        UpdateStudent1.put(Students.COLUMN_NAME_GROUP_NAME,groupnameString);
        long newwRowID = db.update(DbContract.Students.TABLE_NAME, UpdateStudent1,Students.COLUMN_NAME_STUDENT + "= ?",new String [] {Student1Name} );
        Log.v("CreateNewGroupActivity","New group is added to Student 1"+ newwRowID);

        ContentValues UpdateStudent2 = new ContentValues();
        UpdateStudent2.put(Students.COLUMN_NAME_GROUP_NAME,groupnameString);

        long newwRowID2 = db.update(DbContract.Students.TABLE_NAME, UpdateStudent2,Students.COLUMN_NAME_STUDENT + "= ?",new String [] {Student2Name} );
        Log.v("CreateNewGroupActivity","New group is added to Student 2"+ newwRowID);

        if (newwRowID == -1){
            Toast.makeText(this,"Error with saving Group to Student 1", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Group name updated successfully for Student 1: " + newwRowID, Toast.LENGTH_SHORT).show ();
        }

        if (newwRowID2 == -1){
            Toast.makeText(this,"Error with saving Group to Student 2", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Group name updated successfully for Student 2: " + newwRowID, Toast.LENGTH_SHORT).show ();
        }

        db.close(); // Closing database connection

    }


    public boolean UpdateSession (Session session,int SessionId,TaskPerformance newTP ){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Sessions.COLUMN_NAME_PERFORMANCE_NUM,newTP.getPerformanceId());
        long newRowId = db.update(DbContract.Sessions.TABLE_NAME, values,Sessions._SessionID + "=?",SessionId);
        List<TaskPerformance> taskPerformance =session.getTaskPerformanceList ();
        for(int i = 0; i < taskPerformance.size(); i++){
            TaskPerformance run = taskPerformance.get(i);
            ContentValues runValues = new ContentValues();
            runValues.put(TaskPerformanceEntry.COLUMN_NAME_SESSION, newRowId);
            runValues.put(TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM, i);
            long attemptRowId = db.insert(DbContract.TaskPerformanceEntry.TABLE_NAME, null, runValues);
            if(attemptRowId < 1){
                return false;
            }
        }
        return newRowId > 0;

    }

    /*

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
        List<Session> Sessions=readAllFormSessionTable();
        List<TaskPerformance> taskPerf = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("select * from " +
                DbContract.TaskPerformanceEntry.TABLE_NAME, null);
        int PerfID=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM);
        int TaskNUM=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_TASK_NUM);
        int SessionNu=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_SESSION);
        int TaskComp=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_COMPLETED);
        int interfaceType_Clum=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE);
        while(resultCursor.moveToNext()){
            int perfNum = resultCursor.getInt(PerfID);
            int taskNum = resultCursor.getInt(TaskNUM);
            int SesNum=resultCursor.getInt(SessionNu);
            boolean completed = resultCursor.getInt(TaskComp) == 1;
            String inter = resultCursor.getString(interfaceType_Clum);

            InterfaceType interfaceType;
            if(inter.equals(InterfaceType.SCREEN.toString())){
                interfaceType = InterfaceType.SCREEN;
            } else {
                interfaceType = InterfaceType.TANGIBLE;
            }
            List<TaskAttempt> taskAttempts = readSelectedFromTaskAttemptsTable(perfNum);
       //     long time = resultCursor.getLong(Tasktime);
            taskPerf.add(new TaskPerformance(perfNum,Sessions.get(SesNum-1), tasks.get(taskNum-1), completed,
                    interfaceType, taskAttempts));
        }
        resultCursor.close();
        return taskPerf;
    }

    /**
     * Reads all performance of a session with the provided session id from the task performance  table.
     * @param sessionID   to match against.
     * @return list of Perfromance that match the session ID.
     */
    public List<TaskPerformance> readSelectedFromTaskTPTable(int sessionID){
        List<TaskPerformance> PerfData = new ArrayList<>();
        List<Task> tasks = readAllFromTaskTable();
        List<Session> Allsession = readAllFormSessionTable();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("SELECT * FROM " + DbContract.TaskPerformanceEntry.TABLE_NAME +
                " WHERE " + TaskPerformanceEntry.COLUMN_NAME_SESSION + " = " +
                sessionID , null);
        int PerfID=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_PERFORMANCE_NUM);
        int TaskNUM=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_TASK_NUM);
        int SessionNu=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_SESSION);
        int TaskComp=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_COMPLETED);
        int interfaceType_Clum=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_INTERFACE_TYPE);
      //  long TaskPtime=resultCursor.getColumnIndex(TaskPerformanceEntry.COLUMN_NAME_TP_TIME);
        while(resultCursor.moveToNext()){
            int current_PerfId = resultCursor.getInt(PerfID);
            int current_Tasknum= resultCursor.getInt(TaskNUM);
            int current_SesNum=resultCursor.getInt(SessionNu);
            boolean current_TaskComp=resultCursor.getInt(TaskComp)==1;
            String inter=resultCursor.getString(interfaceType_Clum);
            InterfaceType interfaceType;
            if(inter.equals(InterfaceType.SCREEN.toString())){
                interfaceType = InterfaceType.SCREEN;
            } else {
                interfaceType = InterfaceType.TANGIBLE;
            }
            List<TaskAttempt> taskAttempts = readSelectedFromTaskAttemptsTable(current_PerfId);

            long current_time = resultCursor.getLong(2);
            PerfData.add(new TaskPerformance(current_PerfId,Allsession.get(sessionID-1), tasks.get(current_Tasknum-1),current_TaskComp,interfaceType,taskAttempts));
        }
        resultCursor.close();
        return PerfData;
    }



    /**
     * Reads all data from the task Session table.
     * @return a list of all task performances in the able.
     */
    public List<Session> readAllFormSessionTable(){
        List<Session> SessionkPerf = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor resultCursor = db.rawQuery("select * from " +
                DbContract.Sessions.TABLE_NAME, null);
        int SessionNu=resultCursor.getColumnIndex(Sessions._SessionID);
        int SessionGroup=resultCursor.getColumnIndex(Sessions.COLUMN_NAME_GROUP_NAME);
        int SessionTPSize=resultCursor.getColumnIndex(Sessions.COLUMN_NAME_PERFORMANCE_NUM);
        while(resultCursor.moveToNext()){
            int sessionID = resultCursor.getInt(SessionNu);
            String groupname = resultCursor.getString(SessionGroup);
           int TPsize=resultCursor.getInt(SessionTPSize);
            SessionkPerf.add(new Session(sessionID, groupname,TPsize));
        }
        resultCursor.close();
        return SessionkPerf;
    }

    // read the selected school
    public List<String> fetchAllSchools()
    {     List<String> SchoolNameslist = new LinkedList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String [] projection  = {Students.COLUMN_NAME_SCHOOL};
        String sortOrder = Students.COLUMN_NAME_SCHOOL + " DESC";
        Cursor schoolCursor = db.query(true, Students.TABLE_NAME,projection, null, null, Students.COLUMN_NAME_SCHOOL, null,sortOrder,null);
       //But you MUST remember to send argument in GROUPBY (NOT NULL send).
        //You must give column name for distinct.
        int schoolname=schoolCursor.getColumnIndex(Students.COLUMN_NAME_SCHOOL);

        while (schoolCursor.moveToNext())

        { String currentschool = schoolCursor.getString(schoolname);
            SchoolNameslist.add(currentschool);
        }
        schoolCursor.close();
        db.close();
        return SchoolNameslist;

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
     * Reads all Students from the students table.
     * @return a list of all tasks in the task table.
     */
    public List<CStudents> fetchAllStudents()
    {  List<CStudents> Studentslist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // How how the result stor in the cursor
        String sortOrder = Students.COLUMN_NAME_STUDENT + " DESC";
        Cursor StudentCursor = db.query( Students.TABLE_NAME,null, null, null, null, null,sortOrder,null);
        int StudentID=StudentCursor.getColumnIndex(Students._StudentID);
        int Studentstudentname=StudentCursor.getColumnIndex(Students.COLUMN_NAME_STUDENT);
        int Studentgender=StudentCursor.getColumnIndex(Students.COLUMN_NAME_GENDER);
        int StudentsSchool = StudentCursor.getColumnIndex(Students.COLUMN_NAME_SCHOOL);
        int StudentsGroup = StudentCursor.getColumnIndex(Students.COLUMN_NAME_GROUP_NAME);
        while (StudentCursor.moveToNext())
        {
            int CurrentID= StudentCursor.getInt(StudentID);
            String Currentname= StudentCursor.getString(Studentstudentname);
            int Currentgender= StudentCursor.getInt(Studentgender);
            String CurrentSchool = StudentCursor.getString(StudentsSchool);
            String  CurrentGroup = StudentCursor.getString(StudentsGroup);


            Studentslist.add(new CStudents(CurrentID,Currentname,Currentgender,CurrentSchool,CurrentGroup));
        }
        StudentCursor.close();
        return Studentslist;
    }

    //not used
    public List<CStudents> fetchAllStudents(String schoolname)
    {  List<CStudents> Studentslist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Students.COLUMN_NAME_SCHOOL +"=?";
        String [] selesctionArgs = {schoolname};
        // How how the result stor in the cursor
        String sortOrder = Students.COLUMN_NAME_STUDENT + " DESC";
        Cursor StudentCursor = db.query( Students.TABLE_NAME,null, selection, selesctionArgs, null, null,sortOrder,null);
        int StudentID=StudentCursor.getColumnIndex(Students._StudentID);
        int Studentstudentname=StudentCursor.getColumnIndex(Students.COLUMN_NAME_STUDENT);
        int Studentgender=StudentCursor.getColumnIndex(Students.COLUMN_NAME_GENDER);
        int StudentsSchool = StudentCursor.getColumnIndex(Students.COLUMN_NAME_SCHOOL);
        int StudentsGroup = StudentCursor.getColumnIndex(Students.COLUMN_NAME_GROUP_NAME);
        while (StudentCursor.moveToNext())
        {
            int CurrentID= StudentCursor.getInt(StudentID);
            String Currentname= StudentCursor.getString(Studentstudentname);
            int Currentgender= StudentCursor.getInt(Studentgender);
            String CurrentSchool = StudentCursor.getString(StudentsSchool);
            String  CurrentGroup = StudentCursor.getString(StudentsGroup);


            Studentslist.add(new CStudents(CurrentID,Currentname,Currentgender,CurrentSchool,CurrentGroup));
        }
        StudentCursor.close();
        return Studentslist;
    }



}
