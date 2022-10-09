package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.*;
import static com.parasoft.demoapp.e2e.locators.OrderDetailLocators.*;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DESCRIPTION;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NEW_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_RECYCLER_VIEW;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_REFRESH;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.BaseTest;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;
import com.parasoft.demoapp.e2e.data.TestDataUtils;
import com.parasoft.demoapp.e2e.locators.OrderDetailLocators;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

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
    void test() throws Throwable {

    }

    @Test
    void testShouldRemoveNewLabelAfterClickClose() throws Throwable {
        getNewOrders().forEach(order -> {
            testClickNewOrderCloseOrCancelButton(order, ORDER_CLOSE_BUTTON);
        });
    }

    @Test
    void testShouldRemoveNewLabelAfterClickCancel() throws Throwable {
        getNewOrders().forEach(order -> {
            testClickNewOrderCloseOrCancelButton(order, ORDER_DISMISS_BUTTON);
        });
    }

    @Test
    void testShouldApproveOrderWhenNoComments() throws Throwable {

    }

    @Test
    void testShouldDenyOrderWhenNoComments() throws Throwable {

    }

    @Test
    void testShouldApproveOrderWhenHaveComments() throws Throwable {

    }

    @Test
    void testShouldDenyOrderWhenHaveComments() throws Throwable {

    }

    @Test
    void testShouldNotApproveOrderWhenCommentsAreTooLong() throws Throwable {

    }

    @Test
    void testShouldNotDenyOrderWhenCommentsAreTooLong() throws Throwable {

    }

    @Test
    void testShouldShowOrderDetailsCorrectly() throws Throwable {

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
        Optional<WebElement> orderNewStatusElemOptional = orderListItem.
                findElements(ORDER_NEW_STATUS).stream().findFirst();
        assertThat(orderNewStatusElemOptional.isPresent(), is(false));
    }
}
