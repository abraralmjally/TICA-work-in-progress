package com.wilki.tica;

import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.logicLayer.Session;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskAttempt;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.logicLayer.TaskPerformance;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by John Wilkie on 30/11/2016.
 * Unit tests for TaskPerformance class.
 */

public class TaskPerformanceUnitTest {

    private int performanceNum;
    private boolean completed;
    private InterfaceType interfaceMode;
    private long duration1 = 20;
    private long duration2 = 15;
    private TaskPerformance taskPerf;
    private Task task;
    private Session SessionToPerf;

    @Before
    public void setUp(){
        performanceNum = 1;
        completed = true;
        interfaceMode = InterfaceType.SCREEN;
        task = new Task(new TaskLayout("", ""), new HashMap<String, Integer>());

        List<TaskAttempt> attemptsList = new ArrayList<>();
        attemptsList.add(new TaskAttempt(1, duration1, "forward"));
        attemptsList.add(new TaskAttempt(2, duration2, "forward"));
        taskPerf = new TaskPerformance(performanceNum,SessionToPerf, task, completed,
                interfaceMode, attemptsList);

    }

    @Test
    public void testCreation(){
        assertEquals(interfaceMode, taskPerf.getInterfaceType());
    }

    @Test
    public void testPerformanceDuration(){
        assertEquals((duration1 + duration2), taskPerf.getTaskPerformanceDuration());
    }

    @Test
    public void testSetAndGetTaskCompleted(){
        TaskPerformance taskPerf2 = new TaskPerformance(performanceNum,null, null, false,
                interfaceMode, null);
        assertFalse(taskPerf2.getTaskCompleted());
        taskPerf2.setTaskAsCompleted();
        assertTrue(taskPerf2.getTaskCompleted());
    }

    @Test
    public void testGetNumTaskAttempts(){
        assertEquals(2, taskPerf.getNumAttempts());
    }

    @Test
    public void testGetTaskAttempt(){
        List<TaskAttempt> attempts = taskPerf.getTaskAttempts();
        assertEquals(duration1, attempts.get(0).getAttemptDuration());
        assertEquals(duration2, attempts.get(1).getAttemptDuration());
    }

    @Test
    public void testGetTask(){
        assertEquals(task, taskPerf.getTask());
    }

    @Test
    public void testGetInterfaceType(){
        assertEquals(interfaceMode, taskPerf.getInterfaceType());
        TaskPerformance taskPerf2 = new TaskPerformance(1, SessionToPerf,task, true, InterfaceType.TANGIBLE, null);
        assertEquals(InterfaceType.TANGIBLE, taskPerf2.getInterfaceType());
    }

    @Test
    public void testGetPerformanceId(){
        assertEquals(performanceNum, taskPerf.getPerformanceId());
    }

}
