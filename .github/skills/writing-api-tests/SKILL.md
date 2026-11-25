---
name: writing-api-tests
description: Write plain TestNG API contract tests for the HealthHub REST API (not Gherkin). Use when adding coverage for an endpoint that doesn't have a test yet, or when asked to create, write, build, or add an API test for X.
---

# writing-api-tests

Write plain TestNG API contract tests for the HealthHub REST API (not
Gherkin — see `CLAUDE.md` §2 on when API tests skip BDD). Use when adding
coverage for an endpoint that doesn't have a test yet, or when asked to
create, write, build, or add an API test for X.

<!--
AMOHAN'S TICKET, ITEM 3: the biggest of your five artifacts. Rules below
are already filled in — they're just `api/CLAUDE.md`'s conventions
restated as a recipe, nothing new to discover there. The Template stays
generic on purpose (see its own note below) — the actual work here is
finishing `scripts/verify-endpoint.sh`. Don't write a single test by
hand against whatever gap Agent A finds for you until this skill itself
is real — that's the whole point of building the skill first.
-->

## Rules

1. Use `ApiClient.authenticated()` for any request that needs auth —
   never hand-roll a login call in a new test.
2. Every endpoint returns `{"success": true/false, "data": ...}` on
   success or `{"success": false, "error": {"code": ..., ...}}` on
   failure — assert against this envelope, not against an invented shape.
3. **Verify the real response shape against the live app before writing
   an assertion about it.** Same discipline as a locator: call the
   endpoint for real (via this skill's script, or the `/check-endpoint`
   prompt), read what actually comes back, then write the test. Don't
   assume a field name just because it sounds plausible.
4. A test that needs multiple input variations and has no business-
   readable framing worth preserving uses TestNG `@DataProvider`
   (`dataprovider/`) — not a Gherkin Scenario Outline. See the worked
   example for what this looks like in practice.
5. File goes in `src/test/java/com/healthhub/api/<Feature>ApiTest.java`.

## Template

**This stays generic** — fill in `Feature`/`someEndpoint`/the asserted
fields *each time this skill is used*, for whatever real endpoint you're
covering. Don't try to guess specifics for this course's actual gap
while you're still building the skill itself — you don't know what it
is yet (that's Agent A's job, two steps from now).

```java
package com.healthhub.api;

import org.testng.annotations.Test;

public class FeatureApiTest {

    @Test
    public void checksSomeRealObservedBehavior() {
        ApiClient.authenticated()
                .when()
                .get("/someEndpoint")               // the real, verified path
                .then()
                .statusCode(200)                     // the real, observed status code
                .body("success", org.hamcrest.Matchers.is(true));
                // assert on fields you actually observed via
                // verify-endpoint.sh or /check-endpoint — never a
                // guessed field name
    }

    @Test
    public void unauthenticatedRequestIsRejected() {
        io.restassured.RestAssured.given()
                .baseUri(com.healthhub.config.ConfigReader.apiBaseUrl())
                .when()
                .get("/someEndpoint")                // same path as above
                .then()
                .statusCode(401);
    }
}
```

## Worked example

See [resources/appointments-api-example.md](resources/appointments-api-example.md)
for how the existing, already-covered `/appointments` endpoint's test
was actually built — the real pattern to copy, applied to whatever gap
you find, not to this specific endpoint.

## Verification script

[TODO: `scripts/verify-endpoint.sh` should take an endpoint path, log in
with the test account, call it authenticated, and print back the real
status code and JSON shape — so you (or anyone using this skill later)
never has to assume a response shape by eye. See the stub for what's
already wired and what's left.]
