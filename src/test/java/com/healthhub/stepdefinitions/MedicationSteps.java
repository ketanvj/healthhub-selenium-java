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

    @Then("I should see my current medications list")
    public void iShouldSeeMyCurrentMedicationsList() {
        Assert.assertTrue(context.getMedicationsPage().isLoaded(),
                "Expected the medications list to be visible");
        Assert.assertTrue(context.getMedicationsPage().medicationCount() > 0,
                "Expected at least one medication in the list for the seeded patient account");
    }
}
