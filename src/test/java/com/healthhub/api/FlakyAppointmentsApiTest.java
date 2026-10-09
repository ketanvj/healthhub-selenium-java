package com.healthhub.api;

import org.testng.annotations.Test;

/**
 * DELIBERATELY FLAKY — this is Day 4's "debug a known flaky test" exercise.
 * Do not fix this. It's left broken on purpose.
 *
 * Why it fails intermittently: the request below adds ?_flaky=0.3 to the
 * real HealthHub API, which makes the server return a 500 roughly 30% of
 * the time (confirmed by calling the endpoint directly and observing the
 * behavior), independent of anything this test or the application code
 * does wrong. RetryAnalyzer (see
 * com.healthhub.listeners.RetryAnalyzer) will mask this most of the time by
 * retrying up to twice, which is itself worth noticing and questioning
 * during the exercise: is "retry until it passes" actually fixing anything,
 * or just hiding the instability? That tension is the point of this file.
 */
public class FlakyAppointmentsApiTest {

    @Test
    public void appointmentsEndpointUnderSimulatedInstability() {
        ApiClient.authenticated()
                .queryParam("_flaky", "0.3")
                .when()
                .get("/appointments")
                .then()
                .statusCode(200);
    }
}
