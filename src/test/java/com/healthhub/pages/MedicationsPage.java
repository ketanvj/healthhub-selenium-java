package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * Locators verified against the live running app (Playwright MCP DOM
 * query against /medications), not application source, real data-testid
 * values. View-only: create/edit/delete controls are deliberately not modelled.
 */
public class MedicationsPage extends BasePage {

    private final By list = testId("medications-list");
    // Rows are medication-item-{n}; matched by testid prefix.
    private final By items = By.cssSelector("[data-testid^='medication-item-']");

    public MedicationsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            return waitVisible(list).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int medicationCount() {
        return driver.findElements(items).size();
    }
}
