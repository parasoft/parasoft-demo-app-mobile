package com.parasoft.demoapp.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtil {
    public static boolean isShouldHideInput(View view, MotionEvent motionEvent) {
        // Determine if the focus view includes EditText, and if not, return false
        if (view instanceof EditText) {
            int[] location = {0, 0};
            view.getLocationOnScreen(location);
            int left = location[0],
                top = location[1],
                right = left + view.getWidth(),
                bottom = top + view.getHeight();

            // Determine if the touch point is outside of EditText
            return motionEvent.getRawX() < left
                    || motionEvent.getRawX() > right
                    || motionEvent.getRawY() < top
                    || motionEvent.getRawY() > bottom;
        }
        return false;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}
