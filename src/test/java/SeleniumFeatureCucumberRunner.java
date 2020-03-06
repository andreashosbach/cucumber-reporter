import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/selenium",
        glue = "stepdefs",
        plugin = "com.github.andreashosbach.cucumber_reporter.CucumberReportPlugin:target/scenarioo/selenium",
        strict = true)
public class SeleniumFeatureCucumberRunner {
}
