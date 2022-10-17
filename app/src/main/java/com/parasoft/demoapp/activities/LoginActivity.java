package com.parasoft.demoapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.dialogs.BaseUrlSettingDialog;
import com.parasoft.demoapp.dialogs.UserInformationDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;
import com.parasoft.demoapp.util.CommonUIUtil;
import com.parasoft.demoapp.util.CommonUtil;

import lombok.Getter;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    private EditText usernameInput;
    private EditText passwordInput;
    private Button signInButton;
    private TextView errorMessage;
    private TextView forgotPasswordLink;
    private PDAService pdaService;
    private final BaseUrlSettingDialog baseUrlSettingDialog = new BaseUrlSettingDialog();
    private final UserInformationDialog userInformationDialog = new UserInformationDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pdaService = PDAService.Factory.getInstance();
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out);
        setContentView(R.layout.activity_login);
        initCustomActionBar();
        CommonUIUtil.initializedFooter(this);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in);
        errorMessage = findViewById(R.id.login_error_message);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);

        usernameInput.addTextChangedListener(new InputTextWatcher());
        passwordInput.addTextChangedListener(new InputTextWatcher());

        forgotPasswordLink.setOnClickListener(view -> openUserInformationModal());
        signInButton.setOnClickListener(view -> signIn());
        setElementEnabledStatus(signInButton, false);

        // Set default base url or existing base url
        PDAService.setBaseUrl(CommonUtil.getBaseUrl(this));
    }

    public void initCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View customActionBar = View.inflate(this, R.layout.login_title_layout, null);

        actionBar.setCustomView(customActionBar, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        Toolbar parent = (Toolbar) customActionBar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageButton settingButton = customActionBar.findViewById(R.id.setting_button);
        settingButton.setOnClickListener(view -> openSettingModal());
    }

    public void openUserInformationModal() {
        userInformationDialog.show(getSupportFragmentManager(), UserInformationDialog.TAG);
    }

    public void openSettingModal() {
        baseUrlSettingDialog.show(getSupportFragmentManager(), BaseUrlSettingDialog.TAG);
    }

    public void signIn() {
        setElementsEnabledStatus(false);
        errorMessage.setText("");

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        try {
            pdaService.getClient(ApiInterface.class).login(username, password)
                .enqueue(new Callback<ResultResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<Void>> call, @NonNull Response<ResultResponse<Void>> response) {
                        int code = response.code();
                        if(code == 200) {
                            PDAService.setAuthToken(Credentials.basic(username, password));
                            // Go to home page
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                            return;
                        } else if (code == 401) {
                            errorMessage.setText(getResources().getString(R.string.wrong_username_or_password));
                            Log.e(TAG, "Not authorized to login");
                        } else {
                            errorMessage.setText(getResources().getString(R.string.wrong_base_url));
                            Log.e(TAG, "Base URL error");
                        }
                        setElementsEnabledStatus(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<Void>> call, @NonNull Throwable t) {
                        errorMessage.setText(getResources().getString(R.string.wrong_base_url));
                        Log.e(TAG, "Error login", t);
                        setElementsEnabledStatus(true);
                    }
                });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Base URL error", e);
            errorMessage.setText(getResources().getString(R.string.wrong_base_url));
            setElementsEnabledStatus(true);
        }
    }

    private void setElementsEnabledStatus(boolean enabled) {
        setElementEnabledStatus(usernameInput, enabled);
        setElementEnabledStatus(passwordInput, enabled);
        setElementEnabledStatus(signInButton, enabled);
    }

    private void setElementEnabledStatus(TextView element, boolean enabled) {
        element.setEnabled(enabled);
        if(enabled) {
            if(element.equals(signInButton)) {
                element.setBackgroundColor(getResources().getColor(R.color.orange_button));
            } else {
                element.setBackground(AppCompatResources.getDrawable(this, R.drawable.input_background));
            }
            element.setTextColor(getResources().getColor(R.color.dark_blue));
        } else {
            if(element.equals(signInButton)) {
                element.setBackgroundColor(getResources().getColor(R.color.button_disabled));
            } else {
                element.setBackground(AppCompatResources.getDrawable(this, R.drawable.input_background_disabled));
            }
            element.setTextColor(getResources().getColor(R.color.button_text_disabled));
        }
    }

    private void changeSignInButtonEnabledStatus() {
        String username = usernameInput.getText().toString().trim();
        setElementEnabledStatus(signInButton, !TextUtils.isEmpty(username));
    }

    private class InputTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            changeSignInButtonEnabledStatus();
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}