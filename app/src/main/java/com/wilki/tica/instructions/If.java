package com.wilki.tica.instructions;

import com.wilki.tica.R;

/**
 * Created by John Wilkie on 6/04/2017.
 * Class represents the If instruction.
 */

public class If extends Instruction {

    public static final String TAG = "if";
    public static final int IMGRESOURCELOCATION = R.drawable.iff;
    public static final int CODENUMBER = 199;

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
