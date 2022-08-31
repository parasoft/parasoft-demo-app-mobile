package com.parasoft.demoapp.util;

import android.app.Activity;
import android.widget.TextView;

import com.parasoft.demoapp.BuildConfig;
import com.parasoft.demoapp.R;

public class FooterUtil {

    public static void setFooterInfo(Activity activity) {
        TextView textView = activity.findViewById(R.id.footer_text_view) ;
        String result = activity.getResources().getString(R.string.app_title) + " v" + BuildConfig.VERSION_CODE + '.' + BuildConfig.VERSION_NAME + '_' + BuildConfig.buildMetadata;
        textView.setText(result);
    }
}