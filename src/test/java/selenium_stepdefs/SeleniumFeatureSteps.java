package selenium_stepdefs;

import com.github.andreashosbach.cucumber_reporter.model.Screenshot;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

    @AfterStep
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
        webDriver.close();
        webDriver.quit();
    }

    @Given("a webbrowser")
    public void theWebsite() {
        webDriver.navigate().to("about:version");
    }

    @Given("the website {string} is displayed")
    public void theWebsite(String url) {
        webDriver.navigate().to(url);
    }

    @When("the link {string} is clicked")
    public void theLinkIsClicked(String linkText) {
        WebDriverWait wait = new WebDriverWait(webDriver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(linkText))).click();
    }

    @Then("the website with title {string} should be displayed")
    public void theWebsiteWithTitleIsDisplayed(String title) {
        assertEquals(title, webDriver.getTitle());
    }
}
