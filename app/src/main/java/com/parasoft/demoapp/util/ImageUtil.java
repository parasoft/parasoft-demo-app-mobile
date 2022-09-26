package com.parasoft.demoapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parasoft.demoapp.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtil {

    public static void loadImage(ImageView imageView, String orderImage, @Nullable OrderItemAdapter.ViewHolder viewHolder) {
        PDAService pdaService = new PDAService();
        pdaService.getClient(ApiInterface.class).getImage(orderImage)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bitmap);
                    } else {
                        showLoadingFailedImage(imageView, viewHolder);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    showLoadingFailedImage(imageView, viewHolder);
                    Log.e("OrderDialog", "Load image " + orderImage + " failed", t);
                }
            });
    }

    private static void showLoadingFailedImage (ImageView imageView, @Nullable OrderItemAdapter.ViewHolder viewHolder) {
        imageView.getLayoutParams().height = 350;
        imageView.getLayoutParams().width = 150;
        imageView.setImageResource(R.mipmap.ic_image_loading_failed);
        if (viewHolder != null) {
            imageView.getLayoutParams().height = 150;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.orderItemNameLayout.getLayoutParams();
            params.leftMargin = 600;
            viewHolder.darkOverlay.setVisibility(View.GONE);
        }
    }
}
