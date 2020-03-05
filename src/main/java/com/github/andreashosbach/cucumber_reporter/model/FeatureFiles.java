package com.github.andreashosbach.cucumber_reporter.model;

import cucumber.api.TestCase;

import java.util.ArrayList;
import java.util.List;

public class FeatureFiles {
    private List<FeatureFile> featureFiles = new ArrayList<>();

    public void addFeatureFile(FeatureFile featureFile){
        featureFiles.add(featureFile);
    }

    public FeatureFile getFeatureFile(String uri) {
        return featureFiles.stream().filter(f -> f.getUri().equals(uri)).findFirst().orElse(null);
    }
}
