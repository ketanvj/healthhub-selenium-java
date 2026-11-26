package com.healthhub.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class HealthRecordsSteps {

    private final ScenarioContext context;

    public HealthRecordsSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I open the Health Records page from the sidebar")
    public void iOpenTheHealthRecordsPageFromTheSidebar() {
        context.setHealthRecordsPage(context.getDashboardPage().goToHealthRecords());
    }

    @Then("the health records table should list at least one record")
    public void theHealthRecordsTableShouldListAtLeastOneRecord() {
        Assert.assertTrue(context.getHealthRecordsPage().isLoaded(),
                "Expected the health records table to be displayed");
        int rowCount = context.getHealthRecordsPage().getRecordRowCount();
        Assert.assertTrue(rowCount > 0,
                "Expected the health records table to contain at least one record but found " + rowCount);
    }
}
