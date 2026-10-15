# HealthHub Selenium + Gherkin + Java

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
Override environment with system properties, e.g.:

```bash
mvn test -Dbrowser=firefox -Dheadless=true
```

## Directory structure

Two folders also carry their own `CLAUDE.md` with folder-specific
conventions (locator verification steps, gotchas already hit and fixed
once, DI requirements) that this file doesn't repeat — Claude Code loads
them automatically when you're working inside that folder:
`src/test/java/com/healthhub/pages/CLAUDE.md` and
`src/test/java/com/healthhub/stepdefinitions/CLAUDE.md`.

```
src/test/java/com/healthhub/
├── config/            ConfigReader — reads config.properties, -D overrides win
├── driver/            DriverManager — ThreadLocal<WebDriver>, chrome/firefox via WebDriverManager
├── pages/             Page Objects — all extend BasePage (see its own CLAUDE.md)
├── hooks/             Cucumber @Before/@After — one browser per scenario, screenshot-on-failure
├── listeners/         TestListener (wires RetryAnalyzer in), RetryAnalyzer (max 2 retries)
├── stepdefinitions/   Step classes + ScenarioContext (see its own CLAUDE.md)
├── runners/           TestNGRunner (primary), RunCucumberJUnitTest (JUnit Platform backup)
├── api/               Plain TestNG tests hitting the REST API directly (no Gherkin)
└── dataprovider/       TestNG @DataProvider classes, for plain API tests only

src/test/resources/
├── config.properties  base.url, api.base.url, browser, headless, test user creds
└── features/          *.feature files
```

## Critical conventions

### 1. Page Objects
- Every page extends `BasePage`, which supplies `waitVisible`, `waitClickable`,
  `click`, `type`, `textOf`, `isDisplayed`, and `testId(String)`.
- `testId(id)` builds a `By` from the app's `data-testid` attributes — **always
  locate via `data-testid`**, never CSS classes or XPath text matches.
  Discover the real testid by inspecting the live running app (see
  "Discovering real locators" above) before inventing one.
- Navigate via real UI interaction (click the sidebar link, submit the form),
  not `driver.get(url)` reconstruction — mirrors the Playwright sibling
  framework's "navigate via sidebar, not direct page.goto()" rule.
- No assertions inside page objects — steps/tests own assertions.

### 2. Gherkin vs TestNG `@DataProvider`
Two different data-driven mechanisms, used for two different things —
don't mix them:
- **Scenario Outline + Examples** → data-driven *Gherkin* scenarios
  (`login.feature`). Use this when the data variation is part of the
  business-readable behavior.
- **TestNG `@DataProvider`** (`dataprovider/`) → plain, non-Gherkin API
  contract tests (`api/AppointmentsApiTest`). Use this when the test is a
  technical check where Gherkin's business-readable framing adds no value.

### 3. Shared state across step classes
Step definition classes don't reach into each other directly. Inject
`ScenarioContext` (Cucumber-PicoContainer DI, no-arg constructor required)
into each step class's constructor and read/write page objects through it.

### 4. Retry is a listener, not an annotation
Retry-on-failure is wired globally in `testng.xml` via
`TestListener` (an `IAnnotationTransformer`) calling
`annotation.setRetryAnalyzer(RetryAnalyzer.class)` on every test — not by
adding `retryAnalyzer = RetryAnalyzer.class` per `@Test`. Don't add that
annotation attribute by hand; it's redundant and easy to forget on new tests.

### 5. API tests
`api/ApiClient.authenticated()` logs in once (cached JWT) and returns a
RestAssured `RequestSpecification` with the bearer token and base URL
already set. Use it instead of hand-rolling auth per test. `baseUri` is
`config.properties`' `api.base.url` — the real mount point is `/api/v1`,
**not** `/api`; this was confirmed by calling the API directly and
observing the response, not by reading backend code — do the same if it
ever needs re-deriving.

### 6. The deliberate flaky test
`api/FlakyAppointmentsApiTest` calls `/appointments?_flaky=0.3` on purpose —
that query param makes HealthHub's API return a random HTTP 500
(`{"success":false,"error":{"code":"FLAKY_ERROR",...}}`) roughly 30% of
the time, confirmed by calling the endpoint directly and observing the
behavior. **Do not fix this test.** It
exists so RetryAnalyzer has a real failure mode to retry against, and so
Day 4's debugging exercise has a genuinely flaky (not just badly-written)
test to diagnose.

### 7. Backup runner and provider profiles
`runners/RunCucumberJUnitTest` (JUnit Platform Suite) runs the same
`.feature` files as a secondary example. Run it directly when comparing
runners:

```bash
mvn test -P junit-backup
```

This exists because of a real gotcha worth knowing: with both TestNG and
the JUnit Platform on the classpath, Maven Surefire's provider
auto-detection silently picks the JUnit Platform provider and ignores
`testng.xml` entirely — `mvn test` with no profile would then run *only*
`RunCucumberJUnitTest` and skip `TestNGRunner`, the API tests, and the
flaky test completely, with no error. `pom.xml` avoids this with two
Maven profiles that each force the right provider explicitly: `testng`
(active by default, forces `org.apache.maven.surefire:surefire-testng`)
and `junit-backup` (forces `surefire-junit-platform`, restricted to just
`RunCucumberJUnitTest` via `<includes>`). Plain `mvn test` uses the
`testng` profile.

## Reporting

Allure results land in `target/allure-results/` (via `allure-cucumber7-jvm`
+ `allure-testng`, both woven in through the `aspectjweaver` javaagent in
`pom.xml`'s surefire config). Serve locally:

```bash
mvn allure:serve
```

## Agents and skills

Mirrors the `.claude/agents/` and `.claude/skills/` pattern used in the
sibling `healthhub-playwright` framework, adapted for this stack.

### Agents (`.claude/agents/`, isolated context)
| Agent | Trigger phrases |
|-------|------------------|
| `flaky-test-diagnostician` | "why is this test flaky", "diagnose the flaky test", "is this a real bug or flakiness" |
| `conventions-reviewer` | "review this against conventions", "check this follows the framework rules", "review my page object / step def" |

### Skills (`.claude/skills/`)
| Skill | Trigger phrases |
|-------|------------------|
| `writing-page-objects` | "create a page object for X", "add a POM for X" |

The same agents/skill exist for GitHub Copilot under `.github/agents/` and
`.github/skills/` (plain markdown, this one using YAML frontmatter where it
matters — `tools:`/`agents:` restrictions — unlike these two). **Copilot's
side now has more than this file does** (added Oct 14, `.github/` only, no
`.claude/` equivalent yet): agents `test-report-reader` (tool-restricted,
read-only), `locator-researcher` and `convention-checker` (subagents), and
`page-coverage-builder` (coordinator, delegates to those two); skills
`selenium-wait-strategies` and `selenium-exception-debug`. See
`.github/copilot-instructions.md`'s own table for the full, current list —
don't assume this file's two tables above are the complete picture anymore.
Custom instructions equivalent to this file live in
`.github/copilot-instructions.md`.
