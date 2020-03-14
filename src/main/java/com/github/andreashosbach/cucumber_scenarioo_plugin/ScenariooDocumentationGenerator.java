package com.github.andreashosbach.cucumber_scenarioo_plugin;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFiles;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.Screenshot;
import com.github.andreashosbach.cucumber_scenarioo_plugin.mapper.*;
import io.cucumber.plugin.event.*;
import org.scenarioo.api.ScenarioDocuWriter;
import org.scenarioo.api.exception.ScenarioDocuSaveException;
import org.scenarioo.model.docu.entities.Status;
import org.scenarioo.model.docu.entities.Step;
import org.scenarioo.model.docu.entities.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class ScenariooDocumentationGenerator {
    private static final Logger logger = Logger.getGlobal();

    private final ScenarioDocuWriter writer;

    private final FeatureFiles featureFiles;

    private final Branch branch;
    private final Build build;

    private UseCase currentUseCase;
    private Scenario currentScenario;
    private Status aggregatedScenarioStatus;
    private int currentStepIndex;
    private Step currentStep;
    private FeatureFile currentFeatureFile;

    public ScenariooDocumentationGenerator(String branchName, String buildName, String revision, String outputDirectory) {
        featureFiles = new FeatureFiles();
        createOutputDirectory(outputDirectory);
        writer = new ScenarioDocuWriter(new File(outputDirectory), branchName, buildName);

        branch = BranchMapper.mapBranch(branchName);
        logger.info(String.format("Branch '%s'", branchName));
        build = BuildMapper.mapBuild(buildName, revision);
        logger.info(String.format("Bulid '%s', Revivision '%s'", buildName, revision));
    }

    private void createOutputDirectory(String outputDirectory) {
        if (Files.notExists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
                logger.info(String.format("Created directory '%s'", outputDirectory));
            } catch (IOException e) {
                logger.severe(String.format("Unable to create output directory at '%s'", outputDirectory));
                throw new RuntimeException(e);
            }
        }
    }

    public void finish() {
        writer.saveBranchDescription(branch);
        writer.saveBuildDescription(build);

        try {
            writer.flush();
        } catch (ScenarioDocuSaveException e) {
            logger.info("Finishing report failed");
            throw new RuntimeException(e);
        }
        logger.info("Report finished");
    }

    public void addFeatureFile(FeatureFile featureFile) {
        featureFiles.addFeatureFile(featureFile);
        logger.fine(String.format("Source file '%s' read", featureFile.getUri()));
    }

    public void useCaseStarted(String uri) {
        currentFeatureFile = featureFiles.getFeatureFile(uri);
        currentUseCase = UseCaseMapper.mapUseCase(currentFeatureFile);
        aggregatedScenarioStatus = null;
        logger.fine(String.format("UseCase '%s' started", currentUseCase.getName()));
    }

    public void useCaseFinished() {
        if (currentUseCase != null) {
            currentUseCase.setStatus(aggregatedScenarioStatus);
            writer.saveUseCase(currentUseCase);
            logger.fine(String.format("UseCase '%s' finished", currentUseCase.getName()));
        }
        currentUseCase = null;
    }

    public void scenarioStarted(TestCase testCase) {
        currentScenario = ScenarioMapper.mapScenario(testCase, currentFeatureFile);
        currentStepIndex = -1;
        Screenshot.reset();
        logger.fine(String.format("Scenario '%s' started", currentScenario.getName()));
    }

    public boolean isInNewFeature(TestCase testCase) {
        return currentFeatureFile == null || !testCase.getUri().toString().equals(currentFeatureFile.getUri());
    }

    public void scenarioFinished(Result result) {
        currentScenario.setStatus(mapResult(result));
        writer.saveScenario(currentUseCase, currentScenario);
        logger.fine(String.format("UseCase '%s' finished", currentUseCase.getName()));
    }

    public void gherkinStepStarted(PickleStepTestStep step) {
        if(currentStep != null){
            logger.severe("Step not finished probably missing AFTER_STEP hook");
        }
        currentStepIndex++;
        currentStep = StepMapper.mapStep(step, currentFeatureFile);
        currentStep.getStepDescription().setIndex(currentStepIndex);
        logger.fine(String.format("Gherkin step '%s' started", currentStep.getStepDescription().getTitle()));
    }

    public void hookStepStarted(HookTestStep step) {
        logger.fine(String.format("Hook step '%s' started", step.getHookType().toString()));
    }

    public void gherkinStepFinished(PickleStepTestStep step, Result result) {
        // Save only on the after hook
        Status status = mapResult(result);
        currentStep.getStepDescription().setStatus(status);
        currentStep.getStepDescription().addDetail("step_duration", mapDuration(result.getDuration()));
        logger.fine(String.format("Gherkin step '%s' finished expecting AFTER_STEP hook", currentStep.getStepDescription().getTitle()));
    }


    public void hookStepFinished(HookTestStep step, Result result) {
        if (step.getHookType() == HookType.AFTER_STEP) {
            logger.fine(String.format("Hook step '%s 'for '%s' finished", step.getHookType().toString(), currentStep.getStepDescription().getTitle()));
            Page page = new Page();
            page.setName(Screenshot.getPageName(currentStepIndex));
            currentStep.setPage(page);
            Status status = mapResult(result);
            currentStep.getStepDescription().addDetail(step.getHookType() + "_duration", mapDuration(result.getDuration()));
            if(status == Status.FAILED){
                logger.fine("Hook step failed, set step status to failed");
                currentStep.getStepDescription().setStatus(status);
            }
            writer.saveStep(currentUseCase, currentScenario, currentStep);
            writer.saveScreenshotAsPng(currentUseCase.getName(), currentScenario.getName(), currentStepIndex, Screenshot.getScreenshotImage(currentStepIndex));
            logger.fine(String.format("Written data for Step '%s' ", currentStep.getStepDescription().getTitle()));
            currentStep = null;
        }
    }

    private String mapDuration(Duration duration) {
        return String.format("%d s %d ms", duration.getSeconds(), TimeUnit.MILLISECONDS.convert(duration.getNano(), TimeUnit.NANOSECONDS));
    }

    private Status mapResult(Result result) {
        Status status = StatusMapper.mapStatus(result);
        aggregatedScenarioStatus = aggregateStatus(aggregatedScenarioStatus, status);
        return status;
    }

    private Status aggregateStatus(Status currentStatus, Status newStatus){
        if (currentStatus == null) {
            return  newStatus;
        } else if (newStatus == Status.FAILED) {
            return  newStatus;
        }
        return currentStatus;
    }
}
