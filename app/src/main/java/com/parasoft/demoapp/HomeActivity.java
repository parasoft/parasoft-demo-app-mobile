package com.parasoft.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.parasoft.demoapp.util.RecycleOnScrollListener;

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
    private List<OrderResponse> orderResponseList = new ArrayList<>();
    private List<List<OrderResponse>> orderPagination;
    private int page = 0;

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
                        if (response.code() != 200) {
                            showErrorView(getResources().getString(R.string.orders_loading_error));
                            return;
                        }
                        List<OrderResponse> orderList = response.body() != null ? response.body().getData().getContent() : null;
                        if (orderList == null || orderList.size() == 0) {
                            showNoOrderView();
                        } else {
                            orderResponseList = orderList;
                            orderPagination = ListUtils.partition(orderList, ITEMS_PER_PAGE);
                            showOrderListView(loadFirstTime);
                        }
                        orderItemClickable = true;
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<OrderListResponse>> call, @NonNull Throwable t) {
                        ordersLoadFinished();
                        showErrorView(getResources().getString(R.string.orders_loading_error));
                        orderItemClickable = true;
                        Log.e(TAG, "Load orders error", t);
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
        page = 0;
        getData(page);
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
    }

    private void initListener() {
        // slide-up load more
        recyclerView.addOnScrollListener(new RecycleOnScrollListener() {
            @Override
            public void onLoadMore() {
                // to avoid possible repeated slide-up operation
                if (OrderAdapter.LOADING == orderAdapter.getLoadState()) {
                    orderAdapter.notifyItemRemoved(orderAdapter.getItemCount());
                    return;
                }

                orderAdapter.setLoadState(OrderAdapter.LOADING);
                if (orderDisplayList.size() < orderResponseList.size()) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                page++;
                                getData(page);
                                orderAdapter.setLoadState(OrderAdapter.LOAD_FINISH);
                            });
                        }
                    }, 3000);
                } else {
                    page = 0;
                    orderAdapter.setLoadState(OrderAdapter.LOAD_END);
                }
            }
        });
        // slide-down refresh
        ordersLoader.setOnRefreshListener(() -> new Handler().postDelayed(() -> loadOrderList(false), 3000));
    }

    private void getData(int page) {
        if (page == 0) {
            orderDisplayList.addAll(orderPagination.get(0));
        } else if (page < orderPagination.size()) {
            orderAdapter.addFooterItem(orderPagination.get(page));
        }
    }

    private void showNoOrderView() {
        recyclerView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        noOrderInfo.setVisibility(View.VISIBLE);
    }

    private void showOrderListView(boolean loadFirstTime) {
        noOrderInfo.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        if (loadFirstTime || orderDisplayList.size() == 0) {
            initRecyclerView();
        } else {
            orderAdapter.addHeaderItem(orderPagination.get(0));
        }

        orderAdapter.setCanStart(true);
        orderAdapter.setLoadState(OrderAdapter.LOAD_FINISH);
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
}