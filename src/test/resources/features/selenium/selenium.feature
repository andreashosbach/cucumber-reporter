Feature: Checking on Wikipedia
  We are checkin out birds

  Especially nice are the *passerines*

  We will see
  * Water Pipits
  * Meadow Pipits

  Background: Browser Version
    Given a webbrowser

  Scenario: Searching a page
    Searching on Wikipedia

    We should automatically see the description page after the search

    Given the website "https://www.wikipedia.org" is displayed
    And the user enters "water pipit" into the field "searchInput"
    When the user clicks on the submit button
    Then a page containing the following text should be displayed
    """
    The water pipit (Anthus spinoletta) is a small passerine bird which breeds in the mountains of Southern Europe
    and the Palearctic eastwards to China. It is a short-distance migrant; many birds move to lower altitudes or wet
    open lowlands in winter.
    """

  Scenario Outline: Different Pipits
    Directly jumping to the description pages

    Depending on the run we will see differen *pipits*

    Given the website <website> is displayed
    Examples:
      | website|
      |  "https://en.wikipedia.org/wiki/Water_pipit"  |
      |  "https://en.wikipedia.org/wiki/Meadow_pipit" |

  Scenario: Digging deeper
    Given the website "https://en.wikipedia.org/wiki/Water_pipit" is displayed
    When the user clicks on the link "passerine"
    Then the website with title "Passerine - Wikipedia" should be displayed

