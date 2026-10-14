package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Locators verified against the live running app (DOM query of the
 * data-testid attributes on /medications), not application source.
 * Each medication renders as an li with testid "medication-item-{id}".
 */
public class MedicationsPage extends BasePage {

    private final By list = testId("medications-list");
    private final By items = By.cssSelector("[data-testid^='medication-item-']");

    public MedicationsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(list);
    }

    public int medicationCount() {
        waitVisible(list);
        return driver.findElements(items).size();
    }
}
