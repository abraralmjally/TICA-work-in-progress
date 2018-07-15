package com.wilki.tica.logicLayer;

import java.io.Serializable;

/**
 * Created by John Wilkie on 30/11/2016.
 * The Pos class is a wrapper for an X and a Y value.
 */

public class Pos implements Serializable {

    private final int y;
    private final int x;

    /**
     * Constructor for Pos class.
     * @param y the y value.
     * @param x the x value.
     */
    public Pos(int y, int x){
        this.y = y;
        this.x = x;
    }

    /**
     * @return y value.
     */
    public int getY(){
        return y;
    }

    /**
     * @return x value.
     */
    public int getX(){
        return x;
    }
}
