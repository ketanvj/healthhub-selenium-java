package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private final By welcomeHeading = testId("dashboard-welcome");
    private final By appointmentsNavLink = testId("nav-appointments");
    private final By medicationsNavLink = testId("nav-medications");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public String getWelcomeText() {
        return textOf(welcomeHeading);
    }

    public boolean isLoaded() {
        return isDisplayed(welcomeHeading);
    }

    public AppointmentsPage goToAppointments() {
        click(appointmentsNavLink);
        return new AppointmentsPage(driver);
    }

    public MedicationsPage goToMedications() {
        click(medicationsNavLink);
        return new MedicationsPage(driver);
    }
}
