package com.github.andreashosbach.cucumber_reporter.event_handler;

import com.github.andreashosbach.cucumber_reporter.model.*;
import io.cucumber.plugin.event.*;
import org.scenarioo.api.ScenarioDocuWriter;
import org.scenarioo.api.exception.ScenarioDocuSaveException;
import org.scenarioo.model.docu.entities.*;
import org.scenarioo.model.docu.entities.Status;
import org.scenarioo.model.docu.entities.Step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class CucumberFormatterEventHandler {

    private static Logger logger = Logger.getGlobal();

    private ScenarioDocuWriter writer;

    private String outputDirectory;
    private String branchName;
    private String buildName;
    private String revision;

    private FeatureFiles featureFiles;

    private Branch currentBranch;
    private Build currentBuild;
    private UseCase currentUseCase;
    private Scenario currentScenario;
    private Status aggregatedScenarioStatus;
    private int currentStepIndex;
    private Step currentStep;
    private FeatureFile currentFeatureFile;

    public CucumberFormatterEventHandler(String branchName, String buildName, String revision, String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.branchName = branchName;
        this.buildName = buildName;
        this.revision = revision;
    }

    //Start of Test
    public void startReport() {
        featureFiles = new FeatureFiles();
        if (Files.notExists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
                logger.info(String.format("Created directory '%s'", outputDirectory));
            } catch (IOException e) {
                logger.severe(String.format("Unable to create output directory at '%s'", outputDirectory ));
                throw new RuntimeException(e);
            }
        }
        writer = new ScenarioDocuWriter(new File(outputDirectory), branchName, buildName);

        currentBranch = BranchMapper.mapBranch(branchName);
        logger.info(String.format("Branch '%s'", branchName));
        currentBuild = BuildMapper.mapBuild(buildName, revision);
        logger.info(String.format("Bulid '%s', Revivision '%s'", buildName, revision));
    }

    //End of Test
    public void finishReport() {
        handleTestSuiteFinished();
        writer.saveBranchDescription(currentBranch);
        writer.saveBuildDescription(currentBuild);

        try {
            writer.flush();
        } catch (ScenarioDocuSaveException e) {
            logger.info("Finishing report failed");
            throw new RuntimeException(e);
        }
        logger.info("Report finished");
    }

    //Start of Feature
    public void handleTestSourceRead(TestSourceRead event) {
        featureFiles.addFeatureFile(new FeatureFile(event.getUri().toString(), event.getSource()));
        logger.fine(String.format("Source file '%s' read", event.getUri().toString()));
    }

    //Start of Feature
    private void handleTestSuiteStarted() {
        currentUseCase = UseCaseMapper.mapUseCase(currentFeatureFile);
        aggregatedScenarioStatus = null;
        logger.fine(String.format("UseCase '%s' started", currentUseCase.getName()));
    }

    //End of Feature
    private void handleTestSuiteFinished() {
        if (currentUseCase != null) {
            currentUseCase.setStatus(aggregatedScenarioStatus);
            writer.saveUseCase(currentUseCase);
            logger.fine(String.format("UseCase '%s' finished", currentUseCase.getName()));

        }
        currentUseCase = null;
    }

    //Start of Feature Element
    public void handleTestCaseStarted(TestCaseStarted event) {
        if (isInNewFeature(event.getTestCase())) {
            handleTestSuiteFinished();
            currentFeatureFile = featureFiles.getFeatureFile(event.getTestCase().getUri().toString());
            handleTestSuiteStarted();
        }
        currentScenario = ScenarioMapper.mapScenario(event.getTestCase(), currentFeatureFile);
        currentStepIndex = -1;
        Screenshot.reset();
        logger.fine(String.format("Scenario '%s' started", currentScenario.getName()));
    }

    private boolean isInNewFeature(TestCase testCase) {
        return currentFeatureFile == null || !testCase.getUri().toString().equals(currentFeatureFile.getUri());
    }

    //End of Feature Element
    public void handleTestCaseFinished(TestCaseFinished event) {
        currentScenario.setStatus(mapResult(event.getResult()));
        writer.saveScenario(currentUseCase, currentScenario);
        logger.fine(String.format("UseCase '%s' finished", currentUseCase.getName()));
    }

    //Start of Step
    public void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            currentStepIndex++;
            PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
            currentStep = StepMapper.mapStep(testStep, currentFeatureFile);
            currentStep.getStepDescription().setIndex(currentStepIndex);
            logger.fine(String.format("PickleStep '%s' started", currentStep.getStepDescription().getTitle()));
        } else if (event.getTestStep() instanceof HookTestStep) {
            // do nothing we are still in the same gherkin step
            HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
            logger.fine(String.format("HookSTep '%s' started", hookTestStep.getHookType().toString()));
        } else {
            logger.severe("Unknown step type encountered in handleTestStepStarted()");
            throw new IllegalStateException("Unknown step type");
        }
    }

    //End of Step
    public void handleTestStepFinished(TestStepFinished event) {
        try {
            Thread.sleep(100); // need to wait to avoid task rejection exception in scenarioo writer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (event.getTestStep() instanceof PickleStepTestStep) {
            // Save only on the after hook
            logger.fine(String.format("PickleStep '%s' finished", currentStep.getStepDescription().getTitle()));
        } else if (event.getTestStep() instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
            if (hookTestStep.getHookType() == HookType.AFTER_STEP) {
                logger.fine(String.format("HookStep '%s 'for '%s' finished", hookTestStep.getHookType().toString(), currentStep.getStepDescription().getTitle()));
                Page page = new Page();
                page.setName(Screenshot.getPageName(currentStepIndex));
                currentStep.setPage(page);
                writer.saveStep(currentUseCase, currentScenario, currentStep);
                writer.saveScreenshotAsPng(currentUseCase.getName(), currentScenario.getName(), currentStepIndex, Screenshot.getScreenshotImage(currentStepIndex));
                logger.fine(String.format("Written data for Step '%s' ", currentStep.getStepDescription().getTitle()));
            }
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

    private Status mapResult(Result result) {
        Status status = StatusMapper.mapStatus(result);

        if (aggregatedScenarioStatus == null) {
            aggregatedScenarioStatus = status;
        } else if (status == Status.FAILED) {
            aggregatedScenarioStatus = Status.FAILED;
        }
        return status;
    }
}
