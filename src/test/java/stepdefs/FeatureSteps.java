package stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.fail;

public class FeatureSteps {

    @Given("the status before")
    public void theStatusBefore() {
    }

    @When("the thing happens")
    public void theThingHappens() {
    }

    @Then("nothing is ok")
    public void nothingIsOk() {
        fail("This failed!");
    }

    @And("this is skipped")
    public void thisIsSkipped() {
        // Write code here that turns the phrase above into concrete actions
        throw new RuntimeException();
    }

    @And("some more")
    public void someMore() {
    }

    @Then("all is ok")
    public void allIsOk() {
    }

    @And("and well")
    public void andWell() {
    }

    @Given("there are {int} cucumbers")
    public void thereAreCucumbers(int arg0) {
    }

    @When("I eat {int} cucumbers")
    public void iEatCucumbers(int arg0) {
    }

    @Then("I should have {int} cucumbers")
    public void iShouldHaveCucumbers(int arg0) {
    }
}
