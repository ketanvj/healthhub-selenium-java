package com.healthhub.api;

import com.healthhub.config.ConfigReader;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around RestAssured, not a full client library. Logs in once
 * and caches the JWT for the lifetime of the test run, real API test suites
 * would want per-test or per-class token scoping, this is deliberately
 * simple for a training skeleton.
 */
public final class ApiClient {

    private static String cachedToken;

    private ApiClient() {
    }

    public static String getAuthToken() {
        if (cachedToken == null) {
            Response response = given()
                    .baseUri(ConfigReader.apiBaseUrl())
                    .contentType("application/json")
                    .body("""
                            {
                              "email": "%s",
                              "password": "%s"
                            }
                            """.formatted(ConfigReader.testUserEmail(), ConfigReader.testUserPassword()))
                    .post("/auth/login");

            response.then().statusCode(200);
            cachedToken = response.jsonPath().getString("data.token");
        }
        return cachedToken;
    }

    public static RequestSpecification authenticated() {
        return given()
                .baseUri(ConfigReader.apiBaseUrl())
                .header("Authorization", "Bearer " + getAuthToken())
                .contentType("application/json");
    }
}
