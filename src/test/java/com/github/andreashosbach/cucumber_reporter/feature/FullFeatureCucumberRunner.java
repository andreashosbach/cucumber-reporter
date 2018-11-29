package com.github.andreashosbach.cucumber_reporter.feature;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/features/full", glue="com.github.andreashosbach.cucumber_reporter.feature", plugin="com.github.andreashosbach.cucumber_reporter.formatter.CucumberFormatter:fullreport.html", strict=true)
public class FullFeatureCucumberRunner {
}
