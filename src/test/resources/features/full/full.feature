@featureTag
Feature: Full blown feature
  This is the description of the feature

  @firstTag
  Scenario: A first scenario
    Description of the first scenario
    Given the status before
      And some more
    When the thing happens
    Then all is ok
      And and well

  @secondTag @anotherTag
  Scenario: A second scenario
    Given the status before
    And some more
    When the thing happens
    Then all is ok
    And and well


  Scenario: A failed scenario
  Description of the faILED scenario
    Given the status before
    And some more
    When the thing happens
    Then nothing is ok
    And this is skipped