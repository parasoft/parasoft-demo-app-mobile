package com.parasoft.demoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in);
        errorMessage = findViewById(R.id.login_error_message);

        signInButton.setOnClickListener(view -> signIn());

        // Set default base url or existing base url
        PDAService.setBaseUrl(getBaseUrl());
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

        ImageButton settingButton = (ImageButton) customActionBar.findViewById(R.id.settingButton);
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
                            errorMessage.setText(getResources().getString(R.string.internal_error));
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
    }

    public String getBaseUrl() {
        // TODO: Integrate with PDA-997
        /*String baseUrl = SettingsUtil.getSetting(this, "baseURL");*/
        String baseUrl = null;
        if(baseUrl == null) {
            baseUrl = getResources().getString(R.string.default_url);
        }
        return baseUrl;
    }

    private void setElementsEnabledStatus(boolean enabled) {
        usernameInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);
        signInButton.setEnabled(enabled);
    }
}