package com.parasoft.demoapp.e2e.testCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.parasoft.demoapp.e2e.common.AppiumConfig;
import com.parasoft.demoapp.e2e.common.BaseTest;
import com.parasoft.demoapp.e2e.common.LoginUtil;
import com.parasoft.demoapp.e2e.locators.HomeActivityLocators;
import com.parasoft.demoapp.e2e.locators.LoginActivityLocators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.time.Duration;

public class LoginActivityTest extends BaseTest {
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        beforeClass();
        wait = new WebDriverWait(driver, Duration.ofSeconds(AppiumConfig.durationOfSeconds()));
    }

    @AfterEach
    public void tearDown() {
        afterClass();
    }

    public void waitForLoginPageOpen() {
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.APP_TITLE));
    }

    @Test
    public void testSetBaseUrl() {
        waitForLoginPageOpen();

        WebElement SettingButton = driver.findElement(LoginActivityLocators.SETTING_BUTTON);
        SettingButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));

        WebElement baseUrlInput = driver.findElement(LoginActivityLocators.BASE_URL_INPUT);
        WebElement baseUrlResetLink = driver.findElement(LoginActivityLocators.BASE_URL_RESET_LINK);
        WebElement saveButton = driver.findElement(LoginActivityLocators.BASE_URL_SAVE_BUTTON);
        WebElement cancelButton = driver.findElement(LoginActivityLocators.BASE_URL_CANCEL_BUTTON);
        WebElement loginErrorMessage = driver.findElement(LoginActivityLocators.BASE_URL_ERROR_MESSAGE);

        assertEquals("Settings", driver.findElement(LoginActivityLocators.SETTING_DIALOG_TITLE).getText());
        assertTrue(saveButton.isEnabled());
        assertTrue(cancelButton.isEnabled());
        assertEquals("", loginErrorMessage.getText());
        assertEquals("http://10.0.2.2:8080", baseUrlInput.getText());

        // Empty base url
        baseUrlInput.clear();
        baseUrlInput.sendKeys("");
        assertFalse(saveButton.isEnabled());
        assertTrue(cancelButton.isEnabled());
        assertEquals("Base URL must not be empty", loginErrorMessage.getText());

        // Invalid base url
        baseUrlInput.clear();
        baseUrlInput.sendKeys("http");
        assertFalse(saveButton.isEnabled());
        assertTrue(cancelButton.isEnabled());
        assertEquals("Invalid URL", loginErrorMessage.getText());

        // Reset base url
        baseUrlResetLink.click();
        assertTrue(saveButton.isEnabled());
        assertTrue(cancelButton.isEnabled());
        assertEquals("", loginErrorMessage.getText());

        // Save base url
        baseUrlInput.clear();
        baseUrlInput.sendKeys("http://10.0.2.2:8081");
        saveButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
        SettingButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
        baseUrlInput = driver.findElement(LoginActivityLocators.BASE_URL_INPUT);
        assertEquals("http://10.0.2.2:8081", baseUrlInput.getText());

        // Cancel
        cancelButton = driver.findElement(LoginActivityLocators.BASE_URL_CANCEL_BUTTON);
        cancelButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
    }

    @Test
    public void testElementStatusInSignInBox() {
        waitForLoginPageOpen();

        WebElement usernameInput = driver.findElement(LoginActivityLocators.USERNAME_INPUT);
        WebElement passwordInput = driver.findElement(LoginActivityLocators.PASSWORD_INPUT);
        WebElement singInButton = driver.findElement(LoginActivityLocators.SIGN_IN_BUTTON);

        assertTrue(usernameInput.isEnabled());
        assertTrue(passwordInput.isEnabled());
        assertFalse(singInButton.isEnabled());

        usernameInput.clear();
        usernameInput.sendKeys("approver");
        assertTrue(singInButton.isEnabled());
        usernameInput.clear();
        assertFalse(singInButton.isEnabled());

        passwordInput.clear();
        assertFalse(singInButton.isEnabled());
        passwordInput.sendKeys("password");
        assertFalse(singInButton.isEnabled());
    }

    @Test
    public void testLoginLogout() {
        waitForLoginPageOpen();

        // Login with wrong username
        LoginUtil.login(driver, "wrong username", "password");
        WebElement loginErrorMessage = driver.findElement(LoginActivityLocators.LOGIN_ERROR_MESSAGE);
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.LOGIN_ERROR_MESSAGE));
        assertEquals("Incorrect username or password", loginErrorMessage.getText());

        // Login with bad base url
        WebElement settingButton = driver.findElement(LoginActivityLocators.SETTING_BUTTON);
        settingButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
        driver.findElement(LoginActivityLocators.BASE_URL_INPUT)
                .sendKeys("http://10.0.2.2:7777");
        driver.findElement(LoginActivityLocators.BASE_URL_SAVE_BUTTON).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LoginActivityLocators.SETTING_DIALOG_TITLE));
        LoginUtil.login(driver, "approver", "password");
        wait.until(ExpectedConditions.textToBe(LoginActivityLocators.LOGIN_ERROR_MESSAGE, "Base URL is not reachable"));
        settingButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.BASE_URL_INPUT))
                .sendKeys(AppiumConfig.pdaServerUrl());
        driver.findElement(LoginActivityLocators.BASE_URL_SAVE_BUTTON).click();

        // Login successfully
        LoginUtil.login(driver, "approver", "password");
        wait.until(ExpectedConditions.presenceOfElementLocated(HomeActivityLocators.HOME_TITLE));
        WebElement homeTitle = driver.findElement(HomeActivityLocators.HOME_TITLE);
        assertEquals("APPROVALS", homeTitle.getText());

        // Logout
        wait.until(ExpectedConditions.presenceOfElementLocated(HomeActivityLocators.MORE_OPTION)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(HomeActivityLocators.SIGN_OUT_MENU_ITEM)).click();
    }

    @Test
    public void testForgotPassword() {
        waitForLoginPageOpen();

        driver.findElement(LoginActivityLocators.FORGOT_PASSWORD_LINK).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(LoginActivityLocators.USER_INFORMATION_DIALOG_TITLE));
        assertEquals("Username:", driver.findElement(LoginActivityLocators.USER_INFORMATION_USERNAME_LABEL).getText());
        assertEquals("approver", driver.findElement(LoginActivityLocators.USER_INFORMATION_USERNAME_VALUE).getText());
        assertEquals("Password:", driver.findElement(LoginActivityLocators.USER_INFORMATION_PASSWORD_LABEL).getText());
        assertEquals("password", driver.findElement(LoginActivityLocators.USER_INFORMATION_PASSWORD_VALUE).getText());

        driver.findElement(LoginActivityLocators.USER_INFORMATION_CLOSE_BUTTON).click();
    }
}
