package com.healthhub.driver;

import com.healthhub.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * One WebDriver instance per thread, so TestNG's parallel="methods"/"classes"
 * execution doesn't share a browser across tests. Cucumber Hooks own the
 * create/quit lifecycle, this class only knows how to build and hand back
 * the driver for the current thread.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver createDriver() {
        WebDriver driver;
        String browser = ConfigReader.browser().toLowerCase();

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (ConfigReader.headless()) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (ConfigReader.headless()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1440,900");
                driver = new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser
                    + " (supported: chrome, firefox)");
        }

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.implicitWaitSeconds()));
        driver.manage().window().maximize();
        return driver;
    }
}
