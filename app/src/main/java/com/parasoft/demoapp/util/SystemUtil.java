package com.parasoft.demoapp.util;

import android.content.Context;

import java.util.Locale;

public class SystemUtil {

    public static String getLocalizedLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage().toUpperCase();
        if("EN".equals(language) || "ZH".equals(language)) {
            return language;
        } else {
            return "EN";
        }
    }

}
