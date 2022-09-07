package com.parasoft.demoapp.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.widget.TextView;

import com.parasoft.demoapp.BuildConfig;
import com.parasoft.demoapp.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FooterUtilTest {
    @Mock
    private Activity activity;
    @Mock
    private Resources resources;
    @Mock
    private TextView textView;
    @Captor
    private ArgumentCaptor<Editable> captor;

    @Test
    public void setFooterInfo() {
        // Given
        String app_title = "PARASOFT DEMO APP";
        String footer_info = app_title + " v" +  BuildConfig.VERSION_CODE + '.' +
                BuildConfig.VERSION_NAME + '_' + BuildConfig.buildMetadata;

        // When
        doNothing().when(textView).setText(captor.capture());
        when(textView.getText()).thenAnswer(invocation -> captor.getValue());
        when(activity.findViewById(R.id.footer_text_view)).thenReturn(textView);
        when(resources.getString(R.string.app_title)).thenReturn(app_title);
        when(activity.getResources()).thenReturn(resources);

        // Then
        FooterUtil.setFooterInfo(activity);
        assertEquals(footer_info, textView.getText());
    }
}