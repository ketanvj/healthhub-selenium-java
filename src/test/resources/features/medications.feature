Feature: Medications

  Scenario: View current medications
    Given I am on the HealthHub login page
    When I log in with email "patient@healthhub.test" and password "Test123!"
    And I navigate to Medications
    Then the current medications list should be displayed
