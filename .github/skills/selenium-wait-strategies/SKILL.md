---
name: selenium-wait-strategies
description: Replaces brittle Thread.sleep() and implicit waits with correct Selenium explicit wait patterns using WebDriverWait, FluentWait, and ExpectedConditions. Use when tests are flaky due to timing issues, when asked to fix hardcoded waits, or when adding wait logic to new Selenium Java tests.
metadata:
  author: Niche Thyself
  version: "1.0"
  category: debugging
compatibility: Selenium WebDriver 4.x (Java). WebDriverWait and FluentWait are in selenium-java artifact.
---

# Selenium Wait Strategies

## Workflow

```
Progress:
- [ ] Step 1: Identify the wait type needed (element state, URL, title, custom condition)
- [ ] Step 2: Choose the right wait class (WebDriverWait vs FluentWait)
- [ ] Step 3: Select the correct ExpectedConditions method
- [ ] Step 4: Replace Thread.sleep() or implicit wait with the explicit wait
- [ ] Step 5: Validate — run the test 5+ times, confirm 0% failure rate
```

## Wait type decision

| I need to wait for... | Use |
|---|---|
| Element to appear in DOM | `visibilityOfElementLocated` |
| Element to be clickable | `elementToBeClickable` |
| Element text to match | `textToBePresentInElement` |
| Element to disappear | `invisibilityOf` |
| URL to change | `urlContains` / `urlToBe` |
| Page title to change | `titleContains` |
| Alert to appear | `alertIsPresent` |
| Custom / polling condition | `FluentWait` with custom `Function` |
| AJAX / JS to complete | Custom JS executor condition |
| Multiple elements to load | `numberOfElementsToBeMoreThan` |

Full reference: See [references/expected-conditions.md](references/expected-conditions.md)

## Core setup (put in base class)

```java
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import java.time.Duration;

// In BaseTest or BasePage:
protected WebDriverWait wait;

@BeforeMethod
public void setUp() {
    driver = DriverFactory.getDriver();
    wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    // Do NOT use driver.manage().timeouts().implicitlyWait() alongside WebDriverWait
    // Mixing implicit and explicit waits causes unpredictable behaviour
}
```

## Pattern 1: WebDriverWait — standard explicit wait

```java
// ❌ Replace this:
Thread.sleep(3000);
driver.findElement(By.id("submit")).click();

// ✅ With this:
wait.until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();

// ❌ Replace this:
Thread.sleep(2000);
String msg = driver.findElement(By.cssSelector("[data-testid='success-msg']")).getText();

// ✅ With this:
WebElement msg = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='success-msg']"))
);
assertThat(msg.getText()).isEqualTo("Patient saved successfully.");
```

## Pattern 2: WebDriverWait — chaining waits in page objects

```java
public DashboardPage clickSaveAndWait() {
    wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

    // Wait for success toast, then for it to disappear (confirms action completed)
    WebElement toast = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='toast-success']"))
    );
    wait.until(ExpectedConditions.invisibilityOf(toast));

    return new DashboardPage(driver);
}
```

## Pattern 3: FluentWait — custom polling for dynamic content

Use FluentWait when: the element appears slowly and inconsistently, you want to ignore specific exceptions during polling, or you need a custom polling interval.

```java
import org.openqa.selenium.support.ui.FluentWait;
import java.util.function.Function;

FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(20))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class)
    .ignoring(StaleElementReferenceException.class);

// Wait for patient search results to load
WebElement firstResult = fluentWait.until(driver ->
    driver.findElement(By.cssSelector("[data-testid='result-row']:first-child"))
);
```

## Pattern 4: Custom JavaScript wait — for AJAX-heavy pages

```java
private void waitForAjaxComplete() {
    new WebDriverWait(driver, Duration.ofSeconds(15)).until(webDriver ->
        (Boolean) ((JavascriptExecutor) webDriver)
            .executeScript("return jQuery.active === 0")
    );
}

private void waitForPageLoad() {
    new WebDriverWait(driver, Duration.ofSeconds(15)).until(webDriver ->
        ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete")
    );
}
```

## Pattern 5: Waiting for URL or title change

```java
// After clicking a nav link
wait.until(ExpectedConditions.urlContains("/dashboard"));

// After triggering a page-title-changing action
wait.until(ExpectedConditions.titleContains("Patient Record"));
```

## Anti-patterns to avoid

```java
// ❌ Never mix implicit and explicit waits — causes doubled timeout behaviour
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // + WebDriverWait = chaos

// ❌ Never use Thread.sleep() as a wait strategy
Thread.sleep(5000); // blocks test thread; doesn't respond to element state

// ❌ Never catch TimeoutException to hide a broken wait
try {
    wait.until(ExpectedConditions.visibilityOf(element));
} catch (TimeoutException e) {
    // silently swallow — now the test proceeds with the element not visible
}

// ❌ Never findElement without a wait when element loads dynamically
driver.findElement(By.id("async-result")).getText(); // NoSuchElementException likely
```

## Find all Thread.sleep calls in the project

```bash
python scripts/find_bad_waits.py --dir src/test/java/
```

See [scripts/find_bad_waits.py](scripts/find_bad_waits.py) for details.
