# HealthHub Selenium + Gherkin + Java — Copilot instructions

Training-reference automation framework for the HealthHub app. Selenium
WebDriver + Cucumber (Gherkin) + TestNG, with a secondary JUnit Platform
runner kept working as a backup/comparison example. Built against the real
HealthHub app, centrally hosted at `https://healthhub.qualintia.com`
(API mounted at `/api/v1`).

## Discovering real locators and API behavior (no source access)

Participants don't have — and shouldn't rely on — the HealthHub app's
source code. Treat it as a black box, the way you would a real
third-party application, and discover everything by inspecting it live.
Nothing in this framework should ever point you at the app's component
or backend files; if a doc still does, that's a bug in the doc.

**UI locators**: use Playwright MCP (set up on Day 3) to navigate to the
running app and query the live DOM:

```js
// Via the Playwright MCP browser_evaluate tool, after navigating to the
// page in question:
() => Array.from(document.querySelectorAll('[data-testid]')).map(el => ({
  testid: el.getAttribute('data-testid'),
  tag: el.tagName.toLowerCase(),
  role: el.getAttribute('role') || null,
}))
```

This lists every `data-testid` actually rendered on the current page,
with enough context (tag, role) to tell a button from a toggle from an
input. No MCP configured? The manual fallback is identical in spirit:
open browser DevTools, Inspect Element, read the `data-testid` off the
real element.

**API behavior**: established the same way — by calling the API directly
and observing real responses, not by reading backend code. The specific
facts stated elsewhere in this file (the `/api/v1` mount point, the
`_flaky` query param's behavior) were confirmed this way.

## Quick start

HealthHub is centrally hosted at `https://healthhub.qualintia.com` —
nothing to start locally. `config.properties` already points there by
default.

```bash
mvn test
```

Runs via `testng.xml` (see `pom.xml`'s surefire config, which points at it).
Override environment with system properties, e.g. `mvn test -Dbrowser=firefox -Dheadless=true`.

## Directory structure

Two folders also carry their own path-scoped instructions file under
`.github/instructions/` (`pages.instructions.md`, applied via `applyTo` to
`src/test/java/com/healthhub/pages/**`, and `stepdefinitions.instructions.md`
scoped to `src/test/java/com/healthhub/stepdefinitions/**`) — Copilot
applies those automatically on top of this file when you're editing inside
that folder. They cover locator verification steps, gotchas already hit
and fixed once, and DI requirements that this file doesn't repeat.

```
src/test/java/com/healthhub/
├── config/            ConfigReader — reads config.properties, -D overrides win
├── driver/            DriverManager — ThreadLocal<WebDriver>, chrome/firefox via WebDriverManager
├── pages/             Page Objects — all extend BasePage (see pages.instructions.md)
├── hooks/             Cucumber @Before/@After — one browser per scenario, screenshot-on-failure
├── listeners/         TestListener (wires RetryAnalyzer in), RetryAnalyzer (max 2 retries)
├── stepdefinitions/   Step classes + ScenarioContext (see stepdefinitions.instructions.md)
├── runners/           TestNGRunner (primary), RunCucumberJUnitTest (JUnit Platform backup)
├── api/               Plain TestNG tests hitting the REST API directly (no Gherkin)
└── dataprovider/       TestNG @DataProvider classes, for plain API tests only
```

## Conventions to follow when generating or editing code here

1. **Page Objects** extend `BasePage` and use its `waitVisible`,
   `waitClickable`, `click`, `type`, `textOf`, `isDisplayed`, `testId(String)`
   helpers. Always locate elements via `testId(id)`, which builds a `By`
   from the app's `data-testid` attributes — never CSS classes or XPath text
   matches. Discover the real testid by inspecting the live running app (see
   "Discovering real locators" above) before inventing one. Navigate via
   real UI interaction, not `driver.get(url)` reconstruction. No assertions
   inside page objects.

2. **Gherkin Scenario Outline vs TestNG `@DataProvider`** are deliberately
   not interchangeable. Use a Scenario Outline when the data variation is
   part of business-readable behavior (see `login.feature`). Use a TestNG
   `@DataProvider` (`dataprovider/`) only for plain, non-Gherkin technical
   checks (see `api/AppointmentsApiTest`).

3. **Shared state across step classes** goes through `ScenarioContext`
   (injected via Cucumber-PicoContainer DI, requires a no-arg constructor).
   Step classes read/write page objects through it rather than reaching
   into each other.

4. **Retry is wired globally**, not per-test. `TestListener`
   (`IAnnotationTransformer`, registered in `testng.xml`) calls
   `annotation.setRetryAnalyzer(RetryAnalyzer.class)` on every test. Don't
   add `retryAnalyzer = RetryAnalyzer.class` by hand on new `@Test` methods.

5. **API tests** use `api/ApiClient.authenticated()` for a RestAssured spec
   with auth and base URL already set — don't hand-roll login per test. The
   real API mount point is `/api/v1`, not `/api` (confirmed by calling the
   API directly and observing the response, not by reading backend code).

6. **`api/FlakyAppointmentsApiTest` is deliberately flaky and must not be
   "fixed."** It calls `/appointments?_flaky=0.3`, which makes HealthHub's
   API return a random HTTP 500 roughly 30% of the time on purpose (confirmed
   by calling the endpoint directly), as a target for the retry listener and
   for a debugging exercise.

7. **The JUnit Platform runner is a secondary example**, not the one `mvn
   test` runs by default. Run it explicitly with `mvn test -P junit-backup`.
   Don't add it to `testng.xml`. (With both TestNG and the JUnit Platform on
   the classpath, Surefire's auto-detection silently picks the JUnit
   Platform provider and ignores `testng.xml` — `pom.xml` avoids this with
   two profiles, `testng` (default) and `junit-backup`, that each force the
   right provider explicitly. Don't remove those profiles or go back to
   plain auto-detection.)

## Reporting

Allure results land in `target/allure-results/`. Serve locally with
`mvn allure:serve`.

## Agents and skills

Equivalent agents/skills live in both `.claude/` (YAML-frontmatter format,
for Claude Code) and `.github/` (plain markdown, this format):

| Agent/skill | Location | Trigger phrases |
|---|---|---|
| `flaky-test-diagnostician` | `.github/agents/` | "why is this test flaky", "diagnose the flaky test" |
| `conventions-reviewer` | `.github/agents/` | "review this against conventions" |
| `writing-page-objects` | `.github/skills/` | "create a page object for X" |
