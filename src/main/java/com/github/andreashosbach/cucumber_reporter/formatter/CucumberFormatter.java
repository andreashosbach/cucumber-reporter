package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;

public interface CucumberFormatter {

    void addFeature(TestFeature feature);

    void writeOutput();
}
