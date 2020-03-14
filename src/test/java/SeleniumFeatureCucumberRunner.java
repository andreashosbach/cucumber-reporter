import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/selenium",
        glue = "selenium_stepdefs",
        plugin = "com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberReportPlugin:target/scenarioo/selenium",
        strict = true)
public class SeleniumFeatureCucumberRunner {
    static{
        Logger log = Logger.getGlobal();
        log.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        log.addHandler(handler);
    }
}
