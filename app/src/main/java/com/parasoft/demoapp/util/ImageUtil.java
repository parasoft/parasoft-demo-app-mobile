package com.parasoft.demoapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtil {

    public static void loadImage(ImageView imageView, String orderImage) {
        PDAService pdaService = new PDAService();
        pdaService.getClient(ApiInterface.class).getImage(orderImage)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bitmap);
                    } else {
                        // TODO: Set a default local image when load image failed
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("OrderDialog", "Load image " + orderImage + " failed", t);
                    // TODO: Set a default local image when load image failed
                }
            });
    }
}