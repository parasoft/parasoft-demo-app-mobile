package com.parasoft.demoapp.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.OrdersRelativeApis;
import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.ApiInterface;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;
import com.parasoft.demoapp.util.CommonUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class HomeActivityTest extends MockPDAService {

    @Before
    public void setUp() {
        resetMockedPDAService();
        OrdersRelativeApis.FakeData.resetOrderListResponse();
    }

    public void test_showHomeActivityTitle() {
        onView(ViewMatchers.withText(R.string.home_title)).check(matches(isDisplayed()));
        onView(withId(R.id.order_requests_title)).check(matches(isDisplayed()));
    }

    public void test_showErrorView(int errorStringId, ApiInterface apiInterface) {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(apiInterface);

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            test_showHomeActivityTitle();
            onView(withId(R.id.order_error_message)).check(matches(withText(errorStringId)));
            onView(withId(R.id.order_recycler_view)).check(matches(not(isDisplayed())));
            onView(withId(R.id.display_no_orders_info)).check(matches(not(isDisplayed())));
        }
    }

    public void test_getOrdersWithoutError() {
        onView(withId(R.id.order_error_message)).check(matches(not(isDisplayed())));
        onView(withId(R.id.order_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.display_no_orders_info)).check(matches(not(isDisplayed())));
        test_showHomeActivityTitle();
    }

    @Test
    public void test_getOrderList_with200Response_noOrder() {
        OrdersRelativeApis.FakeData.resetOrderListResponse();
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            test_showHomeActivityTitle();
            onView(withId(R.id.display_no_orders_info)).check(matches(withText(R.string.no_order_requests)));
            onView(withId(R.id.order_error_message)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_recycler_view)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void test_getOrderList_with400Response() {
        test_showErrorView(R.string.current_user_not_exist, OrdersRelativeApis.getOrderList_with400Response());
    }

    @Test
    public void test_getOrderList_with401Response() {
        test_showErrorView(R.string.no_authorization_to_get_order_list, OrdersRelativeApis.getOrderList_with401Response());
    }

    @Test
    public void test_getOrderList_with500Response() {
        test_showErrorView(R.string.orders_loading_error, OrdersRelativeApis.getOrderList_with500Response());
    }

    @Test
    public void test_getOrderList_onFailure() {
        test_showErrorView(R.string.orders_loading_error, OrdersRelativeApis.getOrderList_onFailure());
    }

    @Test
    public void test_signOut() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            test_showHomeActivityTitle();
            openContextualActionModeOverflowMenu();
            onView(withText(R.string.sign_out)).perform(click());
            onView(withText(R.string.app_title)).check(matches((isDisplayed())));
            onView(withText(R.string.home_title)).check(doesNotExist());
            onView(withId(R.id.order_requests_title)).check(doesNotExist());
            onView(withId(R.id.order_refresh)).check(doesNotExist());
        }
    }

    @Test
    public void test_swipeUpToLoadMoreOrder() throws InterruptedException {
        OrdersRelativeApis.FakeData.addMoreOrders(16);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            test_getOrdersWithoutError();
            String theLastOrderNumber = "#23-456-001";
            onView(allOf(withId(R.id.order_number), withText(theLastOrderNumber))).check(doesNotExist());

            onView(withId(R.id.order_recycler_view)).perform(swipeUp());
            Thread.sleep(1000);
            onView(allOf(withId(R.id.order_number), withText(theLastOrderNumber))).check(matches((isDisplayed())));
            onView(withId(R.id.order_recycler_view)).perform(swipeUp());
        }
    }

    @Test
    public void test_swipeDownRefresh() {
        OrdersRelativeApis.FakeData.addAnOrder("23-456-010", OrderStatus.PROCESSED, 1, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.order_number)).check(matches(withText("#23-456-010")));
            onView(withId(R.id.order_detail_date)).check(matches(withText(CommonUtil.getLocalDate("2022-09-26T08:00:00.000+00:00"))));
            onView(withId(R.id.order_detail_time)).check(matches(withText(CommonUtil.getLocalTime("2022-09-26T08:00:00.000+00:00"))));
            onView(withId(R.id.order_detail_requested_by)).check(matches(withText("purchaser")));
            onView(withId(R.id.order_status)).check(matches(withText(R.string.status_open)));
            onView(withId(R.id.order_new_status)).check(matches(withText(R.string.status_new)));
            onView(withText("#23-456-011")).check(doesNotExist());

            OrdersRelativeApis.FakeData.addAnOrder("23-456-011", OrderStatus.PROCESSED, 1, false);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

            onView(withId(R.id.order_recycler_view)).perform(swipeDown());
            onView(withText("#23-456-011")).check(matches(isDisplayed()));
            onView(withText("#23-456-010")).check(matches(isDisplayed()));
        }
    }
}