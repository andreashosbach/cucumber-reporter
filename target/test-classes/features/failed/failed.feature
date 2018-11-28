Feature: Very Simple feature

  Scenario: Simple Scenario
    Given the status before
    When the thing happens
    Then all is ok

  Scenario: A failing Scenario
    Given the status before
    When the thing happens
    Then nothing is ok
      And this is skipped

  Scenario: Another failing Scenario
    Given the status before
    When the thing happens
    Then nothing is ok
    And this is skipped
