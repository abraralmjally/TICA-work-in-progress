package com.wilki.tica;

import com.wilki.tica.logicLayer.SquareTypes;
import com.wilki.tica.logicLayer.Task;
import com.wilki.tica.logicLayer.TaskEvaluation;
import com.wilki.tica.logicLayer.TaskLayout;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

import static org.junit.Assert.*;

/**
 * Created by John Wilkie on 29/11/2016.
 * Unit tests for Task class.
 */

public class TaskUnitTests {

    private String board;
    private int taskNum;
    private Task testTask;
    private List<Instruction> instructionList;
    private int forwardCount = 5;
    private int noiseCount = 4;
    private int leftCount = 3;
    private int rightCount = 2;
    private int repeatCount = 1;
    private String instructionInventory;

    @Before
    public void setUp(){
        instructionInventory = "{(forward "+forwardCount+"),(turn_right "+rightCount+"),(turn_left "+
                leftCount+"),(noise "+noiseCount+"),(repeat "+ repeatCount +")}";
        board = ""+ SquareTypes.START+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+",-"+
                SquareTypes.FINISH+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+ ",-"+
                SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+",";
        taskNum = 1;
        testTask = new Task(new TaskLayout(board, ""), taskNum, instructionInventory);
        instructionList = new ArrayList<>();
    }

    @Test
    public void testCreation(){
        assertEquals(board, testTask.getTaskLayout().toString());
        assertEquals(taskNum, testTask.getTaskNumber());
    }

    @Test
    public void testGetInstructionQty(){
        HashMap<String, Integer> instructions = testTask.getInstructions();

        assertEquals(instructions.size(), 5);
        assertEquals(instructions.get(Forward.TAG).intValue(), forwardCount);
        assertEquals(instructions.get(TurnRight.TAG).intValue(), rightCount);
        assertEquals(instructions.get(TurnLeft.TAG).intValue(), leftCount);
        assertEquals(instructions.get(Noise.TAG).intValue(), noiseCount);
        assertEquals(instructions.get(Repeat.TAG).intValue(), repeatCount);
    }

    @Test
    public void testBasicCompletion(){
        instructionList.add(new TurnRight());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertTrue(testTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testOutOfBoundsNorth(){
        instructionList.add(new Forward());
        assertFalse(testTask.checkForTaskCompletion(instructionList).taskCompleted());
        assertEquals(testTask.checkForTaskCompletion(instructionList).getInstructionsOnBoard().size(), 0);
    }

    @Test
    public void testOutOfBoundsSouth(){
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        assertFalse(testTask.checkForTaskCompletion(instructionList).taskCompleted());
        assertEquals(testTask.checkForTaskCompletion(instructionList).getInstructionsOnBoard().size(), 5);
    }

    @Test
    public void testOutOfBoundsEast(){
        instructionList.add(new TurnLeft());
        instructionList.add(new Forward());
        assertFalse(testTask.checkForTaskCompletion(instructionList).taskCompleted());
        assertEquals(testTask.checkForTaskCompletion(instructionList).getInstructionsOnBoard().size(), 1);
    }

    @Test
    public void testOutOfBoundsWest(){
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        assertFalse(testTask.checkForTaskCompletion(instructionList).taskCompleted());
        assertEquals(testTask.checkForTaskCompletion(instructionList).getInstructionsOnBoard().size(), 3);
    }

    @Test
    public void testSingleNoiseSquare(){
        Task noiseTask = new Task(new TaskLayout("S,N,E,-E,F,E,-E,E,E,", ""), 2, instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertTrue(noiseTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testMultipleNoiseSquares(){
        Task noiseTask = new Task(new TaskLayout("S,N,N,-E,E,F,-E,E,E,", ""), 2, instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertTrue(noiseTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testMissedNoiseSquare(){
        Task noiseTask = new Task(new TaskLayout("S,N,E,-E,F,E,-E,E,E,", ""), 2, instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertFalse(noiseTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testOneMissedNoiseSquare(){
        Task noiseTask = new Task(new TaskLayout("S,N,N,-E,E,F,-E,E,E,", ""), 2, instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new Forward());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertFalse(noiseTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testNoiseOnSameSquareTwice(){
        Task noiseTask = new Task(new TaskLayout("S,N,E,-E,F,E,-E,E,E,", ""), 2, instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new Noise());
        instructionList.add(new TurnRight());
        instructionList.add(new Forward());
        assertFalse(noiseTask.checkForTaskCompletion(instructionList).taskCompleted());
    }

    @Test
    public void testSuccessfulRepeat(){
        Task repeatTask = new Task(new TaskLayout("E,F,E,E,E,-E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E," +
                "-E,S,E,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        instructionList.add(new Repeat());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        for(Instruction inst: taskEval.getInstructionsOnBoard()){
            assertEquals(Forward.TAG, inst.getTag());
        }
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testUnsuccessfulRepeat(){
        Task repeatTask = new Task(new TaskLayout("E,F,E,E,E,-E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E," +
                "-E,S,E,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        instructionList.add(new TurnRight());
        instructionList.add(new Repeat());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + TurnRight.TAG + ", " + Forward.TAG + ", " +
                TurnRight.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testRepeatWithOneInstruction(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,F,E,E,E,-E,E,E,E,E,-E,E,E,E,E," +
                "-E,S,E,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG + ", " + Forward.TAG + "]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testEmptyRepeat(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E," +
                "-E,S,F,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new TurnRight());
        instructionList.add(new Repeat());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + TurnRight.TAG + ", " + Forward.TAG + "]", onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testRepeatOutOfBounds(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E,-E,E,F,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new TurnLeft());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        instructionList.add(new Repeat());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + TurnLeft.TAG + ", " + Forward.TAG + ", " + Forward.TAG +"]",
                onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testSuccessfulNoiseInRepeat(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,F,E,E,-E,E,N,E,E,-E,E,N,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        instructionList.add(new Noise());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Noise.TAG + ", " + Forward.TAG + ", " + Noise.TAG +
                ", " + Forward.TAG +"]", onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testUnsuccessfulNoiseInRepeat(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,F,E,E,-E,E,N,E,E,-E,E,N,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Noise());
        instructionList.add(new Forward());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Noise.TAG + ", " + Forward.TAG + ", " + Noise.TAG + ", " + Forward.TAG +
                ", " + Forward.TAG +"]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testUnsuccessfulNoiseInRepeat2(){
        Task repeatTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,F,E,E,-E,E,N,E,E,-E,E,N,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Repeat());
        instructionList.add(new Noise());
        instructionList.add(new Repeat());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = repeatTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Noise.TAG + ", " + Noise.TAG + ", " + Forward.TAG +"]",
                onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testAttemptToEnterBlockedSquare(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,F,E,E,-E,E,X,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testBasicIfBlockedUse(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new If());
        instructionList.add(new TurnRight());
        instructionList.add(new If());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + TurnRight.TAG + ", " + Forward.TAG +"]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testBasicNotBlockedIf(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,F,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new If());
        instructionList.add(new TurnRight());
        instructionList.add(new If());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG +"]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testEmptyIfBodyBlocked(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new If());
        instructionList.add(new If());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + "]",
                onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testEmptyIfBodyNotBlocked(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,F,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new If());
        instructionList.add(new If());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG +"]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testForwardInstructionInIfBody(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,F,E,E,-E,E,X,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new If());
        instructionList.add(new Forward());
        instructionList.add(new If());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    @Test
    public void testBasicIfElseUsingIfBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + TurnRight.TAG + ", " + Forward.TAG + "]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testBasicIfElseUsingElseBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,F,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG + "]", onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testBasicIfElseUsingIfBranchWithEmptyElseBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + TurnRight.TAG + ", " + Forward.TAG + "]",
                onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }

    @Test
    public void testBasicIfElseUsingElseBranchWithEmptyIfBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new TurnLeft());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + TurnRight.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    // Test else if using if branch with an empty if branch
    @Test
    public void testBasicIfElseUsingIfBranchWithEmptyIfBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    // Test else if  using else branch with an empty else branch
    @Test
    public void testBasicIfElseUsingElseBranchWithEmptyElseBranch(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,E,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new TurnRight());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    // Test else if using if branch with both branches empty
    @Test
    public void testBasicIfElseUsingIfBranchWithBothBranchesEmpty(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,X,E,E,-E,E,E,F,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + "]", onBoard.toString());
        assertFalse(taskEval.taskCompleted());
    }

    // Test else if using else branch with both branches empty
    @Test
    public void testBasicIfElseUsingElseBranchWithBothBranchesEmpty(){
        Task ifTask = new Task(new TaskLayout("E,E,E,E,E,-E,E,E,E,E,-E,E,F,E,E,-E,E,E,E,E," +
                "-E,E,S,E,E", ""), 2,
                instructionInventory);
        instructionList.add(new Forward());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new IfElse());
        instructionList.add(new Forward());
        TaskEvaluation taskEval = ifTask.checkForTaskCompletion(instructionList);
        List<Instruction> onBoard = taskEval.getInstructionsOnBoard();
        assertEquals("[" + Forward.TAG + ", " + Forward.TAG + "]", onBoard.toString());
        assertTrue(taskEval.taskCompleted());
    }
}

