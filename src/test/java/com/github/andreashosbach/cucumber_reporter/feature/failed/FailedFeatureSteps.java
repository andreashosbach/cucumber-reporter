package com.github.andreashosbach.cucumber_reporter.feature.failed;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.fail;

public class FailedFeatureSteps {
    @Given("^the status before$")
    public void theStatusBefore() {
    }

    @When("^the thing happens$")
    public void theThingHappens() {
    }

    @Then("^all is ok$")
    public void andWell() {
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
}
