@secondFeature
Feature: And a third full blown feature
  This is the description of the feature
  And a second description line

  Background:
    Given nothing is ok

  Scenario: Next scenario with background
    Description of the next scenario
    Given the status before
      And some more
    When the thing happens
    Then all is ok
      And and well

  Scenario: Next scenario with background
  Description of the next scenario
    Given the status before
    And some more
    When the thing happens
    Then all is ok
    And and well
