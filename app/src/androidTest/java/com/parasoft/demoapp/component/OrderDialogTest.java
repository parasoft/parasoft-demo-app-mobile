package com.parasoft.demoapp.component;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.OrdersRelativeApis;
import com.parasoft.demoapp.HomeActivity;
import com.parasoft.demoapp.MockPDAService;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrderDialogTest extends MockPDAService {

    @Before
    public void setUp() {
        resetMockedPDAService();
        OrdersRelativeApis.FakeDate.resetOrderListResponse();
    }

    @Test
    public void test_orderDetailDialog() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeDate.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 1, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.return200Response_allRequest());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_open)));

            // Check close button
            onView(withId(R.id.order_close_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(doesNotExist());
            onView(withId(R.id.order_new_status)).check(matches(not(isDisplayed())));

            // Check cancel button
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));
            onView(withId(R.id.order_dismiss_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(doesNotExist());


            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));
            onView(withId(R.id.order_save_button)).check(matches(isNotEnabled()));

            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response)));
            onView(withId(R.id.order_response_spinner)).perform(click());
            onView(withText(R.string.order_response_approve)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response_approve)));

            onView(withId(R.id.order_save_button)).check(matches(isEnabled()));
            onView(withId(R.id.comments_field)).perform(typeText("A comment"), closeSoftKeyboard());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_approved)));

            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_approved)));
            onView(withId(R.id.order_save_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_dismiss_button)).check(matches(not(isDisplayed())));
        }
    }
}