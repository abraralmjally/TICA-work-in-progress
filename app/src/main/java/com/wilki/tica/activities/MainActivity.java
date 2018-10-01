package com.wilki.tica.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import 	android.content.Context;
import android.os.Handler;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wilki.tica.R;
import com.wilki.tica.dataLayer.DbContract;
import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.logicLayer.BluetoothConnectionManager;
import com.wilki.tica.exceptions.BluetoothDisabledException;
import com.wilki.tica.exceptions.NoBluetoothException;
import com.wilki.tica.TICA;
import com.wilki.tica.fragments.BusyDialogFragment;
import com.wilki.tica.logicLayer.InterfaceType;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by John Wilkie on 15/11/2016.
 * Activity for start screen of the app. Bluetooth connection is set up while this screen is
 * displayed.
 */

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    private  BluetoothConnectionManager btConManager;
    TextView textInfo, textStatus;
    ListView listViewPairedDevice;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    //Register for the ACTION_FOUND broadcast//
    boolean isRegistered = false;

    IntentFilter filter ;
    private DbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        textInfo = (TextView)findViewById(R.id.info);
        textStatus = (TextView)findViewById(R.id.status);
        listViewPairedDevice = (ListView)findViewById(R.id.pairedlist);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Register for the ACTION_FOUND broadcast//



        /* * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user * performs a swipe-to-refresh gesture. */
   mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
                String stInfo = bluetoothAdapter.getName() + "\n" + bluetoothAdapter.getAddress();
                textInfo.setText(stInfo);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                },2000);

            }//end of OnRefresh()
        }
        );


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mDbHelper = new DbHelper(this);

    }// end of OnCreate



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
        Intent toLaunch = new Intent(getApplicationContext(), GroupSelectorActivity.class);
        startActivity(toLaunch);
    }



    /*
     * Starts a bluetooth connection to the robot.
     */
    private void startBluetooth(){
        //getBondedDevices method, which will return a set of BluetoothDevice objects representing devices that are paired to the local adapter
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there’s 1 or more paired devices...//
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            //...then loop through these devices//
            for (BluetoothDevice device : pairedDevices) {
                //Retrieve each device’s public identifier and MAC address.

                pairedDeviceArrayList.add(device);
            }
            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);
                    btConManager = new BluetoothConnectionManager(bluetoothAdapter,device,
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
                            Toast.makeText(MainActivity.this,
                                    "Name: " + device.getName() + "\n"
                                            + "Address: " + device.getAddress() + "\n"
                                            + "BondState: " + device.getBondState() + "\n"
                                            + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                            + "Class: " + device.getClass(),
                                    Toast.LENGTH_LONG).show();
                               textStatus.setText("stats: Connected");
                                textInfo.setText("Information:"+"Name: " + device.getName() + "\n"
                                        + "Address: " + device.getAddress() + "\n");

                               /* if(connection.getisConnected())
                            {                                Intent selectTaskIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
                               selectTaskIntent.putExtra("INTERFACE_TYPE", InterfaceType.SCREEN);
                                startActivity(selectTaskIntent);}*/




                        } else {
                            // Bluetooth disabled request user to enable.
                            //Turn ON BlueTooth if it is OFF
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    } else {
                        // device does not have Bluetooth, notify user.
                        Toast.makeText(getApplicationContext(), "No Bluetooth available on this device, this " +
                                "app can't operate.", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}

    /*
     * Starts a discovery request connection to the robot.
     */
    private void startdiscovery(){
        // not used
        //https://www.androidauthority.com/adding-bluetooth-to-your-app-742538/
        if (bluetoothAdapter.startDiscovery()) {

            //If discovery has started, then display the following toast....//
            Toast.makeText(getApplicationContext(), "Discovering other bluetooth devices...",
                    Toast.LENGTH_SHORT).show();
            //  To ensure your app gets notified whenever a new device is discovered, you’ll need to register a BroadcastReceiver for the ACTION_FOUND intent.
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, filter);
            isRegistered=true;

        } else {

            //If discovery hasn’t started, then display this alternative toast//
            Toast.makeText(getApplicationContext(), "Something went wrong! Discovery has failed to start.",
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cut down on unnecessary system overhead, by unregistering the ACTION_FOUND receiver//
        // I added the if statment because of the error eceiver not registered: com.wilki.tica.activities.MainActivity$3@43f05788
        // try to dolve by following https://stackoverflow.com/questions/12421449/android-broadcastreceiver-unregisterreceiver-issue-not-registered
        if(isRegistered) {
            this.unregisterReceiver(broadcastReceiver);
            isRegistered=false;
        }


    }



    //Create a BroadcastReceiver for ACTION_FOUND//
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

//Whenever a remote Bluetooth device is found...//
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                //….retrieve the BluetoothDevice object and its EXTRA_DEVICE field, which contains information about the device’s characteristics and capabilities//
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //You’ll usually want to display information about any devices you discover, so here I’m adding each device’s name and address to an ArrayAdapter,
                //which I’d eventually incorporate into a ListView//
                pairedDeviceArrayList.add(device);         }
        }
    };



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
    private class SetUpBluetoothConnection extends AsyncTask<Void, Void, Void>  {
        private boolean isConnected =false;

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

        public BusyDialogFragment getRobotRunning() {
            return robotRunning;
        }

        public boolean getisConnected ()
        {
            return  isConnected;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(robotRunning != null) {
                // async task finished, dismiss fragment.
                robotRunning.dismiss();
                isConnected=true;

            }

        }



    }



    private void insertStudent()
    {


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Students.COLUMN_NAME_STUDENT,"Umar");
        values.put(DbContract.Students.COLUMN_NAME_GENDER,1);
        values.put(DbContract.Students.COLUMN_NAME_SCHOOL,"140");
        long newwRowID = db.insert(DbContract.Students.TABLE_NAME,null,values);
        Log.v("MainActivity","New Student ID"+ newwRowID);
        if (newwRowID == -1){
            Toast.makeText(this,"Error with saving Student", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Student  saved with row id:" + newwRowID, Toast.LENGTH_SHORT).show ();

        }
        ContentValues values1 = new ContentValues();
        values.put(DbContract.Students.COLUMN_NAME_STUDENT,"Raseel");
        values.put(DbContract.Students.COLUMN_NAME_GENDER,1);
        values.put(DbContract.Students.COLUMN_NAME_SCHOOL,"AlEhasan");
        long newwRowID1 = db.insert(DbContract.Students.TABLE_NAME,null,values1);
        Log.v("MainActivity","New Student ID"+ newwRowID);
        if (newwRowID1 == -1){
            Toast.makeText(this,"Error with saving Student", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Student  saved with row id:" + newwRowID1, Toast.LENGTH_SHORT).show ();

        }
        ContentValues values2 = new ContentValues();
        values.put(DbContract.Students.COLUMN_NAME_STUDENT,"Abrar");
        values.put(DbContract.Students.COLUMN_NAME_GENDER,1);
        values.put(DbContract.Students.COLUMN_NAME_SCHOOL,"AlEhasan");
        long newwRowID2 = db.insert(DbContract.Students.TABLE_NAME,null,values2);
        Log.v("MainActivity","New Student ID"+ newwRowID);
        if (newwRowID2 == -1){
            Toast.makeText(this,"Error with saving Student", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Student  saved with row id:" + newwRowID2, Toast.LENGTH_SHORT).show ();

        }
        ContentValues values3 = new ContentValues();
        values.put(DbContract.Students.COLUMN_NAME_STUDENT,"Abeer");
        values.put(DbContract.Students.COLUMN_NAME_GENDER,1);
        values.put(DbContract.Students.COLUMN_NAME_SCHOOL,"AlEhasan");
        long newwRowID3 = db.insert(DbContract.Students.TABLE_NAME,null,values3);
        Log.v("MainActivity","New Student ID"+ newwRowID);
        if (newwRowID3 == -1){
            Toast.makeText(this,"Error with saving Student", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Student  saved with row id:" + newwRowID3, Toast.LENGTH_SHORT).show ();

        }
        db.close();

    }

   /* private void insertGroup()

    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.Groups.COLUMN_NAME_NAME,"Abrar");
        values.put(DbContract.Groups.COLUMN_NAME_SCHOOL_ID,1);
        values.put(DbContract.Groups.COLUMN_NAME_STUDENT1,1);
        values.put(DbContract.Groups.COLUMN_NAME_STUDENT2,1);
        long newwRowID = db.insert(DbContract.Students.TABLE_NAME,null,values);
        Log.v("MainActivity","New Student ID"+ newwRowID);
        if (newwRowID == -1){
            Toast.makeText(this,"Error with saving Student", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Student  saved with row id:" + newwRowID, Toast.LENGTH_SHORT).show ();

        }
    }*/


    private void insertSession()

    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.Sessions.COLUMN_NAME_GROUP_NAME, "aa123");
        long newwRowID = db.insert(DbContract.Sessions.TABLE_NAME,null,values);
        Log.v("MainActivity","New Session added"+ newwRowID);
        if (newwRowID == -1){
            Toast.makeText(this,"Error with saving Session", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Session  saved with row id:" + newwRowID, Toast.LENGTH_SHORT).show ();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_session_dummy_data:
                insertSession();
                return true;
            case R.id.action_insert_student_dummy_data:
                // Respond to a click on the "Delete all entries" menu option
                insertStudent();
                return true;
            case R.id.action_delete_all_Sessions:
                // Do nothing for now
                return true;
            case R.id.action_delete_all_students:
                // Do nothing for now
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
