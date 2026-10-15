package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Locators verified against the live running app (DOM query of
 * data-testid values on /medications), not application source.
 * Each medication row is rendered as medication-item-{id}.
 */
public class MedicationsPage extends BasePage {

    private final By medicationsList = testId("medications-list");
    private final By medicationItems = By.cssSelector("[data-testid^='medication-item-']");

    public MedicationsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        waitVisible(medicationsList);
        return true;
    }

    public int medicationCount() {
        // The list is fetched from the API after navigation — wait for rows.
        return wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(medicationItems, 0)).size();
    }
}
