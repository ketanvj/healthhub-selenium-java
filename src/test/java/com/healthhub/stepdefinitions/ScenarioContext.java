package com.healthhub.stepdefinitions;

import com.healthhub.pages.AppointmentsPage;
import com.healthhub.pages.DashboardPage;
import com.healthhub.pages.LoginPage;
import com.healthhub.pages.MedicationsPage;

/**
 * Shared state across step definition classes within one scenario, injected
 * by Cucumber's PicoContainer DI (hence the no-arg constructor). Keeps step
 * classes from reaching into each other directly, each one just reads/writes
 * the page it cares about here.
 */
public class ScenarioContext {

    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private AppointmentsPage appointmentsPage;
    private MedicationsPage medicationsPage;

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public DashboardPage getDashboardPage() {
        return dashboardPage;
    }

    public void setDashboardPage(DashboardPage dashboardPage) {
        this.dashboardPage = dashboardPage;
    }

    public AppointmentsPage getAppointmentsPage() {
        return appointmentsPage;
    }

    public void setAppointmentsPage(AppointmentsPage appointmentsPage) {
        this.appointmentsPage = appointmentsPage;
    }

    public MedicationsPage getMedicationsPage() {
        return medicationsPage;
    }

    public void setMedicationsPage(MedicationsPage medicationsPage) {
        this.medicationsPage = medicationsPage;
    }
}
