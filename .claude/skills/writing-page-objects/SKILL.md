---
name: writing-page-objects
description: Create Page Object classes for the HealthHub Selenium + Gherkin + Java framework. Use when adding a new page, extracting locators/interactions out of a step definition into a POM, or when asked to create a page object. TRIGGER on "create a page object", "add a page object for X", "extract page interactions", "make a page class".
---

# Writing Page Objects

## Rules

1. Extend `BasePage` (`com.healthhub.pages.BasePage`) — it supplies the
   `WebDriver`/`WebDriverWait` and the helper methods below.
2. Locate everything via `testId(String)`, which builds a `By` from the
   app's `data-testid` attribute. **Verify the real testid against the
   live running app before writing it** — don't guess, and don't read
   application source (participants don't have it). Navigate to the page
   with Playwright MCP and query the DOM:
   `() => Array.from(document.querySelectorAll('[data-testid]')).map(el => el.getAttribute('data-testid'))`
   via the `browser_evaluate` tool — or inspect manually in DevTools if
   no MCP is configured.
3. Use the inherited helpers instead of calling `driver`/`wait` directly:
   `waitVisible(locator)`, `waitClickable(locator)`, `click(locator)`,
   `type(locator, text)`, `textOf(locator)`, `isDisplayed(locator)`.
4. **No assertions inside page objects** — the step definition or test
   method owns assertions. A page object only drives the UI and returns
   state (strings, booleans); it never calls a `Then`-style check itself.
5. Navigate via a real UI interaction (click the sidebar link, submit a
   form) — never `driver.get(url)` to "jump" to a page. If the page is
   reached from the sidebar, the nav-link testid follows the pattern
   `nav-{label-slug}`, observed live, e.g. `nav-appointments`.
6. File goes in `src/test/java/com/healthhub/pages/<Feature>Page.java`.

## Template

```java
package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FeaturePage extends BasePage {

    private final By someField = testId("feature-some-field-input");
    private final By saveButton = testId("feature-save-btn");

    public FeaturePage(WebDriver driver) {
        super(driver);
    }

    public void fillSomeField(String value) {
        type(someField, value);
    }

    public void save() {
        click(saveButton);
    }

    public boolean isSaved() {
        // return observable state only — the step/test decides what it means
        return isDisplayed(testId("feature-success-message"));
    }
}
```

## Worked example against a real HealthHub page

See [references/settings-page-example.md](references/settings-page-example.md)
for a full `SettingsPage` built entirely from live DOM inspection (no
source access), including a locator pattern (`role="switch"` +
`aria-checked`) that needed a small extrapolation beyond this skill's
basic rules — plus a matching step definition.

## Scaffolding script

`scripts/new-page-object.sh <FeatureName>` prints a ready-to-save skeleton
with the right package, imports, and constructor already in place — see
[scripts/new-page-object.sh](scripts/new-page-object.sh). It's a starting
point, not a substitute for verifying real testids against the live app.
