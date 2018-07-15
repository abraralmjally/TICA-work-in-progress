package com.wilki.tica.exceptions;

/**
 * Created by John Wilkie on 03/12/2016.
 * Exception for when a robot goes out of the layout.
 */

public class RobotOutOfLayoutException extends Exception {
    public RobotOutOfLayoutException(String msg){
        super(msg);
    }
}
