package com.wilki.tica.fragments;

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

import com.wilki.tica.R;

/**
 * Created by John Wilkie on 15/03/2017.
 * General fragment used to post messages to the user. Desired message is provided using a bundle.
 */

public class MessageFragment extends DialogFragment {

    private String message;

    /*
     * Extract message to be shown.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            message = bundle.getString("message");
        }
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
     * Display the message and set the button to close the dialog.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message_dialogue, container, false);

        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.font_path));
        Button okButton = (Button) rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        TextView messageTv = (TextView) rootView.findViewById(R.id.messageTextView);
        messageTv.setTypeface(customFont);
        messageTv.setText(message);

        return rootView;
    }
}
