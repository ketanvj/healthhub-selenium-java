package com.healthhub.dataprovider;

import org.testng.annotations.DataProvider;

/**
 * Standalone TestNG @DataProvider, deliberately kept separate from the
 * Cucumber layer: Gherkin Scenario Outlines already cover data-driven BDD
 * scenarios (see login.feature), this shows the idiomatic TestNG mechanism
 * for plain (non-Gherkin) test classes like AppointmentsApiTest.
 */
public class AppointmentDataProvider {

    @DataProvider(name = "appointmentStatusFilters")
    public static Object[][] appointmentStatusFilters() {
        return new Object[][]{
                {"scheduled"},
                {"completed"},
                {"cancelled"}
        };
    }
}
