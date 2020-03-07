import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/simple",
        glue = "stepdefs",
        plugin = "com.github.andreashosbach.cucumber_reporter.CucumberReportPlugin:target/scenarioo/simplereport",
        strict = true)
public class SimpleFeatureCucumberRunner {
}
