---
description: Recommend the best Selenium locator strategy for a UI element
agent: agent
tools: ["playwright/*"]
---

<!--
Prompt file syntax reference (.github/prompts/*.prompt.md), confirmed
against VS Code's own docs (code.visualstudio.com/docs/copilot/
customization/prompt-files) — not from memory:

YAML frontmatter fields, all optional:
  description: shown in the / picker UI
  name:        what you type after `/` to invoke it — defaults to the
               filename (so this file is invoked as `/locator`)
  agent:       which mode it runs in — ask | edit | agent | a specific
               custom agent's name. Leave unset to inherit whatever mode
               you invoked it from. IMPORTANT: ask mode has no tool
               access at all, including no MCP — don't set this to ask
               if the prompt needs to verify anything live (see the real
               mistake this exact file had, fixed same night it was built)
  model:       defaults to whatever's currently selected in the model
               picker if unset
  tools:       which tools this prompt is allowed to use — same
               allow-list mechanism as a custom agent's own `tools:`
               frontmatter

Body syntax:
  ${input:variableName}        a named placeholder — documented to
                                prompt you to fill it in when invoked.
                                NOT independently confirmed (as of
                                Oct 15) to behave identically to Claude
                                Code's $ARGUMENTS (which just captures
                                whatever free text follows the command)
  ${input:variableName:hint}   same, with placeholder hint text shown
  #tool:<tool-name>            reference a specific agent tool directly
                                in the body text

Invoke by typing /locator in Copilot Chat, same picker as any other
slash command.
-->

Recommend the best Selenium locator for: **${input:element}**

Give a direct recommendation, not a list of options ranked by preference. Pick one and explain why it's the right choice for this element, following this framework's own convention: locate via `testId(id)`, the app's `data-testid` attribute, verified against the live running app — never CSS classes or XPath text matches. If there's a meaningful trade-off worth mentioning, say it once — don't hedge.

---

## Recommended locator

```java
private final By someField = testId("verified-data-testid-here");
```

**Why this one:** [Explain the reasoning — was this testid confirmed live against the running app? Is it stable across re-renders?]
