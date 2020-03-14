package com.github.andreashosbach.cucumber_scenarioo_plugin.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GherkinUtilsTest {

    @Test
    void isFeatureTitle_positive() {
        assertTrue(GherkinUtils.isFeatureTitle("Feature: My Feature"));
        assertTrue(GherkinUtils.isFeatureTitle("Feature:My Feature"));
        assertTrue(GherkinUtils.isFeatureTitle(" Feature: My Feature"));
        assertTrue(GherkinUtils.isFeatureTitle("Feature:"));
    }


    @Test
    void isFeatureTitle_negative() {
        assertFalse(GherkinUtils.isFeatureTitle("Feature in a nutshell"));
        assertFalse(GherkinUtils.isFeatureTitle("Given My Feature: an example"));

    }

    @Test
    void isScenarioTitle_positive() {
        assertTrue(GherkinUtils.isScenarioTitle("Scenario: My Sceanrio"));
        assertTrue(GherkinUtils.isScenarioTitle("Scenario:My Scenario"));
        assertTrue(GherkinUtils.isScenarioTitle(" Scenario: My Scenario"));
        assertTrue(GherkinUtils.isScenarioTitle("Scenario:"));

        assertTrue(GherkinUtils.isScenarioTitle("Example: My Scenario"));
        assertTrue(GherkinUtils.isScenarioTitle("Background: My Scenario"));
        assertTrue(GherkinUtils.isScenarioTitle("Scenario Outline: My Scenario"));
    }

    @Test
    void isScenarioTitle_negative() {
        assertFalse(GherkinUtils.isScenarioTitle("Scenario in a nutshell"));
        assertFalse(GherkinUtils.isScenarioTitle("Given My Scenario: an example"));
    }

    @Test
    void startsWithTag_all() {
        assertTrue(GherkinUtils.startsWithTag("@Tag"));
        assertTrue(GherkinUtils.startsWithTag(" @Tag"));
        assertTrue(GherkinUtils.startsWithTag("@Tag noTag"));

        assertFalse(GherkinUtils.startsWithTag("noTag @Tag"));
    }

    @Test
    void isComment_all() {
        assertTrue(GherkinUtils.isComment("#Tag"));
        assertTrue(GherkinUtils.isComment(" #Tag"));
        assertTrue(GherkinUtils.isComment("# Tag noTag"));

        assertFalse(GherkinUtils.isComment("no Comment #"));
    }

    @Test
    void isStep_positive() {
        assertTrue(GherkinUtils.isStep("Given something"));
        assertTrue(GherkinUtils.isStep(" Given something"));

        assertTrue(GherkinUtils.isStep("When something"));
        assertTrue(GherkinUtils.isStep("Then something"));
        assertTrue(GherkinUtils.isStep("But something"));
        assertTrue(GherkinUtils.isStep("And something"));
    }

    @Test
    void isStep_negative() {
        assertFalse(GherkinUtils.isStep("Givensomething"));
        assertFalse(GherkinUtils.isStep(" Not Given something"));
        assertFalse(GherkinUtils.isStep("Given "));
        assertFalse(GherkinUtils.isStep("given something"));
        assertFalse(GherkinUtils.isStep("@Given something"));
        assertFalse(GherkinUtils.isStep("# Given something"));
    }
}
