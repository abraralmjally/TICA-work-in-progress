package com.wilki.tica;

import com.wilki.tica.logicLayer.InstructionSlot;

import org.junit.Before;
import org.junit.Test;

import com.wilki.tica.instructions.Forward;
import com.wilki.tica.instructions.Instruction;
import com.wilki.tica.instructions.Noise;
import com.wilki.tica.instructions.TurnLeft;
import com.wilki.tica.instructions.TurnRight;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by John Wilkie on 02/02/2017.
 * Unit testing for InstructionSlot class.
 */

public class InstructionSlotUnitTest {
    private InstructionSlot slot;

    @Before
    public void setUp(){
        slot = new InstructionSlot();
    }

    @Test
    public void testInstructionSlotCreation(){
        assertTrue(slot.isEmpty());
        assertNull(slot.getInstruction());
    }

    @Test
    public void testSetSlot(){
        Forward forwardInstruction = new Forward();
        assertTrue(slot.isEmpty());
        assertNull(slot.getInstruction());
        setSlot(forwardInstruction);
    }

    @Test
    public void testMultipleSetSlot(){
        Noise noiseInstruction = new Noise();
        TurnRight rightInstruction = new TurnRight();
        TurnLeft leftInstruction = new TurnLeft();
        setSlot(noiseInstruction);
        setSlot(rightInstruction);
        setSlot(leftInstruction);
    }

    private void setSlot(Instruction instructionToSet){
        slot.setSlot(instructionToSet);
        assertFalse(slot.isEmpty());
        assertEquals(slot.getInstruction(), instructionToSet);
    }

    @Test
    public void testClearSlot(){
        Forward forwardInstruction = new Forward();
        setSlot(forwardInstruction);
        slot.clearSlot();
        assertTrue(slot.isEmpty());
        assertNull(slot.getInstruction());
    }

    @Test
    public void testInAnInstructionBody(){
        assertFalse(slot.isInAnInstructionBody());
        slot.setInAnInstructionBody(true);
        assertTrue(slot.isInAnInstructionBody());
    }

}
