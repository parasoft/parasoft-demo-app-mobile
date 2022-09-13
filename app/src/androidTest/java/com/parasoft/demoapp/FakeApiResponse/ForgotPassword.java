package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfo;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword {
    public static ApiInterface return200Response() {
        return new With200Response();
    }

    public static ApiInterface return200ButNoDataResponse() {
        return new With200ButNoDataResponse();
    }

    public static ApiInterface return200ButNoUserInfoResponse() {
        return new With200ButNoUserInfoResponse();
    }

    public static ApiInterface return500Response() {
        return new With500Response();
    }

    public static ApiInterface onFailure() {
        return new OnFailure();
    }

    private static class With200Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfo>>> callback) {

                    // Prepare approver user info
                    ForgotPasswordUserInfo.PrimaryUserInfo approverPrimaryUserInfo = new ForgotPasswordUserInfo.PrimaryUserInfo();
                    approverPrimaryUserInfo.setPassword("fakePassword");
                    approverPrimaryUserInfo.setUserName("fakeUsername");
                    ForgotPasswordUserInfo approverUserInfo = new ForgotPasswordUserInfo();
                    approverUserInfo.setHasPrimaryUser(true);
                    approverUserInfo.setRoleName("ROLE_APPROVER");
                    approverUserInfo.setPrimaryUserInfo(approverPrimaryUserInfo);

                    List<ForgotPasswordUserInfo> data = new ArrayList<>();
                    data.add(approverUserInfo);

                    ResultResponse<List<ForgotPasswordUserInfo>> resultResponse = new ResultResponse<>();
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("User info got successfully.");
                    resultResponse.setData(data);

                    Response<ResultResponse<List<ForgotPasswordUserInfo>>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With200ButNoDataResponse extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfo>>> callback) {

                    ResultResponse<List<ForgotPasswordUserInfo>> resultResponse = new ResultResponse<>();
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("User info got successfully.");

                    Response<ResultResponse<List<ForgotPasswordUserInfo>>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With200ButNoUserInfoResponse extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfo>>> callback) {

                    // Prepare approver user info
                    ForgotPasswordUserInfo approverUserInfo = new ForgotPasswordUserInfo();
                    approverUserInfo.setHasPrimaryUser(false);
                    approverUserInfo.setRoleName("ROLE_APPROVER");

                    List<ForgotPasswordUserInfo> data = new ArrayList<>();
                    data.add(approverUserInfo);

                    ResultResponse<List<ForgotPasswordUserInfo>> resultResponse = new ResultResponse<>();
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("User info got successfully.");
                    resultResponse.setData(data);

                    Response<ResultResponse<List<ForgotPasswordUserInfo>>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With500Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfo>>> callback) {
                    Response<ResultResponse<List<ForgotPasswordUserInfo>>> response = Response.error(500, ResponseBody.create(null, "Internal error."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class OnFailure extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfo>>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }
}
