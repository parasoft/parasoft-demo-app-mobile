package com.parasoft.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parasoft.demoapp.util.FooterUtil;
import com.parasoft.demoapp.util.OrderAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    private ProgressBar progressBar;
    private TextView errorMessage;
    private PDAService pdaService;
    private RecyclerView recyclerView;
    private TextView noOrderInfo;
    private SwipeRefreshLayout ordersLoader;
    private OrderAdapter orderAdapter;

    public OrderAdapter getOrderAdapter() {
        return orderAdapter;
    }

    private boolean hasOrders = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pdaService = new PDAService();
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out);
        setContentView(R.layout.activity_home);
        initCustomActionBar();
        FooterUtil.setFooterInfo(this);

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
        if(actionBar != null){
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.home_title_layout);
        }
    }

    public void loadOrderList(boolean loadFirstTime) {
        if (loadFirstTime) {
            progressBar.setVisibility(View.VISIBLE);
        }
        errorMessage.setText("");
        errorMessage.setVisibility(View.GONE);
        pdaService.getClient(ApiInterface.class).getOrderList()
                .enqueue(new Callback<ResultResponse<OrderListResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<OrderListResponse>> call,
                                           @NonNull Response<ResultResponse<OrderListResponse>> response) {
                        ordersLoader.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        if (response.code() != 200) {
                            if (hasOrders) {
                                Toast.makeText(HomeActivity.this, R.string.loading_orders_failed, Toast.LENGTH_LONG).show();
                            } else {
                                showErrorView(getResources().getString(R.string.orders_loading_error));
                            }
                            return;
                        }

                        OrderListResponse res = response.body().getData();
                        if (res.getContent().size() == 0) {
                            hasOrders = false;
                            showNoOrderView();
                        } else {
                            hasOrders = true;
                            showOrderListView(res.getContent());
                        }
                        if(!loadFirstTime) {
                            Toast.makeText(HomeActivity.this, R.string.loading_orders_successful, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<OrderListResponse>> call, @NonNull Throwable t) {
                        ordersLoader.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        if (!hasOrders) {
                            showErrorView(getResources().getString(R.string.orders_loading_error));
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.loading_orders_failed, Toast.LENGTH_LONG).show();
                        }
                        Log.e(TAG, t.getMessage());
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

    public void initRecyclerView(List<OrderResponse> orders) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(orders, item -> openOrderDialog(item.getOrderNumber()));
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showNoOrderView() {
        recyclerView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        noOrderInfo.setVisibility(View.VISIBLE);
    }

    private void showOrderListView(List<OrderResponse> orderList) {
        noOrderInfo.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        initRecyclerView(orderList);
    }

    private void showErrorView (String errorString) {
        noOrderInfo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        errorMessage.setText(errorString);
        errorMessage.setVisibility(View.VISIBLE);
    }
}