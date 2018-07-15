package com.wilki.tica.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wilki.tica.fragments.AttemptDataFragment;
import com.wilki.tica.fragments.DeletePerformanceDataFragment;
import com.wilki.tica.dragAndTouchListeners.DialogClickListener;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.TaskPerformance;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by John Wilkie on 15/12/2016.
 * Activity to display task activity data
 */
public class ViewTaskPerformanceDataActivity extends AppCompatActivity implements DialogClickListener {

    private List<TaskPerformance> perfData;
    private CustomPerformanceAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_data);

        // set font
        TextView taskPerformanceTitle = (TextView) findViewById(R.id.taskPerformanceTitle);
        TextView taskPerformanceDetails = (TextView)findViewById(R.id.taskPerformanceDetails);
        Typeface customFont = Typeface.createFromAsset(getAssets(),  getString(R.string.font_path));
        taskPerformanceDetails.setTypeface(customFont);
        taskPerformanceTitle.setTypeface(customFont);

        displayTaskPerformanceData();
    }

    /*
     * Displays a list of task performance data.
     */
    private void displayTaskPerformanceData(){
        perfData = TaskPerformance.getAllTaskPerformances(getApplicationContext());
        ListView lView = (ListView) findViewById(R.id.listViewPerformance);
        listAdapter = new CustomPerformanceAdapter();
        lView.setAdapter(listAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFragment attemptDataFragment = new AttemptDataFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("performanceID", i+1);
                attemptDataFragment.setArguments(bundle);
                attemptDataFragment.show(getFragmentManager(), "attempt data");
            }
        });
    }

    /*
     * Called when button pressed to return to the task menu.
     */
    public void returnToTaskMenu(View view) {
        startActivity(new Intent(getApplicationContext(), TaskOptionsActivity.class));
    }

    /*
     * Called to delete all task performance data.
     */
    public void clearPerformanceData(View view) {
        DialogFragment confimDeleteFragment = new DeletePerformanceDataFragment();
        confimDeleteFragment.show(getFragmentManager(), getString(R.string.confirm_delete));
    }

    /**
     * Called when the choice is made to delete all task performance data.
     */
    @Override
    public void onYesClick() {
        TaskPerformance.deleteAllTaskPerformanceData(getApplicationContext());
        perfData = TaskPerformance.getAllTaskPerformances(getApplicationContext());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoClick() {
        // do nothing
    }

    /*
     * List adapter to display task performance data.
     */
    private class CustomPerformanceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return perfData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if( view == null){
                view = getLayoutInflater().inflate(R.layout.perf_list_item, null);
            }
            ImageView taskImage = (ImageView) view.findViewById(R.id.taskImg);
            TextView taskNum = (TextView) view.findViewById(R.id.taskNum);
            TextView taskAttempts = (TextView) view.findViewById(R.id.taskAttempts);
            TextView taskCompleted = (TextView) view.findViewById(R.id.taskCompleted);
            TextView taskInterface = (TextView) view.findViewById(R.id.taskInterface);
            TextView taskTime = (TextView) view.findViewById(R.id.taskTime);

            TaskPerformance curPerformance = perfData.get(i);

            String layoutPath = curPerformance.getTask().getTaskLayout().getLayoutImageFilePath();
            File image = new File(layoutPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            taskImage.setImageBitmap(bitmap);

            taskNum.setText(Integer.toString(curPerformance.getTask().getTaskNumber()));
            taskAttempts.setText(Integer.toString(curPerformance.getNumAttempts()));
            taskCompleted.setText(Boolean.toString(curPerformance.getTaskCompleted()));
            taskInterface.setText(curPerformance.getInterfaceType().toString());
            long duration = curPerformance.getTaskPerformanceDuration();
            taskTime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
            return view;
        }
    }
}
