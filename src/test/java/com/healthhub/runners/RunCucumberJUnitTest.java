package com.healthhub.runners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Kept as a working, runnable example per the course's "keep both handy"
 * decision. NOT the day-to-day runner, TestNGRunner is, this exists so
 * participants coming from a JUnit-only background have something they
 * recognize, and so the framework shows both are genuinely wired up, not
 * just mentioned.
 *
 * Run with: mvn test -Dtest=RunCucumberJUnitTest
 * (the default `mvn test` uses testng.xml / TestNGRunner instead)
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,
        value = "com.healthhub.stepdefinitions,com.healthhub.hooks")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report-junit/cucumber.html")
public class RunCucumberJUnitTest {
    // No body needed, annotations configure everything.
}
