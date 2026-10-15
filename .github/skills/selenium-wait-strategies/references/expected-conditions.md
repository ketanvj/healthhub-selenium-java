# ExpectedConditions — Complete Reference

## Contents
- Element state conditions
- Element presence conditions
- Text and attribute conditions
- URL and title conditions
- Alert conditions
- List/count conditions
- Negation conditions
- Composing multiple conditions

---

## Element state conditions

| Method | Waits until... | Returns |
|---|---|---|
| `elementToBeClickable(locator)` | Element is visible AND enabled | `WebElement` |
| `elementToBeClickable(element)` | Existing WebElement becomes clickable | `WebElement` |
| `visibilityOfElementLocated(locator)` | Element is in DOM and visible | `WebElement` |
| `visibilityOf(element)` | Existing WebElement becomes visible | `WebElement` |
| `invisibilityOfElementLocated(locator)` | Element is not visible or not in DOM | `Boolean` |
| `invisibilityOf(element)` | Existing WebElement becomes invisible | `Boolean` |
| `elementToBeSelected(element)` | Checkbox/radio is selected | `Boolean` |
| `elementSelectionStateToBe(element, true)` | Element selection matches expected state | `Boolean` |

```java
// Most common patterns:

// Wait to click
WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id("save")));
btn.click();

// Wait to read text
WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector("[data-testid='success-msg']")
));
assertThat(msg.getText()).isEqualTo("Saved");

// Wait for loading spinner to disappear before next action
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector("[data-testid='spinner']")
));
```

---

## Element presence conditions

| Method | Waits until... | Returns |
|---|---|---|
| `presenceOfElementLocated(locator)` | Element is in DOM (may not be visible) | `WebElement` |
| `presenceOfAllElementsLocatedBy(locator)` | At least one element is in DOM | `List<WebElement>` |
| `visibilityOfAllElementsLocatedBy(locator)` | All matching elements are visible | `List<WebElement>` |
| `visibilityOfAllElements(elements)` | Existing list — all become visible | `List<WebElement>` |

```java
// Presence (element in DOM but may be hidden — useful for reading hidden data)
WebElement hiddenData = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.id("hidden-patient-id"))
);

// All rows loaded in a results table
List<WebElement> rows = wait.until(
    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("[data-testid='result-row']"))
);
assertThat(rows).hasSize(5);
```

---

## Text and attribute conditions

| Method | Waits until... | Returns |
|---|---|---|
| `textToBePresentInElement(element, text)` | Element's text contains the substring | `Boolean` |
| `textToBePresentInElementLocated(locator, text)` | Located element's text contains substring | `Boolean` |
| `textToBePresentInElementValue(element, text)` | Element's `value` attribute contains text | `Boolean` |
| `attributeToBe(element, attr, value)` | Attribute equals exact value | `Boolean` |
| `attributeContains(element, attr, value)` | Attribute contains substring | `Boolean` |
| `attributeToBeNotEmpty(element, attr)` | Attribute is not null/empty | `Boolean` |

```java
// Wait for a field to be populated by the application
wait.until(ExpectedConditions.textToBePresentInElementLocated(
    By.cssSelector("[data-testid='mrn-field']"), "MRN-TEST"
));

// Wait for a button to become disabled after submission
wait.until(ExpectedConditions.attributeToBe(
    driver.findElement(By.cssSelector("[data-testid='submit-btn']")),
    "disabled", "true"
));

// Wait for a status badge to change to "ACTIVE"
wait.until(ExpectedConditions.textToBePresentInElement(
    driver.findElement(By.cssSelector("[data-testid='status-badge']")),
    "ACTIVE"
));
```

---

## URL and title conditions

| Method | Waits until... | Returns |
|---|---|---|
| `urlToBe(url)` | Current URL exactly equals `url` | `Boolean` |
| `urlContains(fraction)` | Current URL contains `fraction` | `Boolean` |
| `urlMatches(regex)` | Current URL matches regex | `Boolean` |
| `titleIs(title)` | Page title exactly equals `title` | `Boolean` |
| `titleContains(fraction)` | Page title contains `fraction` | `Boolean` |

```java
// After login redirect
wait.until(ExpectedConditions.urlContains("/dashboard"));

// After navigating to a specific patient record
wait.until(ExpectedConditions.urlMatches(".*/patients/[0-9a-f-]{36}"));

// After a title-changing navigation
wait.until(ExpectedConditions.titleContains("Patient Record"));
```

---

## Alert conditions

| Method | Waits until... | Returns |
|---|---|---|
| `alertIsPresent()` | A JavaScript alert is present | `Alert` |

```java
// Wait for alert then dismiss it
Alert alert = wait.until(ExpectedConditions.alertIsPresent());
assertThat(alert.getText()).contains("Are you sure");
alert.accept(); // or alert.dismiss()
```

---

## List / count conditions

| Method | Waits until... | Returns |
|---|---|---|
| `numberOfElementsToBe(locator, n)` | Exactly `n` elements match | `List<WebElement>` |
| `numberOfElementsToBeMoreThan(locator, n)` | More than `n` elements match | `List<WebElement>` |
| `numberOfElementsToBeLessThan(locator, n)` | Fewer than `n` elements match | `List<WebElement>` |

```java
// Wait for exactly 5 search results to load
List<WebElement> results = wait.until(
    ExpectedConditions.numberOfElementsToBe(By.cssSelector("[data-testid='result-row']"), 5)
);

// Wait for at least 1 result
wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
    By.cssSelector("[data-testid='result-row']"), 0
));
```

---

## Negation

```java
// Negate any condition using not()
wait.until(ExpectedConditions.not(
    ExpectedConditions.urlContains("/login")
));

wait.until(ExpectedConditions.not(
    ExpectedConditions.elementToBeSelected(By.id("my-checkbox"))
));
```

---

## Composing multiple conditions (AND logic)

```java
// Wait for BOTH conditions to be true simultaneously
wait.until(ExpectedConditions.and(
    ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[data-testid='spinner']")),
    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='result-table']"))
));
```

---

## Choosing the right condition

```
Do you need to click the element?     → elementToBeClickable
Do you need to read the element?      → visibilityOfElementLocated
Is the element hidden but in the DOM? → presenceOfElementLocated
Do you need to wait for text to load? → textToBePresentInElement
Are you waiting for a page change?    → urlContains / titleContains
Are you waiting for content to go?    → invisibilityOfElementLocated
Do you need a list of results?        → visibilityOfAllElementsLocatedBy
```
