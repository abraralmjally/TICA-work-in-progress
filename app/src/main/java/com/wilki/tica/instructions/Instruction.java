package com.wilki.tica.instructions;

import com.wilki.tica.exceptions.TopCodeNotRecognisedException;
import com.wilki.tica.logicLayer.TopCode;

/**
 * Created by John Wilkie on 30/01/2017.
 * Abstract class that all instructions extend.
 */

public abstract class Instruction {

    /**
     * Returns an instruction that matches the tag provided as an argument.
     * @param tag the tag of the instruction to match against.
     * @return the instruction that matches the tag given as an argument.
     */
    public static Instruction matchTagToInstruction(String tag){
        switch (tag) {
            case Forward.TAG:
                return new Forward();
            case TurnLeft.TAG:
                return new TurnLeft();
            case TurnRight.TAG:
                return new TurnRight();
            case Noise.TAG:
                return new Noise();
            case Repeat.TAG:
                return new Repeat();
            case If.TAG:
                return new If();
            case IfElse.TAG:
                return new IfElse();
        }
        return null;
    }

    /**
     * Given a TopCode object returns the instruction that matches the code. If no instruction
     * matches the code an exception is thrown.
     * @param code TopCode to match the instruction to.
     * @return the instruction that matches the TopCode.
     * @throws TopCodeNotRecognisedException thrown when no instruction matches the topCode given.
     */
    public static Instruction matchTopCodeToInstruction(TopCode code)
            throws TopCodeNotRecognisedException {
        if(code.getCode() == Forward.CODENUMBER){
            return new Forward();
        } else if(code.getCode() == TurnLeft.CODENUMBER){
            return new TurnLeft();
        } else if(code.getCode() == TurnRight.CODENUMBER){
            return new TurnRight();
        } else if(code.getCode() == Noise.CODENUMBER){
            return new Noise();
        } else if(code.getCode() == If.CODENUMBER){
            return new If();
        } else if(code.getCode() == IfElse.CODENUMBER || code.getCode() == IfElse.CODENUMBER2){
            return new IfElse();
        } else {
            throw new TopCodeNotRecognisedException();
        }
    }

    /**
     * @return location of image of instruction.
     */
    public abstract int getImgResourceLocation();

    /**
     * @return tag representing the instruction.
     */
    public abstract String getTag();

    /**
     * @return string that represents the instruction to send via Bluetooth.
     */
    public abstract String getBluetoothInstruction();

    /**
     * @return true if forward, noise, turnLeft or turnRight instruction, false otherwise.
     */
    public abstract boolean isABasicInstruction();
}
