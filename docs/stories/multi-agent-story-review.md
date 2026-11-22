# Multi-agent story review in GitHub Copilot (VS Code)

One story, three read-only specialists in parallel, then a human decision. The story is `docs/stories/HEALTH-247.md`, a patient booking an appointment.

**First rehearsed 22 Nov 2026:** the three subagents ran in parallel. Parts of this depend on the VS Code version and settings, so check each step below on the machine you'll present from.

## Files

`.github/agents/`:

| Agent | Role | Tools |
|---|---|---|
| `Story Review Lead` | Coordinator. Pick it in the Agent dropdown | agent, read, search, edit |
| `Story Analyst` | Gaps and questions for the product owner | read, search |
| `Test Designer` | Functional scenarios, grouped by risk | read, search |
| `Coverage Checker` | Which ACs the Cucumber scenarios and API tests already cover | read, search |

The three specialists have `user-invocable: false`, so they don't appear in the dropdown. The coordinator's `agents:` list limits it to exactly these three.

## Before the first run

1. **VS Code 1.109 or later** (Code > About Visual Studio Code). Parallel subagents arrived in January 2026.
2. **Turn on custom agents as subagents** (needed, confirmed 22 Nov):
   - Code > Settings… > Settings (or Cmd + ,).
   - In the search box, type `subagent`.
   - Tick **Chat › Custom Agent In Subagent: Enabled**. It applies straight away.
   - Alternative: Cmd + Shift + P > "Open User Settings (JSON)", add `"chat.customAgentInSubagent.enabled": true`, save.
   - Set it in your user settings, not in the repo.
3. **Reload:** Cmd + Shift + P > "Developer: Reload Window", so VS Code picks up the files in `.github/agents/`.
4. **Agent dropdown** (bottom of the Chat box): **Story Review Lead** is listed. The three specialists aren't, on purpose.

**Confirmed in the 22 Nov run:** with the setting on, `tools: ['agent', ...]` was enough; `runSubagent` wasn't needed. All three subagents started together ("3 subagents running"). Copilot picked the model itself ("Auto routed task to GPT-5 Luna"), so another run may use a different model and give a different summary.

## Prompt 1 (with Story Review Lead selected)

```
Review the story in docs/stories/HEALTH-247.md. Run Story Analyst, Test Designer and Coverage Checker in parallel, then give me one summary: open questions, the scenarios that matter most, and which acceptance criteria have no automated test. Don't write any code yet.
```

**Check:**
- Three nested subagent calls in the chat. Did they start together, or one after the other? Only say "in parallel" if the transcript shows it.
- It stops and waits.

**Expect much less coverage than in the Playwright repo.** `appointments.feature` has one booking scenario, and the API tests cover status filtering and unauthenticated access. So most ACs should come back Not covered.

**A QA moment to look for:** the "Book a new appointment" scenario books a date of 2026-01-01 (now in the past, though AC4 asks for a future date), and checks that "Annual Check-up" appears in the list. The shared patient account already has many "Annual Check-up" appointments, so this check can pass even if the new booking failed. See whether Coverage Checker spots it.

**What the 22 Nov run gave (Prompt 1):** three subagents in parallel, one combined summary, no files changed.
- 5 open questions (Type required, Duration saved, "future" date and time zone, Title rules, failure handling).
- Coverage: AC3, AC6, AC7 not covered; AC1, AC2, AC4, AC5 partly covered through "Book a new appointment". It caught the past date (2026-01-01).
- **It missed the false pass.** `rowExistsForTitle()` only checks that some row contains "Annual Check-up", and the shared patient already has dozens. The scenario passes even if the booking fails. Coverage Checker's own instructions ask about exactly this, and it still didn't report it. That's the point for the room: the agents speed up the review, and a human still reads it.
- Compared with Claude Code on the Playwright repo: the same flow, and parallel in both. Copilot's summary is shorter and quotes less evidence.

## Prompt 2 (after reading the summary)

```
Add one Cucumber scenario for AC7: cancelling a filled-in booking form creates no appointment. Use a title that includes a timestamp, so the check can't match old data. Add it to src/test/resources/features/appointments.feature, with any new steps in AppointmentSteps.java and any new page methods in AppointmentsPage.java. Only use data-testid values that already appear in AppointmentsPage.java; if you need one that isn't there, stop and tell me instead of guessing. Don't create or change any other file.
```

**Look for:** a unique title, no invented `data-testid`, and changes only in those three files. If it says it needs a Cancel button locator, that's the right answer: discover it live (see `CLAUDE.md`, "Discovering real locators") before writing the step.

Then `git status --short` and `git diff`.

## If it doesn't delegate

Don't keep re-prompting. Open `story-review-lead.md`, show the `agents:` and `tools:` lines, explain the intended flow, and say plainly that this run didn't delegate.

## Reset

`git checkout -- src/ && git clean -fd src/`
