package com.parasoft.demoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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

import com.parasoft.demoapp.component.SettingDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;
import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.parasoft.demoapp.util.FooterUtil;
import com.parasoft.demoapp.util.SettingsUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button signInButton;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initCustomActionBar();
        FooterUtil.setFooterInfo(this);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in);
        errorMessage = findViewById(R.id.login_error_message);

        usernameInput.addTextChangedListener(new InputTextWatcher());
        passwordInput.addTextChangedListener(new InputTextWatcher());
        signInButton.setOnClickListener(view -> signIn());
        setElementEnabledStatus(signInButton, false);

        // Set default base url or existing base url
        PDAService.setBaseUrl(SettingsUtil.getBaseUrl(this));
    }

    public void initCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View customActionBar = View.inflate(this, R.layout.title_layout, null);

        actionBar.setCustomView(customActionBar, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        Toolbar parent = (Toolbar) customActionBar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageButton settingButton = customActionBar.findViewById(R.id.settingButton);
        settingButton.setOnClickListener(view -> openSettingModal());
    }

    public void openSettingModal() {
        SettingDialog dialog = new SettingDialog();
        dialog.show(getSupportFragmentManager(), SettingDialog.TAG);
    }

    public void signIn() {
        setElementsEnabledStatus(false);
        errorMessage.setText("");

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        try {
            PDAService.getClient(ApiInterface.class).login(username, password)
                .enqueue(new Callback<ResultResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultResponse<Void>> call, @NonNull Response<ResultResponse<Void>> response) {
                        int code = response.code();
                        if(code == 200) {
                            PDAService.setAuthToken(Credentials.basic(username, password));
                            PDAService.refreshRetrofit();
                            // Go to home page
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else if(code == 401) {
                            errorMessage.setText(getResources().getString(R.string.wrong_username_or_password));
                        } else {
                            errorMessage.setText(getResources().getString(R.string.wrong_base_url));
                        }
                        setElementsEnabledStatus(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultResponse<Void>> call, @NonNull Throwable t) {
                        errorMessage.setText(getResources().getString(R.string.wrong_base_url));
                        Log.e("LoginActivity", "Login error", t);
                        setElementsEnabledStatus(true);
                    }
                });
        } catch (IllegalArgumentException e) {
            Log.e("LoginActivity", "Base URL error", e);
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
            element.setTextColor(getResources().getColor(R.color.dark_blue));
        } else {
            element.setTextColor(getResources().getColor(R.color.button_disabled));
        }
    }

    private void changeSignInButtonEnabledStatus() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        setElementEnabledStatus(signInButton, !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password));
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
}