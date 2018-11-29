package com.github.andreashosbach.cucumber_reporter.feature;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.fail;

public class FeatureSteps {
    @Given("^the status before$")
    public void theStatusBefore() {
    }

    @When("^the thing happens$")
    public void theThingHappens() {
    }

    @Then("^nothing is ok$")
    public void nothingIsOk() throws Throwable {
        fail("This failed!");
    }

    @And("^this is skipped$")
    public void thisIsSkipped() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^some more$")
    public void someMore() {
    }

    @Then("^all is ok$")
    public void allIsOk() {
    }

    @And("^and well$")
    public void andWell() {
    }

    @Given("^there are (\\d+) cucumbers$")
    public void thereAreCucumbers(int arg0) {
    }

    @When("^I eat (\\d+) cucumbers$")
    public void iEatCucumbers(int arg0){
    }

    @Then("^I should have (\\d+) cucumbers$")
    public void iShouldHaveCucumbers(int arg0){
    }
}
