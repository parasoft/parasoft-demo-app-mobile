package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.*;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.*;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NEW_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_STATUS;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import io.restassured.RestAssured;

class OrderDetailTest extends OrderBaseTest {

    @BeforeEach
    public void setUp() throws Throwable {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        resetDatabase();
        createTestDataOrders();
        TestUtils.setBaseUrlAndlogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);
    }

    @AfterEach
    public void tearDown() {
        afterClass();
        RestAssured.reset();
    }

    @Test
    void testShouldRemoveNewLabelAfterClickClose() {
        getNewOrders().stream().findFirst().ifPresent(order ->
                testClickNewOrderCloseOrCancelButton(order, ORDER_CLOSE_BUTTON));
    }

    @Test
    void testShouldRemoveNewLabelAfterClickCancel() {
        getNewOrders().stream().findFirst().ifPresent(order ->
                testClickNewOrderCloseOrCancelButton(order, ORDER_DISMISS_BUTTON));
    }

    @Test
    void testShouldApproveOrderWhenNoComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testApproveOrDenyOrderSuccessfully(order.getOrderNumber(), true, false));
    }

    @Test
    void testShouldApproveOrderWhenHaveComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testApproveOrDenyOrderSuccessfully(order.getOrderNumber(), true, true));
    }

    @Test
    void testShouldDenyOrderWhenNoComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testApproveOrDenyOrderSuccessfully(order.getOrderNumber(), false, false));
    }

    @Test
    void testShouldDenyOrderWhenHaveComments() {
        getOpenOrders().stream().findFirst().ifPresent(order ->
                testApproveOrDenyOrderSuccessfully(order.getOrderNumber(), false, true));
    }
    
    @Test
    void testShouldNotApproveOrderWhenCommentsAreTooLong() {

    }

    @Test
    void testShouldNotDenyOrderWhenCommentsAreTooLong() {

    }

    @Test
    void testShouldShowOrderDetailsCorrectly() {

    }

    private void testClickNewOrderCloseOrCancelButton(Order order, By closeOrCancelButtonLocator) {
        String orderNumber = order.getUiOrderNumber();
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        orderListItem.click();
        wait.until(ExpectedConditions.
                presenceOfElementLocated(closeOrCancelButtonLocator));
        WebElement closeOrCancelButton = driver.findElement(closeOrCancelButtonLocator);
        closeOrCancelButton.click();
        waitForOrderListToBeDisplayed();
        orderListItem = scrollToOrder(orderNumber);
        assertThat(orderListItem.findElements(ORDER_NEW_STATUS).isEmpty(), is(true));
    }

    private void testApproveOrDenyOrderSuccessfully(String orderNumber, boolean isApprove,
                                                    boolean hasComments) {
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        orderListItem.click();
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
        final String comments = isApprove ?
                "We'll send to this location as soon as possible." :
                "We can’t send so many large backpacks to this location.";
        if (hasComments) {
            WebElement commentsField = driver.findElement(COMMENTS_FIELD);
            commentsField.sendKeys(comments);
        }
        wait.until(ExpectedConditions.elementToBeClickable(ORDER_SAVE_BUTTON));
        WebElement orderSaveButton = driver.findElement(ORDER_SAVE_BUTTON);
        orderSaveButton.click();
        waitForOrderListToBeDisplayed();
        orderListItem = scrollToOrder(orderNumber);
        final String expectedOrderStatus = isApprove ? Order.APPROVED_STATUS : Order.DENIED_STATUS;
        WebElement orderStatus = orderListItem.findElement(ORDER_STATUS);
        assertThat(orderStatus.getText(), is(expectedOrderStatus));
        orderListItem.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_DIALOG_ORDER_STATUS));
        WebElement orderDialogOrderStatus = driver.findElement(ORDER_DIALOG_ORDER_STATUS);
        assertThat(orderDialogOrderStatus.getText(), is(expectedOrderStatus));
        if (hasComments) {
            wait.until(ExpectedConditions.presenceOfElementLocated(COMMENTS_DETAIL));
            assertThat(driver.findElement(COMMENTS_DETAIL).getText(), is(comments));
        } else {
            assertThat(driver.findElements(COMMENTS).isEmpty(), is(true));
        }
        assertThat(driver.findElements(ORDER_RESPONSE_SPINNER).isEmpty(), is(true));
        assertThat(driver.findElements(COMMENTS_FIELD).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_DISMISS_BUTTON).isEmpty(), is(true));
        assertThat(driver.findElements(ORDER_SAVE_BUTTON).isEmpty(), is(true));
    }

    private void testApproveOrDenyOrderFailed(String orderNumber, boolean isApprove,
                                                    boolean hasComments) {
        waitForOrderListToBeDisplayed();
        WebElement orderListItem = scrollToOrder(orderNumber);
        orderListItem.click();
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
        final String comments = isApprove ?
                "We'll send to this location as soon as possible." :
                "We can’t send so many large backpacks to this location.";
        if (hasComments) {
            WebElement commentsField = driver.findElement(COMMENTS_FIELD);
            commentsField.sendKeys(comments);
        }
        wait.until(ExpectedConditions.elementToBeClickable(ORDER_SAVE_BUTTON));
        WebElement orderSaveButton = driver.findElement(ORDER_SAVE_BUTTON);
        orderSaveButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_UPDATING_ERROR_MESSAGE));
        assertThat(driver.findElement(ORDER_UPDATING_ERROR_MESSAGE).getText(),
                is("Error updating order"));

//        waitForOrderListToBeDisplayed();
//        orderListItem = scrollToOrder(orderNumber);
//        final String expectedOrderStatus = isApprove ? Order.APPROVED_STATUS : Order.DENIED_STATUS;
//        WebElement orderStatus = orderListItem.findElement(ORDER_STATUS);
//        assertThat(orderStatus.getText(), is(expectedOrderStatus));
//        orderListItem.click();
//        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_DIALOG_ORDER_STATUS));
//        WebElement orderDialogOrderStatus = driver.findElement(ORDER_DIALOG_ORDER_STATUS);
//        assertThat(orderDialogOrderStatus.getText(), is(expectedOrderStatus));
//        if (hasComments) {
//            wait.until(ExpectedConditions.presenceOfElementLocated(COMMENTS_DETAIL));
//            assertThat(driver.findElement(COMMENTS_DETAIL).getText(), is(comments));
//        } else {
//            assertThat(driver.findElements(COMMENTS).isEmpty(), is(true));
//        }
//        assertThat(driver.findElements(ORDER_RESPONSE_SPINNER).isEmpty(), is(true));
//        assertThat(driver.findElements(COMMENTS_FIELD).isEmpty(), is(true));
//        assertThat(driver.findElements(ORDER_DISMISS_BUTTON).isEmpty(), is(true));
//        assertThat(driver.findElements(ORDER_SAVE_BUTTON).isEmpty(), is(true));
    }
}
