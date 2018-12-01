package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestScenario;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import cucumber.api.HookTestStep;
import cucumber.api.PickleStepTestStep;
import cucumber.api.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CucumberFormatterEventHandler{
    private final CucumberFormatter formatter;

    private Map<String, List<String>> testSources = new HashMap<>();
    private Map<String, String> featureNames = new HashMap<>();

    private List<String> currentTestSource;
    private TestFeature currentFeature;
    private TestScenario currentScenario;
    private TestStep currentStep;

    public CucumberFormatterEventHandler(CucumberFormatter formatter) {
        this.formatter = formatter;
    }

    public void handleTestSourceRead(TestSourceRead event) {
        List<String> lines = new ArrayList<>();
        for (String line : event.source.split("\n")) {
            lines.add(line.trim());
        }
        testSources.put(event.uri, lines);
        int featureStart = event.source.indexOf("Feature:");
        int featureEnd = event.source.indexOf("\n", featureStart);
        String featureName = event.source.substring(featureStart + 8, featureEnd).trim();
        featureNames.put(event.uri, featureName);
    }

    public void handleTestCaseStarted(TestCaseStarted event) {
        currentTestSource = testSources.get(event.testCase.getUri());
        if (currentFeature == null || !event.testCase.getUri().equals(currentFeature.getUri())) {
            TestFeature feature = new TestFeature();
            feature.setName(featureNames.get(event.testCase.getUri()));
            feature.setUri(event.testCase.getUri());
            formatter.addFeature(feature);
            currentFeature = feature;
        }

        TestScenario scenario = new TestScenario();
        scenario.setName(event.testCase.getName());

        event.testCase.getTags().forEach((t) -> scenario.addTag(t.getName()));
        currentFeature.addScenario(scenario);
        currentScenario = scenario;
    }

    public void handleTestCaseFinished(TestCaseFinished event) {
    }

    public void handleTestStepStarted(TestStepStarted event) {
        TestStep step = new TestStep();
        if (event.testStep instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.testStep;
            step.setSource(currentTestSource.get(testStep.getStepLine() - 1));
            currentScenario.addStep(step);
        } else if (event.testStep instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.testStep;
            step.setSource(hookTestStep.getHookType().name());
        } else {
            throw new IllegalStateException();
        }
        currentStep = step;
    }

    public void handleWrite(WriteEvent event) {
    }

    public void handleEmbed(EmbedEvent event) {
    }

    public void handleTestStepFinished(TestStepFinished event) {
        currentStep.setResult(event.result.getStatus().name());
        currentStep.setError(event.result.getErrorMessage());
    }

    public void startReport() {
    }

    public void finishReport() {
        formatter.writeOutput();
    }
}
