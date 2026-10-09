package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Locators verified against the live running app (Playwright MCP DOM
 * query against /appointments and its "new appointment" form), not
 * application source, real data-testid values.
 */
public class AppointmentsPage extends BasePage {

    private final By newAppointmentBtn = testId("appointments-new-btn");
    private final By table = testId("appointments-table");
    private final By emptyState = testId("appointments-empty");
    private final By searchInput = testId("appointments-search");

    // Form (opens as a modal when "new" is clicked)
    private final By titleInput = testId("appointment-title-input");
    private final By typeSelect = testId("appointment-type-select");
    private final By dateInput = testId("appointment-date-input");
    private final By timeInput = testId("appointment-time-input");
    private final By providerInput = testId("appointment-provider-input");
    private final By saveBtn = testId("appointment-save-btn");

    public AppointmentsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(table) || isDisplayed(emptyState);
    }

    public AppointmentsPage openNewAppointmentForm() {
        click(newAppointmentBtn);
        return this;
    }

    public AppointmentsPage fillAndSaveAppointment(String title, String type, String date,
                                                     String time, String provider) {
        type(titleInput, title);
        selectByVisibleText(typeSelect, type);
        // Native date/time inputs ignore sendKeys' character order reliably
        // across locales — set the value directly instead (see BasePage).
        setNativeValue(dateInput, date);
        setNativeValue(timeInput, time);
        type(providerInput, provider);
        click(saveBtn);
        return this;
    }

    public boolean rowExistsForTitle(String title) {
        By rowWithTitle = By.xpath(
                "//*[@data-testid='appointments-table']//*[contains(text(), '" + title + "')]");
        try {
            // Saving triggers an API call + table refetch, not an instant DOM
            // update — wait for the row rather than snapshotting immediately.
            return wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .presenceOfElementLocated(rowWithTitle)) != null;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    private void selectByVisibleText(By locator, String visibleText) {
        new org.openqa.selenium.support.ui.Select(waitVisible(locator)).selectByVisibleText(visibleText);
    }
}
