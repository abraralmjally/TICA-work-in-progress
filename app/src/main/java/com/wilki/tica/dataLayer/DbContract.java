package com.wilki.tica.dataLayer;

import android.provider.BaseColumns;

/**
 * Created by John Wilkie on 29/11/2016.
 * Contract for the database tables that sets table names and column names.
 */

class DbContract {

    private DbContract(){}

    /*
     * Contract for task table
     */
    static class TaskEntry implements BaseColumns {
        static final String TABLE_NAME = "task";
        static final String COLUMN_NAME_NUMBER = "number";
        static final String COLUMN_NAME_BOARD = "board";
        static final String COLUMN_NAME_INSTRUCTIONS = "instructions";
        static final String COLUMN_NAME_FILEPATH = "filePath";
    }

    /*
     * Contract for task performance table.
     */
    static class TaskPerformanceEntry implements BaseColumns {
        static final String TABLE_NAME = "taskPerformance";
        static final String COLUMN_NAME_PERFORMANCE_NUM = "performanceNum";
        static final String COLUMN_NAME_TASK_NUM = "taskNum";
        static final String COLUMN_NAME_ATTEMPTS = "attempts";
        static final String COLUMN_NAME_COMPLETED = "completed";
        static final String COLUMN_NAME_INTERFACE_TYPE = "interfaceType";
    }

    /*
     * Contract for task attempt table.
     */
    static class TaskAttemptEntry implements BaseColumns {
        static final String TABLE_NAME = "taskAttempt";
        static final String COLUMN_NAME_PERFORMANCE_NUM = "performanceNum";
        static final String COLUMN_NAME_ATTEMPT_NUM = "attemptNum";
        static final String COLUMN_NAME_PIECES_USED = "piecesUsed";
        static final String COLUMN_NAME_ATTEMPT_TIME = "attemptTime";
    }
}
