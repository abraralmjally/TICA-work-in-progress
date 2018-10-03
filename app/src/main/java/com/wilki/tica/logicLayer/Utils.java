package com.wilki.tica.logicLayer;

import android.support.design.widget.Snackbar;
import android.view.View;

public class Utils {

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
