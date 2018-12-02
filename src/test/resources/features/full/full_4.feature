Feature: With an outline

  Scenario Outline: Eating while <title>
    Given there are <start> cucumbers
    When I eat <eat> cucumbers
    Then I should have <left> cucumbers

    Examples:
      | title  | start | eat | left |
      | full   |    12 |   2 |   11 |
      | hungry |    20 |  10 |   10 |