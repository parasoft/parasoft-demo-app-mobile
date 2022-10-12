package com.parasoft.demoapp.e2e.locators;

import static com.parasoft.demoapp.e2e.common.TestUtils.byId;

import org.openqa.selenium.By;

public final class OrderDetailLocators {

    public static final By SETTING_DIALOG = byId("setting_dialog");

    public static final By ORDER_INFO_ERROR_MESSAGE = byId("order_info_error_message");

    public static final By ORDER_CLOSE_BUTTON = byId("order_close_button");

    public static final By ORDER_DIALOG_TITLE = byId("order_dialog_title");

    public static final By ORDER_SCROLL_VIEW = byId("order_scroll_view");

    public static final By ORDER_DIALOG_ORDER_DATE = byId("order_dialog_order_date");

    public static final By ORDER_DIALOG_ORDER_TIME = byId("order_dialog_order_time");

    public static final By ORDER_DIALOG_ORDER_STATUS = byId("order_dialog_order_status");

    public static final By COMMENTS = byId("comments");

    public static final By COMMENTS_DETAIL = byId("comments_detail");

    public static final By PURCHASER_NAME = byId("purchaser_name");

    public static final By LOCATION = byId("location");

    public static final By RECEIVER_NAME = byId("receiver_name");

    public static final By GPS_COORDINATES = byId("gps_coordinates");

    public static final By REQUESTED_ITEM_TOTAL_QUANTITY =
            byId("requested_item_total_quantity");

    public static final By ORDER_ITEMS_RECYCLER_VIEW = byId("order_items_recycler_view");

    public static final By DESCENDANT_ANDROID_VIEW_VIEW_GROUP = By.xpath("./descendant::android.view.ViewGroup");

    public static final By ORDER_ITEM_NAME = byId("order_item_name");

    public static final By ORDER_ITEM_QUANTITY = byId("order_item_quantity");

    public static final By INVOICE_TITLE = byId("invoice_title");

    public static final By INVOICE_NUMBER = byId("invoice_number");

    public static final By PURCHASE_ORDER_NUMBER = byId("purchase_order_number");

    public static final By ORDER_UPDATING_ERROR_MESSAGE = byId("order_updating_error_message");

    public static final By ORDER_RESPONSE_SPINNER = byId("order_response_spinner");

    public static final By ORDER_RESPONSE_VALUE = byId("order_response_value");

    public static final By COMMENTS_FIELD = byId("comments_field");

    public static final By ORDER_DISMISS_BUTTON = byId("order_dismiss_button");

    public static final By ORDER_SAVE_BUTTON = byId("order_save_button");

}
