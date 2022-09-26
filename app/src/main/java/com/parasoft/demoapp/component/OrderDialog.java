package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parasoft.demoapp.HomeActivity;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.request.OrderStatusRequest;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse.OrderItemInfo;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;
import com.parasoft.demoapp.util.CommonUtil;
import com.parasoft.demoapp.util.ImageUtil;
import com.parasoft.demoapp.util.CommonUIUtil;
import com.parasoft.demoapp.util.OrderItemAdapter;

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
    private LinearLayout comments;
    private TextView commentsDetail;
    private TextView orderStatus;
    private TextView orderSubmissionDate;
    private TextView orderSubmissionTime;
    private TextView purchaserName;
    private TextView location;
    private TextView receiverName;
    private TextView gpsCoordinates;
    private ImageView map;
    private TextView invoiceNumber;
    private TextView totalQuantity;
    private TextView purchaseOrderNumber;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private HomeActivity homeActivity;
    private RecyclerView recyclerView;
    private Spinner responseSpinner;
    private String responseValue;
    private View contentDivider;
    private EditText commentsField;

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
        comments = view.findViewById(R.id.comments);
        commentsDetail = view.findViewById(R.id.comments_detail);
        orderStatus = view.findViewById(R.id.order_status);
        orderSubmissionDate = view.findViewById(R.id.order_time_year);
        orderSubmissionTime = view.findViewById(R.id.order_time_hour);
        purchaserName = view.findViewById(R.id.purchaser_name);
        location = view.findViewById(R.id.location);
        receiverName = view.findViewById(R.id.receiver_name);
        gpsCoordinates = view.findViewById(R.id.gps_coordinates);
        map = view.findViewById(R.id.map);
        totalQuantity = view.findViewById(R.id.requested_item_total_quantity);
        invoiceNumber = view.findViewById(R.id.invoice_number);
        purchaseOrderNumber = view.findViewById(R.id.purchase_order_number);
        recyclerView = view.findViewById(R.id.order_items_recycler_view);
        commentsField = view.findViewById(R.id.comments_field);
        responseSpinner = view.findViewById(R.id.order_response_spinner);
        scrollView = view.findViewById(R.id.order_scroll_view);
        progressBar = view.findViewById(R.id.order_dialog_progressBar);
        contentDivider = view.findViewById(R.id.order_content_divider);

        homeActivity = (HomeActivity) getActivity();
        initSpinner();
        setClickEvent();
        showLoadingPage();
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = displayMetrics.widthPixels;
        params.height = (displayMetrics.heightPixels / 100) * 95; // Fix the cast violation of `(int) (displayMetrics.heightPixels * 0.95)`
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View view = getCurrentFocus();
                    if (view instanceof EditText) {
                        if (CommonUIUtil.isFocusInsideView(view, event)) {
                            CommonUIUtil.hideKeyboardForView(getContext(), view);
                        }
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
    }



    private void setClickEvent() {
        cancelButton.setOnClickListener(v -> closeAndRefresh());
        saveButton.setOnClickListener(v -> saveOrderDetails());
        closeButton.setOnClickListener(v -> closeAndRefresh());
    }

    private void closeAndRefresh() {
        dismiss();
        SwipeRefreshLayout ordersLoader = homeActivity.findViewById(R.id.order_refresh);
        ordersLoader.setRefreshing(true);
        homeActivity.loadOrderList(false);
    }

    private void getOrderDetails() {
        pdaService.getClient(ApiInterface.class).getOrderDetails(orderNumber)
            .enqueue(new Callback<ResultResponse<OrderResponse>>() {
                @Override
                public void onResponse(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Response<ResultResponse<OrderResponse>> response) {
                    int code = response.code();
                    if (code == 200) {
                        assert response.body() != null;
                        orderInfo = response.body().getData();
                        setOrderLayout();
                        showOrderPage();
                        if (!orderInfo.getReviewedByAPV()) {
                            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
                            orderStatusRequest.setReviewedByAPV(true);
                            updateOrderDetails(orderStatusRequest, false);
                        }
                    } else if (code == 404) {
                        String errMsg = getResources().getString(R.string.order_not_found, orderNumber);
                        showErrorPage(errMsg);
                        Log.e(TAG, errMsg);
                    } else {
                        String errMsg = getResources().getString(R.string.order_loading_error);
                        showErrorPage(errMsg);
                        Log.e(TAG, errMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Throwable t) {
                    if (getDialog() != null) {
                        showErrorPage(getResources().getString(R.string.order_loading_error));
                    }
                    Log.e(TAG, "Load order info error", t);
                }
            });
    }

    private void updateOrderDetails(OrderStatusRequest orderStatusRequest, boolean closeDialog) {
        pdaService.getClient(ApiInterface.class).updateOrderDetails(orderNumber, orderStatusRequest)
                .enqueue(new Callback<ResultResponse<OrderResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Response<ResultResponse<OrderResponse>> response) {
                        if(response.code() == 200) {
                            assert response.body() != null;
                            orderInfo = response.body().getData();
                            if (closeDialog) {
                                closeAndRefresh();
                            }
                        } else if (response.code() == 404) {
                            // TODO waiting for feedback on where to display error
                            enableSaveButton(true);
                            Log.e(TAG, "Order not found");
                        } else {
                            // TODO waiting for feedback on where to display error
                            enableSaveButton(true);
                            Log.e(TAG, "Comments are too long");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<OrderResponse>> call, @NonNull Throwable t) {
                        // TODO waiting for feedback on where to display error
                        enableSaveButton(true);
                        Log.e(TAG, "Update Order details failed", t);
                    }
                });
    }

    private void getLocation(String locationKey) {
        pdaService.getClient(ApiInterface.class)
            .localizedValue(CommonUtil.getLocalizedLanguage(getContext()), locationKey)
                .enqueue(new Callback<ResultResponse<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<String>> call, @NonNull Response<ResultResponse<String>> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            location.setText(response.body().getData());
                        } else {
                            showLocationError();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<String>> call, @NonNull Throwable t) {
                        showLocationError();
                        Log.e(TAG, "Load location error", t);
                    }
                });
    }

    private void setOrderLayout() {
        if (orderInfo.getComments() == null || orderInfo.getComments().length() == 0) {
            comments.setVisibility(View.GONE);
        } else {
            comments.setVisibility(View.VISIBLE);
            commentsDetail.setText(orderInfo.getComments());
        }
        String submissionDate = CommonUtil.convertToLocalTime(orderInfo.getSubmissionDate());
        if (submissionDate != null) {
            orderSubmissionDate.setText(submissionDate.substring(0, 10));
            orderSubmissionTime.setText(submissionDate.substring(11, 19));
        }
        orderStatus.setText(getStatus(orderInfo.getStatus().getStatus()));
        purchaserName.setText(orderInfo.getRequestedBy());
        receiverName.setText(orderInfo.getReceiverId());
        getLocation(orderInfo.getRegion());
        gpsCoordinates.setText(orderInfo.getLocation());
        ImageUtil.loadImage(map, orderInfo.getOrderImage());
        totalQuantity.setText(getTotalQuantity());
        invoiceNumber.setText(orderInfo.getEventId());
        purchaseOrderNumber.setText(orderInfo.getEventNumber());

        initOrderItemRecyclerView();
    }

    private String getStatus(String status) {
        switch (status) {
            case "Submitted":
                status = getResources().getString(R.string.status_open);
                break;
            case "Declined":
                status = getResources().getString(R.string.status_denied);
                orderStatus.setTextColor(getResources().getColor(R.color.light_black));
                break;
            case "Approved":
                status = getResources().getString(R.string.status_approved);
                orderStatus.setTextColor(getResources().getColor(R.color.light_black));
                break;
            default:
                status = "";
        }
        return status;
    }

    private void initOrderItemRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(homeActivity);
        recyclerView.setLayoutManager(layoutManager);
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(orderInfo.getOrderItems());
        recyclerView.setAdapter(orderItemAdapter);
    }

    private String getTotalQuantity() {
        Integer totalQuantity = 0;
        for (OrderItemInfo orderItem : orderInfo.getOrderItems()) {
            totalQuantity += orderItem.getQuantity();
        }
        return totalQuantity.toString();
    }

    private void showLocationError() {
        location.setText(getResources().getString(R.string.location_loading_error));
        location.setTextColor(getResources().getColor(R.color.error));
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
                    enableSaveButton(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        responseSpinner.setAdapter(adapter);
    }

    public void showLoadingPage() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        commentsField.setVisibility(View.GONE);
        responseSpinner.setVisibility(View.GONE);
        contentDivider.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    public void showOrderPage() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        String status = orderInfo.getStatus().getStatus();
        if ("Submitted".equals(status)) {
            commentsField.setVisibility(View.VISIBLE);
            responseSpinner.setVisibility(View.VISIBLE);
            contentDivider.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            commentsField.setVisibility(View.GONE);
            responseSpinner.setVisibility(View.GONE);
            contentDivider.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
        errorMessage.setVisibility(View.GONE);
    }

    public void showErrorPage(String errMsg) {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        commentsField.setVisibility(View.GONE);
        responseSpinner.setVisibility(View.GONE);
        contentDivider.setVisibility(View.GONE);
        errorMessage.setText(errMsg);
        errorMessage.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    private void saveOrderDetails() {
        enableSaveButton(false);
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        if (responseValue.equals("Deny")) {
            orderStatusRequest.setStatus(OrderStatus.DECLINED);
        } else {
            orderStatusRequest.setStatus(OrderStatus.APPROVED);
        }
        orderStatusRequest.setComments(commentsField.getText().toString());
        updateOrderDetails(orderStatusRequest, true);
    }

    private void enableSaveButton(boolean enable) {
        if (isAdded()){
            int textColor = enable ? getResources().getColor(R.color.dark_blue) : getResources().getColor(R.color.button_disabled);
            saveButton.setEnabled(enable);
            saveButton.setTextColor(textColor);
        }
    }
}
