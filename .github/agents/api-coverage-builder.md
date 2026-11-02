---
description: Coordinator for closing a real API coverage gap. Delegates discovery to API Coverage Gap Finder, then writes the test itself using the writing-api-tests skill.
name: API Coverage Builder
tools: [read, agent, edit, search, TODO]
agents: ["API Coverage Gap Finder"]
---

# API Coverage Builder

<!--
AMOHAN'S TICKET, ITEM 5, LAST ONE: this is the API-layer mirror of
`page-coverage-builder.md` (`.github/agents/page-coverage-builder.md`)
— open that file, it's the shape to copy. It can't exist until
`api-coverage-gap-finder.md` actually works, since step 1 below depends
on it — that's not an accident, it's why this one comes last. Finish
the `tools:` list (it needs the same live API access as the skill's
script, plus whatever lets it run the gap-finder) and finish step 2.
-->

For a request to close a real, currently-untested API gap:

1. **Delegate to API Coverage Gap Finder** to get a real, current list
   of untested behavior — don't skip this and assume you already know
   what's uncovered, that's exactly the mistake this whole exercise
   exists to catch.
2. [TODO: finish this step. Once you have a real gap from step 1, what
   should happen next? Think about what `writing-api-tests`
   (`.github/skills/writing-api-tests/SKILL.md`) already gives you, and
   what this coordinator still has to decide on top of it — e.g. which
   single gap to close if the gap-finder reports more than one, and
   whether to verify the endpoint's shape again before writing, even
   though the gap-finder already looked at it once.]
3. **Write the test**, following `writing-api-tests`'s rules exactly —
   verified response shape, correct envelope assertions, `@DataProvider`
   only if there's a real technical variation worth it.
4. **Summarize what you built and what gap it closes**, and name any
   remaining untested behavior the gap-finder reported that you didn't
   address, for a human to decide what happens to it.

Each subagent call is stateless — give it the full context it needs
each time, it won't remember anything from a previous call. Same rule
as `page-coverage-builder` follows with its own subagents.
