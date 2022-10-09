package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.data.TestDataUtils.*;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Lists;
import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.TestUtils;
import com.parasoft.demoapp.e2e.data.Order;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        TestUtils.setBaseUrlAndlogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);

        wait.until(ExpectedConditions.
                presenceOfElementLocated(ORDER_REQUESTS_TITLE));
        WebElement orderRequestsTitle = driver.findElement(ORDER_REQUESTS_TITLE);
        assertEquals("ORDER REQUESTS", orderRequestsTitle.getText());

        WebElement displayNoOrdersInfo = driver.
                findElement(DISPLAY_NO_ORDERS_INFO);
        assertEquals("There are no order requests", displayNoOrdersInfo.getText());

        List<Order> testDataOrders = createTestDataOrders();

        swipe(0.5, 0.25, 0.5, 0.75, Duration.ofMillis(1000));
        checkOrderList(testDataOrders);
    }

    @Test
    void testShouldShowExistingOrdersAfterLogin() throws Throwable {
        List<Order> testDataOrders = createTestDataOrders();

        TestUtils.setBaseUrlAndlogin(driver, wait, APPROVER_USERNAME, APPROVER_PASSWORD);
        checkOrderList(testDataOrders);
    }

    private void checkOrderList(List<Order> testDataOrders) {
        waitForOrderListToBeDisplayed();
        new ArrayList<>(Lists.reverse(testDataOrders)).forEach(this::checkOrder);
    }

    private void checkOrder(Order order) {
        WebElement orderViewGroupElem = scrollToOrder(order.getUiOrderNumber());
        if (orderViewGroupElem == null) {
            fail(String.format("Can not find order(%s) in the list.", order.getUiOrderNumber()));
        }
        checkOrderProperties(order, orderViewGroupElem);
    }

    private static void checkOrderProperties(Order order, WebElement orderViewGroupElem) {
        WebElement orderNumberElem = orderViewGroupElem.findElement(ORDER_NUMBER);
        assertEquals(order.getUiOrderNumber(), orderNumberElem.getText());
        WebElement orderDetailDateElem = orderViewGroupElem.
                findElement(ORDER_DETAIL_DATE);
        assertEquals(order.getOrderDetailDate(), orderDetailDateElem.getText());
        WebElement orderDetailTimeElem = orderViewGroupElem.
                findElement(ORDER_DETAIL_TIME);
        assertEquals(order.getOrderDetailTime(), orderDetailTimeElem.getText());
        WebElement orderDetailRequestedByElem = orderViewGroupElem.
                findElement(ORDER_DETAIL_REQUESTED_BY);
        assertEquals(order.getRequestedBy(), orderDetailRequestedByElem.getText());
        WebElement orderStatusElem = orderViewGroupElem.findElement(ORDER_STATUS);
        assertEquals(order.getUiStatus(), orderStatusElem.getText());
        Optional<WebElement> orderNewStatusElemOptional = orderViewGroupElem.
                findElements(ORDER_NEW_STATUS).stream().findFirst();
        assertThat(orderNewStatusElemOptional.isPresent(),
                is(not(BooleanUtils.toBoolean(order.getReviewedByAPV()))));
    }
}
