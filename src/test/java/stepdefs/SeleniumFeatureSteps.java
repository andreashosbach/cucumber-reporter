package stepdefs;

import com.github.andreashosbach.cucumber_reporter.model.Screenshot;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class SeleniumFeatureSteps {

    private WebDriver webDriver;

    @AfterStep(value = "@Screenshots")
    public void afterStep() {
        TakesScreenshot scrShot = ((TakesScreenshot) webDriver);
        Screenshot.save(webDriver.getTitle(), scrShot.getScreenshotAs(OutputType.BYTES));
    }

    @Before
    public void setupDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        webDriver = new ChromeDriver();

        webDriver.manage().window().maximize();

        webDriver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public void cleanupDriver() {
        webDriver.close();;
    }

    @Given("the website {string}")
    public void theWebsite(String arg0) {
        webDriver.get(arg0);
    }

    @When("the link {string} is clicked")
    public void theLinkIsClicked(String arg0) {
        WebDriverWait wait = new WebDriverWait(webDriver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(arg0))).click();
    }

    @Then("the website with title {string} is displayed")
    public void theWebsiteWithTitleIsDisplayed(String arg0) {
        assertEquals(arg0, webDriver.getTitle());
    }
}
