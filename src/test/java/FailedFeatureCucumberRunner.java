import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/failed",
        glue = "com.github.andreashosbach.cucumber_reporter.stepdefs",
        plugin = "com.github.andreashosbach.cucumber_reporter.CucumberReportPlugin:target/scenarioo/failedreport",
        strict = true)
public class FailedFeatureCucumberRunner {
}
