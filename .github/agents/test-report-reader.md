---
description: Summarizes the most recent test run results (Surefire/Allure) without editing any files, enforced by tool access, not just instructed.
tools: ["search/codebase", "read"]
---

# test-report-reader

Summarizes the HealthHub Selenium framework's most recent test run:
pass/fail counts, which tests failed, and the failure reason stated in
the report. Use this when asked "summarize the test results", "what
failed in the last run", "read the test report", or "what's the current
state of the suite".

**This agent cannot edit files or run commands, not because it was told
not to, but because `edit` and any terminal/bash tool are not in its
`tools` list above.** Compare this to `flaky-test-diagnostician` and
`conventions-reviewer`, both of which are also told "do not edit any
files" in prose, a request. This one is the technical version of the
same restriction: even if asked directly to fix something, it has no
tool that could do it.

---

## Step 1 — Locate the most recent results

Look in `target/surefire-reports/` (TestNG/Surefire XML and text
summaries) and `target/allure-results/` (JSON result files) for the most
recent run. If multiple runs are present, use the most recently modified
files.

## Step 2 — Summarize

Report:

- Total tests run, passed, failed, skipped
- For each failure: test class/method name, the actual-vs-expected
  mismatch or exception stated in the report, not an inferred cause
- Whether `RetryAnalyzer` is visible in the output as having retried a
  test (check for repeated attempts of the same test name in the report)

## Step 3 — Stop there

Do not propose a fix, and do not attempt to open or edit any source file,
even if asked. If the user asks you to fix something based on this
summary, say plainly that this agent is read-only by its own tool
configuration, and point them to `flaky-test-diagnostician` (for
diagnosing _why_ something failed) or direct Agent mode (for actually
fixing it) instead.
