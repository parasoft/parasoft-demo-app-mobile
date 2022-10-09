package com.parasoft.demoapp.e2e.locators;

import org.openqa.selenium.By;

public final class OrderListLocators {

    private static final String ID_PREFIX = "com.parasoft.demoapp:id/";

    public static final By ORDER_REQUESTS_TITLE = By.id(ID_PREFIX + "order_requests_title");

    public static final By ORDER_REFRESH = By.id(ID_PREFIX + "order_refresh");

    public static final By DISPLAY_NO_ORDERS_INFO = By.id(ID_PREFIX + "display_no_orders_info");

    public static final By ORDER_RECYCLER_VIEW= By.id(ID_PREFIX + "order_recycler_view");

    public static final By DESCENDANT_ANDROID_VIEW_VIEW_GROUP = By.xpath("./descendant::android.view.ViewGroup");

    public static final By ORDER_DESCRIPTION = By.id(ID_PREFIX + "order_description");

    public static final By ORDER_NUMBER= By.id(ID_PREFIX + "order_number");

    public static final By ORDER_DETAIL_DATE= By.id(ID_PREFIX + "order_detail_date");

    public static final By ORDER_DETAIL_TIME= By.id(ID_PREFIX + "order_detail_time");

    public static final By ORDER_DETAIL_REQUESTED_BY= By.id(ID_PREFIX + "order_detail_requested_by");

    public static final By ORDER_NEW_STATUS= By.id(ID_PREFIX + "order_new_status");

    public static final By ORDER_STATUS= By.id(ID_PREFIX + "order_status");

    public static final By BOTTOM_BLANK = By.id(ID_PREFIX + "bottom_blank");

    public static final By PROGRESS_BAR = By.id(ID_PREFIX + "progress_bar");

}
