package com.parasoft.demoapp.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
    private static final String TAG = "TimeUtil";

    public static String convertToLocalTime(String time) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT)));
        try {
            date = simpleDateFormat.parse(time);

        } catch (ParseException e) {
            Log.e(TAG, "Get local time failed", e);
        }
        return date == null ? time : simpleDateFormat.format(date);
    }
}
