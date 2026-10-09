package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Locators verified against the live running app (Playwright MCP DOM
 * query against /login), not application source, not guessed values.
 */
public class LoginPage extends BasePage {

    private final By emailInput = testId("auth-email-input");
    private final By passwordInput = testId("auth-password-input");
    private final By submitButton = testId("auth-submit-button");
    private final By errorMessage = testId("auth-error-message");
    private final By rememberCheckbox = testId("auth-remember-checkbox");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage loginAs(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(submitButton);
        return new DashboardPage(driver);
    }

    public LoginPage attemptLogin(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(submitButton);
        return this;
    }

    public String getErrorMessage() {
        return textOf(errorMessage);
    }

    public boolean hasErrorMessage() {
        return isDisplayed(errorMessage);
    }
}
