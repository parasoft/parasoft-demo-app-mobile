package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.*;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.*;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NEW_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_STATUS;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    @BeforeEach
    void setUp() throws Throwable {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        resetDatabase();
        createTestDataOrders();
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

    @Disabled
    @Test
    void testShouldShowOrderDetailsCorrectly() {
        getAllOrders().forEach(order -> {
            openOrderDetailFromOrderList(order.getOrderNumber());
        });
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
        assertThat(driver.findElements(ORDER_RESPONSE_SPINNER).isEmpty(), is(true));
        assertThat(driver.findElements(COMMENTS_FIELD).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_DISMISS_BUTTON).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_SAVE_BUTTON).isEmpty(), is(true));
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
    }

    private void testCheckOrderDetail() {

    }

    private WebElement scrollToItem(By locator, SearchContext context) {
        Optional<WebElement> itemOptional = context.findElements(locator).stream().findFirst();
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        }

        if (!context.findElements(PURCHASE_ORDER_NUMBER).isEmpty()) {
            fail(String.format("Can not find item(%s).", locator));
            return null;
        }

        scroll(ScrollDirection.DOWN, 0.5);
        return scrollToItem(locator, context);
    }
}
