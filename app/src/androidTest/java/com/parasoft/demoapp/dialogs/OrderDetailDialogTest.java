package com.parasoft.demoapp.dialogs;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.OrdersRelativeApis;
import com.parasoft.demoapp.activities.HomeActivity;
import com.parasoft.demoapp.activities.MockPDAService;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.util.CommonUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrderDetailDialogTest extends MockPDAService {

    @Before
    public void setUp() {
        resetMockedPDAService();
        OrdersRelativeApis.FakeData.resetOrderListResponse();
    }

    @Test
    public void test_openAndClose() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            // Open dialog and check order info
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_open)));
            onView(withId(R.id.order_dialog_order_date)).check(matches(withText(CommonUtil.getLocalDate("2022-09-26T08:00:00.000+00:00"))));
            onView(withId(R.id.order_dialog_order_time)).check(matches(withText(CommonUtil.getLocalTime("2022-09-26T08:00:00.000+00:00"))));
            onView(withId(R.id.comments_detail)).check(matches(not(isDisplayed())));
            onView(withId(R.id.purchaser_name)).check(matches(withText("purchaser")));
            onView(withId(R.id.location)).check(matches(withText("Localized location value")));
            onView(withId(R.id.receiver_name)).check(matches(withText(orderNumber + " - receiver name")));
            onView(withId(R.id.gps_coordinates)).check(matches(withText(orderNumber + " - 29.90° E, 54.41° N")));
            onView(withId(R.id.purchase_order_number)).perform(ViewActions.scrollTo());
            onView(withId(R.id.requested_item_total_quantity)).check(matches(withText("3")));
            onView(allOf(withId(R.id.order_item_name), withText("Item name 1"))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.order_item_quantity), withText("x1"))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.order_item_name), withText("Item name 2"))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.order_item_quantity), withText("x2"))).check(matches(isDisplayed()));

            // Check close button
            onView(withId(R.id.order_close_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(doesNotExist());
            onView(withId(R.id.order_new_status)).check(matches(not(isDisplayed())));

            // Check cancel button
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));
            onView(withId(R.id.order_dismiss_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(doesNotExist());

            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_approveOrder() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            // Open dialog and check order info
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_open)));

            // Update order info
            onView(withId(R.id.comments_detail)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_save_button)).check(matches(isNotEnabled()));
            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response)));
            onView(withId(R.id.order_response_spinner)).perform(click());
            onView(withText(R.string.order_response_approve)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response_approve)));
            onView(withId(R.id.order_save_button)).check(matches(isEnabled()));
            String comment = "A comment.";
            onView(withId(R.id.comments_field)).perform(typeText(comment), closeSoftKeyboard());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_approved)));

            // Check the update works
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_approved)));
            onView(withId(R.id.comments_detail)).check(matches(withText(comment)));
            onView(withId(R.id.order_save_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_dismiss_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_response_spinner)).check(matches(not(isDisplayed())));
            onView(withId(R.id.comments_field)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_save_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_dismiss_button)).check(matches(not(isDisplayed())));

            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_denyOrder() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            // Open dialog and check order info
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_open)));

            // Update order info
            onView(withId(R.id.order_response_spinner)).perform(click());
            onView(withText(R.string.order_response_deny)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response_deny)));
            onView(withId(R.id.order_save_button)).check(matches(isEnabled()));
            String comment = "A comment.";
            onView(withId(R.id.comments_field)).perform(typeText(comment), closeSoftKeyboard());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_denied)));

            // Check the update works
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_order_status)).check(matches(withText(R.string.status_denied)));
            onView(withId(R.id.comments_detail)).check(matches(withText(comment)));
            onView(withId(R.id.order_save_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_dismiss_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_response_spinner)).check(matches(not(isDisplayed())));
            onView(withId(R.id.comments_field)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_save_button)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_dismiss_button)).check(matches(not(isDisplayed())));

            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_getOrderDetails_negative() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            // Open dialog but 401 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getOrderDetails_with401Response());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_info_error_message)).check(matches(withText(R.string.no_authorization_to_get_order)));
            onView(withId(R.id.order_close_button)).perform(click());

            // Open dialog but 404 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getOrderDetails_with404Response());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_info_error_message)).check(matches(withText("Order: #23-456-001 not found")));
            onView(withId(R.id.order_close_button)).perform(click());


            // Open dialog but 500 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getOrderDetails_with500Response());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_info_error_message)).check(matches(withText(R.string.order_loading_error)));
            onView(withId(R.id.order_close_button)).perform(click());

            // Open dialog but on failure.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getOrderDetails_onFailure());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_info_error_message)).check(matches(withText(R.string.order_loading_error)));
            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_updateOrderDetails_negative() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            // Open dialog and check error message when 401.
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber)));

            // Prepare to update.
            onView(withId(R.id.order_response_spinner)).perform(click());
            onView(withText(R.string.order_response_approve)).inRoot(isPlatformPopup()).perform(click());
            onView(withId(R.id.order_response_spinner)).check(matches(withSpinnerText(R.string.order_response_approve)));

            // Comments length exceed 225.
            String comments = "The error message will display on the page if comments length exceeds 225, the length of this comments are 226." +
                             "The error message will display on the page if comments length exceeds 225, the length of this comments are 226.....";
            onView(withId(R.id.comments_field)).perform(typeText(comments), closeSoftKeyboard());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.over_length_comments)));

            comments = "Normal comments.";
            onView(withId(R.id.comments_field)).perform(clearText(), typeText(comments), closeSoftKeyboard());

            // Update order info but 400 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_with400Response());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.updating_order_request_error)));

            // Update order info but 401 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_with401Response());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.no_authorization_to_update_order)));

            // Update order info but 403 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_with403Response());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.no_permission_to_update_order)));


            // Update order info but 404 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_with404Response());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText("Order: #" + orderNumber + " not found")));

            // Update order info but 500 happens.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_with500Response());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.updating_order_error)));

            // Update order info but on failure.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.updateOrderDetails_onFailure());
            onView(withId(R.id.order_save_button)).perform(click());
            onView(withId(R.id.order_dialog_title)).check(matches(withSubstring(orderNumber))); // Do not close dialog if error happens
            onView(withId(R.id.order_updating_error_message)).check(matches(withText(R.string.unable_to_connect_to_server)));
            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_getImage_negative() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            // Open dialog and check order info when 404.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getImage_with404Response());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.order_close_button)).perform(click());
            // If failure happens on loading image, the broken image should display.
            // Currently, can not check the broken image displays on the page.
            // If this test is pass, it shows that there is no problem
            // with the code of handling the situation when loading image unsuccessfully.


            // Open dialog and check order info when on failure.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.getImage_onFailure());
            onView(withText("#" + orderNumber)).perform(click());
            // If failure happens on loading image, the broken image should display.
            // Currently, can not check the broken image displays on the page.
            // If this test is pass, it shows that there is no problem
            // with the code of handling the situation when loading image unsuccessfully.

            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }

    @Test
    public void test_localizedValue_negative() {
        String orderNumber = "23-456-001";
        OrdersRelativeApis.FakeData.addAnOrder(orderNumber, OrderStatus.SUBMITTED, 2, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(ViewMatchers.withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));

            // Open dialog and check error message when 404.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.localizedValue_with404Response());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.location)).check(matches(withText(R.string.location_loading_error)));
            onView(withId(R.id.order_close_button)).perform(click());

            // Open dialog and check error message when on failure.
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.localizedValue_onFailure());
            onView(withText("#" + orderNumber)).perform(click());
            onView(withId(R.id.location)).check(matches(withText(R.string.location_loading_error)));

            verify(mockedPdaService, atLeastOnce()).getClient(ApiInterface.class);
        }
    }
}