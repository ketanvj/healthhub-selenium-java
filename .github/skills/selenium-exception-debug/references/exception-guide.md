# Selenium Exception Guide — Detailed Reference

## Contents
- Full exception list with package names
- Root cause decision tree
- Diagnostic code snippets
- Prevention checklist

---

## Full exception list

| Exception class | Package | Most common cause |
|---|---|---|
| `NoSuchElementException` | `org.openqa.selenium` | `findElement` ran before element was in DOM |
| `StaleElementReferenceException` | `org.openqa.selenium` | DOM updated after element was found |
| `ElementNotInteractableException` | `org.openqa.selenium` | Element hidden, disabled, or off-screen |
| `ElementClickInterceptedException` | `org.openqa.selenium` | Overlay/modal covers the element |
| `TimeoutException` | `org.openqa.selenium` | ExpectedCondition not met before timeout |
| `InvalidSelectorException` | `org.openqa.selenium` | CSS or XPath syntax error in selector |
| `NoSuchFrameException` | `org.openqa.selenium` | `switchTo().frame()` target doesn't exist |
| `NoSuchWindowException` | `org.openqa.selenium` | Browser tab/window closed or handle is stale |
| `NoAlertPresentException` | `org.openqa.selenium` | `switchTo().alert()` when no alert is open |
| `MoveTargetOutOfBoundsException` | `org.openqa.selenium.interactions` | Actions target is outside the viewport |
| `JavascriptException` | `org.openqa.selenium` | Injected JS threw an error |
| `SessionNotCreatedException` | `org.openqa.selenium.remote` | WebDriver binary version mismatch |
| `WebDriverException` (target frame detached) | `org.openqa.selenium` | iFrame navigated away mid-interaction |
| `UnhandledAlertException` | `org.openqa.selenium` | JavaScript alert opened unexpectedly |
| `InsecureCertificateException` | `org.openqa.selenium` | Self-signed cert not accepted |

---

## Root cause decision tree

```
Test throws an exception
│
├── NoSuchElementException
│   ├── Always fails → wrong locator (inspect DOM)
│   └── Sometimes fails → timing: add explicit wait
│
├── StaleElementReferenceException
│   ├── After AJAX call → re-locate after DOM settles
│   ├── After page navigation → don't cache cross-page elements
│   └── In parallel tests → shared WebElement (use ThreadLocal or re-find)
│
├── ElementNotInteractableException
│   ├── Element is hidden → wait for visibilityOfElementLocated
│   ├── Element is disabled → wait for elementToBeClickable
│   └── Element is off-screen → scroll into view then click
│
├── ElementClickInterceptedException
│   ├── Loading overlay → wait for invisibilityOfElementLocated(overlay)
│   ├── Sticky header → scroll element into view before clicking
│   ├── Cookie banner → dismiss it in @BeforeMethod
│   └── Toast notification → wait for it to disappear
│
├── TimeoutException
│   ├── Wrong locator → check selector against live DOM
│   ├── Element inside iFrame → switchTo().frame() first
│   ├── Timeout too short → increase Duration.ofSeconds()
│   └── Page in unexpected state → check previous step succeeded
│
└── SessionNotCreatedException
    ├── Driver binary outdated → run WebDriverManager.chromedriver().setup()
    └── Chrome and ChromeDriver version mismatch → align versions
```

---

## Diagnostic code snippets

### Print page source to find locator issues

```java
// Run this when NoSuchElementException or TimeoutException occurs
// and you suspect the locator is wrong
System.out.println("=== PAGE SOURCE ===");
System.out.println(driver.getPageSource());
System.out.println("=== CURRENT URL ===");
System.out.println(driver.getCurrentUrl());
```

### Screenshot on any exception (add to BaseTest)

```java
@AfterMethod(alwaysRun = true)
public void captureScreenshotOnFailure(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String name = result.getName() + "_" + System.currentTimeMillis() + ".png";
        try {
            FileUtils.copyFile(screenshot, new File("target/screenshots/" + name));
            System.out.println("Screenshot saved: " + name);
        } catch (IOException e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
        }
    }
}
```

### Detect iFrame context issues

```java
// If elements are inside an iFrame, always switch first
// Check if iFrame exists before switching
List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
System.out.println("iFrames on page: " + iframes.size());
for (int i = 0; i < iframes.size(); i++) {
    System.out.println("iFrame " + i + " src: " + iframes.get(i).getAttribute("src"));
}

// Switch to iFrame by index, name, or element
driver.switchTo().frame(0);                          // by index
driver.switchTo().frame("embedded-form");           // by name/id attribute
driver.switchTo().frame(iframes.get(0));            // by WebElement
driver.switchTo().defaultContent();                 // return to main document
```

### Check element state before interacting

```java
private void diagnoseElement(By locator) {
    try {
        WebElement el = driver.findElement(locator);
        System.out.println("Found: " + locator);
        System.out.println("  Displayed: " + el.isDisplayed());
        System.out.println("  Enabled:   " + el.isEnabled());
        System.out.println("  Selected:  " + el.isSelected());
        System.out.println("  Tag:       " + el.getTagName());
        System.out.println("  Location:  " + el.getLocation());
        System.out.println("  Size:      " + el.getSize());
    } catch (NoSuchElementException e) {
        System.out.println("NOT FOUND: " + locator);
    }
}
```

---

## Prevention checklist

Apply these practices to prevent the most common exceptions:

### For all Selenium tests
- [ ] Never use `Thread.sleep()` — use `WebDriverWait` + `ExpectedConditions`
- [ ] Never mix implicit and explicit waits
- [ ] Always check that the correct URL / page is loaded before interacting
- [ ] Add `captureScreenshotOnFailure` to `@AfterMethod` in BaseTest
- [ ] Use `WebDriverManager` to auto-manage driver binaries

### For page objects
- [ ] Never store `WebElement` references across page navigations
- [ ] Always call `PageFactory.initElements(driver, this)` in the constructor
- [ ] Wait inside action methods before each interaction — don't assume elements are ready
- [ ] Use `try/catch NoSuchElementException` only for genuinely optional elements

### For dynamic / AJAX pages
- [ ] Wait for loading spinners to disappear after triggering AJAX actions
- [ ] Use `FluentWait` with `StaleElementReferenceException` ignored for high-churn lists
- [ ] Re-locate elements after any action that triggers a full or partial DOM refresh
- [ ] For React/Angular: wait for framework-level idle state if available

### For parallel execution
- [ ] Use `ThreadLocal<WebDriver>` in `DriverFactory` — never share driver across threads
- [ ] Never use `static WebElement` fields shared across test instances
- [ ] Each test thread must manage its own driver lifecycle (init + quit)
- [ ] Use unique test data per thread (factory with sequence numbers)
