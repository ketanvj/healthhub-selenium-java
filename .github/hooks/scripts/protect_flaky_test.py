#!/usr/bin/env python3
"""
PreToolUse hook: blocks any edit-tool call that targets
FlakyAppointmentsApiTest.java.

CLAUDE.md already says "Do not fix this test" (the file exists so
RetryAnalyzer has a real failure mode, and Day 4 has a genuinely flaky
test to diagnose) -- but that sentence is only a request an agent can
read and choose to follow. This hook makes it a real technical gate: the
tool call is denied before it runs, regardless of what the agent decided
to do.

NOT YET LIVE-VERIFIED IN VS CODE. Built from VS Code's own docs
(code.visualstudio.com/docs/agent-customization/hooks and
.../docs/agents/reference/hooks-reference), which confirm the PreToolUse
event, the JSON stdin/stdout contract, and the permissionDecision output
shape -- but do NOT publish a full field-name list for every tool's
input. This script checks several plausible field names for the edited
path (path, filePath, file_path, file). Before relying on this live:
check your own Agent-mode debug logs for the Local harness's actual tool
names and input schema (the hooks reference page itself recommends this
step) and adjust PATH_FIELDS below if the real field name differs.

Also unconfirmed: whether GitHub Copilot's standard Agent-mode Chat
session (the surface this course has used all week) actually runs as
VS Code's "Local" harness, or some other harness under "Agent Host" with
a different hook config entirely -- VS Code's own docs describe harness
selection via a separate "Session Target" control that this course has
never explicitly touched, and didn't resolve which one Chat's default
Agent mode maps to. If this hook doesn't fire at all when tested, that
harness mismatch is the first thing to check, not a bug in this script.
"""
import json
import sys

PROTECTED_FILE = "FlakyAppointmentsApiTest.java"
PATH_FIELDS = ("path", "filePath", "file_path", "file")


def main():
    try:
        event = json.load(sys.stdin)
    except Exception:
        # Can't parse the event -> fail open (allow). A hook that
        # misbehaves on unexpected input shouldn't become a worse
        # problem than the one it exists to prevent.
        sys.exit(0)

    tool_input = event.get("tool_input", {}) or {}
    target = ""
    for field in PATH_FIELDS:
        value = tool_input.get(field)
        if value:
            target = str(value)
            break

    if PROTECTED_FILE in target:
        print(json.dumps({
            "hookSpecificOutput": {
                "hookEventName": "PreToolUse",
                "permissionDecision": "deny",
                "permissionDecisionReason": (
                    f"{PROTECTED_FILE} is the deliberately flaky test for "
                    "Day 4's debugging exercise (see CLAUDE.md, section 6: "
                    "'The deliberate flaky test'). This hook blocks edits "
                    "to it regardless of what the agent decided to do."
                ),
            }
        }))

    sys.exit(0)


if __name__ == "__main__":
    main()
