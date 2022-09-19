package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.parasoft.demoapp.util.SettingsUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    private TextView orderTimeYear;
    private TextView orderTimeHour;
    private TextView purchaserName;
    private TextView location;
    private TextView receiverName;
    private TextView gpsCoordinates;
    private ImageView map;
    private HomeActivity homeActivity;
    private final Handler handler = new Handler(msg -> {
        map.setImageBitmap((Bitmap) msg.obj);
        return true;
    });

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
        orderTimeYear = view.findViewById(R.id.order_time_year);
        orderTimeHour = view.findViewById(R.id.order_time_hour);
        purchaserName = view.findViewById(R.id.purchaser_name);
        location = view.findViewById(R.id.location);
        receiverName = view.findViewById(R.id.receiver_name);
        gpsCoordinates = view.findViewById(R.id.gps_coordinates);
        map = view.findViewById(R.id.map);
        homeActivity = (HomeActivity) getActivity();
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
                        setOrderLayout();
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

    public void setOrderLayout() {
        if (orderInfo.getComments() == null || orderInfo.getComments().length() == 0) {
            comments.setVisibility(View.GONE);
        } else {
            comments.setVisibility(View.VISIBLE);
            commentsDetail.setText(orderInfo.getComments());
            if(commentsDetail.getLineCount() == 1) {
                commentsDetail.setGravity(Gravity.END);
            } else {
                commentsDetail.setGravity(Gravity.START);
            }
        }
        orderTimeYear.setText(orderInfo.getSubmissionDate().substring(0, 10));
        orderTimeHour.setText(orderInfo.getSubmissionDate().substring(11, 19));
        orderStatus.setText(getStatus(orderInfo.getStatus().getStatus()));
        purchaserName.setText(orderInfo.getRequestedBy());
        location.setText(getRegion(orderInfo.getRegion()));
        receiverName.setText(orderInfo.getReceiverId());
        gpsCoordinates.setText(orderInfo.getLocation());
        new Thread(() -> {
            Message message = new Message();
            message.obj = getImage(orderInfo.getOrderImage());
            handler.sendMessage(message);
        }).start();
    }

    public String getRegion(String region) {
        switch (region) {
            case "LOCATION_1" :
                region = getResources().getString(R.string.location_1);
                break;
            case "LOCATION_2" :
                region = getResources().getString(R.string.location_2);
                break;
            case "LOCATION_3" :
                region = getResources().getString(R.string.location_3);
                break;
            case "LOCATION_4" :
                region = getResources().getString(R.string.location_4);
                break;
            case "LOCATION_5" :
                region = getResources().getString(R.string.location_5);
                break;
            case "LOCATION_6" :
                region = getResources().getString(R.string.location_6);
                break;
            case "LOCATION_7" :
                region = getResources().getString(R.string.location_7);
                break;
            case "LOCATION_8" :
                region = getResources().getString(R.string.location_8);
                break;
        }
        return region;
    }

    public String getStatus(String status) {
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
        }
        return status;
    }

    public Bitmap getImage(String orderImage) {
        Bitmap bmp = null;
        String imageUrl = SettingsUtil.getBaseUrl(homeActivity) + orderImage;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
