package com.healthhub.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties once and exposes typed getters.
 * Every value can be overridden with a matching -D system property,
 * so CI can point the same framework at a different environment without
 * touching this file.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            PROPERTIES.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int implicitWaitSeconds() {
        return Integer.parseInt(get("implicit.wait.seconds"));
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(get("explicit.wait.seconds"));
    }

    public static String testUserEmail() {
        return get("test.user.email");
    }

    public static String testUserPassword() {
        return get("test.user.password");
    }
}
