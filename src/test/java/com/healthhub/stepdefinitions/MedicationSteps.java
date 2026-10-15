package com.healthhub.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MedicationSteps {

    private final ScenarioContext context;

    public MedicationSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I navigate to the Medications page")
    public void iNavigateToTheMedicationsPage() {
        context.setMedicationsPage(context.getDashboardPage().goToMedications());
    }

    @Then("the current medications list should be displayed")
    public void theCurrentMedicationsListShouldBeDisplayed() {
        Assert.assertTrue(context.getMedicationsPage().isLoaded(),
                "Expected the medications list to be displayed");
        Assert.assertTrue(context.getMedicationsPage().medicationCount() > 0,
                "Expected at least one medication in the list");
    }
}
