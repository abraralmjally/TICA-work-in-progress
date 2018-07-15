package com.wilki.tica.instructions;

import com.wilki.tica.R;

import java.io.Serializable;

/**
 * Created by John Wilkie on 30/01/2017.
 * Class represents the turn left instruction.
 */

public class TurnLeft extends Instruction implements Serializable {

    public static final String TAG = "turn_left";
    public static final int IMGRESOURCELOCATION = R.drawable.left;
    public static final int CODENUMBER = 47;
    private final String bluetoothInstruction = "l";

    @Override
    public String toString(){
        return TAG;
    }

    @Override
    public int getImgResourceLocation() {
        return IMGRESOURCELOCATION;
    }

    @Override
    public String getTag(){ return TAG; }

    @Override
    public String getBluetoothInstruction(){ return bluetoothInstruction; }

    @Override
    public boolean isABasicInstruction() {
        return true;
    }
}
