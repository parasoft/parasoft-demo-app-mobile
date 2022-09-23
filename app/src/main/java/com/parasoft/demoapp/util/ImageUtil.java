package com.parasoft.demoapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtil {

    public static void loadImage(ImageView imageView, String orderImage, OrderItemAdapter.ViewHolder viewHolder) {
        PDAService pdaService = new PDAService();
        pdaService.getClient(ApiInterface.class).getImage(orderImage)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bitmap);
                    } else {
                        showLoadingFailedImage(imageView, viewHolder);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showLoadingFailedImage(imageView, viewHolder);
                    Log.e("OrderDialog", "Load image " + orderImage + " failed", t);
                }
            });
    }

    private static void showLoadingFailedImage (ImageView imageView, OrderItemAdapter.ViewHolder viewHolder) {
        if (viewHolder != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.orderItemNameLayout.getLayoutParams();
            params.leftMargin = 550;
            viewHolder.darkOverlay.setVisibility(View.GONE);
        }
        imageView.getLayoutParams().height = 150;
        imageView.getLayoutParams().width = 150;
        imageView.setImageResource(R.mipmap.ic_image_loading_failed);
    }
}
