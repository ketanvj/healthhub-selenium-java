# Worked example: SettingsPage

Built entirely from live DOM inspection — no application source was read.
After navigating to `/settings` in the running app and logging in,
running this via the Playwright MCP `browser_evaluate` tool:

```js
() => Array.from(document.querySelectorAll('[data-testid]')).map(el => ({
  testid: el.getAttribute('data-testid'),
  tag: el.tagName.toLowerCase(),
  role: el.getAttribute('role') || null,
  ariaChecked: el.getAttribute('aria-checked')
}))
```

returned (relevant rows only):

```json
[
  { "testid": "settings-theme-toggle",         "tag": "button", "role": "switch", "ariaChecked": "false" },
  { "testid": "settings-notifications-toggle", "tag": "button", "role": "switch", "ariaChecked": "true" },
  { "testid": "settings-email-reminders",      "tag": "input",  "role": null,     "ariaChecked": null },
  { "testid": "settings-save-btn",             "tag": "button", "role": null,     "ariaChecked": null }
]
```

That one query answered two questions at once: the real testids, and how
the toggles actually represent their state — `role="switch"` buttons with
an `aria-checked` attribute, not native checkbox inputs. No existing page
object in this folder had hit that pattern before, so there was no
precedent to copy; reading the live DOM is what revealed it, where
reading nothing (or guessing) would have led to a `<input type="checkbox">`
assumption that's simply wrong for this app.

Reached from the sidebar via `nav-settings` — confirmed the same way, by
listing every `[data-testid]` on the dashboard page and finding the
`nav-{label-slug}` pattern among the nav links.

```java
package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SettingsPage extends BasePage {

    private final By themeToggle = testId("settings-theme-toggle");
    private final By notificationsToggle = testId("settings-notifications-toggle");
    private final By saveButton = testId("settings-save-btn");

    public SettingsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(themeToggle);
    }

    public SettingsPage toggleTheme() {
        click(themeToggle);
        return this;
    }

    public SettingsPage toggleNotifications() {
        click(notificationsToggle);
        return this;
    }

    public boolean isDarkThemeOn() {
        return "true".equals(driver.findElement(themeToggle).getAttribute("aria-checked"));
    }

    public boolean isNotificationsOn() {
        return "true".equals(driver.findElement(notificationsToggle).getAttribute("aria-checked"));
    }

    public SettingsPage save() {
        click(saveButton);
        return this;
    }
}
```

## Matching step definitions

```java
package com.healthhub.stepdefinitions;

import com.healthhub.pages.SettingsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

public class SettingsSteps {

    private final ScenarioContext context;

    public SettingsSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I turn on dark theme")
    public void iTurnOnDarkTheme() {
        SettingsPage settingsPage = new SettingsPage(
                com.healthhub.driver.DriverManager.getDriver());
        settingsPage.toggleTheme().save();
        context.setSettingsPage(settingsPage);
    }

    @Then("dark theme should be on")
    public void darkThemeShouldBeOn() {
        assertTrue(context.getSettingsPage().isDarkThemeOn());
    }
}
```

This is illustrative, not wired into the framework — adding it for real
means also adding `getSettingsPage()`/`setSettingsPage()` to
`ScenarioContext` and a `settings.feature` file, following the same
pattern as `appointments.feature`/`AppointmentSteps`. The two fields the
real Settings page also exposes (`settings-email-reminders`,
`settings-delete-account-btn`) were deliberately left out of
`SettingsPage` above since nothing asked for them yet — add locators for
them the same way, by querying the live DOM, if a real test needs them.
