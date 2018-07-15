package com.wilki.tica.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wilki.tica.R;

/**
 * Created by John Wilkie on 12/12/2016.
 * Activity for task options menu. A simple activity with three buttons.
 */

public class TaskOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_options);

        TextView title = (TextView) findViewById(R.id.taskSettingsTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(),  getString(R.string.font_path));
        title.setTypeface(customFont);
    }

    /**
     * Start view task performance data activity.
     * @param view view that was clicked.
     */
    public void viewTaskData(View view){
        startActivity(new Intent(getApplicationContext(), ViewTaskPerformanceDataActivity.class));
    }

    /**
     * Start the create task activity.
     * @param view view that was clicked.
     */
    public void createNewTask(View view){
        startActivity(new Intent(getApplicationContext(), CreateNewTaskActivity.class));
    }

    /**
     * Retrun to the main menu activity.
     * @param view view that was clicked.
     */
    public void returnToMainMenu(View view) {
        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
    }
}
