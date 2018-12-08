package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestCase;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import cucumber.api.HookTestStep;
import cucumber.api.PickleStepTestStep;
import cucumber.api.event.*;

import java.util.*;

public final class CucumberFormatterEventHandler {
    private final CucumberFormatter formatter;

    private Map<String, List<String>> testSources = new HashMap<>();
    private Map<String, String> featureNames = new HashMap<>();

    private List<String> currentTestSource;
    private TestFeature currentFeature;
    private TestCase currentTestCase;
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
            feature.setDescription(getFeatureDescription(event.testCase.getUri()));
            feature.setUri(event.testCase.getUri());
            formatter.addFeature(feature);
            currentFeature = feature;
        }

        TestCase testCase = new TestCase();
        testCase.setName(event.testCase.getName());

        event.testCase.getTags().forEach((t) -> testCase.addTag(t.getName().substring(1)));
        currentFeature.addTestCase(testCase);
        currentTestCase = testCase;
    }

    public String getFeatureDescription(String uri) {
        List<String> source = testSources.get(uri);

        //find line with Feature:
        //get all lines that are not commented until: "Scenario:" "Scenario Outline:" "Background:"
        int pos = 0;
        while (pos < source.size() && !source.get(pos).trim().startsWith("Feature:")) {
            pos++;
        }
        pos++;

        String description = "";
        while (pos < source.size() && !startsWithKeyword(source.get(pos))) {
            if (!isComment(source.get(pos)) && !source.get(pos).isEmpty())
                description += source.get(pos) + "\n";
            pos++;
        }
        return description;
    }

    boolean startsWithKeyword(String line) {
        String l = line.trim();
        return l.startsWith("Scenario:") || l.startsWith("Example:") || l.startsWith("Background:") || l.startsWith("Scenario Outline:") || l.startsWith("@");
    }

    boolean isComment(String line) {
        return line.trim().startsWith("#");
    }

    public void handleTestCaseFinished(TestCaseFinished event) {
        currentTestCase.setDuration(event.result.getDuration());
    }

    public void handleTestStepStarted(TestStepStarted event) {
        TestStep step = new TestStep();
        if (event.testStep instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.testStep;
            step.setSource(currentTestSource.get(testStep.getStepLine() - 1));
            step.setText(testStep.getPickleStep().getText());
            currentTestCase.addStep(step);
        } else if (event.testStep instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.testStep;
            step.setSource(hookTestStep.getHookType().name());
            System.out.println("Hook" + hookTestStep.getHookType().name());
        } else {
            throw new IllegalStateException("Unknown step type");
        }
        currentStep = step;
    }

    public void handleWrite(WriteEvent event) {
        System.out.println("Write: " + event.text);
    }

    public void handleEmbed(EmbedEvent event) {
        System.out.println("Embed: " + Arrays.toString(event.data));
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
