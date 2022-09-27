package com.parasoft.demoapp.util;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.content.SharedPreferences;

import com.parasoft.demoapp.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.TimeZone;

@RunWith(MockitoJUnitRunner.class)
public class CommonUtilTest {

    @Mock
    SharedPreferences sharedPrefs;

    @Mock
    SharedPreferences.Editor editor;

    @Mock
    Context appContext;

    public static final String BASE_URL = "http://88.8.8.8:8888";
    public static final String BASE_URL_DEF = "http://10.0.2.2:8080";
    public static final String BASE_URL_KEY = "baseUrl";

    @Before
    public void setUp() {
        Mockito.when(appContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(sharedPrefs.edit()).thenReturn(editor);
        Mockito.when(appContext.getString(R.string.default_url)).thenReturn(BASE_URL_DEF);
    }

    @Test
    public void getBaseUrl() {
        Assert.assertEquals(BASE_URL_DEF, CommonUtil.getBaseUrl(appContext));
    }

    @Test
    public void setBaseUrl() {
        CommonUtil.saveBaseUrl(appContext, BASE_URL);
        verify(editor, times(1)).putString(anyString(), anyString());
        verify(editor, times(1)).apply();

        Mockito.when(sharedPrefs.getString(BASE_URL_KEY, null)).thenReturn(BASE_URL);
        Assert.assertEquals(BASE_URL, CommonUtil.getBaseUrl(appContext));
    }

    @Test
     public void getLocalDate() {
        TimeZone defaultTimezone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Assert.assertEquals("2022-09-26", CommonUtil.getLocalDate("2022-09-26T08:00:00.000+00:00"));
        Assert.assertEquals("2022-09-26", CommonUtil.getLocalDate("2022-09-26T08:00:00.000+08:00"));
        Assert.assertEquals("2022-09-27", CommonUtil.getLocalDate("2022-09-26T09:00:00.000-07:00"));
        Assert.assertNull(CommonUtil.getLocalDate("validTimePattern"));
        TimeZone.setDefault(defaultTimezone);
    }

    @Test
    public void getLocalTime() {
        TimeZone defaultTimezone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Assert.assertEquals("16:00:00", CommonUtil.getLocalTime("2022-09-26T08:00:00.000+00:00"));
        Assert.assertEquals("08:00:00", CommonUtil.getLocalTime("2022-09-26T08:00:00.000+08:00"));
        Assert.assertEquals("00:00:00", CommonUtil.getLocalTime("2022-09-26T09:00:00.000-07:00"));
        Assert.assertNull(CommonUtil.getLocalTime("validTimePattern"));
        TimeZone.setDefault(defaultTimezone);
    }
}