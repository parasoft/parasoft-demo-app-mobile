package com.parasoft.demoapp.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private static final String TAG = "TimeUtil";

    public static String convertToLocalTime(String time) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT)));
        try {
            date = simpleDateFormat.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Get local time failed");
        }
        return simpleDateFormat.format(date);
    }
}
