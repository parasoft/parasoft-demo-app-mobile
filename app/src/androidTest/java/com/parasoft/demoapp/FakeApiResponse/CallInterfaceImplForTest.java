package com.parasoft.demoapp.FakeApiResponse;

import java.io.IOException;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is just for test use.
 * <br/>
 * No real implementation for all methods. You need to override the method/methods in subclass if you will use it/them.
 */
public class CallInterfaceImplForTest<T> implements Call<T> {
    @Override
    public Response<T> execute() throws IOException {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public void enqueue(Callback<T> callback) {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public boolean isExecuted() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public boolean isCanceled() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Call<T> clone() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Request request() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }

    @Override
    public Timeout timeout() {
        throw new UnsupportedOperationException("Need to override this method in subclass");
    }
}
