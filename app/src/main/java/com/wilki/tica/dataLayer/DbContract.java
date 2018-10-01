package com.wilki.tica.dataLayer;

import android.provider.BaseColumns;

/**
 * Created by John Wilkie on 29/11/2016.
 * Contract for the database tables that sets table names and column names.
 */

public class DbContract {

    private DbContract(){}



    /*
     * Contract for Students table
     */
    public static class Students implements BaseColumns {
        public static final String TABLE_NAME = "Students";
        public final static String _StudentID = BaseColumns._ID;
        public static final String COLUMN_NAME_GROUP_NAME = "groupname";
        public static final String COLUMN_NAME_SCHOOL = "schoolname";
        public static final String COLUMN_NAME_STUDENT = "studentname";
        public static final String COLUMN_NAME_GENDER= "gender";
        public static final int GENDER_MALE=1;
        public static final int GENDER_FEMALE = 2;
    }

    /*
     * Contract for Groups table

    public static class Groups implements BaseColumns {
        public final static String _GroupID = BaseColumns._ID;
        public static final String TABLE_NAME = "Groups";
        public static final String COLUMN_NAME_NAME= "name";
        public static final String COLUMN_NAME_STUDENT1= "student1_ID";
        public static final String COLUMN_NAME_STUDENT2= "student2_ID";
        public static final String COLUMN_NAME_SCHOOL = "schoolname";
    }
  */
    /*
     * Contract for Sessions table
     */
    public static class Sessions implements BaseColumns {
        public  static final String TABLE_NAME = "Sessions";
        public final static String _SessionID = BaseColumns._ID;
        public  static final String COLUMN_NAME_GROUP_NAME= "groupname";
       //public  static final String COLUMN_NAME_TASKS = "tasks";
        public static final String COLUMN_NAME_PERFORMANCE_NUM = "performanceNum";
    }

    /*
     * Contract for task table
     */
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_BOARD = "board";
        public static final String COLUMN_NAME_INSTRUCTIONS = "instructions";
        public  static final String COLUMN_NAME_FILEPATH = "filePath";
    }

    /*
     * Contract for task performance table.
     */
    public static class TaskPerformanceEntry implements BaseColumns { static final String TABLE_NAME = "taskPerformance";
        public static final String COLUMN_NAME_PERFORMANCE_NUM = "performanceNum";
        public static final String COLUMN_NAME_TASK_NUM = "taskNum";
        public static final String COLUMN_NAME_SESSION = "sessionNum";
        public static final String COLUMN_NAME_ATTEMPTS = "attempts";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_TP_TIME = "PTime";
        public static final String COLUMN_NAME_INTERFACE_TYPE = "interfaceType";
    }

    /*
     * Contract for task attempt table.
     */
    public static class TaskAttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "taskAttempt";
        public  static final String COLUMN_NAME_PERFORMANCE_NUM = "performanceNum";
        public static final String COLUMN_NAME_ATTEMPT_NUM = "attemptNum";
        public  static final String COLUMN_NAME_PIECES_USED = "piecesUsed";
        public static final String COLUMN_NAME_ATTEMPT_TIME = "attemptTime";
        public static final String COLUMN_NAME_HELP_COUNT = "helpcount";

    }



}
