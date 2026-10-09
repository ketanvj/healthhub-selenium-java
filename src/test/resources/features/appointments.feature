Feature: Appointments

  Background:
    Given I am on the HealthHub login page
    And I log in with email "patient@healthhub.test" and password "Test123!"

  # The patient account's appointments list is sorted by date ascending and
  # paginates at 10 per page, and the seed data already has 10 appointments
  # for this user (earliest 2025-11-20, latest 2026-03-05, see backend/db/seed.js).
  # Pick a date earlier than the seeded maximum so the new row sorts onto
  # page 1 — otherwise a correctly-saved appointment gets pushed to page 2
  # and looks like a failure.
  Scenario: Book a new appointment
    When I open the new appointment form
    And I fill in the appointment details
      | title    | Annual Check-up        |
      | type     | General                |
      | date     | 2026-01-01              |
      | time     | 10:00                   |
      | provider | Dr. Wilson              |
    Then the appointment "Annual Check-up" should appear in the list
