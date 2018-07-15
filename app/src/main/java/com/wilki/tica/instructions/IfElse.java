package com.wilki.tica.instructions;

import com.wilki.tica.R;

/**
 * Created by John Wilkie on 6/04/2017.
 * Class represents the If Else instruction.
 */

public class IfElse extends Instruction {
    public static final String TAG = "ifElse";
    public static final int IMGRESOURCELOCATION = R.drawable.ifelse;
    public static final int CODENUMBER = 203;
    public static final int CODENUMBER2 = 179;

    @Override
    public String toString(){
        return TAG;
    }

    @Override
    public int getImgResourceLocation() {
        return IMGRESOURCELOCATION;
    }

    @Override
    public String getTag(){
        return TAG;
    }

    @Override
    public String getBluetoothInstruction(){ return ""; }

    @Override
    public boolean isABasicInstruction() {
        return false;
    }
}
