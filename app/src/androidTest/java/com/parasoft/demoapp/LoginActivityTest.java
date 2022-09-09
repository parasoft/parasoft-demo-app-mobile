package com.parasoft.demoapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest {

    @Test
    public void test000_onCreate() {
        try(ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.login_ui));
                assertNotNull(activity.findViewById(R.id.footer));
                assertNotNull(activity.getUsernameInput());
                assertNotNull(activity.getPasswordInput());
                assertNotNull(activity.getSignInButton());
                assertNotNull(activity.getErrorMessage());
                assertEquals(false, activity.getSignInButton().isEnabled());
            });
        }
    }

    @Test
    public void test001_initCustomActionBar() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            activity.initCustomActionBar();
            assertNotNull(activity.getSupportActionBar());
            View actionbarView = activity.getSupportActionBar().getCustomView();
            TextView textView = actionbarView.findViewById(R.id.display_title);
            ImageButton button = actionbarView.findViewById(R.id.settingButton);
            assertEquals(textView.getText(), activity.getString(R.string.app_title));
            assertNotNull(button);
        });
    }

    @Test
    public void test002_SignInButtonStatus() {
        try(ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                Button button = activity.findViewById(R.id.sign_in);
                EditText usernameInput = activity.findViewById(R.id.username);
                EditText passwordInput = activity.findViewById(R.id.password);
                assertEquals(false, button.isEnabled());
                assertEquals(activity.getResources().getColor(R.color.button_text_disabled), button.getCurrentTextColor());

                usernameInput.setText("testUser");
                passwordInput.setText("testPassword");
                assertEquals(true, button.isEnabled());
                assertEquals(activity.getResources().getColor(R.color.dark_blue), button.getCurrentTextColor());
            });
        }
    }
}