package com.github.andreashosbach.cucumber_scenarioo_plugin.model;

import java.util.ArrayList;
import java.util.List;

public class FeatureFiles {
    private final List<FeatureFile> featureFiles = new ArrayList<>();

    public void addFeatureFile(FeatureFile featureFile){
        featureFiles.add(featureFile);
    }

    public FeatureFile getFeatureFile(String uri) {
        return featureFiles.stream().filter(f -> f.getUri().equals(uri)).findFirst().orElse(null);
    }
}
