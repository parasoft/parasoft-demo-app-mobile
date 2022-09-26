package com.parasoft.demoapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommonUtil {

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
        CommonUtil.saveSetting(context, BASE_URL_KEY, value);
        PDAService.setBaseUrl(value);
    }

    public static String getLocalizedLanguage(Context context) {
        if (context != null) {
            Locale locale = context.getResources().getConfiguration().locale;
            String language = locale.getLanguage().toUpperCase();
            if("ZH".equals(language)) {
                return language;
            }
        }
        return "EN";
    }

    public static String convertToLocalTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date = simpleDateFormat.parse(time);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            if (date != null) return simpleDateFormat.format(date);
        } catch (ParseException e) {
            Log.e("Common", "Convert to local time failed", e);
        }
        return null;
    }
}
