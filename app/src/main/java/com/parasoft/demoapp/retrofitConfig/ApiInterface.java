package com.parasoft.demoapp.retrofitConfig;

import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/login")
    Call<ResultResponse<Void>> login(@Field("username") String username, @Field("password") String password);
}
