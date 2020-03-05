package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import com.github.andreashosbach.cucumber_reporter.model.FeatureFiles;
import cucumber.api.HookTestStep;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.api.TestStep;
import cucumber.api.event.*;
import gherkin.pickles.PickleTag;
import org.scenarioo.api.ScenarioDocuWriter;
import org.scenarioo.api.exception.ScenarioDocuSaveException;
import org.scenarioo.model.docu.entities.*;
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

    public CucumberFormatterEventHandler(String branchName, String buildName,String outputDirectory) {
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
                Files.createDirectory(Paths.get(outputDirectory));
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
        System.out.println("FEATURE FILE READ " + event.uri);
        List<String> lines = Arrays.asList(event.source.split("\n"));
        featureFiles.addFeatureFile(new FeatureFile(event.uri, event.source));
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
        if (currentFeatureFile == null || !event.testCase.getUri().equals(currentFeatureFile.getUri())) {
            handleTestSuiteFinished();
            currentFeatureFile = featureFiles.getFeatureFile(event.testCase.getUri());
            handleTestSuiteStarted();
        }
        System.out.println("  START SCENARIO " + event.testCase.getName());
        currentScenario = new Scenario();
        currentScenario.setName(event.testCase.getName());
        currentScenario.addDetail("designation", event.testCase.getScenarioDesignation());
        currentScenario.setDescription(currentFeatureFile.getScenarioDescription(event.testCase.getLine()));
        event.testCase.getTags().forEach(t -> currentScenario.addLabel(sanitizeTag(t)));
    }

    private static String sanitizeTag(PickleTag tag) {
        return tag.getName().replaceAll("[^a-zA-Z0-9_-]", "");
    }

    //End of Feature Element
    public void handleTestCaseFinished(TestCaseFinished event) {
        System.out.println("  END SCENARIO");
        currentScenario.setStatus(mapResult(event.result));
        writer.saveScenario(currentUseCase, currentScenario);
    }

    //Start of Step
    public void handleTestStepStarted(TestStepStarted event) {
        currentStep = new Step();
        StepDescription stepDescription = new StepDescription();
        stepDescription.setIndex(currentStepIndex);
        Details details = new Details();
        details.addDetail("glue_code", event.testStep.getCodeLocation());
        stepDescription.setDetails(details);
        currentStepIndex++;

        if (event.testStep instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.testStep;
            stepDescription.setTitle(currentFeatureFile.getLine(testStep.getStepLine()));
        } else if (event.testStep instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.testStep;
            stepDescription.setTitle(hookTestStep.getHookType().toString());
        } else {
            throw new IllegalStateException("Unknown step type");
        }

        System.out.println("    " + stepDescription.getTitle());
        currentStep.setStepDescription(stepDescription);
    }

    //End of Step
    public void handleTestStepFinished(TestStepFinished event) {
        System.out.println("STEP FINISHED");
        try {
            Thread.sleep(100); // need to wait to avoid task rejection exception in scenarioo writer
        } catch (InterruptedException e) {
        }
        writer.saveStep(currentUseCase, currentScenario, currentStep);
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
