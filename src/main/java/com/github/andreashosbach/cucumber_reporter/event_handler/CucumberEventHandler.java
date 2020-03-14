package com.github.andreashosbach.cucumber_reporter.event_handler;

import com.github.andreashosbach.cucumber_reporter.ScenariooDocumentationGenerator;
import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import io.cucumber.plugin.event.*;

import java.util.logging.Logger;

public final class CucumberEventHandler {
    private static final Logger logger = Logger.getGlobal();

    private final String outputDirectory;
    private final String branchName;
    private final String buildName;
    private final String revision;

    private ScenariooDocumentationGenerator docGenerator;

    public CucumberEventHandler(String branchName, String buildName, String revision, String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.branchName = branchName;
        this.buildName = buildName;
        this.revision = revision;
    }

    //Start of Test
    public void startReport() {
        docGenerator = new ScenariooDocumentationGenerator(branchName, buildName, revision, outputDirectory);
    }

    //End of Test
    public void finishReport() {
        docGenerator.useCaseFinished();
        docGenerator.finish();
    }

    //Start of Feature
    public void handleTestSourceRead(TestSourceRead event) {
        docGenerator.addFeatureFile(new FeatureFile(event.getUri().toString(), event.getSource()));
    }

    //Start of Feature Element
    public void handleTestCaseStarted(TestCaseStarted event) {
        if (docGenerator.isInNewFeature(event.getTestCase())) {
            docGenerator.useCaseFinished();
            docGenerator.useCaseStarted(event.getTestCase().getUri().toString());
        }
        docGenerator.scenarioStarted(event.getTestCase());
    }

    public void handleTestCaseFinished(TestCaseFinished event) {
        docGenerator.scenarioFinished(event.getResult());
    }

    public void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            docGenerator.gherkinStepStarted((PickleStepTestStep) event.getTestStep());
        } else if (event.getTestStep() instanceof HookTestStep) {
            docGenerator.hookStepStarted((HookTestStep) event.getTestStep());
        } else {
            logger.severe("Unknown step type encountered in handleTestStepStarted()");
            throw new IllegalStateException("Unknown step type");
        }
    }

    public void handleTestStepFinished(TestStepFinished event) {
        try {
            Thread.sleep(100); // need to wait to avoid task rejection exception in scenarioo writer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (event.getTestStep() instanceof PickleStepTestStep) {
            docGenerator.gherkinStepFinished((PickleStepTestStep) event.getTestStep(), event.getResult());
        } else if (event.getTestStep() instanceof HookTestStep) {
            docGenerator.hookStepFinished((HookTestStep) event.getTestStep(), event.getResult());
        } else {
            logger.severe("Unknown step type encountered in handleTestStepFinished()");
            throw new IllegalStateException("Unknown step type");
        }
    }

    //Write Hook
    public void handleWrite(WriteEvent event) {
        logger.warning("Handle write ignored");
    }

    //Embed Hook
    public void handleEmbed(EmbedEvent event) {
        logger.warning("Handle embed ignored");
    }
}
