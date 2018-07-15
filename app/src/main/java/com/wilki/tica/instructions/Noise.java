package com.wilki.tica.instructions;

import com.wilki.tica.R;

import java.io.Serializable;

/**
 * Created by John Wilkie on 30/01/2017.
 * Class represents the noise instruction.
 */

public class Noise extends Instruction implements Serializable {

    public static final String TAG = "noise";
    public static final int IMGRESOURCELOCATION = R.drawable.noise;
    public static final int CODENUMBER = 313;
    private final String bluetoothInstruction = "b";

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
