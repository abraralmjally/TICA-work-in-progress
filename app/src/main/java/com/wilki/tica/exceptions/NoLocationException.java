package com.wilki.tica.exceptions;

/**
 * Created by John Wilkie on 01/02/2017.
 * Exception for when an instruction cannot be placed in the location it was dropped.
 *
 */

public class NoLocationException extends Exception{

    public NoLocationException(String message){
        super(message);
    }
}
