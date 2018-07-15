package com.wilki.tica.logicLayer;

/**
 * Created by John Wilkie on 29/11/2016.
 * Enum defining the types of possible interface.
 */

public enum InterfaceType {

    SCREEN ("screen"), TANGIBLE ("tangible");

    private String name;


    InterfaceType(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }


}
