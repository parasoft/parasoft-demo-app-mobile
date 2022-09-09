package com.parasoft.demoapp.util;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import android.content.Context;
import android.content.SharedPreferences;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.PDAService;

public class SettingsUtilTest {
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context appContext;

    public static final String BASE_URL_KEY = "baseUrl";
    public static final String BASE_URL = "http://88.8.8.8:8888";
    public static final String BASE_URL_DEF = "http://10.0.2.2:8080";
    public static final String VALUE = "foobar";

    @Before
    public void setUp() throws Exception {
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.editor = Mockito.mock(SharedPreferences.Editor.class);
        this.appContext = Mockito.mock(Context.class);
    }

    @Test
    public void saveSetting() {
        Mockito.when(appContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(sharedPrefs.edit()).thenReturn(editor);
        //When
        SettingsUtil.saveSetting(appContext, "peppa", VALUE);
        //Then
        verify(editor, times(1)).putString(anyString(), anyString());
        verify(editor, times(1)).apply();
    }

    @Test
    public void getSetting() {
        Mockito.when(appContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(sharedPrefs.getString(VALUE, null)).thenReturn(VALUE);

        Assert.assertEquals(VALUE, SettingsUtil.getSetting(appContext, VALUE));
    }

    @Test
    public void getBaseUrl() {
        try (MockedStatic<SettingsUtil> mockedStatic = Mockito.mockStatic(SettingsUtil.class)) {
            //When baseUrl is not null
            mockedStatic.when(() -> SettingsUtil.getSetting(appContext, BASE_URL_KEY)).thenReturn(BASE_URL);
            //Should call real method, otherwise the methods inside the method would not be called
            mockedStatic.when(() -> SettingsUtil.getBaseUrl(appContext)).thenCallRealMethod();
            //Then
            Assert.assertEquals(BASE_URL, SettingsUtil.getBaseUrl(appContext));

            //When baseUrl is null
            mockedStatic.when(() -> SettingsUtil.getSetting(appContext, BASE_URL_KEY)).thenReturn(null);
            when(appContext.getString(R.string.default_url)).thenReturn(BASE_URL_DEF);
            //Then
            Assert.assertEquals(BASE_URL_DEF, SettingsUtil.getBaseUrl(appContext));
        }
    }

    @Test
    public void saveBaseUrl() {
        try (MockedStatic<SettingsUtil> mockedStatic = Mockito.mockStatic(SettingsUtil.class);
             MockedStatic<PDAService> pdaServiceMockedStatic = Mockito.mockStatic(PDAService.class)) {
            //When called SettingsUtil.saveSetting() and PDAService.setBaseUrl(), then do nothing
            mockedStatic.when(() -> SettingsUtil.saveSetting(appContext, BASE_URL_KEY, BASE_URL)).thenAnswer((Answer<Void>) invocation -> null);
            pdaServiceMockedStatic.when(() -> PDAService.setBaseUrl(BASE_URL)).thenAnswer((Answer<Void>) invocation -> null);
            //Should call real method, otherwise the methods inside the method would not be called
            mockedStatic.when(() -> SettingsUtil.saveBaseUrl(appContext, BASE_URL)).thenCallRealMethod();
            SettingsUtil.saveBaseUrl(appContext, BASE_URL);

            //Then
            //Verify if the SettingsUtil.saveSetting() is been called
            PowerMockito.verifyStatic(SettingsUtil.class, Mockito.times(1));
            SettingsUtil.saveSetting(appContext, BASE_URL_KEY, BASE_URL);

            //Verify if the PDAService.setBaseUrl() is been called
            PowerMockito.verifyStatic(PDAService.class, Mockito.times(1));
            PDAService.setBaseUrl(BASE_URL);
        }
    }
}