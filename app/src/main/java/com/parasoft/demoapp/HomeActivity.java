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

    RecyclerView recyclerView;
    private ProgressBar progressBar;

    private OrderDialog orderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out);
        setContentView(R.layout.activity_home);
        initCustomActionBar();
        FooterUtil.setFooterInfo(this);

        progressBar = findViewById(R.id.progress_bar);
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
        PDAService.getClient(ApiInterface.class).getOrderList()
            .enqueue(new Callback<ResultResponse<OrderListResponse>>() {
                @Override
                public void onResponse(@NonNull Call<ResultResponse<OrderListResponse>> call,
                                       @NonNull Response<ResultResponse<OrderListResponse>> response) {
                    if (response.code() == 200) {
                        initRecyclerView(response.body().getData().getContent());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResultResponse<OrderListResponse>> call, @NonNull Throwable t) {
                    TextView message = findViewById(R.id.order_error_message);
                    message.setText(t.getMessage());
                    Log.e("onFailure", t.getMessage());
                }
            });
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void signOut() {
        PDAService.getClient(ApiInterface.class).logout();
        PDAService.setAuthToken(null);

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        HomeActivity.this.finish();
    }

    public void openOrderDialog(String orderNumber) {
        orderDialog = new OrderDialog(orderNumber);
        orderDialog.show(getSupportFragmentManager(), OrderDialog.TAG);
    }

    public void initRecyclerView(List<OrderResponse> orders) {
        recyclerView = findViewById(R.id.order_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderAdapter orderAdapter = new OrderAdapter(orders, item -> openOrderDialog(item.getOrderNumber()));
        recyclerView.setAdapter(orderAdapter);
    }
}