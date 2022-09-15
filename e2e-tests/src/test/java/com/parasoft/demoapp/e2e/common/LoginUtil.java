package com.parasoft.demoapp.e2e.common;

import com.parasoft.demoapp.e2e.locators.LoginActivityLocators;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginUtil {

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
}
