package com.healthhub.stepdefinitions;

import com.healthhub.pages.AppointmentsPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.Map;

public class AppointmentSteps {

    private final ScenarioContext context;

    public AppointmentSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I open the new appointment form")
    public void iOpenTheNewAppointmentForm() {
        AppointmentsPage appointmentsPage = context.getDashboardPage().goToAppointments();
        context.setAppointmentsPage(appointmentsPage.openNewAppointmentForm());
    }

    @And("I fill in the appointment details")
    public void iFillInTheAppointmentDetails(DataTable dataTable) {
        Map<String, String> row = dataTable.asMap(String.class, String.class);
        context.getAppointmentsPage().fillAndSaveAppointment(
                row.get("title"),
                row.get("type"),
                row.get("date"),
                row.get("time"),
                row.get("provider")
        );
    }

    @Then("the appointment {string} should appear in the list")
    public void theAppointmentShouldAppearInTheList(String title) {
        Assert.assertTrue(context.getAppointmentsPage().rowExistsForTitle(title),
                "Expected to find a row containing '" + title + "' in the appointments table");
    }
}
