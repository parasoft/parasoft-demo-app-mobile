package com.parasoft.demoapp.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.ImageView;

import com.parasoft.demoapp.FakeApiResponse.LoadImageApi;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImageUtilTest {

    @Mock
    ImageView imageView;

    @Mock
    ImageView overlay;

    @Captor
    private ArgumentCaptor<ImageView.ScaleType> scaleTypeCaptor;

    @Captor
    private ArgumentCaptor<Integer> imageCaptor;

    @Mock
    PDAService pdaService;

    @Test
    public void loadImage_200() {
        ImageUtil.setPdaService(pdaService);
        when(pdaService.getClient(ApiInterface.class)).thenReturn(LoadImageApi.return200Response());

        ImageUtil.loadImage(imageView, "test", overlay);

        verify(imageView, times(1)).setImageBitmap(any());
        verify(overlay, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void loadImage_200_failed_to_decode() {
        ImageUtil.setPdaService(pdaService);
        when(pdaService.getClient(ApiInterface.class)).thenReturn(LoadImageApi.return200Response_without_Image());

        ImageUtil.loadImage(imageView, "test", overlay);

        verify(imageView, times(0)).setImageBitmap(any());
        verify(overlay, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void loadImage_404() {
        ImageUtil.setPdaService(pdaService);
        when(pdaService.getClient(ApiInterface.class)).thenReturn(LoadImageApi.return404Response());
        doNothing().when(imageView).setScaleType(scaleTypeCaptor.capture());
        doNothing().when(imageView).setImageResource(imageCaptor.capture());
        when(imageView.getScaleType()).thenAnswer(invocation -> scaleTypeCaptor.getValue());

        ImageUtil.loadImage(imageView, "test", null);

        Integer resId = R.mipmap.ic_image_loading_failed;
        Assert.assertEquals(ImageView.ScaleType.CENTER, imageView.getScaleType());
        Assert.assertEquals(resId, imageCaptor.getValue());
    }

    @Test
    public void loadImage_onFailure() {
        ImageUtil.setPdaService(pdaService);
        when(pdaService.getClient(ApiInterface.class)).thenReturn(LoadImageApi.onFailure());
        doNothing().when(imageView).setImageResource(imageCaptor.capture());
        doNothing().when(imageView).setScaleType(scaleTypeCaptor.capture());
        when(imageView.getScaleType()).thenAnswer(invocation -> scaleTypeCaptor.getValue());

        ImageUtil.loadImage(imageView, "test", null);

        Integer resId = R.mipmap.ic_image_loading_failed;
        Assert.assertEquals(ImageView.ScaleType.CENTER, imageView.getScaleType());
        Assert.assertEquals(resId, imageCaptor.getValue());
    }
}