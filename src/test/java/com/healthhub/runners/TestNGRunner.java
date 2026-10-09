package com.healthhub.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Primary runner for this course. Run via:
 *   mvn test                         (uses testng.xml, which registers this)
 *   mvn test -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.healthhub.stepdefinitions", "com.healthhub.hooks"},
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "html:target/cucumber-report/cucumber.html"
        },
        monochrome = true
)
public class TestNGRunner extends AbstractTestNGCucumberTests {

    // AbstractTestNGCucumberTests already provides @DataProvider(parallel = true)
    // "scenarios" that TestNG uses to run each Cucumber scenario as its own
    // TestNG test method, that's what makes RetryAnalyzer apply per-scenario.
}
