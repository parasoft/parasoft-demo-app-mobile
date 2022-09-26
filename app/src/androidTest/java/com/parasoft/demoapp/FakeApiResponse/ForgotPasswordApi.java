package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfoResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordApi {
    public static ApiInterface return200Response() {
        return new With200Response();
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
        public Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfoResponse>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfoResponse>>> callback) {

                    // Prepare approver user info
                    ForgotPasswordUserInfoResponse.PrimaryUserInfo approverPrimaryUserInfo = new ForgotPasswordUserInfoResponse.PrimaryUserInfo();
                    approverPrimaryUserInfo.setPassword("fakePassword");
                    approverPrimaryUserInfo.setUserName("fakeUsername");
                    ForgotPasswordUserInfoResponse approverUserInfo = new ForgotPasswordUserInfoResponse();
                    approverUserInfo.setHasPrimaryUser(true);
                    approverUserInfo.setRoleName("ROLE_APPROVER");
                    approverUserInfo.setPrimaryUserInfo(approverPrimaryUserInfo);

                    List<ForgotPasswordUserInfoResponse> data = new ArrayList<>();
                    data.add(approverUserInfo);

                    ResultResponse<List<ForgotPasswordUserInfoResponse>> resultResponse = new ResultResponse<>();
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("User info got successfully.");
                    resultResponse.setData(data);

                    Response<ResultResponse<List<ForgotPasswordUserInfoResponse>>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With200ButNoUserInfoResponse extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfoResponse>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfoResponse>>> callback) {

                    // Prepare approver user info
                    ForgotPasswordUserInfoResponse approverUserInfo = new ForgotPasswordUserInfoResponse();
                    approverUserInfo.setHasPrimaryUser(false);
                    approverUserInfo.setRoleName("ROLE_APPROVER");

                    List<ForgotPasswordUserInfoResponse> data = new ArrayList<>();
                    data.add(approverUserInfo);

                    ResultResponse<List<ForgotPasswordUserInfoResponse>> resultResponse = new ResultResponse<>();
                    resultResponse.setStatus(1);
                    resultResponse.setMessage("User info got successfully.");
                    resultResponse.setData(data);

                    Response<ResultResponse<List<ForgotPasswordUserInfoResponse>>> response = Response.success(200, resultResponse);
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class With500Response extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfoResponse>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfoResponse>>> callback) {
                    Response<ResultResponse<List<ForgotPasswordUserInfoResponse>>> response = Response.error(500, ResponseBody.create(null, "Internal error."));
                    callback.onResponse(null, response);
                }
            };
        }
    }

    private static class OnFailure extends ApiInterfaceImplForTest {
        @Override
        public Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword() {
            return new CallInterfaceImplForTest<ResultResponse<List<ForgotPasswordUserInfoResponse>>>() {
                @Override
                public void enqueue(Callback<ResultResponse<List<ForgotPasswordUserInfoResponse>>> callback) {
                    callback.onFailure(null, new RuntimeException("On failure"));
                }
            };
        }
    }
}
