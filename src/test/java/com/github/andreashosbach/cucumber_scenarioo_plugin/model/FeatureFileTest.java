package com.github.andreashosbach.cucumber_scenarioo_plugin.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureFileTest {

    private FeatureFile featureFile;

    @BeforeEach
    void setUp() {
        String uri = "testFile.feature";
        String code = "" +
                "@FeatureTag\n" +
                "Feature:My special feature \n" +
                " This is a Feature description\n" +
                " over two lines\n" +
                "# -------------\n" +
                " Scenario: Scenario 1\n" +
                "  Description of scenario 1 but only one line \n" +
                " Given something\n" +
                "# ---------- \n" +
                "@Scenario2\n" +
                " Scenario:Scenario 2\n" +
                " # No description\n" +
                " When a step\n" +
                "# ---------- \n" +
                " Scenario Outline: Scenario Outline 1\n" +
                "  Description of scenario outline\n" +
                " Given something <nr>\n" +
                " Examples:\n" +
                "| nr |\n" +
                "| 1  |\n" +
                "| 2  |\n" +
                "# ---------- \n" +
                " Scenario:Scenario 3\n" +
                " # No description\n" +
                " When a step\n";

        featureFile = new FeatureFile(uri, code);
    }

    @Test
    void getLine() {
        assertEquals("@FeatureTag", featureFile.getLine(1));
        assertEquals("over two lines", featureFile.getLine(4));
    }

    @Test
    void getFeatureDescription() {
        assertEquals("This is a Feature description\nover two lines", featureFile.getFeatureDescription());
    }

    @Test
    void getScenarioDescription() {
        assertEquals("Description of scenario 1 but only one line", featureFile.getScenarioDescription(6));
        assertEquals("", featureFile.getScenarioDescription(11));
        assertEquals("Description of scenario outline", featureFile.getScenarioDescription(20));
    }

    @Test
    void getFeatureName() {
        assertEquals("My special feature", featureFile.getFeatureName());
    }
}
