package com.parasoft.demoapp.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.parasoft.demoapp.BuildConfig;
import com.parasoft.demoapp.R;

/**
 * Common utils on UI elements
 */
public class CommonUIUtil {
    /**
     * Determines if the focus of a motion event is inside specified view
     * @param view
     * @param event motion event which triggered the focus
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

    /**
     * Initialize value in footer for specified activity
     * @param activity activity which includes the footer
     */
    public static void initializedFooter(Activity activity) {
        TextView textView = activity.findViewById(R.id.footer_text_view) ;
        String result = activity.getResources().getString(R.string.app_title) + " v" + BuildConfig.VERSION_CODE + '.' + BuildConfig.VERSION_NAME + '_' + BuildConfig.buildMetadata;
        textView.setText(result);
    }
}
