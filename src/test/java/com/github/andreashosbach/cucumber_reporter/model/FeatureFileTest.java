package com.github.andreashosbach.cucumber_reporter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureFileTest {

    FeatureFile featureFile;

    @BeforeEach
    void setUp() {
        String uri = "testFile.feature";
        String code = "" +
                "@FeatureTag\n" +
                "Feature:My special feature \n" +
                " This is a Feature description\n" +
                " over two lines\n"+
                "# -------------\n"+
                " Scenario: Scenario 1\n"+
                "  Description of scenario 1 but only one line \n"+
                " Given something\n"+
                "# ---------- \n"+
                "@Scenario2\n"+
                " Scenario:Scenario 2\n"+
                " # No description\n"+
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
    }

    @Test
    void getFeatureName() {
        assertEquals("My special feature", featureFile.getFeatureName());
    }
}
