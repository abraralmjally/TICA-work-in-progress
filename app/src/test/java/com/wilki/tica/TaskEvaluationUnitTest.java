package com.wilki.tica;

import com.wilki.tica.logicLayer.TaskEvaluation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.TurnLeft;

import static junit.framework.Assert.assertEquals;

/**
 * Created by John Wilkie on 29/01/2017.
 * Unit testing for TaskEvaluation class.
 */

public class TaskEvaluationUnitTest {

    @Test
    public void testCreateTaskEvaluation(){
        boolean taskCompleted = true;
        List<Instruction> instructionsUsed = new ArrayList<>();
        TaskEvaluation taskEval = new TaskEvaluation(taskCompleted, instructionsUsed);
        assertEquals(taskEval.taskCompleted(), taskCompleted);
    }

    @Test
    public void testFalseTaskEvaluation(){
        boolean taskCompleted = false;
        List<Instruction> instructionsUsed = new ArrayList<>();
        TaskEvaluation taskEval = new TaskEvaluation(taskCompleted, instructionsUsed);
        assertEquals(taskEval.taskCompleted(), taskCompleted);
    }

    @Test
    public void testInstructionsMatch(){
        boolean taskCompleted = true;
        List<Instruction> instructionsUsed = new ArrayList<>();
        instructionsUsed.add(new Forward());
        instructionsUsed.add(new TurnLeft());
        instructionsUsed.add(new TurnLeft());
        TaskEvaluation taskEval = new TaskEvaluation(taskCompleted, instructionsUsed);
        List<Instruction> recordedInstructions = taskEval.getInstructionsOnBoard();
        assertEquals(recordedInstructions.size(), instructionsUsed.size());
        for(int i = 0; i < recordedInstructions.size(); i++){
            assertEquals(recordedInstructions.get(i), instructionsUsed.get(i));
        }
        assertEquals(taskEval.taskCompleted(), taskCompleted);
    }

    @Test
    public void testEmptyInstructionList(){
        boolean taskCompleted = true;
        List<Instruction> instructionsUsed = new ArrayList<>();
        TaskEvaluation taskEval = new TaskEvaluation(taskCompleted, instructionsUsed);
        assertEquals(taskEval.getInstructionsOnBoard().size(), 0);
    }
}
