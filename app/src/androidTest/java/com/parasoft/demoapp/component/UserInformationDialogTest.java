package com.parasoft.demoapp.component;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.ForgotPassword;
import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.PDAService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserInformationDialogTest {

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule = new IntentsTestRule<>(LoginActivity.class);

    public void openUserInformationDialog() {
        onView(withId(R.id.forgot_password_link)).perform(click());

        onView(withId(R.id.user_information_dialog_title)).check(matches(withText(R.string.user_information)));
        onView(withId(R.id.close_button)).check(matches(withText(R.string.close)));
    }

    public void closeUserInformationDialog() {
        onView(withId(R.id.close_button)).perform(click());
    }

    @Test
    public void onCreateView_loadUserInfo_200() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPassword.return200Response());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.textDisplayArea)).check(matches(not(isDisplayed())));
        onView(withId(R.id.user_information_table)).check(matches(isDisplayed()));
        onView(withId(R.id.username_label)).check(matches(withText(R.string.user_name_label)));
        onView(withId(R.id.username_value)).check(matches(withText("fakeUsername")));
        onView(withId(R.id.password_label)).check(matches(withText(R.string.password_label)));
        onView(withId(R.id.password_value)).check(matches(withText("fakePassword")));
        onView(withId(R.id.textDisplayArea)).check(matches(withText("")));
        closeUserInformationDialog();
    }

    @Test
    public void onCreateView_loadUserInfo_200_noData() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPassword.return200ButNoDataResponse());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textDisplayArea)).check(matches(withText(R.string.no_users_available)));
        closeUserInformationDialog();
    }

    @Test
    public void onCreateView_loadUserInfo_200_noUserInfo() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPassword.return200ButNoUserInfoResponse());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textDisplayArea)).check(matches(withText(R.string.no_users_available)));
        closeUserInformationDialog();
    }

    @Test
    public void onCreateView_loadUserInfo_500() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPassword.return500Response());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textDisplayArea)).check(matches(withText(R.string.wrong_base_url)));
        closeUserInformationDialog();
    }

    @Test
    public void onCreateView_loadUserInfo_onFailure() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(ForgotPassword.onFailure());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textDisplayArea)).check(matches(withText(R.string.wrong_base_url)));
        closeUserInformationDialog();
    }

    @Test
    public void onCreateView_loadUserInfo_exception() {
        // Given
        PDAService mockedPdaService = mock(PDAService.class);
        when(mockedPdaService.getClient(ApiInterface.class)).thenThrow(new IllegalArgumentException());
        intentsTestRule.getActivity().getUserInformationDialog().setPdaService(mockedPdaService);

        // When
        openUserInformationDialog();

        // Then
        onView(withId(R.id.user_information_table)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textDisplayArea)).check(matches(withText(R.string.wrong_base_url)));
        closeUserInformationDialog();
    }
}