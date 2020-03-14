package com.github.andreashosbach.cucumber_scenarioo_plugin.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureFilesTest {

    @Test
    void getFeatureFile() {
        FeatureFiles featureFiles = new FeatureFiles();
        featureFiles.addFeatureFile(new FeatureFile("1234", "Some code"));
        featureFiles.addFeatureFile(new FeatureFile("5678", "Some code"));
        featureFiles.addFeatureFile(new FeatureFile("91011", "Some code"));

        assertEquals("5678", featureFiles.getFeatureFile("5678").getUri());
    }
}
