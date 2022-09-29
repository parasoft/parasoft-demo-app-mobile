package com.parasoft.demoapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.parasoft.demoapp.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parasoft.demoapp.component.OrderDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtil {

    public static void loadImage(ImageView imageView, String orderImage, ImageView overlayView) {
        PDAService pdaService = PDAService.Factory.getInstance();
        showImage(false, null, imageView, overlayView);
        pdaService.getClient(ApiInterface.class).getImage(orderImage)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            System.out.println("-----------------");
                            System.out.println(response.body().contentType());
                            System.out.println(response.body().getClass());
                            Bitmap image = BitmapFactory.decodeStream(response.body().byteStream());
                            showImage(true, image, imageView, overlayView);
                        } else {
                            showFailedImage(imageView);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        showFailedImage(imageView);
                        Log.e(OrderDialog.TAG, "Error loading the image: " + orderImage, t);
                    }
                });
    }

    private static void showFailedImage(ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.mipmap.ic_image_loading_failed);
    }

    private static void showImage(boolean show, Bitmap image, ImageView imageView, ImageView overlayView) {
        if (show && image != null) {
            imageView.setImageBitmap(image);
        }
        if (overlayView != null) {
            overlayView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

}
