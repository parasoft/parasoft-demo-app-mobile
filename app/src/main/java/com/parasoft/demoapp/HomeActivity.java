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

import com.parasoft.demoapp.component.OrderDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.OrderListResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;
import com.parasoft.demoapp.util.FooterUtil;
import com.parasoft.demoapp.util.OrderAdapter;
import com.parasoft.demoapp.util.RefreshOrderUtil;

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
    public boolean isRefreshSuccess;
    private int orderSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pdaService = new PDAService();
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out);
        setContentView(R.layout.activity_home);
        initCustomActionBar();
        FooterUtil.setFooterInfo(this);
        RefreshOrderUtil.refresh(this);

        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.order_error_message);
        recyclerView = findViewById(R.id.order_recycler_view);
        noOrderInfo = findViewById(R.id.display_no_orders_info);
        errorMessage.setText("");
        errorMessage.setVisibility(View.INVISIBLE);
        loadOrderList();
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

    public void loadOrderList() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setText("");
        errorMessage.setVisibility(View.INVISIBLE);
        pdaService.getClient(ApiInterface.class).getOrderList()
            .enqueue(new Callback<ResultResponse<OrderListResponse>>() {
                @Override
                public void onResponse(@NonNull Call<ResultResponse<OrderListResponse>> call,
                                       @NonNull Response<ResultResponse<OrderListResponse>> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRefreshSuccess = true;
                    if (response.code() == 200) {
                        OrderListResponse res = response.body().getData();
                        if (res != null) {
                            orderSize = res.getContent().size();
                            if (orderSize == 0) {
                                initNoOrderView();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                errorMessage.setVisibility(View.GONE);
                                noOrderInfo.setVisibility(View.GONE);
                                initRecyclerView(res.getContent());
                            }
                        }
                    }
                    showToast();
                }

                @Override
                public void onFailure(@NonNull Call<ResultResponse<OrderListResponse>> call, @NonNull Throwable t) {
                    isRefreshSuccess = false;
                    if (orderSize == 0) {
                        noOrderInfo.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.INVISIBLE);
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(R.string.orders_loading_error);
                        Log.e(TAG, t.getMessage());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setVisibility(View.GONE);
                    }
                    showToast();
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
        OrderAdapter orderAdapter = new OrderAdapter(orders, item -> openOrderDialog(item.getOrderNumber()));
        recyclerView.setAdapter(orderAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void initNoOrderView () {
        noOrderInfo.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
    }

    public void showToast() {
        Toast.makeText(this, isRefreshSuccess? R.string.loading_orders_successful:R.string.loading_orders_failed, Toast.LENGTH_SHORT).show();
    }
}