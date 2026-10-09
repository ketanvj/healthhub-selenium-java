Feature: Login

  Background:
    Given I am on the HealthHub login page

  Scenario: Successful login with valid credentials
    When I log in with email "patient@healthhub.test" and password "Test123!"
    Then I should see the dashboard

  Scenario: Login fails with an incorrect password
    When I log in with email "patient@healthhub.test" and password "WrongPassword1!"
    Then I should see a login error message

  Scenario Outline: Login with multiple credential combinations
    When I log in with email "<email>" and password "<password>"
    Then the login result should be "<result>"

    Examples:
      | email                   | password      | result  |
      | patient@healthhub.test  | Test123!      | success |
      | sarah@healthhub.test    | Test123!      | success |
      | admin@healthhub.test    | Admin123!     | success |
      | patient@healthhub.test  | wrong-pass    | failure |
