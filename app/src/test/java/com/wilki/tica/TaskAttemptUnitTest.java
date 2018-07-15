package com.wilki.tica;

import com.wilki.tica.logicLayer.TaskAttempt;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

import static junit.framework.Assert.assertEquals;

/**
 * Created by John Wilkie on 19/02/2017.
 * Unit tests for the TaskAttemptClass.
 * Cannot test a lot of methods as require the clock.
 */

public class TaskAttemptUnitTest {

    private int attemptId;
    private TaskAttempt attempt;
    private long attemptDuration;
    private String attemptString;

    @Before
    public void setUp(){
        attemptId = 1;
        attemptDuration = 19300;
        attemptString = "" + Forward.TAG + ", " + TurnLeft.TAG + ", " + TurnRight.TAG + ", ";
        attempt = new TaskAttempt(attemptId, attemptDuration, attemptString);
    }

    @Test
    public void testNonEmptyConstructor(){
        assertEquals(attemptId, attempt.getAttemptId());
        assertEquals(attemptDuration, attempt.getAttemptDuration());
    }

    @Test
    public void testSetAndGetInstructionsUsed(){
        List<Instruction> instructionsUsed = new ArrayList<>();
        instructionsUsed.add(new Forward());
        instructionsUsed.add(new Noise());
        instructionsUsed.add(new TurnLeft());
        attempt.setInstructionsUsed(instructionsUsed);
        assertEquals(instructionsUsed, attempt.getInstructionsUsed());
    }

    @Test
    public void testGetInstructionsUsedAsString(){
        assertEquals(attemptString, attempt.getInstructionsUsedAsString());
    }

    @Test
    public void testSetAndGetEmptyInstructionList(){
        List<Instruction> emptyList = new ArrayList<>();
        attempt.setInstructionsUsed(emptyList);
        assertEquals(emptyList, attempt.getInstructionsUsed());
    }

    @Test
    public void testGetInstructionsUsedAsStringWhenSetWIthEmptyList(){
        List<Instruction> emptyList = new ArrayList<>();
        attempt.setInstructionsUsed(emptyList);
        assertEquals("", attempt.getInstructionsUsedAsString());
    }

}
