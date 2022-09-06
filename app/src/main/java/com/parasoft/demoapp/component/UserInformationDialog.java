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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.ForgotPasswordUserInfo;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformationDialog extends DialogFragment {
    public static String TAG = "UserInformationDialog";
    public static final int LOADING = 1;
    public static final int LOADERROR = 2;
    public static final int LOADED = 3;
    public static final int NODATA = 4;
    private ProgressBar progressBar;
    private TableLayout userInformationTable;
    private TextView textDisplayArea;
    private TextView usernameMessage;
    private TextView passwordMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_information_dialog_layout, container, false);
        setClickEvent(view);

        progressBar = view.findViewById(R.id.progressBar);
        userInformationTable = view.findViewById(R.id.user_information_table);
        textDisplayArea = view.findViewById(R.id.textDisplayArea);
        usernameMessage = view.findViewById(R.id.username_value);
        passwordMessage = view.findViewById(R.id.password_value);

        loadContent(LOADING, null);

        try {
            PDAService.getClient(ApiInterface.class).forgotPassword()
                    .enqueue(new Callback<ResultResponse<List<ForgotPasswordUserInfo>>>() {
                        @Override
                        public void onResponse(Call<ResultResponse<List<ForgotPasswordUserInfo>>> call, Response<ResultResponse<List<ForgotPasswordUserInfo>>> response) {
                            if(response.isSuccessful()) {
                                ResultResponse<List<ForgotPasswordUserInfo>> result = response.body();
                                if (result == null) return;
                                List<ForgotPasswordUserInfo> userInfoList = result.getData();
                                if (userInfoList == null) return;
                                for (ForgotPasswordUserInfo userInfo: userInfoList) {
                                    if (userInfo.getRoleName().equals("ROLE_APPROVER")) {
                                        if (userInfo.isHasPrimaryUser()) {
                                            loadContent(LOADED, userInfo);
                                        } else {
                                            loadContent(NODATA, null);
                                        }
                                    }
                                }
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultResponse<List<ForgotPasswordUserInfo>>> call, Throwable t) {
                            Log.e("UserInformationDialog", "Forgot password request error", t);
                            loadContent(LOADERROR, null);
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e("UserInformationDialog", "Base URL error", e);
            loadContent(LOADERROR, null);
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
        window.setAttributes((WindowManager.LayoutParams) params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        super.onStart();
    }

    private void setClickEvent(View view) {
        view.findViewById(R.id.close_button).setOnClickListener(v -> dismiss());
    }

    private void loadContent(int loading, ForgotPasswordUserInfo userInfo) {
        switch (loading) {
            case 1: // when loading
                progressBar.setVisibility(View.VISIBLE);
                userInformationTable.setVisibility(View.INVISIBLE);
                textDisplayArea.setVisibility(View.INVISIBLE);
                break;
            case 2: // when failed to load
                if (isAdded()) {
                    textDisplayArea.setText(getResources().getString(R.string.wrong_base_url));
                    textDisplayArea.setTextColor(getResources().getColor(R.color.error));
                    progressBar.setVisibility(View.INVISIBLE);
                    textDisplayArea.setVisibility(View.VISIBLE);
                }
                break;
            case 3: // when load completed with data
                usernameMessage.setText(userInfo.getPrimaryUserInfo().getUserName());
                passwordMessage.setText(userInfo.getPrimaryUserInfo().getPassword());
                progressBar.setVisibility(View.INVISIBLE);
                userInformationTable.setVisibility(View.VISIBLE);
                break;
            case 4: // when load completed but with no data
                if (isAdded()) {
                    textDisplayArea.setText(getResources().getString(R.string.no_user_with_this_role));
                    textDisplayArea.setTextColor(getResources().getColor(R.color.dark_blue));
                    progressBar.setVisibility(View.INVISIBLE);
                    userInformationTable.setVisibility(View.VISIBLE);
                }
        }
    }
}
