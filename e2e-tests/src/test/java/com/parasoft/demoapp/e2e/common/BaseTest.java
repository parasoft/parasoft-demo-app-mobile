package com.parasoft.demoapp.e2e.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;

public class BaseTest {

    protected static AndroidDriver driver;

    @BeforeAll
    public static void beforeClass() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, AppiumConfig.deviceName());
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        capabilities.setCapability(MobileCapabilityType.APP, AppiumConfig.demoAppApkPath());

        URL remoteUrl = new URL(AppiumConfig.appiumServerUrl());

        driver = new AndroidDriver(remoteUrl, capabilities);
    }

    @AfterAll
    public static void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}
