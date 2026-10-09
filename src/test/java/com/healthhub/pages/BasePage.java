package com.healthhub.pages;

import com.healthhub.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Every Page Object extends this. Convention mirrors healthhub-playwright's
 * BasePage: one shared wait strategy, no assertions in here, page objects
 * describe the page, tests decide what "correct" means.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.explicitWaitSeconds()));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement field = waitVisible(locator);
        field.clear();
        field.sendKeys(text);
    }

    protected String textOf(By locator) {
        return waitVisible(locator).getText();
    }

    /**
     * Sets a native input's value via JS and dispatches a real 'input' event,
     * instead of sendKeys. HTML5 date/time inputs render as segmented pickers
     * (month/day/year, hour/minute/AM-PM) whose keystroke order is locale- and
     * OS-dependent, so typing an ISO string like "2026-11-15" character by
     * character often lands in the wrong segment. Setting .value directly
     * sidesteps that — but React wraps the native value setter to track
     * controlled-input state, so we must call the *native* setter via the
     * prototype descriptor (not element.value = ...) before dispatching the
     * event, or React never sees the change.
     */
    protected void setNativeValue(By locator, String value) {
        WebElement element = waitVisible(locator);
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
                "const el = arguments[0], value = arguments[1];" +
                "const proto = Object.getPrototypeOf(el);" +
                "const desc = Object.getOwnPropertyDescriptor(proto, 'value');" +
                "desc.set.call(el, value);" +
                "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));",
                element, value);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    protected By testId(String id) {
        return By.cssSelector("[data-testid='" + id + "']");
    }
}
