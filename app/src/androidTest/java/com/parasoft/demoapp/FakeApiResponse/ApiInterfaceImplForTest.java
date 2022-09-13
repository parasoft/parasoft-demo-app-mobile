package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfo;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import retrofit2.Call;

/**
 * This class is just for test use.
 * <br/>
 * No real implementation for all methods. You need to override the method/methods in subclass if you will use it/them.
 */
public class ApiInterfaceImplForTest implements ApiInterface {
    @Override
    public Call<ResultResponse<Void>> login(String username, String password) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<Void>> logout() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<List<ForgotPasswordUserInfo>>> forgotPassword() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }
}
