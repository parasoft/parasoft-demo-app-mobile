package com.parasoft.demoapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.FakeApiResponse.OrdersRelativeApis;
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

    public void test_showTitle() {
        onView(withText(R.string.home_title)).check(matches(isDisplayed()));
        onView(withId(R.id.order_requests_title)).check(matches(isDisplayed()));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
    }

    public void test_showErrorView(int errId, ApiInterface apiInterface) {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(apiInterface);

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.order_error_message)).check(matches(withText(errId)));
            onView(withId(R.id.order_recycler_view)).check(matches(not(isDisplayed())));
            onView(withId(R.id.display_no_orders_info)).check(matches(not(isDisplayed())));
            test_showTitle();
        }
    }

    public void test_getOrdersWithoutError() {
        onView(withId(R.id.order_error_message)).check(matches(not(isDisplayed())));
        onView(withId(R.id.order_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.display_no_orders_info)).check(matches(not(isDisplayed())));
        test_showTitle();
    }

    @Test
    public void test_getOrderListWithoutData() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.display_no_orders_info)).check(matches(withText(R.string.no_order_requests)));
            onView(withId(R.id.order_error_message)).check(matches(not(isDisplayed())));
            onView(withId(R.id.order_recycler_view)).check(matches(not(isDisplayed())));
            test_showTitle();
        }
    }

    @Test
    public void test_getOrderList_with400Response() {
        test_showErrorView(R.string.current_user_not_exist, OrdersRelativeApis.returnOrderList_with400Response());
    }

    @Test
    public void test_getOrderList_with401Response() {
        test_showErrorView(R.string.no_authorization_to_get_order_list, OrdersRelativeApis.returnOrderList_with401Response());
    }

    @Test
    public void test_getOrderList_with500Response() {
        test_showErrorView(R.string.orders_loading_error, OrdersRelativeApis.returnOrderList_with500Response());
    }

    @Test
    public void test_getOrderList_onFailure() {
        test_showErrorView(R.string.orders_loading_error, OrdersRelativeApis.returnOrderList_onFailure());
    }

    @Test
    public void test_signOut() {
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.returnOrderList_with500Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.returnOrderList_logOut());
            openContextualActionModeOverflowMenu();
            onView(withText(R.string.sign_out)).perform(click());
            onView(withText(R.string.app_title)).check(matches((isDisplayed())));
            onView(withText(R.string.home_title)).check(doesNotExist());
            onView(withId(R.id.order_requests_title)).check(doesNotExist());
            onView(withId(R.id.order_refresh)).check(doesNotExist());
        }
    }

    @Test
    public void test_moreOrders() throws InterruptedException {
        OrdersRelativeApis.FakeData.addMoreOrders(16);
        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            test_getOrdersWithoutError();
            onView(withText("#23-456-001")).check(doesNotExist());
            String orderNumber;
            int orderStatus = 0;

            onView(withId(R.id.order_recycler_view)).perform(ViewActions.swipeUp());
            Thread.sleep(1000);
            for (int i = 16; i >= 1; i--) {
                orderNumber = "#23-456-0" + (i >= 10 ? "" : "0") + i;
                Thread.sleep(3000);
                onView(withText(orderNumber)).check(matches((isDisplayed())));
                onView(allOf(withText(CommonUtil.getLocalDate("2022-09-26T08:0" + i + ":00.000+00:00")), withText(orderNumber))).check(matches(isDisplayed()));
                onView(withText(CommonUtil.getLocalTime("2022-09-26T08:0" + i + ":00.000+00:00"))).check(matches(isDisplayed()));
                onView(allOf(withText("purchaser"), withText(orderNumber))).check(matches(isDisplayed()));
                onView(allOf(withText(R.string.status_new), withText(orderNumber))).check(matches(isDisplayed()));
                switch (i % 3) {
                    case 0:
                        orderStatus = R.string.status_open;
                        break;
                    case 1:
                        orderStatus = R.string.status_approved;
                        break;
                    case 2:
                        orderStatus = R.string.status_denied;
                        break;
                }
                onView(allOf(withText(orderStatus), withText(orderNumber))).check(matches(isDisplayed()));
            }
            onView(withId(R.id.order_recycler_view)).perform(ViewActions.swipeUp());
        }
    }

    @Test
    public void test_dropDownRefresh() {
        OrdersRelativeApis.FakeData.addAnOrder("23-456-010", OrderStatus.SUBMITTED, 1, false);

        when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.order_number)).check(matches(withText("#23-456-010")));
            onView(withText("#23-456-011")).check(doesNotExist());

            OrdersRelativeApis.FakeData.addAnOrder("23-456-011", OrderStatus.SUBMITTED, 1, false);
            when(mockedPdaService.getClient(ApiInterface.class)).thenReturn(OrdersRelativeApis.allRequests_with200Response());

            onView(withId(R.id.order_recycler_view)).perform(ViewActions.swipeDown());
            onView(withText("#23-456-011")).check(matches(isDisplayed()));
            onView(withText("#23-456-010")).check(matches(isDisplayed()));
        }
    }
}