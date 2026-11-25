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

=== WHEN DOES THIS SCRIPT ACTUALLY RUN? ===
"PreToolUse" means exactly that: this script runs once for EVERY tool
call the agent attempts -- reading a file, searching, running a
terminal command, editing a file, all of it -- and it runs BEFORE that
tool call is actually carried out. Think of it as a checkpoint sitting
between "the agent decided to do X" and "X actually happens":

    agent decides to call a tool
              |
              v
    this script runs, given the full details of that proposed call
    on stdin (which tool, what arguments)
              |
              v
    script prints nothing  -> VS Code lets the tool call proceed
    script prints a "deny" -> VS Code blocks it; the tool call never runs
              |
              v
    (only if allowed) the tool call actually executes

Because it fires on literally every tool call, not just edits, this
script has to figure out for itself whether a given call is even worth
looking at. That's what the two filters below do, in order:

1. Is this an EDIT-type call at all? (the EDIT_TOOL_HINTS check) --
   most calls are reads/searches, which this hook has no opinion about
   and lets through immediately without even checking the file path.
2. If it is an edit, does it touch OUR ONE PROTECTED FILE specifically?
   (the PROTECTED_FILE check) -- this hook only cares about
   FlakyAppointmentsApiTest.java; an edit to any other file is none of
   its business and is let through too.

Only when BOTH are true -- an edit call, targeting this exact file --
does the script actually deny anything. That's "why only this file":
the whole script is a narrow, single-purpose gate, not a general
code-review step. Protecting a second file means writing a second hook
(see protect-scenario-context.json for the other real example in this
repo), not widening this one.
"""
import json
import sys

PROTECTED_FILE = "FlakyAppointmentsApiTest.java"

# PreToolUse's event payload describes the proposed tool call, but the
# exact shape of "where is the file this call touches" isn't one fixed
# field -- it depends on which specific tool the agent used. These are
# the field names actually seen in this repo so far; a different tool
# could in principle use a different name again.
PATH_FIELDS = ("path", "filePath", "file_path", "file")

# Filter #1: is this even an edit-type tool call? VS Code's hook config
# has no built-in way to say "only run this hook for edit tools" -- the
# config can only say "run on PreToolUse," full stop, for every tool.
# So the script itself has to look at the tool's NAME and guess whether
# it's a write. The real tool name varies (e.g. this repo's agent calls
# one named "multi_replace_string_in_file"), so this matches on
# plausible substrings rather than one exact name. Reading this file
# should never be blocked -- only editing it -- which is the whole
# reason this filter exists before the path check even runs.
EDIT_TOOL_HINTS = ("edit", "write", "replace", "patch", "create")


def extract_target_paths(tool_input):
    """Pull every file path this proposed edit would touch, out of
    tool_input, whichever of the two real shapes it comes in:

    Shape A -- a simple, single-file edit tool:
        {"path": ".../FlakyAppointmentsApiTest.java", ...}

    Shape B -- a multi-replace/batch edit tool (confirmed live in this
    repo: the real tool name is "multi_replace_string_in_file"), which
    nests the path(s) one level down, inside a list, one entry per
    individual find/replace it's making -- NOT as a flat top-level
    field at all:
        {"replacements": [{"filePath": "...", "oldString": ..., "newString": ...}, ...]}

    Checking only the flat top-level fields (Shape A) was this script's
    original bug: a real Shape-B call silently found nothing and the
    edit went through unblocked. Both shapes are checked now.
    """
    paths = []
    for field in PATH_FIELDS:
        value = tool_input.get(field)
        if value:
            paths.append(str(value))

    replacements = tool_input.get("replacements")
    if isinstance(replacements, list):
        for entry in replacements:
            if not isinstance(entry, dict):
                continue
            for field in PATH_FIELDS:
                value = entry.get(field)
                if value:
                    paths.append(str(value))

    # Shape C -- a patch tool (seen live 25 Nov 2026: "Apply Patch"),
    # which carries the file name inside the patch text, e.g.
    # "*** Update File: .../FlakyAppointmentsApiTest.java", not in any
    # path field. Shapes A and B found nothing and the edit went through.
    # Fallback for this and any future shape: search the whole input.
    paths.append(json.dumps(tool_input))

    return paths


def main():
    try:
        event = json.load(sys.stdin)
    except Exception:
        # Can't parse the event -> fail open (allow). A hook that
        # misbehaves on unexpected input shouldn't become a worse
        # problem than the one it exists to prevent.
        sys.exit(0)

    # Filter #1 (see EDIT_TOOL_HINTS above): skip everything that isn't
    # even a write attempt -- reads, searches, terminal commands, etc.
    # never reach the path check below.
    tool_name = str(event.get("tool_name", "")).lower()
    if not any(hint in tool_name for hint in EDIT_TOOL_HINTS):
        sys.exit(0)

    # Filter #2: of the edit calls that do reach here, only deny the
    # ones that actually touch FlakyAppointmentsApiTest.java. An edit
    # to any other file is allowed through untouched by this script.
    tool_input = event.get("tool_input", {}) or {}
    targets = extract_target_paths(tool_input)

    if any(PROTECTED_FILE in target for target in targets):
        # This printed JSON IS the deny decision -- VS Code reads this
        # exact output and blocks the tool call because of it. No
        # output at all (the normal exit below, reached whenever this
        # "if" is false) means "I have no objection, proceed."
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
