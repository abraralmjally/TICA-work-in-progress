package com.wilki.tica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wilki.tica.logicLayer.CStudents;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;

import java.util.List;

/**
 * Created by John Wilkie on 29/11/2016.
 * Activity for main menu. Allows selection of interface type of task options.
 */

public class MainMenuActivity extends AppCompatActivity {

    String SelectedGroupName ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent extras = getIntent();
       SelectedGroupName = extras.getStringExtra("SelectedGroup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


    }

    /**
     * Start task selection activity with the screen interface set.
     * @param view button pressed.
     */
    public void startScreenMode(View view){
        Intent selectTaskIntent = new Intent(getApplicationContext(), TaskSelectorActivity.class);
        selectTaskIntent.putExtra("INTERFACE_TYPE", InterfaceType.SCREEN);
        selectTaskIntent.putExtra("SelectedGroup", SelectedGroupName);

        startActivity(selectTaskIntent);
    }

    /**
     * Start task selection activity with the tangible interface set.
     * @param view button pressed.
     */
    public void startTangibleMode(View view){
        Intent selectTaskIntent = new Intent(getApplicationContext(), TaskSelectorActivity.class);
        selectTaskIntent.putExtra("INTERFACE_TYPE", InterfaceType.TANGIBLE);
        selectTaskIntent.putExtra("SelectedGroup", SelectedGroupName);
        startActivity(selectTaskIntent);
    }

    /**
     * Start task options activity.
     * @param view button pressed.
     */
    public void startTaskOptions(View view){
        startActivity(new Intent(getApplicationContext(), TaskOptionsActivity.class));
    }
}
