---
description: Check what a real HealthHub API endpoint actually returns, right now, before trusting any assumption about its shape.
name: check-endpoint
agent: agent
tools: [TODO]
---

<!--
AMOHAN'S TICKET, ITEM 2: a quick, one-off version of "verify before you
trust it" — for when you just want to check ONE endpoint's real shape
right now, without going through the full skill (that's for when you're
actually authoring a test). Pick the right tools: list: this needs
*live* terminal access to call the real API with a real bearer token,
not Playwright MCP (that's for the DOM, not HTTP responses) — same
"verify, don't guess" syntax lesson as locator.prompt.md's own
tools: field had, fix the [TODO] above the same way.
-->

Check the real HealthHub endpoint **${input:path}** against the live
app at `https://healthhub.qualintia.com/api/v1`.

[TODO: finish this instruction. It needs to: log in as the test account
(see `config.properties`) to get a real bearer token, call the given
path authenticated, and print back — not guess — the actual response
shape: status code, top-level keys, and whether `success`/`data`/`error`
look like what `CLAUDE.md` §5 already documents. Flag anything that
doesn't match what's documented, instead of silently assuming the
documented version is still correct.]

---

## What a correct run should tell you

- The real status code, not an assumed one
- The real top-level JSON keys, not a copy of another endpoint's shape
- Whether this matches the envelope documented in
  `.github/instructions/api.instructions.md` (the one you're also
  building) — if you haven't written that section yet, this prompt is a
  good reason to go confirm it for real before you do
