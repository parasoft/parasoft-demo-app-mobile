package com.parasoft.demoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import com.parasoft.demoapp.component.SettingDialog;
import com.parasoft.demoapp.util.FooterUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initCustomActionBar();
        FooterUtil.setFooterInfo(this);
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
}