package com.parasoft.demoapp.util;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SettingsUtilTest {

    Context appContext;

    public static final String BASE_URL = "http://88.8.8.8:8888";
    public static final String BASE_URL_DEF = "http://10.0.2.2:8080";

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testGetAndSaveBaseUrl() {
        Assert.assertEquals(BASE_URL_DEF, SettingsUtil.getBaseUrl(appContext));
        SettingsUtil.saveBaseUrl(appContext, BASE_URL);
        Assert.assertEquals(BASE_URL, SettingsUtil.getBaseUrl(appContext));
    }
}