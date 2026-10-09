package com.healthhub.api;

import com.healthhub.dataprovider.AppointmentDataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasEntry;

/**
 * Plain TestNG API test, intentionally not wrapped in Cucumber/Gherkin.
 * API contract checks like this one are a common place real teams skip BDD,
 * the value of Gherkin (shared business-readable language) matters less for
 * a pure technical contract check than it does for user-facing behavior.
 */
public class AppointmentsApiTest {

    @Test(dataProvider = "appointmentStatusFilters",
            dataProviderClass = AppointmentDataProvider.class)
    public void filteringByStatusReturnsOnlyMatchingAppointments(String status) {
        ApiClient.authenticated()
                .queryParam("status", status)
                .when()
                .get("/appointments")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data", everyItem(hasEntry("status", status)));
    }

    @Test
    public void unauthenticatedRequestIsRejected() {
        io.restassured.RestAssured.given()
                .baseUri(com.healthhub.config.ConfigReader.apiBaseUrl())
                .when()
                .get("/appointments")
                .then()
                .statusCode(401);
    }
}
