package com.wilki.tica.logicLayer;

import com.wilki.tica.exceptions.OutOfBoundsException;
import com.wilki.tica.exceptions.TaskLayoutException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by John Wilkie on 29/01/2017.
 * The Task Layout class represents the tiles that make up a task. It stores information on the
 * position of the tiles as well as the file path to an image representation of the task.
 */

public class TaskLayout implements Serializable {
    private final SquareTypes[][] layout;
    private final int BOARD_SIZE;
    private Pos start;
    private Pos finish;
    private List<Pos> noisePositions;
    private String layoutImageFilePath;

    /**
     * Constructor for creating a new TaskLayout.
     * @param board_size the width of the task layout to be created.
     */
    public TaskLayout(int board_size){
        layout = new SquareTypes[board_size][board_size];
        this.BOARD_SIZE = board_size;
        initializeLayoutEmpty();
    }

    /**
     * Constructor for creating a TaskLayout when a file path to an image representation is known.
     * @param layoutAsString the layout represented as a string.
     * @param layoutImageFilePath file path to an image of the layout.
     */
    public TaskLayout(String layoutAsString, String layoutImageFilePath){
        this.layout = convertStringToLayout(layoutAsString);
        this.layoutImageFilePath = layoutImageFilePath;
        BOARD_SIZE = layout[0].length;
    }

    /*
    * Set the layout to all be empty squares.
     */
    private void initializeLayoutEmpty(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                layout[i][j] = SquareTypes.EMPTY;
            }
        }
    }

    /**
     * @param layoutImageFilePath a path to the location of an image that represents this layout.
     */
    public void setImagePath(String layoutImageFilePath){
        this.layoutImageFilePath = layoutImageFilePath;
    }

    /**
     * @return the file path to an image that represents this layout.
     */
    public String getLayoutImageFilePath(){ return layoutImageFilePath; }

    /**
     * Returns the square as the given position of this layout.
     * @param squarePosition the position of the square that you want on the layout.
     * @return the square at the provided position.
     */
    public SquareTypes getSquare(Pos squarePosition) throws OutOfBoundsException {
        int xPos = squarePosition.getX();
        int yPos = squarePosition.getY();
        if(xPos >= 0 && xPos < BOARD_SIZE && yPos >= 0 && yPos < BOARD_SIZE) {
            return layout[squarePosition.getY()][squarePosition.getX()];
        } else {
            throw new OutOfBoundsException();
        }

    }

    /**
     * Sets the square type provided at the location specified by the position provided.
     * @param squareToSet the type of square to set.
     * @param positionToSet the position to set the square.
     */
    public void setSquare(SquareTypes squareToSet, Pos positionToSet){
        int xPos = positionToSet.getX();
        int yPos = positionToSet.getY();
        if(xPos >= 0 && xPos < BOARD_SIZE && yPos >= 0 && yPos < BOARD_SIZE){
            layout[yPos][xPos] = squareToSet;
        }
    }

    /**
     * Checks if the current task layout of the class is a valid task layout. To be valid it must
     * have one start and finish tile.
     * @throws TaskLayoutException
     */
    public void checkTaskLayoutIsValid() throws TaskLayoutException {
        int numberOfStarts = 0;
        int numberOfFinishes = 0;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(layout[i][j] == SquareTypes.START){
                    numberOfStarts++;
                } else if(layout[i][j] == SquareTypes.FINISH){
                    numberOfFinishes++;
                }
            }
        }
        if(numberOfStarts < 1){
            throw new TaskLayoutException("your task requires one start square.");
        } else if(numberOfStarts > 1){
            throw new TaskLayoutException("your task has too many start squares.");
        } else if(numberOfFinishes < 1){
            throw new TaskLayoutException("your task requires a finish square.");
        } else if(numberOfFinishes > 1){
            throw new TaskLayoutException("your task has too many finish squares, it should have one.");
        }
    }

    /**
     * @return the task layout represented as a string.
     */
    @Override
    public String toString(){
        String task_as_string = "";
        for(SquareTypes[] row: layout){
            for(SquareTypes cell: row){
                task_as_string += cell + ",";
            }
            task_as_string +="-";
        }
        return task_as_string.substring(0, task_as_string.length()-1);
    }

    /*
     * Takes a string that represents a layout and converts it to a two dimensional array of
     * SquareTypes.
     */
    private SquareTypes[][] convertStringToLayout(String layoutAsString) {
        String[] taskRows = layoutAsString.split("-");
        SquareTypes[][] tempLayout = new SquareTypes[taskRows.length][taskRows.length];
        for (int i = 0; i < taskRows.length; i++) {
            String[] taskRow = taskRows[i].split(",");
            SquareTypes[] typesRow = convertToSquareTypes(taskRow);
            tempLayout[i] = typesRow;
        }
        return tempLayout;
    }

    /*
     * Takes a row of a layout in string form and converts it to an array of SquareType
     * corresponding to the string given as an argument.
     */
    private SquareTypes[] convertToSquareTypes(String[] rowAsStringArray){
        SquareTypes[] taskRow = new SquareTypes[rowAsStringArray.length];
        for(int i = 0; i < rowAsStringArray.length; i++){
            taskRow[i] = convertStringToSquareType(rowAsStringArray[i]);
        }
        return taskRow;
    }

    /*
     * Matches a string to a SquareType.
     */
    private SquareTypes convertStringToSquareType(String squareTypeAsString){
        if(squareTypeAsString.equals(SquareTypes.EMPTY.toString())) {
            return SquareTypes.EMPTY;
        } else if(squareTypeAsString.equals(SquareTypes.NOISE.toString())) {
            return SquareTypes.NOISE;
        } else if(squareTypeAsString.equals(SquareTypes.START.toString())) {
            return SquareTypes.START;
        } else if (squareTypeAsString.equals(SquareTypes.FINISH.toString())){
            return SquareTypes.FINISH;
        } else if (squareTypeAsString.equals(SquareTypes.BLOCKED.toString())){
            return SquareTypes.BLOCKED;
        }
        return null;
    }

    /**
     * Identifies the start, finish and noise tiles in this layout.
     */
    public void findLayoutPositions(){
        noisePositions = new ArrayList<>();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(layout[i][j] == SquareTypes.START){
                    start = new Pos(i, j);
                } else if (layout[i][j] == SquareTypes.FINISH){
                    finish = new Pos(i, j);
                } else if (layout[i][j] == SquareTypes.NOISE){
                    noisePositions.add(new Pos(i, j));
                }
            }
        }
    }

    /**
     * @return the position of the start tile in this layout.
     */
    public Pos getStartPosition(){ return start; }

    /**
     * @return the position of the finish tile in this layout.
     */
    public Pos getFinishPosition(){ return finish; }

    /**
     * @return a list of positions of all the noise tiles in this layout.
     */
    public List<Pos> getCopyOfNoisePositions(){
        List<Pos> copyNoiseList = new ArrayList<>(noisePositions);
        return  copyNoiseList;
    }

    /**
     * @return the width of the board.
     */
    public int getBoardSize(){ return BOARD_SIZE; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskLayout that = (TaskLayout) o;
        return this.toString().equals(that.toString());
    }

}
