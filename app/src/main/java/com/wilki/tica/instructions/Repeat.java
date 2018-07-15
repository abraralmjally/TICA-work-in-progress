package com.wilki.tica.instructions;

import com.wilki.tica.R;

import java.io.Serializable;

/**
 * Created by John Wilkie on 30/01/2017.
 * Class represents the repeat instruction.
 */

public class Repeat extends Instruction implements Serializable {

    public static final String TAG = "repeat";
    public static final int IMGRESOURCELOCATION = R.drawable.repeat;
    public static final int CODENUMBER = 361;

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
    public String getBluetoothInstruction(){ return ""; }

    @Override
    public boolean isABasicInstruction() {
        return false;
    }
}
