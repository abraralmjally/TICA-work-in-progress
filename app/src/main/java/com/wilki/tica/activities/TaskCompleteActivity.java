package com.wilki.tica.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;

/**
 * Created by John Wilkie on 17/12/2016.
 * Activity displays a task completion message and is shown when a task is successfully completed.
 */

public class TaskCompleteActivity extends AppCompatActivity {

    private InterfaceType currentInterfaceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complete);

        // set font
        TextView message = (TextView) findViewById(R.id.completionMessageTv);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        message.setTypeface(customFont);

        // play sound
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.correct);
        mp.start();

        Intent receivedIntent = getIntent();
        currentInterfaceType = (InterfaceType) receivedIntent.getSerializableExtra("INTERFACE_TYPE");
    }

    /**
     * Return to the task selection activity with the same interface set as was just used to
     * complete the task.
     * @param view button pressed.
     */
    public void startNewTask(View view){
        Intent newTaskIntent = new Intent(getApplicationContext(), TaskSelectorActivity.class);
        newTaskIntent.putExtra("INTERFACE_TYPE", currentInterfaceType);
        startActivity(newTaskIntent);
    }

    /**
     * Return to the main menu.
     * @param view button pressed.
     */
    public void returnToMainMenu(View view){
        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
    }
}
