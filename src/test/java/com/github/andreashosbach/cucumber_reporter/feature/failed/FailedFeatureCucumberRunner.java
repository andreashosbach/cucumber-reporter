package com.github.andreashosbach.cucumber_reporter.feature.failed;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/features/failed", glue="com.github.andreashosbach.cucumber_reporter.feature.failed", plugin="com.github.andreashosbach.cucumber_reporter.formatter.CucumberFormatter:failedreport.html", strict=true)
public class FailedFeatureCucumberRunner {
}
