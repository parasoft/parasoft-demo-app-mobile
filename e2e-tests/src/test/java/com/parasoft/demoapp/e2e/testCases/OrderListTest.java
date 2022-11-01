package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.APPROVER_PASSWORD;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.APPROVER_USERNAME;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.createTestDataOrdersWithRandomPurchaser;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.getAllOrders;
import static com.parasoft.demoapp.e2e.data.TestDataUtils.resetDatabase;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.DISPLAY_NO_ORDERS_INFO;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DETAIL_DATE;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DETAIL_REQUESTED_BY;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DETAIL_TIME;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NEW_STATUS;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NUMBER;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_REQUESTS_TITLE;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import io.restassured.RestAssured;

class OrderListTest extends OrderBaseTest {

    @BeforeEach
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        resetDatabase();
    }

    @AfterEach
    public void tearDown() {
        afterClass();
        RestAssured.reset();
    }

    @Test
    void testShouldShowNewOrdersAfterSwipeRefresh() throws Throwable {
        TestUtils.setBaseUrlAndLogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);

        wait.until(ExpectedConditions.presenceOfElementLocated(ORDER_REQUESTS_TITLE));
        assertEquals("ORDER REQUESTS", driver.findElement(ORDER_REQUESTS_TITLE).getText());
        assertEquals("There are no order requests",
                driver.findElement(DISPLAY_NO_ORDERS_INFO).getText());

        createTestDataOrdersWithRandomPurchaser();

        swipe(0.5, 0.25, 0.5, 0.75, Duration.ofMillis(1000));
        checkOrderList();
    }

    @Test
    void testShouldShowExistingOrdersAfterLogin() throws Throwable {
        createTestDataOrdersWithRandomPurchaser();

        TestUtils.setBaseUrlAndLogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);
        checkOrderList();
    }

    private void checkOrderList() {
        waitForOrderListToBeDisplayed();
        getAllOrders().forEach(this::checkOrderListItem);
    }

    private void checkOrderListItem(Order order) {
        WebElement orderListItem = scrollToOrder(order.getOrderNumber());
        assertEquals(order.getUiOrderNumber(), orderListItem.findElement(ORDER_NUMBER).getText());
        assertEquals(order.getOrderDetailDate(getDeviceTimeZone()), orderListItem.
                findElement(ORDER_DETAIL_DATE).getText());
        assertEquals(order.getOrderDetailTime(getDeviceTimeZone()), orderListItem.
                findElement(ORDER_DETAIL_TIME).getText());
        assertEquals(order.getRequestedBy(), orderListItem.
                findElement(ORDER_DETAIL_REQUESTED_BY).getText());
        assertEquals(order.getUiStatus(), orderListItem.findElement(ORDER_STATUS).getText());
        assertThat(orderListItem.findElements(ORDER_NEW_STATUS).stream().findFirst().isPresent(),
                is(not(BooleanUtils.toBoolean(order.getReviewedByAPV()))));
    }
}
