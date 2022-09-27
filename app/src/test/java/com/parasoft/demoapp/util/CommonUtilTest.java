package com.parasoft.demoapp.util;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.parasoft.demoapp.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class CommonUtilTest {

    @Mock
    SharedPreferences sharedPrefs;

    @Mock
    SharedPreferences.Editor editor;

    @Mock
    Context appContext;

    @Mock
    Resources resources;

    Configuration configuration = new Configuration();

    public static final String BASE_URL = "http://88.8.8.8:8888";
    public static final String BASE_URL_DEF = "http://10.0.2.2:8080";
    public static final String BASE_URL_KEY = "baseUrl";

    @Before
    public void setUp() {
        Mockito.when(appContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(sharedPrefs.edit()).thenReturn(editor);
        Mockito.when(appContext.getString(R.string.default_url)).thenReturn(BASE_URL_DEF);

        Mockito.when(appContext.getResources()).thenReturn(resources);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
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
    public void getLocalizedLanguage_normal_chinese() {
        configuration.locale = new Locale("zh");

        Assert.assertEquals("ZH", CommonUtil.getLocalizedLanguage(appContext));
    }

    @Test
    public void getLocalizedLanguage_normal_English() {
        configuration.locale = new Locale("others");

        Assert.assertEquals("EN", CommonUtil.getLocalizedLanguage(appContext));
    }

    @Test
    public void getLocalizedLanguage_error() {
        Assert.assertEquals("EN", CommonUtil.getLocalizedLanguage(null));
    }

}