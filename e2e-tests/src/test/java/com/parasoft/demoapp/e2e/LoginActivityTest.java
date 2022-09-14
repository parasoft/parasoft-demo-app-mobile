package com.parasoft.demoapp.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.parasoft.demoapp.e2e.common.BaseTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginActivityTest extends BaseTest {

    @Test
    public void testHeaderTitle() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.
                        presenceOfElementLocated(By.id("com.parasoft.demoapp:id/display_title")));
        WebElement headerTitleElement = driver
                .findElement(By.id("com.parasoft.demoapp:id/display_title"));
        String headerText = headerTitleElement.getText();
        assertEquals("PARASOFT DEMO APP", headerText);
    }

    @Test
    public void testForgotPasswordLinkLabel() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.
                        presenceOfElementLocated(By
                                .id("com.parasoft.demoapp:id/forgot_password_link")));
        WebElement forgotPasswordLink = driver
                .findElement(By.id("com.parasoft.demoapp:id/forgot_password_link"));
        String forgotPasswordText = forgotPasswordLink.getText();
        assertEquals("Forgot password?", forgotPasswordText);
    }

}

