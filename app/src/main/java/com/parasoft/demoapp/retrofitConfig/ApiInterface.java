package com.parasoft.demoapp.retrofitConfig;

import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfoResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/login")
    Call<ResultResponse<Void>> login(@Field("username") String username, @Field("password") String password);

    @GET("/forgotPassword")
    Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword();

    @GET("/v1/orders")
    Call<ResultResponse<OrderListResponse>> getOrderList();

    @GET("/v1/orders/{orderNumber}")
    Call<ResultResponse<OrderResponse>> getOrderDetails(@Path("orderNumber") String orderNumber);

    @PUT("/v1/orders/{orderNumber}")
    Call<ResultResponse<OrderResponse>> updateOrderDetails(@Path("orderNumber") String orderNumber, @Body OrderStatusRequest orderStatusRequest);

    @GET
    Call<ResponseBody> getImage(@Url String imagePath);

    @GET("/localize/{lang}/{key}")
    Call<ResultResponse<String>> localizedValue(@Path("lang") String lang, @Path("key") String key);
}
