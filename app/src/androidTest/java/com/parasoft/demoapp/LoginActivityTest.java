package com.parasoft.demoapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.ForgotPasswordApi;
import com.parasoft.demoapp.FakeApiResponse.LoginApi;
import com.parasoft.demoapp.component.SettingDialog;
import com.parasoft.demoapp.component.UserInformationDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends MockPDAService {

    @Before
    public void setUp() {
        resetMockedPDAService();
    }

    @Test
    public void test_onCreate(){
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();
                TextView errorMessage = loginActivity.getErrorMessage();
                View actionbarView = Objects.requireNonNull(loginActivity.getSupportActionBar()).getCustomView();
                ImageButton settingButton = actionbarView.findViewById(R.id.setting_button);
                TextView titleText = actionbarView.findViewById(R.id.app_title);
                SettingDialog settingDialog = loginActivity.getSettingDialog();
                UserInformationDialog userInformationDialog = loginActivity.getUserInformationDialog();
                PDAService pdaService = loginActivity.getPdaService();

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
                assertEquals(titleText.getText(), loginActivity.getString(R.string.app_title));
                assertNotNull(settingButton);
                assertNotNull(settingDialog);
                assertNotNull(userInformationDialog);
                assertNotNull(pdaService);
                assertFalse(signInButton.isEnabled());
                assertEquals(loginActivity.getResources().getColor(R.color.button_text_disabled), signInButton.getCurrentTextColor());
            });
        }
    }

    @Test
    public void test_signInButtonStatus() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
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

    @Test
    public void test_signIn_200() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(LoginApi.return200Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();

                // When
                usernameInput.setText("purchaser");
                passwordInput.setText("password");
                signInButton.callOnClick();

                // Then
                verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
                assertTrue(loginActivity.isFinishing());
            });
        }
    }

    @Test
    public void test_signIn_401() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(LoginApi.return401Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();
                TextView errorMessage = loginActivity.getErrorMessage();

                // When
                usernameInput.setText("wrongname");
                passwordInput.setText("password");
                signInButton.callOnClick();

                // Then
                verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
                assertTrue(usernameInput.isEnabled());
                assertTrue(passwordInput.isEnabled());
                assertTrue(signInButton.isEnabled());
                assertEquals(loginActivity.getResources().getString(R.string.wrong_username_or_password), errorMessage.getText());
            });
        }
    }

    @Test
    public void test_signIn_500() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(LoginApi.return500Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();
                TextView errorMessage = loginActivity.getErrorMessage();

                // When
                usernameInput.setText("wrongname");
                passwordInput.setText("password");
                signInButton.callOnClick();

                // Then
                verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
                assertTrue(usernameInput.isEnabled());
                assertTrue(passwordInput.isEnabled());
                assertTrue(signInButton.isEnabled());
                assertEquals(loginActivity.getResources().getString(R.string.wrong_base_url), errorMessage.getText());
            });
        }
    }

    @Test
    public void test_signIn_onFailure() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(LoginApi.onFailure());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();
                TextView errorMessage = loginActivity.getErrorMessage();

                // When
                usernameInput.setText("wrongname");
                passwordInput.setText("password");
                signInButton.callOnClick();

                // Then
                verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
                assertTrue(usernameInput.isEnabled());
                assertTrue(passwordInput.isEnabled());
                assertTrue(signInButton.isEnabled());
                assertEquals(loginActivity.getResources().getString(R.string.wrong_base_url), errorMessage.getText());
            });
        }
    }

    @Test
    public void test_signIn_exception() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenThrow(new IllegalArgumentException());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(loginActivity -> {
                // Given
                Button signInButton = loginActivity.getSignInButton();
                EditText usernameInput = loginActivity.getUsernameInput();
                EditText passwordInput = loginActivity.getPasswordInput();
                TextView errorMessage = loginActivity.getErrorMessage();

                // When
                usernameInput.setText("approver");
                passwordInput.setText("password");
                signInButton.callOnClick();

                // Then
                verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
                assertTrue(usernameInput.isEnabled());
                assertTrue(passwordInput.isEnabled());
                assertTrue(signInButton.isEnabled());
                assertEquals(loginActivity.getResources().getString(R.string.wrong_base_url), errorMessage.getText());
            });
        }
    }

    @Test
    public void test_openSettingModal() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            onView(withId(R.id.setting_button)).perform(click());

            // Then
            onView(withId(R.id.setting_dialog)).check(matches(isDisplayed()));
            onView(withId(R.id.setting_dialog_title)).check(matches(withText(R.string.settings)));
        }
    }

    @Test
    public void test_openUserInformationModal() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPasswordApi.return200Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            onView(withId(R.id.forgot_password_link)).perform(click());

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_information_dialog_title)).check(matches(isDisplayed()));
            onView(withId(R.id.user_information_dialog_title)).check(matches(withText(R.string.user_information)));
        }
    }
}