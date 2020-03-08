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

public final class CucumberFormatterEventHandler {

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writer = new ScenarioDocuWriter(new File(outputDirectory), branchName, buildName);

        currentBranch = BranchMapper.mapBranch(branchName);
        currentBuild = BuildMapper.mapBuild(buildName, revision);
    }

    //End of Test
    public void finishReport() {
        handleTestSuiteFinished();
        writer.saveBranchDescription(currentBranch);
        writer.saveBuildDescription(currentBuild);

        try {
            writer.flush();
        } catch (ScenarioDocuSaveException e) {
            throw new RuntimeException(e);
        }
    }

    //Start of Feature
    public void handleTestSourceRead(TestSourceRead event) {
        featureFiles.addFeatureFile(new FeatureFile(event.getUri().toString(), event.getSource()));
    }

    //Start of Feature
    private void handleTestSuiteStarted() {
        currentUseCase = UseCaseMapper.mapUseCase(currentFeatureFile);
        aggregatedScenarioStatus = null;
    }

    //End of Feature
    private void handleTestSuiteFinished() {
        if (currentUseCase != null) {
            currentUseCase.setStatus(aggregatedScenarioStatus);
            writer.saveUseCase(currentUseCase);
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
    }

    private boolean isInNewFeature(TestCase testCase) {
        return currentFeatureFile == null || !testCase.getUri().toString().equals(currentFeatureFile.getUri());
    }

    //End of Feature Element
    public void handleTestCaseFinished(TestCaseFinished event) {
        currentScenario.setStatus(mapResult(event.getResult()));
        writer.saveScenario(currentUseCase, currentScenario);
    }

    //Start of Step
    public void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            currentStepIndex++;
            PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
            currentStep = StepMapper.mapStep(testStep, currentFeatureFile);
        } else if (event.getTestStep() instanceof HookTestStep) {
            // do nothing we are still in the same gherkin step
        } else {
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
        } else if (event.getTestStep() instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
            if (hookTestStep.getHookType() == HookType.AFTER_STEP) {
                Page page = new Page();
                page.setName(Screenshot.getPageName(currentStepIndex));
                currentStep.setPage(page);
                writer.saveStep(currentUseCase, currentScenario, currentStep);
                writer.saveScreenshotAsPng(currentUseCase.getName(), currentScenario.getName(), currentStepIndex, Screenshot.getScreenshotImage(currentStepIndex));
            }
        } else {
            throw new IllegalStateException("Unknown step type");
        }
    }

    //Write Hook
    public void handleWrite(WriteEvent event) {
    }

    //Embed Hook
    public void handleEmbed(EmbedEvent event) {
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
