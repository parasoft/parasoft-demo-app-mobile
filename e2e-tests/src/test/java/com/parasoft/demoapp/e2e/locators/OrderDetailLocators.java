package com.parasoft.demoapp.e2e.locators;

import org.openqa.selenium.By;

public final class OrderDetailLocators {

    private static final String ID_PREFIX = "com.parasoft.demoapp:id/";

    public static final By SETTING_DIALOG = By.id(ID_PREFIX + "setting_dialog");

    public static final By ORDER_INFO_ERROR_MESSAGE = By.id(ID_PREFIX + "order_info_error_message");

    public static final By ORDER_CLOSE_BUTTON = By.id(ID_PREFIX + "order_close_button");

    public static final By ORDER_DIALOG_TITLE = By.id(ID_PREFIX + "order_dialog_title");

    public static final By ORDER_SCROLL_VIEW = By.id(ID_PREFIX + "order_scroll_view");

    public static final By ORDER_DIALOG_ORDER_DATE = By.id(ID_PREFIX + "order_dialog_order_date");

    public static final By ORDER_DIALOG_ORDER_TIME = By.id(ID_PREFIX + "order_dialog_order_time");

    public static final By ORDER_DIALOG_ORDER_STATUS = By.id(ID_PREFIX + "order_dialog_order_status");

    public static final By COMMENTS = By.id(ID_PREFIX + "comments");

    public static final By COMMENTS_DETAIL = By.id(ID_PREFIX + "comments_detail");

    public static final By PURCHASER_NAME = By.id(ID_PREFIX + "purchaser_name");

    public static final By LOCATION = By.id(ID_PREFIX + "location");

    public static final By RECEIVER_NAME = By.id(ID_PREFIX + "receiver_name");

    public static final By GPS_COORDINATES = By.id(ID_PREFIX + "gps_coordinates");

    public static final By REQUESTED_ITEM_TOTAL_QUANTITY =
            By.id(ID_PREFIX + "requested_item_total_quantity");

    public static final By ORDER_ITEMS_RECYCLER_VIEW = By.id(ID_PREFIX + "order_items_recycler_view");

    public static final By ORDER_ITEM_NAME = By.id(ID_PREFIX + "order_item_name");

    public static final By ORDER_ITEM_QUANTITY = By.id(ID_PREFIX + "order_item_quantity");

    public static final By INVOICE_NUMBER = By.id(ID_PREFIX + "invoice_number");

    public static final By PURCHASE_ORDER_NUMBER = By.id(ID_PREFIX + "purchase_order_number");

    public static final By ORDER_UPDATING_ERROR_MESSAGE = By.id(ID_PREFIX + "order_updating_error_message");

    public static final By ORDER_RESPONSE_SPINNER = By.id(ID_PREFIX + "order_response_spinner");

    public static final By ORDER_RESPONSE_VALUE = By.id(ID_PREFIX + "order_response_value");

    public static final By COMMENTS_FIELD = By.id(ID_PREFIX + "comments_field");

    public static final By ORDER_DISMISS_BUTTON = By.id(ID_PREFIX + "order_dismiss_button");

    public static final By ORDER_SAVE_BUTTON = By.id(ID_PREFIX + "order_save_button");
}
