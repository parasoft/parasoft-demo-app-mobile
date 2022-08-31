package com.parasoft.demoapp.component;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.R;

public class SettingDialog extends DialogFragment {

    public static String TAG = "SettingDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_dialog_layout, container, false);
        setClickEvent(view);

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

    public void setClickEvent(View view) {
        view.findViewById(R.id.dismiss_button).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            Toast.makeText(loginActivity, "TODO", Toast.LENGTH_SHORT).show();
        });
    }
}
