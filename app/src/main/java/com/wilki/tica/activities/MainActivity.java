package com.wilki.tica.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wilki.tica.logicLayer.BluetoothConnectionManager;
import com.wilki.tica.exceptions.BluetoothDisabledException;
import com.wilki.tica.exceptions.NoBluetoothException;
import com.wilki.tica.R;
import com.wilki.tica.TICA;
import com.wilki.tica.fragments.BusyDialogFragment;

/**
 * Created by John Wilkie on 15/11/2016.
 * Activity for start screen of the app. Bluetooth connection is set up while this screen is
 * displayed.
 */

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT = 1;
    private  BluetoothConnectionManager btConManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(((TICA) getApplicationContext()).getBtConManager() == null){
            startBluetooth();
        }
    }

    /**
     * Launches main menu activity.
     * @param view
     */
    public void taskMenu(View view){
        Intent toLaunch = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(toLaunch);
    }

    /*
     * Starts a bluetooth connection to the robot.
     */
    private void startBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btConManager = new BluetoothConnectionManager(mBluetoothAdapter,
                getApplicationContext());
        if(btConManager.deviceHasBluetooth()){
            if(btConManager.bluetoothEnabled()){
                // show connecting to robot fragment.
                BusyDialogFragment connectingRobot = new BusyDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("message", "connecting to robot");
                connectingRobot.setArguments(bundle);

                // start connection process
                SetUpBluetoothConnection connection = new SetUpBluetoothConnection(connectingRobot);
                connection.execute();
            } else {
                // Bluetooth disabled request user to enable.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            // device does not have Bluetooth, notify user.
            Toast.makeText(getApplicationContext(), "No Bluetooth available on this device, this " +
                    "app can't operate.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // bluetooth was enabled.
                startBluetooth();
            } else {
                // bluetooth was not enabled.
                Toast.makeText(this, "This device requires bluetooth permissions to operate, please" +
                        " give permission or you will not be able to use the app.",
                        Toast.LENGTH_LONG).show();
                startBluetooth();
            }
        }
    }

    /*
     * Async task to set up Bluetooth connection without freezing the UI thread.
     */
    private class SetUpBluetoothConnection extends AsyncTask<Void, Void, Void> {

        private BusyDialogFragment robotRunning;

        SetUpBluetoothConnection(BusyDialogFragment robotRunning){
            this.robotRunning = robotRunning;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            robotRunning.show(getFragmentManager(), "connecting to bluetooth");
            try {
                btConManager.connectToRobot();
                ((TICA) getApplicationContext()).setBtConManager(btConManager);
            } catch (NoBluetoothException e) {
                Toast.makeText(getApplicationContext(), "No Bluetooth available on this device, this " +
                        "app can't operate.", Toast.LENGTH_LONG).show();
            } catch (BluetoothDisabledException e) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(robotRunning != null) {
                // async task finished, dismiss fragment.
                robotRunning.dismiss();
            }
        }
    }

}
