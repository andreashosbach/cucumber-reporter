Feature: Very simple feature

    Background: Browser Version
    Given the website "about:version" is displayed

  Scenario: Checking Wikipedia
    Given the website "https://en.wikipedia.org/wiki/Water_pipit" is displayed
    When the link "passerine" is clicked
    Then the website with title "Passerine - Wikipedia" should be displayed
