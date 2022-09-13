package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfo;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import retrofit2.Call;

public class ApiInterfaceImplForTest implements ApiInterface {
    @Override
    public Call<ResultResponse<Void>> login(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<ResultResponse<Void>> logout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
        throw new UnsupportedOperationException();
    }
}
