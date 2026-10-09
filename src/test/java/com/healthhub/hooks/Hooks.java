package com.healthhub.hooks;

import com.healthhub.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * Cucumber lifecycle, one browser per scenario. Deliberately simple:
 * no retry logic here, that's TestListener/RetryAnalyzer's job when
 * running via TestNG. These hooks just own setup, teardown, and
 * capturing evidence on failure.
 */
public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Driver is created lazily on first getDriver() call inside a step,
        // nothing to do here except make sure a fresh instance starts clean.
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();

        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
            Allure.addAttachment(scenario.getName() + " - failure screenshot",
                    new ByteArrayInputStream(screenshot));
        }

        DriverManager.quitDriver();
    }
}
