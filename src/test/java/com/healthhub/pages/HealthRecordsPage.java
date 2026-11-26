package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Locators verified against the live running app (Playwright MCP DOM
 * query against /health-records), not application source. Rows carry a
 * per-record id (records-row-4, records-row-5, ...), so they are matched
 * by the verified "records-row-" testid prefix.
 */
public class HealthRecordsPage extends BasePage {

    private final By table = testId("records-table");
    private final By rows = By.cssSelector("[data-testid^='records-row-']");

    public HealthRecordsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(table);
    }

    public int getRecordRowCount() {
        waitVisible(table);
        try {
            // Records load via an API call after the table renders — wait for rows.
            wait.until(ExpectedConditions.presenceOfElementLocated(rows));
        } catch (TimeoutException e) {
            return 0;
        }
        return driver.findElements(rows).size();
    }
}
