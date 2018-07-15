package com.wilki.tica;

import android.app.Application;

import com.wilki.tica.logicLayer.BluetoothConnectionManager;

/**
 * Created by John Wilkie on 27/02/2017.
 * Application class of the TICA app.
 */

public class TICA extends Application {

    private BluetoothConnectionManager btConManager;

    /**
     * @param btConManager set the Bluetooth connection manager to the one provided as a parameter.
     */
    public void setBtConManager(BluetoothConnectionManager btConManager){
        this.btConManager = btConManager;
    }

    /**
     * @return the current Bluetooth connection manager object.
     */
    public BluetoothConnectionManager getBtConManager(){
        return btConManager;
    }
}
