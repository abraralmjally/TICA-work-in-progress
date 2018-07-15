package com.wilki.tica.logicLayer;

import java.io.Serializable;

/**
 * Created by John Wilkie on 29/11/2016.
 * Enum to represent the types of tiles a layout can have.
 */

public enum SquareTypes implements Serializable {
    EMPTY("E"), NOISE("N"), START("S"), FINISH("F"), BLOCKED("X");

    private String symbol;

    SquareTypes(String symbol){ this.symbol = symbol; }

    public String toString(){ return symbol; }
}
