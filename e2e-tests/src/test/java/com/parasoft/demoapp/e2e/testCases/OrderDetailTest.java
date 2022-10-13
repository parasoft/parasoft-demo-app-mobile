package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.APPROVER_PASSWORD;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.APPROVER_USERNAME;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.createTestDataOrdersWithRandomPurchaser;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.getAllOrders;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.getLocalizedValue;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.getNewOrders;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.getOpenOrders;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.resetDatabase;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.COMMENTS;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.COMMENTS_DETAIL;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.COMMENTS_FIELD;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.DESCENDANT_ANDROID_VIEW_VIEW_GROUP;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.GPS_COORDINATES;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.INVOICE_NUMBER;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.INVOICE_TITLE;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.LOCATION;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_CLOSE_BUTTON;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_DIALOG_ORDER_DATE;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_DIALOG_ORDER_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_DIALOG_ORDER_TIME;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_DIALOG_TITLE;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_DISMISS_BUTTON;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_ITEMS_RECYCLER_VIEW;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_ITEM_NAME;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_ITEM_QUANTITY;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_RESPONSE_SPINNER;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_RESPONSE_VALUE;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_SAVE_BUTTON;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_SCROLL_VIEW;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.ORDER_UPDATING_ERROR_MESSAGE;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.PURCHASER_NAME;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.PURCHASE_ORDER_NUMBER;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.RECEIVER_NAME;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.REQUESTED_ITEM_TOTAL_QUANTITY;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.SETTING_DIALOG;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NEW_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;
import com.parasoft.demoapp.e2e.data.OrderItem;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import io.restassured.RestAssured;

class OrderDetailTest extends OrderBaseTest {

    private static final double ORDER_DETAIL_DIALOG_SCROLL_RATIO = 0.25;

    @BeforeEach
    void setUp() throws Throwable {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        resetDatabase();
        createTestDataOrdersWithRandomPurchaser();
        TestUtils.setBaseUrlAndlogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);
    }

    @AfterEach
    void tearDown() {
        afterClass();
        RestAssured.reset();
    }

    @Test
    void testShouldRemoveNewLabelAfterClickClose() {
        getNewOrders().stream().findFirst().ifPresent(order ->
                testClickNewOrderCloseOrCancelButton(order.getOrderNumber(), ORDER_CLOSE_BUTTON));
    }

    @Test
    void testShouldRemoveNewLabelAfterClickCancel() {
        getNewOrders().stream().findFirst().ifPresent(order ->
                testClickNewOrderCloseOrCancelButton(order.getOrderNumber(), ORDER_DISMISS_BUTTON));
    }

    @Test
    void testShouldApproveOrderWhenNoComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testSuccessfulApproveOrDenyOrder(order.getOrderNumber(), true, false));
    }

    @Test
    void testShouldApproveOrderWhenHaveComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testSuccessfulApproveOrDenyOrder(order.getOrderNumber(), true, true));
    }

    @Test
    void testShouldDenyOrderWhenNoComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testSuccessfulApproveOrDenyOrder(order.getOrderNumber(), false, false));
    }

    @Test
    void testShouldDenyOrderWhenHaveComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testSuccessfulApproveOrDenyOrder(order.getOrderNumber(), false, true));
    }
    
    @Test
    void testShouldNotApproveOrderWhenCommentsAreTooLong() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testFailedApproveOrDenyOrderWithTooLongComments(order.getOrderNumber(), true));
    }

    @Test
    void testShouldNotDenyOrderWhenCommentsAreTooLong() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testFailedApproveOrDenyOrderWithTooLongComments(order.getOrderNumber(), false));
    }

    @Test
    void testShouldShowOrderDetailsCorrectly() {
        getAllOrders().subList(0, 6).forEach(this::testCheckOrderDetailInfo);
    }

    private void testClickNewOrderCloseOrCancelButton(String orderNumber,
                                                      By closeOrCancelButtonLocator) {
        openOrderDetailFromOrderList(orderNumber);
        wait.until(ExpectedConditions.
                presenceOfElementLocated(closeOrCancelButtonLocator));
        WebElement closeOrCancelButton = driver.findElement(closeOrCancelButtonLocator);
        closeOrCancelButton.click();
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        assertThat(orderListItem.findElements(ORDER_NEW_STATUS).isEmpty(), is(true));
    }

    private void testSuccessfulApproveOrDenyOrder(String orderNumber, boolean isApprove,
                                                  boolean hasComments) {
        testApproveOrDenyOrder(orderNumber, isApprove, hasComments, false);
    }

    private void testFailedApproveOrDenyOrderWithTooLongComments(String orderNumber,
                                                                 boolean isApprove) {
        testApproveOrDenyOrder(orderNumber, isApprove, true, true);
    }

    private void testApproveOrDenyOrder(String orderNumber, boolean isApprove,
                                        boolean hasComments, boolean isCommentsTooLong) {
        openOrderDetailFromOrderList(orderNumber);
        wait.until(ExpectedConditions.elementToBeClickable(ORDER_RESPONSE_SPINNER));
        WebElement orderResponseSpinner = driver.findElement(ORDER_RESPONSE_SPINNER);
        orderResponseSpinner.click();
        wait.until(ExpectedConditions.numberOfElementsToBe(ORDER_RESPONSE_VALUE, 3));
        List<WebElement> orderResponseValues = driver.findElements(ORDER_RESPONSE_VALUE);
        final String responseValue = isApprove ? "Approve" : "Deny";
        orderResponseValues.stream().
                filter(orderResponseValue ->
                        StringUtils.equals(orderResponseValue.getText(), responseValue)).
                findFirst().ifPresent(WebElement::click);
        wait.until(ExpectedConditions.textToBe(ORDER_RESPONSE_VALUE, responseValue));

        final String comments;
        if (isCommentsTooLong) {
            comments = isApprove ?
                    "We'll send to this location as soon as possible.We'll send to this location as soon as possible.We'll send to this location as soon as possible.We'll send to this location as soon as possible.We'll send to this location as soon as possible.We'll send to this location as soon as possible." :
                    "We can’t send so many large backpacks to this location.We can’t send so many large backpacks to this location.We can’t send so many large backpacks to this location.We can’t send so many large backpacks to this location.We can’t send so many large backpacks to this location.";
        } else {
            comments = isApprove ?
                    "We'll send to this location as soon as possible." :
                    "We can’t send so many large backpacks to this location.";
        }

        if (hasComments) {
            WebElement commentsField = driver.findElement(COMMENTS_FIELD);
            commentsField.sendKeys(comments);
        }
        wait.until(ExpectedConditions.attributeToBe(ORDER_SAVE_BUTTON,
                "enabled", "true"));
        WebElement orderSaveButton = driver.findElement(ORDER_SAVE_BUTTON);
        orderSaveButton.click();

        if (isCommentsTooLong) {
            checkOrderInfoAfterFailedApproveOrDenyWithTooLongComments();
        } else {
            checkOrderInfoAfterSuccessfulApproveOrDeny(orderNumber, isApprove, hasComments, comments);
        }
    }

    private void checkOrderInfoAfterSuccessfulApproveOrDeny(String orderNumber, boolean isApprove,
                                             boolean hasComments, String expectedComments) {
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        final String expectedOrderStatus = isApprove ? Order.APPROVED_STATUS : Order.DENIED_STATUS;
        assertThat(orderListItem.findElement(ORDER_STATUS).getText(), is(expectedOrderStatus));

        orderListItem.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_SCROLL_VIEW));
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_DIALOG_ORDER_STATUS));
        assertThat(driver.findElement(ORDER_DIALOG_ORDER_STATUS).getText(), is(expectedOrderStatus));
        if (hasComments) {
            wait.until(ExpectedConditions.presenceOfElementLocated(COMMENTS_DETAIL));
            assertThat(driver.findElement(COMMENTS_DETAIL).getText(), is(expectedComments));
        } else {
            assertThat(driver.findElements(COMMENTS).isEmpty(), is(true));
        }
        checkResponseSectionNotExist();
    }

    private void checkOrderInfoAfterFailedApproveOrDenyWithTooLongComments() {
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_UPDATING_ERROR_MESSAGE));
        assertThat(driver.findElement(ORDER_UPDATING_ERROR_MESSAGE).getText(),
                is("Error updating order"));

        assertThat(driver.findElement(ORDER_DIALOG_ORDER_STATUS).getText(), is(Order.OPEN_STATUS));
        assertThat(driver.findElements(COMMENTS).isEmpty(), is(true));
        assertThat(driver.findElement(ORDER_SAVE_BUTTON).getAttribute("enabled"),
                is("true"));
    }

    private void openOrderDetailFromOrderList(String orderNumber) {
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        orderListItem.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(SETTING_DIALOG));
    }

    private void testCheckOrderDetailInfo(Order order) {
        openOrderDetailFromOrderList(order.getOrderNumber());
        checkOrderDetail(order);
        driver.findElement(ORDER_CLOSE_BUTTON).click();
    }

    private void checkOrderDetail(Order order) {
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_DIALOG_TITLE));
        assertThat(driver.findElement(ORDER_DIALOG_TITLE).getText(),
                is(String.format("Order request: %s", order.getUiOrderNumber())));
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_SCROLL_VIEW));
        WebElement orderScrollView = driver.findElement(ORDER_SCROLL_VIEW);
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_DIALOG_ORDER_DATE));
        assertThat(scrollToItem(ORDER_DIALOG_ORDER_DATE, orderScrollView).getText(),
                is(order.getOrderDetailDate(getDeviceTimeZone())));
        assertThat(scrollToItem(ORDER_DIALOG_ORDER_TIME, orderScrollView).getText(),
                is(order.getOrderDetailTime(getDeviceTimeZone())));
        assertThat(scrollToItem(ORDER_DIALOG_ORDER_STATUS, orderScrollView).getText(),
                is(order.getUiStatus()));
        if (order.hasComments()) {
            wait.until(ExpectedConditions.presenceOfElementLocated(COMMENTS_DETAIL));
            assertThat(driver.findElement(COMMENTS_DETAIL).getText(), is(order.getComments()));
        } else {
            assertThat(driver.findElements(COMMENTS).isEmpty(), is(true));
        }
        assertThat(scrollToItem(PURCHASER_NAME, orderScrollView).getText(),
                is(order.getRequestedBy()));
        assertThat(scrollToItem(LOCATION, orderScrollView).getText(),
                is(getLocalizedValue(order.getRegion())));
        assertThat(scrollToItem(RECEIVER_NAME, orderScrollView).getText(),
                is(order.getReceiverId()));
        assertThat(scrollToItem(GPS_COORDINATES, orderScrollView).getText(),
                is(order.getLocation()));

        assertThat(scrollToItem(REQUESTED_ITEM_TOTAL_QUANTITY, orderScrollView).getText(),
                is(order.getOrderItemTotalQuantity()));
        WebElement orderItemsRecyclerView = scrollToItem(ORDER_ITEMS_RECYCLER_VIEW, orderScrollView);
        order.getOrderItems().forEach(orderItem -> {
            assert orderItemsRecyclerView != null;
            checkOrderItem(orderItem, orderItemsRecyclerView);
        });

        assertThat(scrollToItem(INVOICE_NUMBER, orderScrollView).getText(),
                is(order.getEventId()));
        assertThat(scrollToItem(PURCHASE_ORDER_NUMBER, orderScrollView).getText(),
                is(order.getEventNumber()));

        if (order.isOpenOrder()) {
            checkResponseSectionInitialState();
        } else {
            checkResponseSectionNotExist();
        }
    }

    private void checkOrderItem(OrderItem orderItem, WebElement orderItemsRecyclerView) {
        List<WebElement> viewGroups = orderItemsRecyclerView.
                findElements(DESCENDANT_ANDROID_VIEW_VIEW_GROUP);
        for (WebElement viewGroup : viewGroups) {
            Optional<WebElement> orderItemNameElemOptional = viewGroup.
                    findElements(ORDER_ITEM_NAME).stream().findFirst();
            Optional<WebElement> orderItemQuantityElemOptional = viewGroup.
                    findElements(ORDER_ITEM_QUANTITY).stream().findFirst();
            if (orderItemNameElemOptional.isPresent() &&
                    orderItemQuantityElemOptional.isPresent() &&
                    StringUtils.equals(orderItemNameElemOptional.get().getText(),
                            orderItem.getName()) &&
                    StringUtils.equals(orderItemQuantityElemOptional.get().getText(),
                            orderItem.getUiQuantity())) {
                return;
            }
        }

        Optional<WebElement> invoiceTitleElemOptional =
                orderItemsRecyclerView.findElements(INVOICE_TITLE).stream().findFirst();
        if (invoiceTitleElemOptional.isPresent()) {
            fail(String.format("Can not find order item(%s) with quantity(%s).",
                    orderItem.getName(), orderItem.getQuantity()));
        } else {
            scroll(ScrollDirection.DOWN, ORDER_DETAIL_DIALOG_SCROLL_RATIO);
            checkOrderItem(orderItem, orderItemsRecyclerView);
        }
    }

    private WebElement scrollToItem(By locator, SearchContext context) {
        Optional<WebElement> itemOptional = context.findElements(locator).stream().findFirst();
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        }

        if (!context.findElements(PURCHASE_ORDER_NUMBER).isEmpty()) {
            fail("Can not find order detail item.");
            throw new RuntimeException();
        }

        scroll(ScrollDirection.DOWN, ORDER_DETAIL_DIALOG_SCROLL_RATIO);
        return scrollToItem(locator, context);
    }

    private void checkResponseSectionInitialState() {
        assertThat(driver.findElement(ORDER_RESPONSE_VALUE).getText(),
                is("Order response..."));
        assertThat(driver.findElement(COMMENTS_FIELD).getText(), is("Comments"));
        assertThat(driver.findElement(ORDER_DISMISS_BUTTON).
                getAttribute("enabled"), is("true"));
        assertThat(driver.findElement(ORDER_SAVE_BUTTON).
                getAttribute("enabled"), is("false"));
    }

    private void checkResponseSectionNotExist() {
        assertThat(driver.findElements(ORDER_RESPONSE_SPINNER).isEmpty(), is(true));
        assertThat(driver.findElements(COMMENTS_FIELD).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_DISMISS_BUTTON).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_SAVE_BUTTON).isEmpty(), is(true));
    }
}
