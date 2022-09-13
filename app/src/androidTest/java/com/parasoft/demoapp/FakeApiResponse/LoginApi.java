package com.parasoft.demoapp.FakeApiResponse;


import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginApi {

    public static ApiInterface return200Response() {
        return new With200Response();
    }

    public static ApiInterface return401Response() {
        return new With401Response();
    }

    public static ApiInterface return500Response() {
        return new With500Response();
    }

    public static ApiInterface onFailure() {
        return new OnFailure();
    }

    private static class With200Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<Void>> login(String username, String password) {
            return new CallInterfaceImplForTest<ResultResponse<Void>>() {
                @Override
                public void enqueue(Callback<ResultResponse<Void>> callback) {
                    ResultResponse<Void> resultResponse = new ResultResponse<>();
                    resultResponse.setData(null);
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("Login successfully.");

                    Response<ResultResponse<Void>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With401Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<Void>> login(String username, String password) {
            return new CallInterfaceImplForTest<ResultResponse<Void>>() {
                @Override
                public void enqueue(Callback<ResultResponse<Void>> callback) {
                    Response<ResultResponse<Void>> response = Response.error(401, ResponseBody.create(null, "Current user is not authorized."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With500Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<Void>> login(String username, String password) {
            return new CallInterfaceImplForTest<ResultResponse<Void>>() {
                @Override
                public void enqueue(Callback<ResultResponse<Void>> callback) {
                    Response<ResultResponse<Void>> response = Response.error(500, ResponseBody.create(null, "Internal error."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class OnFailure extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<Void>> login(String username, String password) {
            return new CallInterfaceImplForTest<ResultResponse<Void>>() {
                @Override
                public void enqueue(Callback<ResultResponse<Void>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }
}