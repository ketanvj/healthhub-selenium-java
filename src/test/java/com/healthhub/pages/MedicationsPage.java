package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MedicationsPage extends BasePage {

    private final By medicationsList = testId("medications-list");

    public MedicationsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isMedicationsListDisplayed() {
        return waitVisible(medicationsList).isDisplayed();
    }
}
