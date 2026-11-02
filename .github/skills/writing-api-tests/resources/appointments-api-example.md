# Worked example: `AppointmentsApiTest`

This endpoint already has coverage — it's here as the reference pattern
to copy, not a gap to fill. Read it to see what "done right" looks like
in this framework before applying the same shape elsewhere.

```java
package com.healthhub.api;

import com.healthhub.dataprovider.AppointmentDataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasEntry;

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
```

## Why this is the pattern, not just an example

- **`ApiClient.authenticated()` for the happy path, a hand-built
  unauthenticated request for the negative case.** The second test
  deliberately does *not* use `ApiClient` — it's checking what happens
  *without* auth, so it builds its own bare request instead.
- **`@DataProvider`, not a Scenario Outline.** Filtering by status is a
  technical variation with no business-readable framing worth
  preserving in Gherkin — a plain TestNG data provider is the right
  call here, per `CLAUDE.md` §2.
- **The assertion matches the real envelope** (`success` + `data`),
  not an invented one — this was confirmed by calling `/appointments`
  directly during this framework's original build, the same way you
  should confirm whatever endpoint you're covering.
- **Status codes are asserted exactly** (`200`, `401`) — not just "it
  didn't error" — because the real contract includes the negative case,
  not only the happy path.

Apply this same shape — happy path via `ApiClient.authenticated()`,
negative case unauthenticated, assertions against the real observed
envelope, `@DataProvider` only if there's a real technical variation
worth parameterizing — to whatever endpoint your own `api-coverage-gap-finder`
agent reports as uncovered.
