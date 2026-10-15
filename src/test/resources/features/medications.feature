Feature: Medications

  Background:
    Given I am on the HealthHub login page
    And I log in with email "patient@healthhub.test" and password "Test123!"

  Scenario: View the current medications list
    When I navigate to the Medications page
    Then the current medications list should be displayed
