# Page Objects (`com.healthhub.pages`)

Folder-specific detail on top of the root `CLAUDE.md`. Read that first for
the project-wide conventions (testId locators, no assertions, navigate via
UI not `driver.get`); this file is about things specific to writing a page
object in *this* folder.

## Before writing a locator

You don't have the app's source code, and shouldn't rely on it even if
it happens to be present on disk — treat HealthHub as a black box.
Navigate to the real page with Playwright MCP and query the live DOM for
its actual `data-testid` attributes:

```js
// Via the Playwright MCP browser_evaluate tool, after navigating to the
// page in question:
() => Array.from(document.querySelectorAll('[data-testid]')).map(el => ({
  testid: el.getAttribute('data-testid'),
  tag: el.tagName.toLowerCase(),
  role: el.getAttribute('role') || null,
}))
```

No MCP configured? Open browser DevTools, Inspect Element, read the
`data-testid` off the real rendered element — same principle, manual
instead of scripted. Every locator in this folder so far (`LoginPage`,
`DashboardPage`, `AppointmentsPage`) was verified this way against the
live app — none were guessed, and none came from reading a component
file. A testid that "looks right" but wasn't checked against the live
page is exactly the kind of mistake `conventions-reviewer`
(`.claude/agents/conventions-reviewer.md`) is written to catch.

## Two gotchas already hit in this folder — don't reintroduce them

1. **Native `<input type="date">` / `<input type="time">` + `sendKeys`.**
   Chrome's date/time widgets are segmented (month/day/year or
   hour/minute/AM-PM), and the segment order is locale/OS-dependent, so
   typing an ISO string like `"2026-01-01"` character-by-character often
   lands in the wrong segment and silently produces an invalid or wrong
   date. Use `setNativeValue(locator, value)` (defined on `BasePage`) for
   any date/time input instead of `type(locator, value)` — it sets the
   value through React's native setter and dispatches a real `input`
   event, bypassing the segmented-keystroke problem entirely. See
   `AppointmentsPage.fillAndSaveAppointment` for the working example.

2. **Checking for a row/element immediately after a save action.**
   A save button click triggers an API call and a table refetch — it is
   not instant. A direct `driver.findElements(...)` snapshot right after
   `click(saveButton)` will intermittently (or consistently, depending on
   timing) find nothing even though the save succeeded. Always wait for
   the expected state via `wait.until(ExpectedConditions...)` rather than
   taking an unwaited snapshot — see `AppointmentsPage.rowExistsForTitle`.

## Lists that paginate

`AppointmentsPage`'s table paginates at 10 rows and sorts by date
ascending by default — observed directly in the running app, not from
reading any source. A page object method that checks "does row X exist"
only looks at the currently-rendered page — it does not crawl pagination.
If a new test's assertion depends on a row appearing, that's a test-data
concern (pick a date/value that keeps the row on page 1, checking the
current state of the live list first since seeded/accumulated data
changes over time), not a reason to add pagination-crawling logic to the
page object. Keep page objects simple; make test data deliberate instead.
