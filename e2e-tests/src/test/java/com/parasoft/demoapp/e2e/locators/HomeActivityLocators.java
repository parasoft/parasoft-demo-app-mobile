package com.parasoft.demoapp.e2e.locators;

import com.parasoft.demoapp.e2e.common.AppiumConfig;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;

public class HomeActivityLocators {

    private static final String APP_PACKAGE = AppiumConfig.appPackage();

    public static final By HOME_TITLE = By.id(APP_PACKAGE + ":id/home_title");

    public static final By MORE_OPTION = AppiumBy.accessibilityId("More options");

    public static final By SIGN_OUT_MENU_ITEM = By.id("com.parasoft.demoapp:id/title");

}
