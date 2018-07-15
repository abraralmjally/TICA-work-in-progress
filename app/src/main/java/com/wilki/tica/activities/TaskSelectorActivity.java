package com.wilki.tica.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wilki.tica.dataLayer.DbHelper;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.Task;

import java.io.File;
import java.util.List;

/**
 * Created by John Wilkie on 14/12/2016.
 * Activity allows selection of a task to attempt.
 */
public class TaskSelectorActivity extends AppCompatActivity {

    private List<Task> taskList;
    private int selectedTask;
    private InterfaceType selectedInterfaceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selector);
        Intent receivedIntent = getIntent();
        selectedInterfaceType = (InterfaceType) receivedIntent.getSerializableExtra("INTERFACE_TYPE");
        selectedTask = 0;

        // set font
        TextView title = (TextView) findViewById(R.id.selectATaskTV);
        Typeface customFont = Typeface.createFromAsset(getAssets(),
                getString(R.string.font_path));
        title.setTypeface(customFont);

        taskList = Task.getAllTasks(getApplicationContext());

        GridView gridview = (GridView) findViewById(R.id.taskGrid);
        gridview.setAdapter(new TaskImageAdapter(this)); // set custom adapter for grid view

        // click listener for grid items being selected.
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedTask = position;
                startTask();
            }
        });

    }

    /**
     * Starts the task description activity with the index to the selected task.
     */
    public void startTask(){
        Intent taskDetailsIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
        taskDetailsIntent.putExtra("SELECTED_TASK", taskList.get(selectedTask));
        if(selectedInterfaceType == InterfaceType.SCREEN){
            taskDetailsIntent.putExtra("MODE", InterfaceType.SCREEN);
        } else if (selectedInterfaceType == InterfaceType.TANGIBLE){
            taskDetailsIntent.putExtra("MODE", InterfaceType.TANGIBLE);
        }
        startActivity(taskDetailsIntent);
    }

    /*
     * Class to generate views to fill the grid view of tasks.
     * Based on - //https://developer.android.com/guide/topics/ui/layout/gridview.html
     */
    public class TaskImageAdapter extends BaseAdapter {
        private Context context;

        public TaskImageAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return taskList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridSquare;
            ImageView imageView;
            TextView taskName;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                gridSquare = inflater.inflate(R.layout.task_item, null);
                imageView = (ImageView) gridSquare.findViewById(R.id.taskGridTaskImage);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {
                gridSquare =  convertView;
            }

            imageView = (ImageView) gridSquare.findViewById(R.id.taskGridTaskImage);
            taskName = (TextView) gridSquare.findViewById(R.id.taskGridTaskName);
            Task currentTask = taskList.get(position);
            taskName.setText("task " + currentTask.getTaskNumber());
            String layoutPath = currentTask.getTaskLayout().getLayoutImageFilePath();
            File image = new File(layoutPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            imageView.setImageBitmap(bitmap);
            return gridSquare;
        }

    }
}

