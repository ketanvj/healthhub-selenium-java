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

## Prompt 2 (after reading the summary)

Decide the gaps after the first rehearsal, then name them, for example:

```
Add a Cucumber scenario for these missing checks only: [gap 1], [gap 2]. Add it to src/test/resources/features/appointments.feature, with any new steps in AppointmentSteps.java. Reuse the existing AppointmentsPage. Don't create or change any other file.
```

Then `git status --short` and `git diff`.

## If it doesn't delegate

Don't keep re-prompting. Open `story-review-lead.md`, show the `agents:` and `tools:` lines, explain the intended flow, and say plainly that this run didn't delegate.

## Reset

`git checkout -- src/ && git clean -fd src/`
