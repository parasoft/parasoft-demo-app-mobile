package com.parasoft.demoapp.e2e.locators;

import static com.parasoft.demoapp.e2e.common.TestUtils.byId;

import org.openqa.selenium.By;

public final class OrderListLocators {
    
    public static final By ORDER_REQUESTS_TITLE = byId("order_requests_title");

    public static final By ORDER_REFRESH = byId("order_refresh");

    public static final By DISPLAY_NO_ORDERS_INFO = byId("display_no_orders_info");

    public static final By ORDER_RECYCLER_VIEW= byId("order_recycler_view");

    public static final By DESCENDANT_ANDROID_VIEW_VIEW_GROUP = By.xpath("./descendant::android.view.ViewGroup");

    public static final By ORDER_DESCRIPTION = byId("order_description");

    public static final By ORDER_NUMBER= byId("order_number");

    public static final By ORDER_DETAIL_DATE= byId("order_detail_date");

    public static final By ORDER_DETAIL_TIME= byId("order_detail_time");

    public static final By ORDER_DETAIL_REQUESTED_BY= byId("order_detail_requested_by");

    public static final By ORDER_NEW_STATUS= byId("order_new_status");

    public static final By ORDER_STATUS= byId("order_status");

    public static final By BOTTOM_BLANK = byId("bottom_blank");

    public static final By PROGRESS_BAR = byId("progress_bar");

}
