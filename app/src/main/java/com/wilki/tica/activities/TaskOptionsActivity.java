package com.wilki.tica.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;


import com.wilki.tica.R;
import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.logicLayer.Utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by John Wilkie on 12/12/2016.
 * Activity for task options menu. A simple activity with three buttons.
 */

public class TaskOptionsActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "Tasks.db";
    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/";
    Button btnExport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_options);

        TextView title = (TextView) findViewById(R.id.taskSettingsTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        title.setTypeface(customFont);
        btnExport = (Button) findViewById(R.id.SQLite2Excel);

        File file = new File(directory_path);
        if (!file.exists()) {
            Log.v("File Created", String.valueOf(file.mkdirs()));

        }

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Export SQLite DB as EXCEL FILE
                sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DATABASE_NAME, directory_path);
                sqliteToExcel.exportAllTables("users.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Utils.showSnackBar(view, "Successfully Exported");
                    }

                    @Override
                    public void onError(Exception e) {
                        Utils.showSnackBar(view, e.getMessage());
                    }
                });
            }
        });


        

    }


    SQLiteToExcel sqliteToExcel = new SQLiteToExcel(this, DATABASE_NAME );

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
