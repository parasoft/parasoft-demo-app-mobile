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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.Login;
import com.parasoft.demoapp.component.SettingDialog;
import com.parasoft.demoapp.component.UserInformationDialog;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

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
        loginActivityScenario.close();
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
            ImageButton settingButton = actionbarView.findViewById(R.id.setting_button);
            TextView titleText = actionbarView.findViewById(R.id.display_title);
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

    @Test
    public void test_signIn_200() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            PDAService mockedPdaService = mock(PDAService.class);
            loginActivity.setPdaService(mockedPdaService);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(Login.return200Response());

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

    @Test
    public void test_signIn_401() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            PDAService mockedPdaService = mock(PDAService.class);
            loginActivity.setPdaService(mockedPdaService);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(Login.return401Response());

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

    @Test
    public void test_signIn_500() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            PDAService mockedPdaService = mock(PDAService.class);
            loginActivity.setPdaService(mockedPdaService);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(Login.return500Response());

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

    @Test
    public void test_signIn_onFailure() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            PDAService mockedPdaService = mock(PDAService.class);
            loginActivity.setPdaService(mockedPdaService);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(Login.onFailure());

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


    @Test
    public void test_signIn_exception() {
        loginActivityScenario.onActivity(loginActivity -> {
            // Given
            PDAService mockedPdaService = mock(PDAService.class);
            loginActivity.setPdaService(mockedPdaService);
            when(mockedPdaService.getClient(ApiInterface.class)).thenThrow(new IllegalArgumentException());

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

    @Test
    public void test_openSettingModal() {
        // When
        onView(withId(R.id.setting_button)).perform(click());

        // Then
        onView(withId(R.id.setting_dialog)).check(matches(isDisplayed()));
        onView(withId(R.id.setting_dialog_title)).check(matches(withText(R.string.settings)));
    }

    @Test
    public void test_openUserInformationModal() {
        // When
        onView(withId(R.id.forgot_password_link)).perform(click());

        // Then
        onView(withId(R.id.user_information_dialog_title)).check(matches(isDisplayed()));
        onView(withId(R.id.user_information_dialog_title)).check(matches(withText(R.string.user_information)));
    }
}