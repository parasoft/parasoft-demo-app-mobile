package com.parasoft.demoapp.e2e.common;

import com.google.common.collect.ImmutableList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Kind;
import org.openqa.selenium.interactions.PointerInput.MouseButton;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;

public class BaseTest {

    protected static AndroidDriver driver;
    protected static DesiredCapabilities capabilities;

    private Dimension windowSize;

    private static final Duration SCROLL_DUR = Duration.ofMillis(1000);
    private static final double SCROLL_RATIO = 0.8;
    private static final int ANDROID_SCROLL_DIVISOR = 3;

    @BeforeAll
    public static void beforeClass() {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, AppiumConfig.deviceName());
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        capabilities.setCapability(MobileCapabilityType.APP, AppiumConfig.demoAppApkPath());
        capabilities.setCapability(AndroidMobileCapabilityType.ADB_PORT, AppiumConfig.adbServerPort());
        capabilities.setCapability(UiAutomator2Options.UIAUTOMATOR2_SERVER_LAUNCH_TIMEOUT_OPTION,
                AppiumConfig.uiautomator2ServerLaunchTimeout());
        AppiumConfig.getAdditionalCapabilities().forEach((key, value) -> {
            capabilities.setCapability(key, value);
        });
    }

    @BeforeEach
    public void beforeTest() throws MalformedURLException {
        URL remoteUrl = new URL(AppiumConfig.appiumServerUrl());
        driver = new AndroidDriver(remoteUrl, capabilities);
    }

    @AfterAll
    public static void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    private Dimension getWindowSize() {
        if (windowSize == null) {
            windowSize = driver.manage().window().getSize();
        }
        return windowSize;
    }

    protected void swipe(Point start, Point end, Duration duration) {
        PointerInput input = new PointerInput(Kind.TOUCH, "finger1");
        Sequence swipe = new Sequence(input, 0);
        swipe.addAction(input.createPointerMove(Duration.ZERO, Origin.viewport(), start.x, start.y));
        swipe.addAction(input.createPointerDown(MouseButton.LEFT.asArg()));
        duration = duration.dividedBy(ANDROID_SCROLL_DIVISOR);
        swipe.addAction(input.createPointerMove(duration, Origin.viewport(), end.x, end.y));
        swipe.addAction(input.createPointerUp(MouseButton.LEFT.asArg()));
        driver.perform(ImmutableList.of(swipe));
    }

    protected void swipe(double startXPct, double startYPct, double endXPct, double endYPct,
                         Duration duration) {
        Dimension size = getWindowSize();
        Point start = new Point((int)(size.width * startXPct), (int)(size.height * startYPct));
        Point end = new Point((int)(size.width * endXPct), (int)(size.height * endYPct));
        swipe(start, end, duration);
    }

    protected void scroll(ScrollDirection dir, double distance) {
        if (distance < 0 || distance > 1) {
            throw new Error("Scroll distance must be between 0 and 1");
        }
        Dimension size = getWindowSize();
        Point midPoint = new Point((int)(size.width * 0.5), (int)(size.height * 0.5));
        int top = midPoint.y - (int)((size.height * distance) * 0.5);
        int bottom = midPoint.y + (int)((size.height * distance) * 0.5);
        int left = midPoint.x - (int)((size.width * distance) * 0.5);
        int right = midPoint.x + (int)((size.width * distance) * 0.5);
        if (dir == ScrollDirection.UP) {
            swipe(new Point(midPoint.x, top), new Point(midPoint.x, bottom), SCROLL_DUR);
        } else if (dir == ScrollDirection.DOWN) {
            swipe(new Point(midPoint.x, bottom), new Point(midPoint.x, top), SCROLL_DUR);
        } else if (dir == ScrollDirection.LEFT) {
            swipe(new Point(left, midPoint.y), new Point(right, midPoint.y), SCROLL_DUR);
        } else {
            swipe(new Point(right, midPoint.y), new Point(left, midPoint.y), SCROLL_DUR);
        }
    }

    protected void scroll(ScrollDirection dir) {
        scroll(dir, SCROLL_RATIO);
    }

    protected void scroll() {
        scroll(ScrollDirection.DOWN, SCROLL_RATIO);
    }

    public enum ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }
}
