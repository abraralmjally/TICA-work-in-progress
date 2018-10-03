package com.wilki.tica.activities.db;

import android.support.test.InstrumentationRegistry;

import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.logicLayer.Session;
import com.wilki.tica.logicLayer.SquareTypes;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.logicLayer.TaskPerformance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DbTest {

    private DbHelper dbHelper;
    private Task testTask;
    private String board;

    @Before
    public void setUp(){
        dbHelper = new DbHelper(InstrumentationRegistry.getTargetContext());
        String instructionInventory = "{(forward " + 5 + "),(turn_right " + 5 + "),(turn_left "+
                5 + "),(noise " + 0 + "),(repeat " + 0 +")}";
        board = ""+ SquareTypes.START+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+",-"+
                SquareTypes.FINISH+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+ ",-"+
                SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+",";
        testTask = new Task(new TaskLayout(board, ""), 1, instructionInventory);
    }

    @After
    public void tearDown() {
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
    }

    @Test
    public void saveTaskTest() {
        dbHelper.addEntryToTaskTable(testTask);
        List<Task> taskList = dbHelper.readAllFromTaskTable();
        Assert.assertEquals(1, taskList.size());
        Task loadedTask = taskList.get(0);
        Assert.assertEquals(testTask, loadedTask);
        Assert.assertEquals(board, loadedTask.getTaskLayout().toString());
    }

    @Test
    public void saveTaskPerformanceTest() {
        Session testSession = new Session("Group 1");
        TaskPerformance testTP = new TaskPerformance(testSession, testTask, InterfaceType.SCREEN); //Session SessionToPerf, Task taskToAttempt, InterfaceType interfaceType
        dbHelper.addEntryToTaskPerformance(testTP);
        List<TaskPerformance> taskPerfList = dbHelper.readAllFormPerformanceTable();
        Assert.assertEquals(1, taskPerfList.size());
        Assert.assertEquals(testTP, taskPerfList.get(0));
    }



}
