package com.parasoft.demoapp.e2e;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.BaseTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import io.appium.java_client.AppiumBy;

public class ExampleAppiumTest extends BaseTest {

    @Test
    public void testLoginLogout() {
        final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("com.parasoft.demoapp:id/settingButton")));
        WebElement settingsGearIcon = driver
                .findElement(By.id("com.parasoft.demoapp:id/settingButton"));
        settingsGearIcon.click();

        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("com.parasoft.demoapp:id/base_url_input")));
        WebElement baseUrlInput = driver
                .findElement(By.id("com.parasoft.demoapp:id/base_url_input"));
        baseUrlInput.clear();
        baseUrlInput.sendKeys(AppiumConfig.pdaServerUrl());
        WebElement saveButton = driver.findElement(By.id("com.parasoft.demoapp:id/save_button"));
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("com.parasoft.demoapp:id/username")));
        WebElement usernameInput = driver.findElement(By.id("com.parasoft.demoapp:id/username"));
        usernameInput.sendKeys("approver");
        WebElement passwordInput = driver.findElement(By.id("com.parasoft.demoapp:id/password"));
        passwordInput.sendKeys("password");
        WebElement signInButton = driver.findElement(By.id("com.parasoft.demoapp:id/sign_in"));
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();

        wait.until(ExpectedConditions
                .presenceOfElementLocated(AppiumBy.accessibilityId("More options")));
        WebElement moreOptionsIcon = driver.findElement(AppiumBy.accessibilityId("More options"));
        moreOptionsIcon.click();

        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("com.parasoft.demoapp:id/title")));
        WebElement signOutMenuItem = driver.findElement(By.id("com.parasoft.demoapp:id/title"));
        wait.until(ExpectedConditions.visibilityOf(signOutMenuItem)).click();
    }
}
