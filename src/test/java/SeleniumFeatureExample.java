import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/selenium",
        glue = "selenium_stepdefs",
        plugin = "com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin:src/test/resources/cucumber_scenarioo_config.json",
        strict = true,
        tags = "not @Ignore")
public class SeleniumFeatureExample {
}
