package selenium_stepdefs;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.Screenshot;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HookSteps extends StepDefinitionBase{

    @AfterStep
    public void afterStep() {
        TakesScreenshot scrShot = ((TakesScreenshot) getWebDriver());
        Screenshot.save(getWebDriver().getTitle(), scrShot.getScreenshotAs(OutputType.BYTES));
    }

    @Before
    public void setupDriver() {
        Logger log = Logger.getGlobal();
        log.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        log.addHandler(handler);

        if(getWebDriver() == null) {
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
            WebDriver webDriver = new ChromeDriver();

            webDriver.manage().window().setSize(new Dimension(1024, 768));

            webDriver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
            webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            setWebDriver(webDriver);
        }
    }

    @After
    public void cleanupDriver() {
//        getWebDriver().close();
//        getWebDriver().quit();
    }
}
