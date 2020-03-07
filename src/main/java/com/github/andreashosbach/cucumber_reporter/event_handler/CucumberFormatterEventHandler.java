package com.github.andreashosbach.cucumber_reporter.event_handler;

import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import com.github.andreashosbach.cucumber_reporter.model.FeatureFiles;
import com.github.andreashosbach.cucumber_reporter.model.Screenshot;
import io.cucumber.plugin.event.*;
import org.scenarioo.api.ScenarioDocuWriter;
import org.scenarioo.api.exception.ScenarioDocuSaveException;
import org.scenarioo.model.docu.entities.*;
import org.scenarioo.model.docu.entities.Status;
import org.scenarioo.model.docu.entities.Step;
import org.scenarioo.model.docu.entities.generic.Details;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class CucumberFormatterEventHandler {

    private ScenarioDocuWriter writer;

    private String outputDirectory;
    private String branchName;
    private String buildName;

    private FeatureFiles featureFiles;

    private Branch currentBranch;
    private Build currentBuild;
    private UseCase currentUseCase;
    private Scenario currentScenario;
    private Status aggregatedScenarioStatus;
    private int currentStepIndex;
    private Step currentStep;
    private FeatureFile currentFeatureFile;

    public CucumberFormatterEventHandler(String branchName, String buildName, String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.branchName = branchName;
        this.buildName = buildName;
    }

    //Start of Test
    public void startReport() {
        System.out.println("START REPORT");
        featureFiles = new FeatureFiles();
        if (Files.notExists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        writer = new ScenarioDocuWriter(new File(outputDirectory), branchName, buildName);

        currentBranch = new Branch();
        currentBranch.setName(branchName);

        currentBuild = new Build();
        currentBuild.setDate(new Date());
        currentBuild.setName(buildName);
        currentBuild.setRevision("0.1");
    }

    //End of Test
    public void finishReport() {
        handleTestSuiteFinished();
        System.out.println("FINISH REPORT");
        writer.saveBranchDescription(currentBranch);
        writer.saveBuildDescription(currentBuild);

        try {
            writer.flush();
        } catch (ScenarioDocuSaveException e) {
            throw new RuntimeException(e);
        }
        System.out.println("REPORT WRITTEN");
    }

    //Start of Feature
    public void handleTestSourceRead(TestSourceRead event) {
        System.out.println("FEATURE FILE READ " + event.getUri());
        featureFiles.addFeatureFile(new FeatureFile(event.getUri().toString(), event.getSource()));
    }

    //Start of Feature
    public void handleTestSuiteStarted() {
        currentUseCase = new UseCase();
        currentUseCase.setName(currentFeatureFile.getFeatureName());
        currentUseCase.setDescription(currentFeatureFile.getFeatureDescription());
        //TODO get Tags from feature
        aggregatedScenarioStatus = null;
    }

    //End of Feature
    public void handleTestSuiteFinished() {
        if (currentUseCase != null) {
            System.out.println("END FEATURE");
            currentUseCase.setStatus(aggregatedScenarioStatus);
            writer.saveUseCase(currentUseCase);
        }
        currentUseCase = null;
    }

    //Start of Feature Element
    public void handleTestCaseStarted(TestCaseStarted event) {
        if (currentFeatureFile == null || !event.getTestCase().getUri().toString().equals(currentFeatureFile.getUri())) {
            handleTestSuiteFinished();
            currentFeatureFile = featureFiles.getFeatureFile(event.getTestCase().getUri().toString());
            handleTestSuiteStarted();
        }
        System.out.println("  START SCENARIO " + event.getTestCase().getName());
        currentScenario = new Scenario();
        currentScenario.setName(event.getTestCase().getName());
        currentScenario.setDescription(currentFeatureFile.getScenarioDescription(event.getTestCase().getLine()));
        event.getTestCase().getTags().forEach(t -> currentScenario.addLabel(sanitizeTag(t)));
        currentStepIndex = 0;
    }

    private static String sanitizeTag(String tag) {
        return tag.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    //End of Feature Element
    public void handleTestCaseFinished(TestCaseFinished event) {
        System.out.println("  END SCENARIO");
        currentScenario.setStatus(mapResult(event.getResult()));
        writer.saveScenario(currentUseCase, currentScenario);
    }

    //Start of Step
    public void handleTestStepStarted(TestStepStarted event) {
        currentStep = new Step();
        StepDescription stepDescription = new StepDescription();
        stepDescription.setIndex(currentStepIndex);
        Details details = new Details();
        details.addDetail("glue_code", event.getTestStep().getCodeLocation());

        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
//            stepDescription.setTitle(testStep.getPickleStep().getText());
            stepDescription.setTitle(currentFeatureFile.getLine(testStep.getStep().getLine()).trim());
        } else if (event.getTestStep() instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
            stepDescription.setTitle(hookTestStep.getHookType().toString());
        } else {
            throw new IllegalStateException("Unknown step type");
        }

        System.out.println("    " + stepDescription.getTitle());
        stepDescription.setDetails(details);
        currentStep.setStepDescription(stepDescription);
    }

    //End of Step
    public void handleTestStepFinished(TestStepFinished event) {
        System.out.println("STEP FINISHED");
        if (event.getTestStep() instanceof PickleStepTestStep) {
            try {
                Thread.sleep(100); // need to wait to avoid task rejection exception in scenarioo writer
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Page page = new Page();
            page.setName(Screenshot.getPageName(currentStepIndex));
            currentStep.setPage(page);
            writer.saveStep(currentUseCase, currentScenario, currentStep);
            writer.saveScreenshotAsPng(currentUseCase.getName(), currentScenario.getName(), currentStepIndex, Screenshot.getScreenshotImage(currentStepIndex));
            currentStepIndex++;
        } else {
            System.out.println("IGNORING HOOK STEPS");
        }
    }

    //Write Hook
    public void handleWrite(WriteEvent event) {
    }

    //Embed Hook
    public void handleEmbed(EmbedEvent event) {
    }

    private Status mapResult(Result result) {
        Status status;
        switch (result.getStatus()) {
            case PASSED:
                status = Status.SUCCESS;
                break;
            case SKIPPED:
            case PENDING:
            case UNDEFINED:
            case AMBIGUOUS:
            case FAILED:
                status = Status.FAILED;
                break;
            default:
                throw new IllegalStateException("Unknown result status " + result.getStatus());
        }

        if (aggregatedScenarioStatus == null) {
            aggregatedScenarioStatus = status;
        } else if (status == Status.FAILED) {
            aggregatedScenarioStatus = Status.FAILED;
        }
        return status;
    }
}
