Feature: Health Records

  Background:
    Given I am on the HealthHub login page
    And I log in with email "patient@healthhub.test" and password "Test123!"

  Scenario: View the health records table
    When I open the Health Records page from the sidebar
    Then the health records table should list at least one record
