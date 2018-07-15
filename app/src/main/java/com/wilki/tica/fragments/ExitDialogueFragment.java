package com.wilki.tica.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.wilki.tica.dragAndTouchListeners.DialogClickListener;
import com.wilki.tica.R;

/**
 * Created by John Wilkie on 26/03/2017.
 * Dialog used to ensure the users wants to exit.
 */

public class ExitDialogueFragment extends DialogFragment {

    DialogClickListener mListener; //Instance of class to call methods of when buttons pressed.

    // get instance of a class implementing DialogClickListener.
    @Override
    public void onAttach(Activity caller){
        super.onAttach(caller);
        mListener = (DialogClickListener) caller;
    }

    /*
     * Request a dialog without a title.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /*
     * Set that when button pressed method is called in class that launched fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exit_dialogue, container, false);

        TextView fragmentText = (TextView) rootView.findViewById(R.id.exitFragmentTv);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.font_path));
        fragmentText.setTypeface(customFont);

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelExitBtn);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mListener.onNoClick();
            }
        });

        Button exitButton = (Button) rootView.findViewById(R.id.confirmExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onYesClick();
            }
        });
        return rootView;
    }

}
