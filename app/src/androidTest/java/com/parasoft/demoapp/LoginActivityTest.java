package com.parasoft.demoapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    private ActivityScenario<LoginActivity> loginActivityScenario;

    @Before
    public void setUp() {
        loginActivityScenario = loginActivityScenarioRule.getScenario();
    }

    @After
    public void tearDown() {
        loginActivityScenario = null;
    }

    @Test
    public void test_onCreate(){
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            Button signInButton = loginActivity.getSignInButton();
            EditText usernameInput = loginActivity.getUsernameInput();
            EditText passwordInput = loginActivity.getPasswordInput();
            TextView errorMessage = loginActivity.getErrorMessage();
            View actionbarView = Objects.requireNonNull(loginActivity.getSupportActionBar()).getCustomView();
            ImageButton settingButton = actionbarView.findViewById(R.id.settingButton);
            TextView titleTest = actionbarView.findViewById(R.id.display_title);

            // Then
            assertNotNull(loginActivity.findViewById(R.id.login_ui));
            assertNotNull(loginActivity.findViewById(R.id.footer));
            assertNotNull(usernameInput);
            assertNotNull(passwordInput);
            assertNotNull(signInButton);
            assertNotNull(errorMessage);
            assertEquals("", errorMessage.getText());
            assertFalse(signInButton.isEnabled());
            assertNotNull(loginActivity.getSupportActionBar());
            assertEquals(titleTest.getText(), loginActivity.getString(R.string.app_title));
            assertNotNull(settingButton);
            assertFalse(signInButton.isEnabled());
            assertEquals(loginActivity.getResources().getColor(R.color.button_text_disabled), signInButton.getCurrentTextColor());
        });
    }

    @Test
    public void test_signInButtonStatus() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            Button signInButton = loginActivity.getSignInButton();
            EditText usernameInput = loginActivity.getUsernameInput();
            EditText passwordInput = loginActivity.getPasswordInput();

            // Then
            assertFalse(signInButton.isEnabled());
            assertEquals(loginActivity.getResources().getColor(R.color.button_text_disabled), signInButton.getCurrentTextColor());

            // When
            usernameInput.setText("testUser");

            // Then
            assertTrue(signInButton.isEnabled());
            assertEquals(loginActivity.getResources().getColor(R.color.dark_blue), signInButton.getCurrentTextColor());

            // When
            usernameInput.setText("");
            passwordInput.setText("password");

            // Then
            assertFalse(signInButton.isEnabled());
            assertEquals(loginActivity.getResources().getColor(R.color.button_text_disabled), signInButton.getCurrentTextColor());
        });
    }
}