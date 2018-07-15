package com.wilki.tica.exceptions;

/**
 * Created by John Wilkie on 19/03/2017.
 * Exception for when a task layout is not correctly formed.
 */

public class TaskLayoutException extends Exception {
    public TaskLayoutException(String msg){
        super(msg);
    }
}
