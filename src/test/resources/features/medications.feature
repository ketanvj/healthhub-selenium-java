Feature: Medications

  Background:
    Given I am on the HealthHub login page
    And I log in with email "patient@healthhub.test" and password "Test123!"

  Scenario: View current medications
    When I navigate to the Medications page
    Then I should see my current medications list
