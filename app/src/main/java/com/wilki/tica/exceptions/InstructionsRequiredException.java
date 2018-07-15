package com.wilki.tica.exceptions;

/**
 * Created by John Wilkie on 30/03/2017.
 * Exception used when no instructions are selected when creating a new task.
 */

public class InstructionsRequiredException extends Exception {

    public InstructionsRequiredException(String msg){
        super(msg);
    }

}
