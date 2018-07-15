package com.wilki.tica.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.wilki.tica.dragAndTouchListeners.DialogClickListener;

/**
 * Created by John Wilkie on 15/03/2017.
 * Dialogue used to check if the user wants to delete all performance data.
 * Based on code from:
 * //https:developer.android.com/guide/topics/ui/dialogs.html
 */

public class DeletePerformanceDataFragment extends DialogFragment {

    DialogClickListener mListener; //Instance of class to call methods of when buttons pressed.

    // get instance of a class implementing DialogClickListener.
    @Override
    public void onAttach(Activity caller){
        super.onAttach(caller);
        mListener = (DialogClickListener) caller;
    }

    /*
     * Build dialogue with two buttons and link buttons back to calling activity.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("are you sure you want to delete all the performance data?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onYesClick();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        return builder.create();
    }

}
