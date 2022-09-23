package com.parasoft.demoapp.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Common utils on UI elements
 */
public class CommonUtil {
    /**
     * Determines if the focus of a motion event is inside specified view
     */
    public static boolean isFocusInsideView(View view, MotionEvent event) {
        int[] location = {0, 0};
        view.getLocationOnScreen(location);
        int left = location[0],
            top = location[1],
            right = left + view.getWidth(),
            bottom = top + view.getHeight();
        return event.getRawX() < left
                || event.getRawX() > right
                || event.getRawY() < top
                || event.getRawY() > bottom;
    }

    /**
     * Hide keyboard attached to specified view
     */
    public static void hideKeyboardForView(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}
