package com.wilki.tica.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.wilki.tica.R;
import com.wilki.tica.logicLayer.TaskAttempt;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 18/03/2017.
 * Fragment used to display data on a specific performance.
 */
public class AttemptDataFragment extends DialogFragment {

    private int performanceID; //ID of performance to display data on.

    /*
     * Extract ID from bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            performanceID = bundle.getInt("performanceID");
        }
    }

    /*
     * Set size of the fragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 570;
        params.height = 1000;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    /*
     * Request a fragment without a title bar.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /*
     * Use layout to set out fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attempt, container, false);

        TextView attemptText = (TextView) rootView.findViewById(R.id.attemptsTV);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.font_path));
        attemptText.setTypeface(customFont);

        Button closeButton = (Button) rootView.findViewById(R.id.closeAttemptDataBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        List<TaskAttempt> attempts = TaskAttempt.getAllTaskAttemptsWithPerformanceId(getActivity(),
                performanceID);
        ListView lv = (ListView) rootView.findViewById(R.id.lv_attempt);
        lv.setAdapter(new ListViewAttemptAdapter(attempts));
        return rootView;
    }


    /*
     * Custom adapter for list items. Each item in the list is one attempt.
     */
    private class ListViewAttemptAdapter extends BaseAdapter {

        private List<TaskAttempt> attempts;
        private LayoutInflater inflater;

        ListViewAttemptAdapter(List<TaskAttempt> attempts){
            this.attempts = attempts;
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return attempts.size();
        }

        @Override
        public Object getItem(int arg0) {
            return attempts.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }


        public View getView(int i, View view, ViewGroup parent) {
            if(view == null) {
                view = inflater.inflate(R.layout.attempt_list_item, null);
            }

            TextView attemptNum = (TextView) view.findViewById(R.id.attempt);
            TextView taskTime = (TextView) view.findViewById(R.id.time);
            LinearLayout instructionHolder = (LinearLayout) view.findViewById(R.id.attempt_inst_holder);
            TaskAttempt curAttempt = attempts.get(i);

            attemptNum.setText("" + (curAttempt.getAttemptId()+1));
            long duration = curAttempt.getAttemptDuration();
            taskTime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
            if(curAttempt.getInstructionsUsed().size() > 0 &&
                    curAttempt.getInstructionsUsed().get(0) != null) {
                for (Instruction inst : curAttempt.getInstructionsUsed()) {
                    ImageView instructionImage = new ImageView(getActivity().getApplicationContext());
                    instructionImage.setImageResource(inst.getImgResourceLocation());
                    instructionImage.setLayoutParams(new LayoutParams(300, 60));
                    instructionHolder.addView(instructionImage);
                }
            }
            return view;
        }

    }
}
