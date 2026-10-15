---
name: selenium-exception-debug
description: Diagnoses and fixes the most common Selenium WebDriver exceptions in Java tests. Use when a Selenium test throws StaleElementReferenceException, ElementNotInteractableException, NoSuchElementException, ElementClickInterceptedException, or similar WebDriver errors.
metadata:
  author: Niche Thyself
  version: "1.0"
  category: debugging
compatibility: Selenium WebDriver 4.x (Java). Exception types are in org.openqa.selenium package.
---

# Selenium Exception Debugging

## Workflow

```
Progress:
- [ ] Step 1: Identify the exception type from the stack trace
- [ ] Step 2: Diagnose the root cause using the questions for that exception type
- [ ] Step 3: Apply the targeted fix pattern
- [ ] Step 4: Validate — run the test 5+ times without the failure
```

## Exception quick-reference

| Exception | Most common cause | Quick fix |
|---|---|---|
| `StaleElementReferenceException` | DOM refreshed after element was found | Re-locate element after the refresh |
| `NoSuchElementException` | Element not in DOM when `findElement` runs | Add explicit wait before finding |
| `ElementNotInteractableException` | Element in DOM but not visible/enabled | Wait for visibility or scroll into view |
| `ElementClickInterceptedException` | Another element overlays the target | Wait for overlay to disappear; use JS click |
| `TimeoutException` | ExpectedCondition never met in time | Increase timeout; check locator; check page state |
| `InvalidSelectorException` | CSS or XPath syntax error | Fix the selector string |
| `WebDriverException: target frame detached` | iFrame navigated away | Re-switch to the frame |
| `MoveTargetOutOfBoundsException` | Actions chain target outside viewport | Scroll element into view first |

Detailed guide: See [references/exception-guide.md](references/exception-guide.md)

---

## StaleElementReferenceException

**Why it happens**: The element was found, then the DOM changed (AJAX update, page refresh, Angular/React re-render), making the reference invalid.

```java
// ❌ Stale — element found before DOM update, used after it
WebElement row = driver.findElement(By.cssSelector("[data-testid='patient-row']"));
searchBox.sendKeys("Smith");      // triggers AJAX reload of the table
row.click();                      // StaleElementReferenceException — row was re-rendered

// ✅ Fixed — re-locate after the DOM update completes
searchBox.sendKeys("Smith");
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector("[data-testid='loading-spinner']")
));
driver.findElement(By.cssSelector("[data-testid='patient-row']")).click();

// ✅ Fixed — FluentWait with StaleElementReferenceException ignored
FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(15))
    .pollingEvery(Duration.ofMillis(300))
    .ignoring(StaleElementReferenceException.class);

fluentWait.until(driver -> {
    driver.findElement(By.cssSelector("[data-testid='patient-row']")).click();
    return true;
});
```

---

## NoSuchElementException

**Why it happens**: `driver.findElement()` ran before the element appeared in the DOM.

```java
// ❌ No wait — element loads asynchronously
String errorText = driver.findElement(By.cssSelector("[data-testid='error-msg']")).getText();

// ✅ Fixed — wait for element to be visible before reading it
WebElement error = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='error-msg']"))
);
String errorText = error.getText();

// ✅ For elements that may or may not exist (optional elements):
private boolean isElementPresent(By locator) {
    try {
        driver.findElement(locator);
        return true;
    } catch (NoSuchElementException e) {
        return false;
    }
}
```

---

## ElementNotInteractableException

**Why it happens**: Element is in the DOM but is hidden (`display:none`, `visibility:hidden`, `opacity:0`), disabled, or off-screen.

```java
// ❌ Element is in DOM but not visible yet
driver.findElement(By.id("password")).sendKeys("TestPass1!");

// ✅ Fixed — wait for visibility specifically
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")))
    .sendKeys("TestPass1!");

// ✅ For elements that require scrolling into view:
WebElement element = driver.findElement(By.cssSelector("[data-testid='save-btn']"));
((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
wait.until(ExpectedConditions.elementToBeClickable(element)).click();
```

---

## ElementClickInterceptedException

**Why it happens**: A cookie banner, modal backdrop, toast notification, or sticky header is positioned over the target element.

```java
// ❌ Click intercepted by a loading overlay
driver.findElement(By.cssSelector("[data-testid='save-btn']")).click();

// ✅ Fixed — wait for the intercepting element to disappear first
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector("[data-testid='loading-overlay']")
));
wait.until(ExpectedConditions.elementToBeClickable(
    By.cssSelector("[data-testid='save-btn']")
)).click();

// ✅ Alternative — JavaScript click (bypasses overlay; use sparingly)
WebElement btn = driver.findElement(By.cssSelector("[data-testid='save-btn']"));
((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
// Note: JS click bypasses real user interaction — only use when overlay cannot be dismissed
```

---

## TimeoutException

**Why it happens**: `WebDriverWait` reached its timeout because the `ExpectedCondition` was never satisfied.

```java
// Diagnosis checklist:
// 1. Is the locator correct? Print driver.getPageSource() to check
// 2. Is the element inside an iFrame? (need driver.switchTo().frame() first)
// 3. Is the timeout long enough for the real-world page load time?
// 4. Did a previous action fail silently, leaving the page in an unexpected state?

// ✅ Diagnostic: screenshot on timeout for CI investigation
try {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='result']")));
} catch (TimeoutException e) {
    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    FileUtils.copyFile(screenshot, new File("target/screenshots/timeout-" + System.currentTimeMillis() + ".png"));
    throw e; // rethrow so the test still fails correctly
}
```

---

## iFrame handling

```java
// Switch to iFrame before interacting with elements inside it
driver.switchTo().frame(driver.findElement(By.cssSelector("[data-testid='embedded-form']")));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("patient-name")))
    .sendKeys("TestFirst");

// Return to main document after finishing with the iFrame
driver.switchTo().defaultContent();
```

## Tips

- Always share the **full stack trace**, not just the exception name
- Mention what action triggered the exception (click, sendKeys, getText)
- State whether it's consistent or intermittent — intermittent usually means timing; consistent usually means wrong locator
