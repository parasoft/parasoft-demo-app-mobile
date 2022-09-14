package com.parasoft.demoapp.retrofitConfig;

import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfo;
import com.parasoft.demoapp.retrofitConfig.response.OrderInfo;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/login")
    Call<ResultResponse<Void>> login(@Field("username") String username, @Field("password") String password);

    @GET("/logout")
    Call<ResultResponse<Void>> logout();

    @GET("/forgotPassword")
    Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword();

    @GET("/v1/orders")
    Call<ResultResponse<OrderListResponse>> getOrderList();

    @GET("/v1/orders/{orderNumber}")
    Call<ResultResponse<OrderInfo>> orderDetails(@Path("orderNumber") String orderNumber);

    @PUT("/v1/orders/{orderNumber}")
    Call<ResultResponse<OrderInfo>> orderDetails(@Path("orderNumber") String orderNumber, @Body OrderStatus orderStatusDTO);
}
