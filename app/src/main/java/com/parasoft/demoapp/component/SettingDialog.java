package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.util.SettingsUtil;

public class SettingDialog extends DialogFragment {
    private LoginActivity loginActivity;
    private EditText baseUrlInput;
    private Button cancelButton;
    private Button saveButton;
    private TextView errorMessage;
    private TextWatcher baseUrlTextWatcher = new BaseUrlTextWatcher();

    public static String TAG = "SettingDialog";
    public static final String WELL_FORMED_URL_REGEX = "^(https?)://([a-zA-Z0-9-_]+.?)*[a-zA-Z0-9-_]+((/[\\S]+)?/?)$";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_dialog_layout, container, false);
        baseUrlInput = view.findViewById(R.id.base_url_input);
        baseUrlInput.addTextChangedListener(baseUrlTextWatcher);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.dismiss_button);
        errorMessage = view.findViewById(R.id.base_url_error_message);
        loginActivity = (LoginActivity) getActivity();
        setClickEvent();
        fillBaseUrl();

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

    public void setClickEvent() {
        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            saveBaseUrl();
            dismiss();
            Toast.makeText(loginActivity, getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        });
    }

    public void saveBaseUrl() {
        String baseUrl = baseUrlInput.getText().toString();
        SettingsUtil.saveSetting(loginActivity, "baseUrl", baseUrl);
    }

    public void fillBaseUrl() {
        String baseUrl = SettingsUtil.getSetting(loginActivity, "baseUrl");
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = getResources().getString(R.string.default_url);
        }
        baseUrlInput.setText(baseUrl);
    }

    private class BaseUrlTextWatcher implements TextWatcher {
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
            String baseUrl = baseUrlInput.getText().toString();
            if (TextUtils.isEmpty(baseUrl) || !baseUrl.matches(WELL_FORMED_URL_REGEX)) {
                saveButton.setEnabled(false);
                saveButton.setTextColor(getResources().getColor(R.color.button_disabled));
                String baseUrlErrorMessage = "";
                if (TextUtils.isEmpty(baseUrl)) {
                    baseUrlErrorMessage = getResources().getString(R.string.base_url_must_not_be_empty);
                } else {
                    baseUrlErrorMessage = getResources().getString(R.string.invalid_url);
                }
                errorMessage.setText(baseUrlErrorMessage);
            } else {
                saveButton.setEnabled(true);
                saveButton.setTextColor(getResources().getColor(R.color.dark_blue));
                errorMessage.setText("");
            }
        }
    }
}
