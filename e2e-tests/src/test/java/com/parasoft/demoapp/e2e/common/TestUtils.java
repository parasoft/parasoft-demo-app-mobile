package com.parasoft.demoapp.e2e.common;

import com.parasoft.demoapp.e2e.locators.LoginActivityLocators;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestUtils {

    public static void login(WebDriver driver, String username, String password) {
        final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.SIGN_IN_TITLE));

        WebElement usernameInput = driver.findElement(LoginActivityLocators.USERNAME_INPUT);
        WebElement passwordInput = driver.findElement(LoginActivityLocators.PASSWORD_INPUT);

        usernameInput.clear();
        usernameInput.sendKeys(username);

        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement singInButton = driver.findElement(LoginActivityLocators.SIGN_IN_BUTTON);
        wait.until(ExpectedConditions.elementToBeClickable(singInButton)).click();
    }

    public static void setBaseUrl(WebDriver driver, String baseUrl) {
        final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.APP_TITLE));

        WebElement settingButton = driver.findElement(LoginActivityLocators.SETTING_BUTTON);
        settingButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
        driver.findElement(LoginActivityLocators.BASE_URL_INPUT).sendKeys(baseUrl);
        wait.until(ExpectedConditions.elementToBeClickable(LoginActivityLocators.BASE_URL_SAVE_BUTTON)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
    }

    public static void setBaseUrlAndlogin(WebDriver driver, WebDriverWait wait, String username,
                                          String password) {
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.APP_TITLE));
        TestUtils.setBaseUrl(driver, AppiumConfig.pdaServerUrl());
        TestUtils.login(driver, username, password);
    }
}
