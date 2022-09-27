package com.parasoft.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parasoft.demoapp.component.OrderDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;
import com.parasoft.demoapp.util.CommonUIUtil;
import com.parasoft.demoapp.util.OrderAdapter;

import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private static final int ITEMS_PER_PAGE = 15;

    private ProgressBar progressBar;
    private TextView errorMessage;
    private PDAService pdaService;
    private RecyclerView recyclerView;
    private TextView noOrderInfo;
    private SwipeRefreshLayout ordersLoader;
    private boolean orderItemClickable = false;

    private OrderAdapter orderAdapter;
    private final List<OrderResponse> orderDisplayList = new ArrayList<>();
    private List<List<OrderResponse>> orderPagination;
    private int pageIndex = 0;
    private int numOfOrders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pdaService = new PDAService();
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out);
        setContentView(R.layout.activity_home);
        initCustomActionBar();
        CommonUIUtil.initializedFooter(this);

        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.order_error_message);
        recyclerView = findViewById(R.id.order_recycler_view);
        noOrderInfo = findViewById(R.id.display_no_orders_info);
        ordersLoader = findViewById(R.id.order_refresh);
        errorMessage.setVisibility(View.GONE);
        noOrderInfo.setVisibility(View.GONE);

        ordersLoader.setOnRefreshListener(() -> loadOrderList(false));
        loadOrderList(true);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.home_title_layout);
        }
    }

    public void loadOrderList(boolean loadFirstTime) {
        if (loadFirstTime) {
            progressBar.setVisibility(View.VISIBLE);
        }
        orderItemClickable = false;
        errorMessage.setText("");
        errorMessage.setVisibility(View.GONE);
        pdaService.getClient(ApiInterface.class).getOrderList()
                .enqueue(new Callback<ResultResponse<OrderListResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<OrderListResponse>> call,
                                           @NonNull Response<ResultResponse<OrderListResponse>> response) {
                        ordersLoadFinished();
                        orderDisplayList.clear();
                        pageIndex = 0;
                        if (response.code() != 200) {
                            handleErrorMessages(response.code());
                            return;
                        }
                        List<OrderResponse> orderList = response.body() != null ? response.body().getData().getContent() : null;
                        if (orderList == null || orderList.size() == 0) {
                            showNoOrderView();
                        } else {
                            numOfOrders = orderList.size();
                            orderPagination = ListUtils.partition(orderList, ITEMS_PER_PAGE);
                            showOrderListView();
                        }
                        orderItemClickable = true;
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<OrderListResponse>> call, @NonNull Throwable t) {
                        ordersLoadFinished();
                        showErrorView(getResources().getString(R.string.orders_loading_error));
                        orderItemClickable = true;
                        Log.e(TAG, "Error loading orders", t);
                    }
                });
    }

    public void signOut() {
        pdaService.getClient(ApiInterface.class).logout();
        PDAService.setAuthToken(null);

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        HomeActivity.this.finish();
    }

    public void openOrderDialog(String orderNumber) {
        OrderDialog orderDialog = new OrderDialog(orderNumber);
        orderDialog.show(getSupportFragmentManager(), OrderDialog.TAG);
    }

    public void initRecyclerView() {
        orderDisplayList.addAll(orderPagination.get(0));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(orderDisplayList, item -> {
            if (orderItemClickable) {
                openOrderDialog(item.getOrderNumber());
            }
        });
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        initListener();
        orderAdapter.setCanStart(true);
        orderAdapter.setLoadState(OrderAdapter.LoadingState.FINISHED);
    }

    private void initListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingUp = false;

            // load more operation will be triggered only when the displayed item is the last item and it's slide-up behavior
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    assert manager != null;
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount() - 1; // footer not included

                    if (lastItemPosition == itemCount && isSlidingUp) {
                        onLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingUp = dy > 0;
            }

            // slide-up load more
            public void onLoadMore() {
                // to avoid possible repeated slide-up operation
                if (OrderAdapter.LoadingState.LOADING == orderAdapter.getLoadState()) {
                    orderAdapter.notifyItemRemoved(orderAdapter.getItemCount());
                    return;
                }
                orderAdapter.setLoadState(OrderAdapter.LoadingState.LOADING);
                if (orderAdapter.getItemCount() <= numOfOrders) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                pageIndex++;
                                if (pageIndex < orderPagination.size()) {
                                    orderAdapter.addMoreItems(orderPagination.get(pageIndex));
                                }
                                orderAdapter.setLoadState(OrderAdapter.LoadingState.FINISHED);
                            });
                        }
                    }, 500);
                } else {
                    orderAdapter.setLoadState(OrderAdapter.LoadingState.END);
                }
            }
        });
    }

    private void showNoOrderView() {
        recyclerView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        noOrderInfo.setVisibility(View.VISIBLE);
    }

    private void showOrderListView() {
        noOrderInfo.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        initRecyclerView();
    }

    private void showErrorView (String errorString) {
        noOrderInfo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        errorMessage.setText(errorString);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void ordersLoadFinished() {
        ordersLoader.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorMessages (int errorCode) {
        String errMsg;
        switch (errorCode) {
            case 400:
                errMsg = getResources().getString(R.string.current_user_not_exist);
                Log.e(TAG, "Invalid request parameter");
                break;
            case 401:
                errMsg = getResources().getString(R.string.no_authorization_to_get_order_list);
                Log.e(TAG, "Not authorized to get all orders");
                break;
            default:
                errMsg = getResources().getString(R.string.orders_loading_error);
                Log.e(TAG, "Error loading all orders");
        }
        showErrorView(errMsg);
    }
}