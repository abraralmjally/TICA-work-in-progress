package com.wilki.tica;

import com.wilki.tica.exceptions.NoLocationException;
import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.If;
import com.wilki.tica.instructions.IfElse;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.Repeat;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;
import com.wilki.tica.logicLayer.SelectedInstructionData;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by John Wilkie on 2/02/2017.
 * Unit tests for the SelectedInstructionData class.
 */

public class SelectedInstructionDataUnitTest {

    private SelectedInstructionData selData;
    private int screenHeight = 600;

    @Before
    public void setUp(){
        selData = new SelectedInstructionData();
    }

    @Test
    public void testCreateSelectedInstructionData(){
        for(int i = 1; i < 11; i++){
            assertTrue(selData.getInstructionSlot(i).isEmpty());
            assertFalse(selData.getInstructionSlot(i).isInAnInstructionBody());
        }
    }

    @Test
    public void testFindSlotForNormalInstruction(){
        try {
            int[] slots =selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            assertEquals(2, slots[0]);
            assertFalse(selData.getInstructionSlot(2).isEmpty());
            assertFalse(selData.getInstructionSlot(2).isInAnInstructionBody());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionAtEndSlot(){
        try {
            int[] slots =selData.findFreeSlots(Forward.TAG, 549, screenHeight);
            assertEquals(10, slots[0]);
            assertFalse(selData.getInstructionSlot(10).isEmpty());
            assertFalse(selData.getInstructionSlot(10).isInAnInstructionBody());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionOverEndSlot(){
        try {
            int[] slots =selData.findFreeSlots(Forward.TAG, 700, screenHeight);
            assertEquals(10, slots[0]);
            assertFalse(selData.getInstructionSlot(10).isEmpty());
            assertFalse(selData.getInstructionSlot(10).isInAnInstructionBody());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionAtSlotZero(){
        try {
            int[] slots =selData.findFreeSlots(Forward.TAG, 1, screenHeight);
            assertEquals(1, slots[0]);
            assertFalse(selData.getInstructionSlot(1).isEmpty());
            assertFalse(selData.getInstructionSlot(1).isInAnInstructionBody());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionWithSlotFull(){
        try {
            int[] slots = selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            assertEquals(2, slots[0]);
            assertFalse(selData.getInstructionSlot(2).isEmpty());
            assertEquals(Forward.TAG, selData.getInstructionSlot(2).getInstruction().getTag());
            slots = selData.findFreeSlots(TurnLeft.TAG, 125, screenHeight);
            assertEquals(1, slots[0]);
            assertEquals(Forward.TAG, selData.getInstructionSlot(2).getInstruction().getTag());
            assertEquals(TurnLeft.TAG, selData.getInstructionSlot(1).getInstruction().getTag());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionWithSlotAndOneNeighborFull(){
        try {
            selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            selData.findFreeSlots(TurnLeft.TAG, 125, screenHeight);
            int[] slots = selData.findFreeSlots(TurnRight.TAG, 125, screenHeight);
            assertEquals(3, slots[0]);
            assertEquals(Forward.TAG, selData.getInstructionSlot(2).getInstruction().getTag());
            assertEquals(TurnLeft.TAG, selData.getInstructionSlot(1).getInstruction().getTag());
            assertEquals(TurnRight.TAG, selData.getInstructionSlot(3).getInstruction().getTag());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotForNormalInstructionWithSlotAndNeighborsFull(){
        try {
            selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            selData.findFreeSlots(TurnLeft.TAG, 125, screenHeight);
            selData.findFreeSlots(TurnRight.TAG, 125, screenHeight);
            selData.findFreeSlots(Noise.TAG, 125, screenHeight);
        } catch (NoLocationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testFindSlotsForRepeatWithEmptySlots(){
        try{
            int[] slots = selData.findFreeSlots(Repeat.TAG, 150, screenHeight);
            assertEquals(1, slots[0]);
            assertEquals(4, slots[1]);
            assertFalse(selData.getInstructionSlot(1).isEmpty());
            assertFalse(selData.getInstructionSlot(4).isEmpty());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotsForRepeatAtMaxSlot(){
        try{
            int[] slots = selData.findFreeSlots(Repeat.TAG, 600, screenHeight);
            assertEquals(7, slots[0]);
            assertEquals(10, slots[1]);
            assertFalse(selData.getInstructionSlot(7).isEmpty());
            assertFalse(selData.getInstructionSlot(10).isEmpty());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotsForRepeatWithEmptySlotsButInstructionInBody(){
        try{
            selData.findFreeSlots(Forward.TAG, 125, screenHeight); // will be in slot 2
            selData.findFreeSlots(Repeat.TAG, 150, screenHeight); // will be in slot 1 and 4
            assertEquals(Forward.TAG, selData.getInstructionSlot(2).getInstruction().getTag());
            assertEquals(Repeat.TAG, selData.getInstructionSlot(1).getInstruction().getTag());
            assertEquals(Repeat.TAG, selData.getInstructionSlot(4).getInstruction().getTag());
            assertTrue(selData.getInstructionSlot(2).isInAnInstructionBody());
        } catch (NoLocationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotsForRepeatWithSlotBlocked(){
        try{
            selData.findFreeSlots(Forward.TAG, 51, screenHeight);
            selData.findFreeSlots(Repeat.TAG, 150, screenHeight);
        } catch (NoLocationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testFindSlotsForIfWithEmptySlots(){
        try{
            int[] slots = selData.findFreeSlots(If.TAG, 149, screenHeight);
            assertEquals(1, slots[0]);
            assertEquals(3, slots[1]);
            assertEquals(If.TAG, selData.getInstructionSlot(1).getInstruction().getTag());
            assertEquals(If.TAG, selData.getInstructionSlot(3).getInstruction().getTag());
        } catch (NoLocationException e){
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotsForIfWithBlockedSlots(){
        try{
            selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            selData.findFreeSlots(If.TAG, 149, screenHeight);
        } catch (NoLocationException e){
            assertTrue(true);
        }
    }

    @Test
    public void testFindSlotsForIfElseWithEmptySlots(){
        try{
            int[] slots = selData.findFreeSlots(IfElse.TAG, 150, screenHeight);
            assertEquals(1, slots[0]);
            assertEquals(3, slots[1]);
            assertEquals(5, slots[2]);
        } catch (NoLocationException e){
            assertTrue(false);
        }
    }

    @Test
    public void testFindSlotsForIfElseWithSlotsBlocked(){
        try{
            selData.findFreeSlots(Forward.TAG, 150, screenHeight);
            selData.findFreeSlots(IfElse.TAG, 150, screenHeight);
        } catch (NoLocationException e){
            assertTrue(true);
        }
    }

    @Test
    public void testRemoveInstructionFromSlot(){
        try{
            selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            assertFalse(selData.getInstructionSlot(2).isEmpty());
            assertEquals(Forward.TAG, selData.getInstructionSlot(2).getInstruction().getTag());
            selData.removeInstructionFromSlot(2);
            assertTrue(selData.getInstructionSlot(2).isEmpty());
            assertEquals(null, selData.getInstructionSlot(2).getInstruction());
        } catch (NoLocationException e){
            assertTrue(false);
        }
    }

    @Test
    public void testRemoveInstructionFromSlotWithoutAnInstruction(){
        assertTrue(selData.getInstructionSlot(2).isEmpty());
        assertEquals(null, selData.getInstructionSlot(2).getInstruction());
        selData.removeInstructionFromSlot(2);
        assertTrue(selData.getInstructionSlot(2).isEmpty());
        assertEquals(null, selData.getInstructionSlot(2).getInstruction());
    }

    @Test
    public void testGetAllInstructionsFromSlots(){
        try {
            selData.findFreeSlots(Forward.TAG, 125, screenHeight);
            selData.findFreeSlots(TurnLeft.TAG, 225, screenHeight);
            selData.findFreeSlots(Forward.TAG, 549, screenHeight);
            selData.findFreeSlots(Noise.TAG, 300, screenHeight);
            List<Instruction> foundInstructions = selData.getAllInstructionsInSlots();
            assertEquals(4, foundInstructions.size());
            assertEquals(Forward.TAG, foundInstructions.get(0).getTag());
            assertEquals(TurnLeft.TAG, foundInstructions.get(1).getTag());
            assertEquals(Noise.TAG, foundInstructions.get(2).getTag());
            assertEquals(Forward.TAG, foundInstructions.get(3).getTag());
        } catch (NoLocationException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testGetAllInstructionsFromSlotsAllSlotsEmpty(){
        List<Instruction> foundInstructions = selData.getAllInstructionsInSlots();
        assertEquals(0, foundInstructions.size());
    }

}
