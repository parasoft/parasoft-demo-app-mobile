package com.parasoft.demoapp.e2e.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class AppiumConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppiumConfig.class);

    private static final String APPIUM_PROFILE_NAME_KEY = "appium.profile.name";

    private static final String DEFAULT_PROFILE_NAME = "dev";

    private static final String CONFIG_FILE_NAME_PREFIX = "appium-";

    private static final String PROPERTIES_FILE_NAME_EXTENSION = ".properties";

    private static final Properties CONFIG_FILE_PROPS = new Properties();

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    private static final String DEVICE_NAME_KEY = "device.name";

    private static final String APPIUM_SERVER_URL_KEY = "appium.server.url";

    private static final String PDA_SERVER_BASE_URL_KEY = "pda.server.base-url";

    private static final String DEMO_APP_APK_PATH_KEY = "demo.app.apk.path";

    private static final String DEFAULT_DEVICE_NAME = "emulator-5554";

    private static final String DEFAULT_APPIUM_SERVER_URL = "http://127.0.0.1:4723/wd/hub";

    private static final String DEFAULT_DEMO_APP_APK_PATH = "../app/build/outputs/apk/debug/parasoft-demo-app-mobile-1.0.apk";

    private static final String DEFAULT_PDA_SERVER_BASE_URL = "http://10.0.2.2:8080";

    static {
        try {
            loadConfig();
        } catch (IOException e) {
            LOGGER.warn("Failed to load Appium profile config file.");
        }
    }

    public static String deviceName() {
        return getConfigValue(DEVICE_NAME_KEY, DEFAULT_DEVICE_NAME);
    }

    public static String appiumServerUrl() {
        return getConfigValue(APPIUM_SERVER_URL_KEY, DEFAULT_APPIUM_SERVER_URL);
    }

    public static String demoAppApkPath() {
        String demoAppApkPath = getConfigValue(DEMO_APP_APK_PATH_KEY, DEFAULT_DEMO_APP_APK_PATH);
        return Paths.get(demoAppApkPath).toAbsolutePath().toString();
    }

    public static String pdaServerUrl() {
        return getConfigValue(PDA_SERVER_BASE_URL_KEY, DEFAULT_PDA_SERVER_BASE_URL);
    }

    private static String getConfigValue(String configKey, String defaultValue) {
        if (CONFIG_MAP.containsKey(configKey)) {
            return CONFIG_MAP.get(configKey);
        }

        String configValue = System.getProperty(configKey);
        if (isNotEmpty(configValue)) {
            LOGGER.info("Use system property config: {}={}", configKey, configValue);
            CONFIG_MAP.put(configKey, configValue);
            return configValue;
        }

        configValue = CONFIG_FILE_PROPS.getProperty(configKey);
        if (isNotEmpty(configValue)) {
            LOGGER.info("Use config file config: {}={}", configKey, configValue);
            CONFIG_MAP.put(configKey, configValue);
            return configValue;
        }

        LOGGER.info("Use default config: {}={}", configKey, defaultValue);
        CONFIG_MAP.put(configKey, defaultValue);
        return defaultValue;
    }

    private static boolean isNotEmpty(String configValue) {
        return configValue != null && !configValue.trim().isEmpty();
    }

    private static void loadConfig() throws IOException {
        InputStream configFile = getConfigFile();
        if (configFile != null) {
            CONFIG_FILE_PROPS.load(configFile);
        }
    }

    private static String getCurrentProfileName() {
        String profileName = System.getProperty(APPIUM_PROFILE_NAME_KEY);
        if (profileName != null) {
            LOGGER.info("Use Appium profile: {}", profileName);
            return profileName;
        }

        LOGGER.info("Use default Appium profile: {}", DEFAULT_PROFILE_NAME);
        return DEFAULT_PROFILE_NAME;
    }

    private static InputStream getConfigFile() {
        return getResourceInputStream(CONFIG_FILE_NAME_PREFIX + getCurrentProfileName()
                + PROPERTIES_FILE_NAME_EXTENSION);
    }

    private static InputStream getResourceInputStream(String resourceLoc) {
        InputStream input = AppiumConfig.class.getResourceAsStream(resourceLoc);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = AppiumConfig.class.getClassLoader().getResourceAsStream(resourceLoc);
        }
        return input;
    }
}
