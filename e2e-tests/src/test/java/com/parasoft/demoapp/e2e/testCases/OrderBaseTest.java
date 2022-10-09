package com.parasoft.demoapp.e2e.testCases;

import static com.parasoft.demoapp.e2e.locators.OrderListLocators.BOTTOM_BLANK;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.DESCENDANT_ANDROID_VIEW_VIEW_GROUP;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DESCRIPTION;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_DETAIL_REQUESTED_BY;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_NUMBER;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.ORDER_RECYCLER_VIEW;
import static com.parasoft.demoapp.e2e.locators.OrderListLocators.PROGRESS_BAR;

import com.parasoft.demoapp.e2e.common.BaseTest;

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
        WebElement orderRecyclerView = driver.findElement(ORDER_RECYCLER_VIEW);
        List<WebElement> viewGroups = orderRecyclerView.
                findElements(DESCENDANT_ANDROID_VIEW_VIEW_GROUP);

        for (WebElement viewGroup : viewGroups) {
            Optional<WebElement> orderNumberElemOptional = viewGroup.
                    findElements(ORDER_NUMBER).stream().findFirst();
            Optional<WebElement> orderDetailRequestedByElemOptional = viewGroup.
                    findElements(ORDER_DETAIL_REQUESTED_BY).stream().findFirst();
            if (orderNumberElemOptional.isPresent() &&
                    orderNumber.contentEquals(orderNumberElemOptional.get().getText()) &&
                    orderDetailRequestedByElemOptional.isPresent()) {
                return viewGroup;
            }
        }

        Optional<WebElement> bottomBlankElemOptional =
                orderRecyclerView.findElements(BOTTOM_BLANK).stream().findFirst();
        if (bottomBlankElemOptional.isPresent()) {
            return null;
        } else {
            scroll(ScrollDirection.DOWN, 0.5);
            wait.until(ExpectedConditions.
                    invisibilityOfElementLocated(PROGRESS_BAR));
            return scrollToOrder(orderNumber);
        }
    }
}
