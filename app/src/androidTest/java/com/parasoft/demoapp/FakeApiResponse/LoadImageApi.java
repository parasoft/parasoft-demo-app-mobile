package com.parasoft.demoapp.FakeApiResponse;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadImageApi {

    public static ApiInterface return200Response() {
        return new With200Response();
    }

    public static ApiInterface return200Response_without_Image() {
        return new With200Response_no_image();
    }

    public static ApiInterface return404Response() {
        return new With404Response();
    }

    public static ApiInterface onFailure() {
        return new OnFailure();
    }

    private static class With200Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ResponseBody responseBody = ResponseBody.create(MediaType.get("image/png"), bitmapData);


                    Response<ResponseBody> response = Response.success(200, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With200Response_no_image extends ApiInterfaceImplForTest {
        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    ResponseBody responseBody = ResponseBody.create(MediaType.get("image/png"), "");

                    Response<ResponseBody> response = Response.success(200, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With404Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    ResponseBody responseBody = ResponseBody.create(MediaType.get("image/png"), "");
                    Response<ResponseBody> response = Response.error(404, responseBody);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class OnFailure extends ApiInterfaceImplForTest {
        @Override
        public Call<ResponseBody> getImage(String imagePath) {
            return new CallInterfaceImplForTest<ResponseBody>() {
                @Override
                public void enqueue(Callback<ResponseBody> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }
}