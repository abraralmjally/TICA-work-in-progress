package com.wilki.tica.logicLayer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.wilki.tica.exceptions.BluetoothDisabledException;
import com.wilki.tica.exceptions.NoBluetoothException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * Created by John Wilkie on 27/02/2017.
 * The BluetoothConnectionManager class is responsible for creating and maintaining Bluetooth
 * connections. Based on code from:
 * https://developer.android.com/guide/topics/connectivity/bluetooth.html
 * http://android-er.blogspot.ae/2015/07/android-example-to-communicate-with.html
 */

public class BluetoothConnectionManager {

    private final UUID ROBOT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList<BluetoothDevice> devices;
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice bluetoothDevice;
    private ConnectedThread robotConnection;
    private final Context cont;
    private boolean threadStarted;

    /**
     * Constructor.
     * @param bluetoothAdapter bluetooth adapter.
     * @param cont application context.
     */
    public BluetoothConnectionManager(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, Context cont){
        this.cont = cont;
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothDevice=bluetoothDevice;
        threadStarted = false;
    }

    /**
     * Attempts to make a Bluetooth connection with the robot.
     * @throws NoBluetoothException when the device does not have Bluetooth.
     * @throws BluetoothDisabledException when Bluetooth is disabled.
     */
    public void connectToRobot() throws NoBluetoothException, BluetoothDisabledException {
        if (bluetoothAdapter == null) {
            throw new NoBluetoothException();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                throw new BluetoothDisabledException();
            } else {
                makeConnection();
            }
        }
    }

    public void resetConnection() {
        robotConnection.cancel();
    }

    /**
     * @return true if the device has Bluetooth and false otherwise.
     */
    public boolean deviceHasBluetooth(){
        return !(bluetoothAdapter == null);
    }

    /**
     * @return true if Bluetooth is enabled on the device and false otherwise.
     */
    public boolean bluetoothEnabled(){
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Sends message via Bluetooth to connected robot.
     * @param messageToWrite message to be written to the robot.
     */
    public void writeToRobot(String messageToWrite) throws IOException {
        if(robotConnection != null){
            robotConnection.write(messageToWrite.getBytes());
            if(!threadStarted) {
                robotConnection.start();
                threadStarted = true;
            }
        }
    }

    /*
     * Gets a list of devices paired via Bluetooth and connects to the robot if present.
     */
    private void makeConnection() {
        robotConnection = createBluetoothConnection();
    }

    /*
     * Creates connection to the robot.
     */
    private ConnectedThread createBluetoothConnection(){
        return connectToDevice(bluetoothDevice);
    }

    /*
     * Attempts to create the connection.
     */
    private ConnectedThread connectToDevice(BluetoothDevice device){
        ConnectThread connection = new ConnectThread(device);
        connection.start();
        try {
            connection.join();
            return new ConnectedThread(connection.getBtSocket());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Class for creating a Bluetooth connection concrrently.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        ConnectThread(BluetoothDevice device) {

            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(ROBOT_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }

        }

        /*
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { e.printStackTrace();}
        }

        BluetoothSocket getBtSocket(){
            return mmSocket;
        }
    }

    /*
     * Class for managing connected Bluetooth connections.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    mmInStream.read(buffer);

                    String receivedString = new String(buffer, 0, 1, "US-ASCII");
                    if(receivedString.equals("x")) { // alert intent that robot replied
                        Intent intent = new Intent("bluetooth_robot_complete");
                        intent.putExtra("message", receivedString);
                        LocalBroadcastManager.getInstance(cont).sendBroadcast(intent);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /*
         * Write data to connection
         */
        void write(byte[] bytes) throws IOException {
            mmOutStream.write(bytes);
        }

        /* Used to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
