package com.wilki.tica.fragments;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.wilki.tica.R;

/**
 * Created by John Wilkie on 13/03/2017.
 * Fragment shown while the system is busy to show the user that the system is not frozen.
 */

public class BusyDialogFragment extends DialogFragment {

    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            message = bundle.getString("message");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busy, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.font_path));
        TextView loadingMessage = (TextView) rootView.findViewById(R.id.loadingMessageTv);
        loadingMessage.setTypeface(customFont);
        loadingMessage.setText(message);
        return rootView;
    }
}