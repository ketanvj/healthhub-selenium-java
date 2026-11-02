---
applyTo: "src/test/java/com/healthhub/api/**"
---

# API Tests

Folder-specific detail on top of `copilot-instructions.md`. Read that
first for project-wide conventions; this file is about things specific
to writing or checking an API test in this folder.

<!--
AMOHAN'S TICKET, ITEM 1: this file didn't exist before you got here —
pages/ and stepdefinitions/ both have one of these, api/ never did. The
real conventions below already exist, documented only in the root
instructions file (see "## 5. API tests" there) and discovered by
calling the API directly, not by reading backend source. Your job: pull
that knowledge down to this level, where the next person adding an API
test will actually be looking, and add anything else you notice while
you're in here. Replace every [TODO] below — don't leave any in the
final file.
-->

## Before writing a request

[TODO: open `ApiClient.java` (`src/test/java/com/healthhub/api/`) and
root `CLAUDE.md` §5. In your own words, what does `authenticated()`
give you, and what should you NOT do instead of using it? One or two
sentences.]

## The mount point

[TODO: check root `CLAUDE.md` §5 and `config.properties`
(`api.base.url`). What's the real base path, where is it configured,
and what's the one wrong guess a new test-writer would plausibly make
instead? State it the way you'd want to have been told on day one, not
as a trivia fact.]

## Response shape

[TODO: describe the envelope every endpoint response is wrapped in —
both the success case and the error case. If you're not sure, call a
real endpoint yourself (authenticated and unauthenticated) and look —
same discipline as locators: observed live, not assumed.]

## The deliberate flaky test

[TODO: one or two sentences, pointing at the real file, making sure
nobody "fixes" it by accident while working in this folder. Check the
root instructions file first — don't re-derive this one differently.]

## Gotchas found while building this capstone

[TODO: once you've built the skill, the two agents, and used them for
real — come back and add anything you learned the hard way. This section
is allowed to be the last thing you fill in, not the first.]
