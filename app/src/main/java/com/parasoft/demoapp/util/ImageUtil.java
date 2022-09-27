package com.parasoft.demoapp.util;

import android.content.res.Resources;
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
        if (viewHolder != null) {
            editItemLayout(120, View.GONE, imageView, viewHolder);
        }
        pdaService.getClient(ApiInterface.class).getImage(orderImage)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            if (viewHolder != null) {
                                editItemLayout(5, View.VISIBLE, imageView, viewHolder);
                            }
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
        imageView.getLayoutParams().height = dp2px(100);
        imageView.getLayoutParams().width = dp2px(65);
        imageView.setImageResource(R.mipmap.ic_image_loading_failed);
        if (viewHolder != null) {
            editItemLayout(170, View.GONE, imageView, viewHolder);
        }
    }

    private static void editItemLayout (int leftMargin, int visibility, ImageView imageView, @Nullable OrderItemAdapter.ViewHolder viewHolder) {
        viewHolder.darkOverlay.setVisibility(visibility);
        imageView.getLayoutParams().height = dp2px(65);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.orderItemNameLayout.getLayoutParams();
        params.leftMargin = dp2px(leftMargin);
    }

    private static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5);
    }
}
