package com.wilki.tica;

import com.wilki.tica.logicLayer.Pos;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by John Wilkie on 30/11/2016.
 * Unit tests for the Pos class
 */

public class PosUnitTest {

    @Test
    public void testCreation(){
        int xVal = 6;
        int yVal = 12;
        testXandY(xVal, yVal);
    }

    @Test
    public void testMaxValues(){
        int xVal = Integer.MAX_VALUE;
        int yVal = Integer.MAX_VALUE;
        testXandY(xVal, yVal);
    }

    @Test
    public void testMaxValuesPlusOne(){
        int xVal = Integer.MAX_VALUE+1;
        int yVal = Integer.MAX_VALUE+1;
        testXandY(xVal, yVal);
    }

    @Test
    public void testMinValues(){
        int xVal = Integer.MIN_VALUE;
        int yVal = Integer.MIN_VALUE;
        testXandY(xVal, yVal);
    }

    private void testXandY(int xVal, int yVal){
        Pos testPos = new Pos(yVal, xVal);
        assertEquals(xVal, testPos.getX());
        assertEquals(yVal, testPos.getY());
    }

}
