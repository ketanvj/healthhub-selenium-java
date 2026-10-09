package com.healthhub.stepdefinitions;

import com.healthhub.config.ConfigReader;
import com.healthhub.driver.DriverManager;
import com.healthhub.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private final ScenarioContext context;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I am on the HealthHub login page")
    public void iAmOnTheLoginPage() {
        DriverManager.getDriver().get(ConfigReader.baseUrl() + "/login");
        context.setLoginPage(new LoginPage(DriverManager.getDriver()));
    }

    @When("I log in with email {string} and password {string}")
    public void iLogInWith(String email, String password) {
        context.setDashboardPage(context.getLoginPage().loginAs(email, password));
    }

    @Then("I should see the dashboard")
    public void iShouldSeeTheDashboard() {
        Assert.assertTrue(context.getDashboardPage().isLoaded(),
                "Expected the dashboard welcome heading to be visible after login");
    }

    @Then("I should see a login error message")
    public void iShouldSeeALoginErrorMessage() {
        // Login failed, so we're still on LoginPage, not the DashboardPage context.setDashboardPage() holds.
        Assert.assertTrue(context.getLoginPage().hasErrorMessage(),
                "Expected an error message after an invalid login attempt");
    }

    @Then("the login result should be {string}")
    public void theLoginResultShouldBe(String expectedResult) {
        if ("success".equals(expectedResult)) {
            Assert.assertTrue(context.getDashboardPage().isLoaded(),
                    "Expected dashboard to load for a valid credential combination");
        } else {
            Assert.assertTrue(context.getLoginPage().hasErrorMessage(),
                    "Expected a login error for an invalid credential combination");
        }
    }
}
