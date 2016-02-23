package com.nullpointexecutioners.buzzfilms.helpers;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class ViewHelper {

    /**
     * Helper method for creating custom Snackbars
     * @param layout view to place the Snackbar in
     * @param text what you want the Snackbar to say
     * @param duration how long you want the Snackbar to appear for
     * @param backgroundColor color you want the Snackbar's background to be
     * @param textColor color you want the Snackbar's text to be
     * @return the custom made Snackbar
     */
    @NonNull
    public static Snackbar makeSnackbar(@NonNull View layout, @NonNull CharSequence  text, int duration, int backgroundColor, int textColor/*, int actionTextColor*/) {
        Snackbar snackBarView = Snackbar.make(layout, text, duration);
        snackBarView.getView().setBackgroundColor(backgroundColor);
        //snackBarView.setActionTextColor(actionTextColor);
        TextView tv = (TextView) snackBarView.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(textColor);
        return snackBarView;
    }
}
