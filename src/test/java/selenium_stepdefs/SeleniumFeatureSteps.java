package selenium_stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeleniumFeatureSteps extends StepDefinitionBase {

    @Given("a webbrowser")
    public void aWebbrowser() {
        getWebDriver().navigate().to("about:version");
    }

    @Given("the website {string} is displayed")
    public void theWebsite(String url) {
        getWebDriver().navigate().to(url);
    }

    @When("the user clicks on the link {string}")
    public void theLinkIsClicked(String linkText) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(linkText))).click();
    }

    @Then("the website with title {string} should be displayed")
    public void theWebsiteWithTitleIsDisplayed(String title) {
        assertEquals(title, getWebDriver().getTitle());
    }


    @Given("the user enters {string} into the field {string}")
    public void theTextIsEnteredInTheField(String text, String fieldId) {
        getWebDriver().findElement(By.id(fieldId)).sendKeys(text);
    }

    @When("the user clicks on the submit button")
    public void theButtonIsClicked() {
        getWebDriver().findElement(By.xpath("//button[@type=\"submit\"]")).click();
    }

    @Then("a page containing the following text should be displayed")
    public void aPageContainingTheFollowingTextShouldBeDisplayed(String text) {
        assertTrue(getWebDriver().findElement(By.tagName("body")).getText().contains(text.replaceAll("\n", " ")));
    }
}