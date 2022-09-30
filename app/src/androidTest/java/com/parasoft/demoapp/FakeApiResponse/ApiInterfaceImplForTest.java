package com.parasoft.demoapp.FakeApiResponse;

import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfoResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import okhttp3.ResponseBody;
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
    public Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> forgotPassword() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<OrderListResponse>> getOrderList() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<OrderResponse>> getOrderDetails(String orderNumber) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<OrderResponse>> updateOrderDetails(String orderNumber, OrderStatusRequest orderStatusRequest) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResponseBody> getImage(String imagePath) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<ResultResponse<String>> localizedValue(String lang, String key) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }
}
