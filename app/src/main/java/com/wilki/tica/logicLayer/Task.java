package com.wilki.tica.logicLayer;


import android.content.Context;

import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.exceptions.OutOfBoundsException;
import com.wilki.tica.exceptions.RobotOutOfLayoutException;
import com.wilki.tica.exceptions.InvalidNoiseInstructionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

/**
 * Created by John Wilkie on 29/11/2016.
 * Represents a task that can be attempted. Holds a task layout for this task as well as recording
 * the quantity of each instruction that may be used for this task.
 */

public class Task implements Serializable {

    private int taskNo;
    private final HashMap<String, Integer> instructionQty;
    private final TaskLayout taskLayout;
    private Facing currentDir;
    private List<Pos> noiseList;
    private Pos finish;
    private Pos start;
    private Pos currentTablePos;
    private List<Instruction> instructionsToSend;


    /**
     * Constructor for a new Task object.
     * @param taskLayout the TaskLayout of the Task.
     * @param instruction_qty a HashMap of the instructions and the quantity of each.
     */
    public Task(TaskLayout taskLayout, HashMap<String, Integer> instruction_qty){
        this.taskLayout = taskLayout;
        this.instructionQty = instruction_qty;
        setTilePositions();
    }

    /**
     * Constructor used to re-construct a Task object from a database.
     * @param taskLayout the TaskLayout for this task.
     * @param taskNo a unique id for the task.
     * @param instructionQty a string representation of the allowed instructions and the quantity
     *                       of each.
     */
    public Task(TaskLayout taskLayout, int taskNo, String instructionQty){
        this.taskLayout = taskLayout;
        this.taskNo = taskNo;
        this.instructionQty = convertPermissibleInstructionsStringToMap(instructionQty);
        setTilePositions();
    }

    /**
     * Returns a list of all tasks currently in the database.
     * @param cont application context.
     * @return a list of all tasks int he database
     */
    public static List<Task> getAllTasks(Context cont){
        DbHelper dbHelper = new DbHelper(cont);
        List<Task> taskList = dbHelper.readAllFromTaskTable();
        dbHelper.close();
        return taskList;
    }

    /**
     * Saves the current task to the database.
     * @param cont application context.
     * @param taskToSave task to be saved.
     */
    public static void saveTask(Context cont, Task taskToSave){
        DbHelper dbHelper = new DbHelper(cont);
        dbHelper.addEntryToTaskTable(taskToSave);
        dbHelper.close();
    }

    /*
     * Finds the position of the start and finish positions and stores them to respective variables.
     */
    private void setTilePositions(){
        taskLayout.findLayoutPositions();
        finish = taskLayout.getFinishPosition();
        start = taskLayout.getStartPosition();
    }

    /**
     * @return the unique number of this task,
     */
    public int getTaskNumber(){
        return taskNo;
    }

    /**
     * Using a list of instructions computes if the current task is completed or not.
     * @param attempt a list of instructions that attempt to complete this task.
     * @return true if the provided instructions completes this task.
     */
    public TaskEvaluation checkForTaskCompletion(List<Instruction> attempt){
        boolean correctAttempt = true;
        prepareForNewAttempt();
        int i = 0;
        while(i < attempt.size()){
            Instruction currentMove = attempt.get(i);
            if(currentMove.isABasicInstruction()) { // instructions without body
                try {
                    processBasicInstruction(currentMove);
                } catch (RobotOutOfLayoutException e) {
                    return new TaskEvaluation(false, instructionsToSend);
                } catch (InvalidNoiseInstructionException e) {
                    correctAttempt = false;
                }
                i++;
                instructionsToSend.add(currentMove);
            } else { // instructions with bodies
                try {
                    ComplexInstructionResponse response = processComplexInstruction(attempt, i);
                    i = response.getIteratorValue();
                    if(correctAttempt){
                        correctAttempt = !(response.isInvalidNoise());
                    }
                } catch (RobotOutOfLayoutException e) {
                    return new TaskEvaluation(false, instructionsToSend);
                }
            }
        }
        System.out.println("correct" + correctAttempt);
        if(noiseList.size() != 0){
            correctAttempt = false; // not all noise tiles dealt with
        } else if( currentTablePos.getY() != finish.getY() ||
                currentTablePos.getX() != finish.getX()){
            correctAttempt = false; // not on finish tile at end
        }
        return new TaskEvaluation(correctAttempt, instructionsToSend);
    }

    /*
     * Resets variables before an attempt.
     */
    private void prepareForNewAttempt(){
        noiseList = taskLayout.getCopyOfNoisePositions();
        currentDir = Facing.NORTH;
        currentTablePos = new Pos(start.getY(), start.getX());
        instructionsToSend = new ArrayList<>();
    }

    /*
     * Determines the type of the instruction and calls appropriate method.
     */
    private void processBasicInstruction(Instruction currentInstruction) throws RobotOutOfLayoutException,
            InvalidNoiseInstructionException {
        if(currentInstruction instanceof Forward){
            currentTablePos = moveForward(currentTablePos, currentDir);
        } else if(currentInstruction instanceof TurnLeft){
            currentDir = turnLeft(currentDir);
        } else if(currentInstruction instanceof TurnRight){
            currentDir = turnRight(currentDir);
        } else if(currentInstruction instanceof Noise){
            validMakeNoise(currentTablePos, noiseList);
        }
    }

    /*
     * Determines the type of complex instruction and calls appropriate method.
     */
    private ComplexInstructionResponse processComplexInstruction(List<Instruction> attempt, int i)
            throws RobotOutOfLayoutException {
        Instruction currentInstruction = attempt.get(i);
        ComplexInstructionResponse response = null;
        if(currentInstruction instanceof If){
            response = precessIf(attempt, i);
        } else if(currentInstruction instanceof IfElse){
            response = processIfElse(attempt, i);
        } else if(currentInstruction instanceof Repeat){
            response = processRepeat(attempt, i);
        }
        return response;
    }

    /*
     * Processes and if else instruction.
     */
    private ComplexInstructionResponse processIfElse(List<Instruction> attempt, int i) {
        boolean invalidNoise = false;
        boolean ifBlockEmpty = emptyIfElseBody(attempt, i);
        boolean elseBlockEmpty;
        int elseBlockInstructionIndex;
        if(ifBlockEmpty){
            elseBlockEmpty = emptyIfElseBody(attempt, i+1);
            elseBlockInstructionIndex = i+2;
        } else {
            elseBlockEmpty = emptyIfElseBody(attempt, i+2);
            elseBlockInstructionIndex = i+3;
        }
        int newI = i;
        if(forwardBlocked()){   // calculate if branch
            if(!(ifBlockEmpty)){ // check if body not empty
                Instruction nextInstruction = attempt.get(i + 1);
                invalidNoise = executeBody(nextInstruction);
            }
        } else { // calculate else branch
            if(!(elseBlockEmpty)){ // check else body not empty
                Instruction elseInstruction = attempt.get(elseBlockInstructionIndex);
                invalidNoise = executeBody(elseInstruction);
            }
        }
        newI += 3;  // ensures that instructions in the body aren't processed twice.
        if(!ifBlockEmpty){
            newI++;
        }
        if(!elseBlockEmpty){
            newI++;
        }
        return new ComplexInstructionResponse(newI, invalidNoise);
    }

    /*
     * Executes the body of a if and if else instructions.
     */
    private boolean executeBody(Instruction instructionToExecute) {
        try {
            processBasicInstruction(instructionToExecute);
            instructionsToSend.add(instructionToExecute);
        } catch (RobotOutOfLayoutException e1) {
            // tile in front is out of bounds so isn't blocked so body is ignored.
        } catch (InvalidNoiseInstructionException e2) {
            return true;
        }
        return false;
    }

    /*
     * Identifies empty body of if else instruction.
     */
    private boolean emptyIfElseBody(List<Instruction> attempt, int blockStartIndex) {
        return attempt.get(blockStartIndex + 1) instanceof IfElse;
    }

    /*
     * Checks if the square in front of the robot in its current facing direction in a blocked tile.
     */
    private boolean forwardBlocked() {
        try {
            moveForward(currentTablePos, currentDir);
        } catch (RobotOutOfLayoutException e) {
            if(e.getMessage().equals("Robot tried to move onto a blocked square")){
                return true;
            }
        }
        return false;
    }

    /*
     * Processes the if instruction.
     */
    private ComplexInstructionResponse precessIf(List<Instruction> attempt, int i) {
        Instruction nextInstruction = attempt.get(i + 1);
        boolean invalidNoise = false;
        int newI = i;
        if(!(nextInstruction instanceof If)) { // checks body isn't empty.
            if(forwardBlocked()) {
                try {
                    processBasicInstruction(nextInstruction);
                    instructionsToSend.add(nextInstruction);
                } catch (RobotOutOfLayoutException e1) {
                    // tile in front is out of bounds so isn't blocked so body is ignored.
                } catch (InvalidNoiseInstructionException e2) {
                    invalidNoise = true;
                }
            }
            newI++;
        }
        newI += 2;
        return new ComplexInstructionResponse(newI, invalidNoise);
    }

    /*
     * Processes the repeat instruction.
     */
    private ComplexInstructionResponse processRepeat(List<Instruction> attempt, int i) throws
            RobotOutOfLayoutException {
        boolean invalidNoise = false;
        if (attempt.size() > (i + 2) && attempt.get(i + 1).isABasicInstruction() &&
                attempt.get(i + 2).isABasicInstruction()) { // 2 instructions in the body
            Instruction instructionOne = attempt.get(i+1);
            Instruction instructionTwo = attempt.get(i+2);
            for(int j = 0; j < 4; j++){
                Instruction tempInstruction;
                if (j % 2 == 0) {
                    tempInstruction = instructionOne;
                } else {
                    tempInstruction = instructionTwo;
                }
                try {
                    processBasicInstruction(tempInstruction);
                } catch (InvalidNoiseInstructionException e){
                    invalidNoise = true;
                }
                instructionsToSend.add(tempInstruction);
            }
            i += 4;
        } else if (attempt.get(i + 1).isABasicInstruction()) { // a single instruction in the body
            try {
                processBasicInstruction(attempt.get(i + 1));
                processBasicInstruction(attempt.get(i + 1));
            } catch (InvalidNoiseInstructionException e){
                invalidNoise = true;
            }
            instructionsToSend.add(attempt.get(i + 1));
            instructionsToSend.add(attempt.get(i + 1));
            i += 3;
        } else { // empty body, so ignored
            i += 2;
        }
        return new ComplexInstructionResponse(i, invalidNoise);
    }

    /*
     * Given a current position and a direction the robot is facing the position of moving one tile
     * forward in the direction the robot is facing is returned.
     */
    private Pos moveForward(Pos oldTablePos, Facing currentDir) throws RobotOutOfLayoutException {
        Pos newTablePos;
        if(currentDir == Facing.NORTH){
            newTablePos = new Pos(oldTablePos.getY()-1, oldTablePos.getX());
        } else if(currentDir == Facing.EAST){
            newTablePos = new Pos(oldTablePos.getY(), oldTablePos.getX()+1);
        } else if(currentDir == Facing.SOUTH){
            newTablePos = new Pos(oldTablePos.getY()+1, oldTablePos.getX());
        } else {
            newTablePos = new Pos(oldTablePos.getY(), oldTablePos.getX()-1);
        }

        if(newTablePos.getY() < 0 || newTablePos.getX() < 0 ||
                newTablePos.getY() >= taskLayout.getBoardSize()
                || newTablePos.getX() >= taskLayout.getBoardSize()){
            throw new RobotOutOfLayoutException("Robot exceeded layout");
        }

        try {
            if(taskLayout.getSquare(newTablePos) == SquareTypes.BLOCKED){
                throw new RobotOutOfLayoutException("Robot tried to move onto a blocked square");
            }
        } catch (OutOfBoundsException e) {
            throw new RobotOutOfLayoutException("Robot exceeded layout");
        }

        return newTablePos;
    }

    /*
     * Given a facing direction will return the direction 90 degrees anti-clockwise.
     */
    private Facing turnLeft(Facing currentDir){
        Facing newDir = currentDir;
        if(currentDir == Facing.NORTH){
            newDir = Facing.WEST;
        } else if(currentDir == Facing.EAST){
            newDir = Facing.NORTH;
        } else if(currentDir == Facing.SOUTH){
            newDir = Facing.EAST;
        } else if(currentDir == Facing.WEST){
            newDir = Facing.SOUTH;
        }
        return newDir;
    }

    /*
     * Given a facing direction will return the direction 90 degrees clockwise.
     */
    private Facing turnRight(Facing currentDir){
        Facing newDir = currentDir;
        if(currentDir == Facing.NORTH){
            newDir= Facing.EAST;
        } else if(currentDir == Facing.EAST){
            newDir = Facing.SOUTH;
        } else if(currentDir == Facing.SOUTH){
            newDir = Facing.WEST;
        } else if(currentDir == Facing.WEST){
            newDir = Facing.NORTH;
        }
        return newDir;
    }

    /*
     * Checks if the position given as an argument is in the list of noise positions. Returns true
      * if it is and removes it from the list else returns false;
     */
    private void validMakeNoise(Pos currentTablePos, List<Pos> noiseList) throws
            InvalidNoiseInstructionException {
        boolean validPos = false;
        for(Pos tablePos : noiseList){
            if(tablePos.getY() == currentTablePos.getY() &&
                    tablePos.getX() == currentTablePos.getX()){
                noiseList.remove(tablePos);
                validPos = true;
                break;
            }
        }
        if(!validPos){
            throw new InvalidNoiseInstructionException();
        }
    }

    /**
     * @return the permissible instructions and quantity of each in a string representation.
     */
    public String getInstructionsAsString(){
        String validInstructions = "{";
        for(String inst: instructionQty.keySet()){
            validInstructions +="(" + inst + " " + instructionQty.get(inst) + "),";
        }
        validInstructions = validInstructions.substring(0, validInstructions.length()-1);
        validInstructions += "}";
        return validInstructions;
    }

    /**
     * @return a HashMap of each instruction and the quantity allocated for this task.
     */
    public HashMap<String, Integer> getInstructions(){
        return instructionQty;
    }

    /*
     * Converts the input string representation of permissible instructions into a HashMap
     * representation.
     */
    private HashMap<String, Integer> convertPermissibleInstructionsStringToMap(String instructions){
        HashMap<String, Integer> instructionQty = new HashMap<>();
        Pattern pattern = Pattern.compile("\\),\\(");
        String[] matches = pattern.split(instructions.substring(2, instructions.length()-2));
        for(String match: matches){
            if(match.contains(Forward.TAG)){
                instructionQty.put(Forward.TAG, Integer.parseInt(match.substring(8)));
            }else if(match.contains(TurnLeft.TAG)){
                instructionQty.put(TurnLeft.TAG, Integer.parseInt(match.substring(10)));
            }else if(match.contains(TurnRight.TAG)){
                instructionQty.put(TurnRight.TAG, Integer.parseInt(match.substring(11)));
            }else if(match.contains(Noise.TAG)){
                instructionQty.put(Noise.TAG, Integer.parseInt(match.substring(6)));
            }else if(match.contains(Repeat.TAG)){
                instructionQty.put(Repeat.TAG, Integer.parseInt(match.substring(7)));
            }else if(match.contains(IfElse.TAG)){
                instructionQty.put(IfElse.TAG, Integer.parseInt(match.substring(7)));
            }else if(match.contains(If.TAG)){
                instructionQty.put(If.TAG, Integer.parseInt(match.substring(3)));
            }
        }
        return instructionQty;
    }

    /**
     * @return the TaskLayout for this task.
     */
    public TaskLayout getTaskLayout(){ return taskLayout; }

    /*
     * wrapper class for returning from processing instructions with bodies.
     */
    private class ComplexInstructionResponse {
        private final int iteratorValue; // new iterator value for iteration over all instructions
        private final boolean invalidNoise;

        /*
         * constructor
         */
        ComplexInstructionResponse(int iteratorValue, boolean invalidNoise){
            this.iteratorValue = iteratorValue;
            this.invalidNoise = invalidNoise;
        }

        // Returns the new value to set the iterator to.
        int getIteratorValue() {
            return iteratorValue;
        }

        // Returns if processing the instruction violated and noise squares.
        boolean isInvalidNoise() {
            return invalidNoise;
        }
    }
}
