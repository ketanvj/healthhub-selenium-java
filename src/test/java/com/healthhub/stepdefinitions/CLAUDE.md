# Step Definitions (`com.healthhub.stepdefinitions`)

Folder-specific detail on top of the root `CLAUDE.md`. Read that first for
the project-wide conventions; this file is about things specific to
writing a step definition class in *this* folder.

## `ScenarioContext` is mandatory, not optional

Every step class takes `ScenarioContext` in its constructor:

```java
public class FeatureSteps {
    private final ScenarioContext context;

    public FeatureSteps(ScenarioContext context) {
        this.context = context;
    }
}
```

This only works because Cucumber-PicoContainer DI is on the classpath
(`cucumber-picocontainer`) and `ScenarioContext` has a no-arg constructor.
Don't instantiate a page object as a local variable in one step and expect
another step (possibly in a different class) to see it — store it in
`context` and read it back from there. If a new page object is introduced,
add a getter/setter pair to `ScenarioContext` for it, following the
existing `LoginPage`/`DashboardPage`/`AppointmentsPage` pattern, rather
than reaching into another step class directly.

## Keep step methods thin

A step method's job is: call a page object method, maybe stash the
returned page object in `context`. It should not contain multi-line UI
logic, waits, or locators — those belong on the page object in `pages/`.
If a step method is growing past a couple of lines, that logic almost
certainly belongs in a page object method instead. `LoginSteps` and
`AppointmentSteps` are the working examples of this: each `@When`/`@Then`
is a one-line delegation.

## One step class per feature area

`LoginSteps` ↔ `login.feature`, `AppointmentSteps` ↔ `appointments.feature`.
A new `.feature` file gets its own step class, even if some steps would be
reusable — this keeps `grep`-ability (`AppointmentSteps.java` for
appointment behavior, no hunting across a shared "CommonSteps" god-class).

## Assertions

`@Then` steps own assertions (via `org.testng.Assert`, for consistency
with the rest of the TestNG-primary suite — `org.junit.Assert` style
assertions only appear if a step is reused from `RunCucumberJUnitTest`'s
run, which it currently isn't). `@Given`/`@When` steps set up state and
act; they don't assert.
