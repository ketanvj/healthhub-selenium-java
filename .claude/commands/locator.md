---
description: Recommend the best Selenium locator strategy for a UI element
---

Recommend the best Selenium locator for: **$ARGUMENTS**

Give a direct recommendation, not a list of options ranked by preference. Pick one and explain why it's the right choice for this element, following this framework's own convention: locate via `testId(id)`, the app's `data-testid` attribute, verified against the live running app — never CSS classes or XPath text matches. If there's a meaningful trade-off worth mentioning, say it once — don't hedge.

---

## Recommended locator

```java
private final By someField = testId("verified-data-testid-here");
```

**Why this one:** [Explain the reasoning — was this testid confirmed live against the running app? Is it stable across re-renders?]
