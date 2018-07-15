package com.wilki.tica;

import com.wilki.tica.logicLayer.Pos;
import com.wilki.tica.logicLayer.SquareTypes;
import com.wilki.tica.logicLayer.TaskLayout;
import com.wilki.tica.exceptions.OutOfBoundsException;
import com.wilki.tica.exceptions.TaskLayoutException;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by John Wilkie on 29/01/2017.
 * Unit testing for TaskLayout class.
 */

public class TaskLayoutUnitTest {

    private final int BOARD_SIZE = 5;
    private TaskLayout testLayout;

    @Before
    public void setUp(){
        testLayout = new TaskLayout(BOARD_SIZE);
    }


    @Test
    public void testCreation(){
        assertEquals(testLayout.getBoardSize(), BOARD_SIZE);
    }

    @Test
    public void testBoardInitializedToEmpty(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.EMPTY);
                } catch (OutOfBoundsException e) {
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testSettingStartTile(){
        Pos startSquarePos = new Pos(0, 0);
        testLayout.setSquare(SquareTypes.START, startSquarePos);
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    if (i == startSquarePos.getY() && j == startSquarePos.getX()) {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.START);
                    } else {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.EMPTY);
                    }
                } catch (OutOfBoundsException e) {
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testSettingFinishTile(){
        Pos startSquarePos = new Pos(0, 0);
        testLayout.setSquare(SquareTypes.FINISH, startSquarePos);
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    if (i == startSquarePos.getY() && j == startSquarePos.getX()) {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.FINISH);
                    } else {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.EMPTY);
                    }
                }catch (OutOfBoundsException e){
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testSettingNoiseTile(){
        Pos startSquarePos = new Pos(0, 0);
        testLayout.setSquare(SquareTypes.NOISE, startSquarePos);
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    if (i == startSquarePos.getY() && j == startSquarePos.getX()) {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.NOISE);
                    } else {
                        assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.EMPTY);
                    }
                }catch(OutOfBoundsException e){
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testSettingTileOutOfLayout(){
        Pos posOverY = new Pos(BOARD_SIZE, 0);
        Pos posUnderY = new Pos(-1, 0);
        Pos posOverX = new Pos(0, BOARD_SIZE);
        Pos posUnderX = new Pos(0, -1);
        testLayout.setSquare(SquareTypes.START, posOverY);
        testLayout.setSquare(SquareTypes.START, posUnderY);
        testLayout.setSquare(SquareTypes.START, posOverX);
        testLayout.setSquare(SquareTypes.START, posUnderX);
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    assertEquals(testLayout.getSquare(new Pos(i, j)), SquareTypes.EMPTY);
                } catch (OutOfBoundsException e) {
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testGettingTileOutOfLayoutOverY(){
        Pos posOverY = new Pos(BOARD_SIZE, 0);
        try {
            testLayout.getSquare(posOverY);
        }catch(OutOfBoundsException e){
            assertTrue(true);
        }
    }

    @Test
    public void testGettingTileOutOfLayoutYUnder(){
        Pos posUnderY = new Pos(-1, 0);
        try {
            testLayout.getSquare(posUnderY);
        }catch(OutOfBoundsException e){
            assertTrue(true);
        }
    }

    @Test
    public void testGettingTileOutOfLayoutOverX(){
        Pos posOverX = new Pos(0, BOARD_SIZE);
        try {
            testLayout.getSquare(posOverX);
        }catch(OutOfBoundsException e){
            assertTrue(true);
        }
    }

    @Test
    public void testGettingTileOutOfLayoutUnderX(){
        Pos posUnderX = new Pos(0, -1);
        try {
            testLayout.getSquare(posUnderX);
        }catch(OutOfBoundsException e){
            assertTrue(true);
        }
    }

    @Test
    public void testSetAndGetImagePath(){
        String somePath = "somePath\\maybe";
        testLayout.setImagePath(somePath);
        assertEquals(testLayout.getLayoutImageFilePath(), somePath);
    }

    @Test
    public void testCheckValidLayoutWithoutAStartTile(){
        try{
            testLayout.checkTaskLayoutIsValid();
        } catch (TaskLayoutException e) {
            String noStartMsg = "your task requires one start square.";
            assertEquals(e.getMessage(), noStartMsg);
        }
    }

    @Test
    public void testCheckValidLayoutWithoutAFinishTile(){
        testLayout.setSquare(SquareTypes.START, new Pos(0,0));
        try{
            testLayout.checkTaskLayoutIsValid();
        } catch (TaskLayoutException e) {
            String noFinishMsg = "your task requires a finish square.";
            assertEquals(e.getMessage(), noFinishMsg);
        }
    }

    @Test
    public void testCheckValidLayoutWithTwoStartTiles(){
        testLayout.setSquare(SquareTypes.START, new Pos(0,0));
        testLayout.setSquare(SquareTypes.START, new Pos(0,1));
        try{
            testLayout.checkTaskLayoutIsValid();
        } catch (TaskLayoutException e) {
            String multipleStartMsg = "your task has too many start squares.";
            assertEquals(e.getMessage(), multipleStartMsg);
        }
    }

    @Test
    public void testCheckValidLayoutWithTwoFinishTiles(){
        testLayout.setSquare(SquareTypes.START, new Pos(0,0));
        testLayout.setSquare(SquareTypes.FINISH, new Pos(0,1));
        testLayout.setSquare(SquareTypes.FINISH, new Pos(1,0));
        try{
            testLayout.checkTaskLayoutIsValid();
        } catch (TaskLayoutException e) {
            String multipleFinishMsg = "your task has too many finish squares, it should " +
                    "have one.";
            assertEquals(e.getMessage(), multipleFinishMsg);
        }
    }

    @Test
    public void testCheckValidLayoutOnValidLayout(){
        testLayout.setSquare(SquareTypes.START, new Pos(0,0));
        testLayout.setSquare(SquareTypes.FINISH, new Pos(0,1));
        testLayout.setSquare(SquareTypes.NOISE, new Pos(1,0));
        boolean noException = true;
        try{
            testLayout.checkTaskLayoutIsValid();
        } catch (TaskLayoutException e) {
            noException = false;
        }
        assertTrue(noException);
    }

    @Test
    public void testConvertTaskLayoutToString(){
        testLayout.setSquare(SquareTypes.START, new Pos(0,0));
        testLayout.setSquare(SquareTypes.FINISH, new Pos(0,1));
        testLayout.setSquare(SquareTypes.NOISE, new Pos(1,0));
        String layoutAsString = testLayout.toString();
        for(int i = 0; i < layoutAsString.length(); i++){
            if(i == 0){
                assertEquals(layoutAsString.charAt(i), SquareTypes.START.toString().charAt(0));
            } else if(i == 2){
                assertEquals(layoutAsString.charAt(i), SquareTypes.FINISH.toString().charAt(0));
            } else if(i == 11){
                assertEquals(layoutAsString.charAt(i), SquareTypes.NOISE.toString().charAt(0));
            } else{
                if(layoutAsString.charAt(i) == SquareTypes.EMPTY.toString().charAt(0) ||
                        layoutAsString.charAt(i) == ',' ||
                        layoutAsString.charAt(i) == '-'){
                    assertTrue(true);
                } else {
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testConstructLayoutFromString(){
        TaskLayout layoutConstructedFromString = new TaskLayout(
                ""+SquareTypes.EMPTY+","+SquareTypes.FINISH+","+SquareTypes.EMPTY+","+
                        SquareTypes.EMPTY+","+SquareTypes.EMPTY+",-"+SquareTypes.NOISE+","+
                        SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+
                        SquareTypes.EMPTY+",-"+SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+
                        SquareTypes.START+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+",-"+
                        SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+
                        SquareTypes.EMPTY+","+SquareTypes.EMPTY+",-"+SquareTypes.EMPTY+","+
                        SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+SquareTypes.EMPTY+","+
                        SquareTypes.EMPTY+",", "");
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                try {
                    if (i == 0 && j == 1) {
                        assertEquals(layoutConstructedFromString.getSquare(new Pos(i, j)),
                                SquareTypes.FINISH);
                    } else if (i == 1 && j == 0) {
                        assertEquals(layoutConstructedFromString.getSquare(new Pos(i, j)),
                                SquareTypes.NOISE);
                    } else if (i == 2 && j == 2) {
                        assertEquals(layoutConstructedFromString.getSquare(new Pos(i, j)),
                                SquareTypes.START);
                    } else {
                        assertEquals(layoutConstructedFromString.getSquare(new Pos(i, j)),
                                SquareTypes.EMPTY);
                    }
                } catch(OutOfBoundsException e){
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testGetStartPosition(){
        Pos startPos = new Pos(2,2);
        testLayout.setSquare(SquareTypes.START, startPos);
        testLayout.findLayoutPositions();
        Pos foundStartPos = testLayout.getStartPosition();
        assertEquals(foundStartPos.getX(), startPos.getX());
        assertEquals(foundStartPos.getY(), startPos.getY());
    }

    @Test
    public void testGetFinishPosition(){
        Pos finishPos = new Pos(3,2);
        testLayout.setSquare(SquareTypes.FINISH, finishPos);
        testLayout.findLayoutPositions();
        Pos foundFinishPos = testLayout.getFinishPosition();
        assertEquals(foundFinishPos.getX(), finishPos.getX());
        assertEquals(foundFinishPos.getY(), finishPos.getY());
    }

    @Test
    public void testGetNoisePositions(){
        Pos noisePos1 = new Pos(2,2);
        Pos noisePos2 = new Pos(1,2);
        Pos noisePos3 = new Pos(4,0);
        testLayout.setSquare(SquareTypes.NOISE, noisePos1);
        testLayout.setSquare(SquareTypes.NOISE, noisePos2);
        testLayout.setSquare(SquareTypes.NOISE, noisePos3);
        testLayout.findLayoutPositions();
        List<Pos> foundNoisePos = testLayout.getCopyOfNoisePositions();

        assertTrue(positionInList(noisePos1, foundNoisePos));
        assertTrue(positionInList(noisePos2, foundNoisePos));
        assertTrue(positionInList(noisePos3, foundNoisePos));
        assertEquals(foundNoisePos.size(), 3);
    }

    private boolean positionInList(Pos pos, List<Pos> list){
        boolean match = false;
        int i = 0;
        while(!match && i < list.size()){
            Pos toMatch = list.get(i);
            if(toMatch.getX() == pos.getX() && toMatch.getY() == pos.getY()){
                match = true;
            }
            i++;
        }
        return match;
    }
}
