package com.parasoft.demoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.PDAService;

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

    public static String getBaseUrl(Context context) {
        String baseUrl = getSetting(context, BASE_URL_KEY);
        if (baseUrl == null) {
            baseUrl = context.getString(R.string.default_url);
        }
        return baseUrl;
    }

    public static void saveBaseUrl(Context context, String value) {
        SettingsUtil.saveSetting(context, BASE_URL_KEY, value);
        PDAService.setBaseUrl(value);
    }
}
