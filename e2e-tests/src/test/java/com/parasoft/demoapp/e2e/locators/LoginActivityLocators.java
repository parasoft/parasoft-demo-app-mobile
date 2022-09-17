package com.parasoft.demoapp.e2e.locators;

import com.parasoft.demoapp.e2e.common.AppiumConfig;

import org.openqa.selenium.By;

public class LoginActivityLocators {

    private static final String APP_PACKAGE = AppiumConfig.appPackage();

    public static final By APP_TITLE = By.id(APP_PACKAGE + ":id/app_title");

    public static final By SETTING_BUTTON = By.id(APP_PACKAGE + ":id/setting_button");

    public static final By SIGN_IN_TITLE = By.id(APP_PACKAGE + ":id/sign_in_title");

    public static final By SIGN_IN_BUTTON = By.id(APP_PACKAGE + ":id/sign_in");

    public static final By FORGOT_PASSWORD_LINK = By.id(APP_PACKAGE + ":id/forgot_password_link");

    public static final By LOGIN_ERROR_MESSAGE = By.id(APP_PACKAGE + ":id/login_error_message");

    public static final By USERNAME_INPUT = By.id("com.parasoft.demoapp:id/username");

    public static final By PASSWORD_INPUT = By.id("com.parasoft.demoapp:id/password");

    public static final By BASE_URL_INPUT = By.id("com.parasoft.demoapp:id/base_url_input");

    public static final By BASE_URL_RESET_LINK = By.id("com.parasoft.demoapp:id/reset_base_url");

    public static final By BASE_URL_SAVE_BUTTON = By.id("com.parasoft.demoapp:id/base_url_save_button");

    public static final By BASE_URL_CANCEL_BUTTON = By.id("com.parasoft.demoapp:id/base_url_dismiss_button");

    public static final By BASE_URL_ERROR_MESSAGE = By.id("com.parasoft.demoapp:id/base_url_error_message");

    public static final By SETTING_DIALOG_TITLE = By.id("com.parasoft.demoapp:id/setting_dialog_title");

    public static final By USER_INFORMATION_DIALOG_TITLE = By.id("com.parasoft.demoapp:id/user_information_dialog_title");

    public static final By USER_INFORMATION_USERNAME_LABEL = By.id("com.parasoft.demoapp:id/username_label");

    public static final By USER_INFORMATION_USERNAME_VALUE = By.id("com.parasoft.demoapp:id/username_value");

    public static final By USER_INFORMATION_PASSWORD_LABEL = By.id("com.parasoft.demoapp:id/password_label");

    public static final By USER_INFORMATION_PASSWORD_VALUE = By.id("com.parasoft.demoapp:id/password_value");

    public static final By USER_INFORMATION_CLOSE_BUTTON = By.id("com.parasoft.demoapp:id/user_information_close_button");

}
