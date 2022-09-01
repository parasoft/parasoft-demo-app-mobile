package com.parasoft.demoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsUtil {

    public static final String BASE_URL_KEY = "baseUrl";

    public static void saveSetting(Context context, String name, String value) {
        SharedPreferences.Editor note = context.getSharedPreferences("applicationSettings", Context.MODE_PRIVATE).edit();
        note.putString(name, value);
        note.apply();
    }

    public static String getSetting(Context context, String name) {
        SharedPreferences read = context.getSharedPreferences("applicationSettings", Context.MODE_PRIVATE);
        return read.getString(name, null);
    }
}
