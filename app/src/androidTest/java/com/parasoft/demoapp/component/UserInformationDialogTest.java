package com.parasoft.demoapp.component;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.ForgotPasswordApi;
import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.MockPDAService;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserInformationDialogTest extends MockPDAService {

    @Before
    public void setUp() {
        resetMockedPDAService();
    }

    public void openUserInformationDialog() {
        onView(withId(R.id.forgot_password_link)).perform(click());

        onView(withId(R.id.user_information_dialog_title)).check(matches(withText(R.string.user_information)));
        onView(withId(R.id.user_information_close_button)).check(matches(withText(R.string.close)));
    }

    public void closeUserInformationDialog() {
        onView(withId(R.id.user_information_close_button)).perform(click());
    }

    @Test
    public void test_loadUserInfo_200() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPasswordApi.return200Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            openUserInformationDialog();

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_info_message)).check(matches(not(isDisplayed())));
            onView(withId(R.id.user_information_table)).check(matches(isDisplayed()));
            onView(withId(R.id.username_label)).check(matches(withText(R.string.user_name_label)));
            onView(withId(R.id.username_value)).check(matches(withText("fakeUsername")));
            onView(withId(R.id.password_label)).check(matches(withText(R.string.password_label)));
            onView(withId(R.id.password_value)).check(matches(withText("fakePassword")));
            onView(withId(R.id.user_info_message)).check(matches(withText("")));
            closeUserInformationDialog();
        }
    }

    @Test
    public void test_loadUserInfo_200_noUserInfo() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPasswordApi.return200ButNoUserInfoResponse());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            openUserInformationDialog();

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
            onView(withId(R.id.user_info_message)).check(matches(withText(R.string.no_users_available)));
            closeUserInformationDialog();
        }
    }

    @Test
    public void test_loadUserInfo_500() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPasswordApi.return500Response());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            openUserInformationDialog();

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
            onView(withId(R.id.user_info_message)).check(matches(withText(R.string.wrong_base_url)));
            closeUserInformationDialog();
        }
    }

    @Test
    public void test_loadUserInfo_onFailure() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPasswordApi.onFailure());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            openUserInformationDialog();

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
            onView(withId(R.id.user_info_message)).check(matches(withText(R.string.wrong_base_url)));
            closeUserInformationDialog();
        }
    }

    @Test
    public void test_loadUserInfo_exception() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenThrow(new IllegalArgumentException());

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            // When
            openUserInformationDialog();

            // Then
            verify(mockedPdaService, times(1)).getClient(ApiInterface.class);
            onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
            onView(withId(R.id.user_info_message)).check(matches(withText(R.string.wrong_base_url)));
            closeUserInformationDialog();
        }
    }
}