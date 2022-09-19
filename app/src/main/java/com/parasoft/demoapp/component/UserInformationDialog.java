package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfoResponse;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;
import java.util.Objects;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformationDialog extends DialogFragment {
    public static final String TAG = "UserInformationDialog";
    public static final int LOADING = 1;
    public static final int LOAD_ERROR = 2;
    public static final int LOADED = 3;
    public static final int NO_DATA = 4;

    private ProgressBar progressBar;
    private TableLayout userInformationTable;
    private TextView textDisplayArea;
    private TextView usernameMessage;
    private TextView passwordMessage;
    private Button closeButton;
    @Setter // Add Setter just for testing to replace it with a mock service.
    private PDAService pdaService = new PDAService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_information_dialog_layout, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        userInformationTable = view.findViewById(R.id.user_information_table);
        textDisplayArea = view.findViewById(R.id.textDisplayArea);
        usernameMessage = view.findViewById(R.id.username_value);
        passwordMessage = view.findViewById(R.id.password_value);
        closeButton = view.findViewById(R.id.user_information_close_button);

        loadContent(LOADING, null);
        closeButton.setOnClickListener(v -> dismiss());

        try {
            pdaService.getClient(ApiInterface.class).forgotPassword()
                .enqueue(new Callback<ResultResponse<List<ForgotPasswordUserInfoResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> call,
                                           @NonNull Response<ResultResponse<List<ForgotPasswordUserInfoResponse>>> response) {
                        if (response.code() != 200) {
                            loadContent(LOAD_ERROR, null);
                            return;
                        }

                        ResultResponse<List<ForgotPasswordUserInfoResponse>> resultResponse = response.body();
                        if (resultResponse == null || resultResponse.getData() == null) {
                            loadContent(NO_DATA, null);
                            return;
                        }

                        List<ForgotPasswordUserInfoResponse> userInfoList = resultResponse.getData();
                        ForgotPasswordUserInfoResponse approverUserInfo = null;
                        for (ForgotPasswordUserInfoResponse userInfo: userInfoList) {
                            if ("ROLE_APPROVER".equals(userInfo.getRoleName())) {
                                approverUserInfo = userInfo;
                                break;
                            }
                        }
                        if(approverUserInfo == null || !approverUserInfo.isHasPrimaryUser()) {
                            loadContent(NO_DATA, null);
                        } else {
                            loadContent(LOADED, approverUserInfo);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<List<ForgotPasswordUserInfoResponse>>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Forgot password request error", t);
                        loadContent(LOAD_ERROR, null);
                    }
                });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Base URL error", e);
            loadContent(LOAD_ERROR, null);
        }
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
        params.width = (int) (displayMetrics.widthPixels * 0.9);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        super.onStart();
    }

    private void loadContent(int loadStatus, @Nullable ForgotPasswordUserInfoResponse userInfo) {
        if(isAdded()) {
            switch (loadStatus) {
                case LOADING: // when loading
                    progressBar.setVisibility(View.VISIBLE);
                    userInformationTable.setVisibility(View.INVISIBLE);
                    textDisplayArea.setVisibility(View.INVISIBLE);
                    break;
                case LOAD_ERROR: // when failed to load
                    textDisplayArea.setText(getResources().getString(R.string.wrong_base_url));
                    textDisplayArea.setTextColor(getResources().getColor(R.color.error));
                    progressBar.setVisibility(View.INVISIBLE);
                    textDisplayArea.setVisibility(View.VISIBLE);
                    break;
                case LOADED: // when load completed with data
                    usernameMessage.setText(Objects.requireNonNull(userInfo).getPrimaryUserInfo().getUserName());
                    passwordMessage.setText(userInfo.getPrimaryUserInfo().getPassword());
                    progressBar.setVisibility(View.INVISIBLE);
                    userInformationTable.setVisibility(View.VISIBLE);
                    break;
                case NO_DATA: // when load completed but with no data
                    textDisplayArea.setText(getResources().getString(R.string.no_users_available));
                    textDisplayArea.setTextColor(getResources().getColor(R.color.dark_blue));
                    progressBar.setVisibility(View.INVISIBLE);
                    textDisplayArea.setVisibility(View.VISIBLE);
            }
        }
    }
}
