package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.locators.OrderListLocators.BOTTOM_BLANK;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.DESCENDANT_ANDROID_VIEW_VIEW_GROUP;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DESCRIPTION;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DETAIL_REQUESTED_BY;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NUMBER;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_RECYCLER_VIEW;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.PROGRESS_BAR;

import static org.junit.jupiter.api.Assertions.fail;

import com.parasoft.demoapp.e2e.common.BaseTest;
import com.parasoft.demoapp.e2e.data.Order;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Optional;

public abstract class OrderBaseTest extends BaseTest {

    protected WebDriverWait wait;

    protected void waitForOrderListToBeDisplayed() {
        wait.until(ExpectedConditions.
                presenceOfElementLocated(ORDER_RECYCLER_VIEW));
        wait.until(ExpectedConditions.
                presenceOfElementLocated(ORDER_DESCRIPTION));
    }

    protected WebElement scrollToOrder(String orderNumber) {
        String orderNumberWithHashPrefix = StringUtils.prependIfMissing(orderNumber, Order.HASH_SIGN);
        WebElement orderRecyclerView = driver.findElement(ORDER_RECYCLER_VIEW);
        List<WebElement> viewGroups = orderRecyclerView.
                findElements(DESCENDANT_ANDROID_VIEW_VIEW_GROUP);

        for (WebElement viewGroup : viewGroups) {
            Optional<WebElement> orderNumberElemOptional = viewGroup.
                    findElements(ORDER_NUMBER).stream().findFirst();
            Optional<WebElement> orderDetailRequestedByElemOptional = viewGroup.
                    findElements(ORDER_DETAIL_REQUESTED_BY).stream().findFirst();
            if (orderNumberElemOptional.isPresent() &&
                    orderNumberWithHashPrefix.contentEquals(orderNumberElemOptional.get().getText()) &&
                    orderDetailRequestedByElemOptional.isPresent()) {
                return viewGroup;
            }
        }

        Optional<WebElement> bottomBlankElemOptional =
                orderRecyclerView.findElements(BOTTOM_BLANK).stream().findFirst();
        if (bottomBlankElemOptional.isPresent()) {
            fail(String.format("Can not find order(%s) in the list.", orderNumber));
            return null;
        } else {
            scroll(ScrollDirection.DOWN, 0.5);
            wait.until(ExpectedConditions.
                    invisibilityOfElementLocated(PROGRESS_BAR));
            return scrollToOrder(orderNumberWithHashPrefix);
        }
    }
}
