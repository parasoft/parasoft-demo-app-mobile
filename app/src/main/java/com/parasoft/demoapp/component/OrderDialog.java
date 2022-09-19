package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.parasoft.demoapp.HomeActivity;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDialog extends DialogFragment {
    public static final String TAG = "OrderDialog";

    private final String orderNumber;
    private Button cancelButton;
    private Button saveButton;
    private ImageButton closeButton;
    private TextView errorMessage;
    private OrderResponse orderInfo;
    private PDAService pdaService;
    private Spinner responseSpinner;
    private String responseValue;
    private HomeActivity homeActivity;
    private EditText comments_field;

    public OrderDialog(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pdaService = new PDAService();
        View view = inflater.inflate(R.layout.order_dialog_layout, container, false);
        saveButton = view.findViewById(R.id.order_save_button);
        cancelButton = view.findViewById(R.id.order_dismiss_button);
        closeButton = view.findViewById(R.id.order_close_button);
        errorMessage = view.findViewById(R.id.order_info_error_message );
        comments_field = view.findViewById(R.id.comments_field);
        responseSpinner = view.findViewById(R.id.order_response_spinner);
        homeActivity = (HomeActivity) getActivity();
        initSpinner();
        setClickEvent();
        TextView orderDialogTitle = view.findViewById(R.id.order_dialog_title);
        orderDialogTitle.setText(getString(R.string.order_dialog_title, orderNumber));
        getOrderDetails();

        return view;
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = displayMetrics.widthPixels;

        params.height = (int) (displayMetrics.heightPixels * 0.95);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        super.onStart();
    }

    public void setClickEvent() {
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> dismiss());
        closeButton.setOnClickListener(v -> dismiss());
    }

    public void getOrderDetails() {
        errorMessage.setText("");
        pdaService.getClient(ApiInterface.class).orderDetails(orderNumber)
            .enqueue(new Callback<ResultResponse<OrderResponse>>() {
                @Override
                public void onResponse(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Response<ResultResponse<OrderResponse>> response) {
                    int code = response.code();
                    if(code == 200) {
                        orderInfo = response.body().getData();
                        if (!orderInfo.getReviewedByAPV()) {
                            updateOrderStatus(orderInfo);
                        }
                    } else if(code == 404) {
                        errorMessage.setText(getResources().getString(R.string.order_not_found, orderNumber));
                    }
                    else {
                        errorMessage.setText(getResources().getString(R.string.order_loading_error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Throwable t) {
                    errorMessage.setText(getResources().getString(R.string.order_loading_error));
                    Log.e("OrderDialog", "Load order info error", t);
                }
            });
    }

    public void updateOrderStatus(OrderResponse oldOrderInfo) {
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.setStatus(oldOrderInfo.getStatus().getStatus());
        orderStatusRequest.setReviewedByAPV(true);

        pdaService.getClient(ApiInterface.class).orderDetails(orderNumber, orderStatusRequest)
                .enqueue(new Callback<ResultResponse<OrderResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Response<ResultResponse<OrderResponse>> response) {
                        if(response.code() == 200) {
                            orderInfo = response.body().getData();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Throwable t) {
                        Log.e("OrderDialog", "Update Order status failed", t);
                    }
                });
    }

    public void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_dropdown_item_layout, R.id.order_response_value, getResources().getStringArray(R.array.order_response)){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        responseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(i);
                if (i > 0) {
                    responseValue = selectedItemText;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        responseSpinner.setAdapter(adapter);
    }
}
