package com.github.andreashosbach.cucumber_reporter.feature;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/features/simple", glue="com.github.andreashosbach.cucumber_reporter.feature", plugin="com.github.andreashosbach.cucumber_reporter.formatter.CucumberFormatter:simplereport.html", strict=true)
public class SimpleFeatureCucumberRunner {
}
