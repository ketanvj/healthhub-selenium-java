package com.healthhub.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MedicationSteps {

    private final ScenarioContext context;

    public MedicationSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I navigate to Medications")
    public void iNavigateToMedications() {
        context.setMedicationsPage(context.getDashboardPage().goToMedications());
    }

    @Then("the current medications list should be displayed")
    public void theCurrentMedicationsListShouldBeDisplayed() {
        Assert.assertTrue(context.getMedicationsPage().isMedicationsListDisplayed(),
                "Expected the current medications list to be displayed");
    }
}
