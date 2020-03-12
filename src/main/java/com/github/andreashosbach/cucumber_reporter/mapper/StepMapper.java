package com.github.andreashosbach.cucumber_reporter.mapper;

import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import io.cucumber.plugin.event.*;
import org.scenarioo.model.docu.entities.Step;
import org.scenarioo.model.docu.entities.StepDescription;
import org.scenarioo.model.docu.entities.generic.Details;

public class StepMapper {
    public static Step mapStep(PickleStepTestStep testStep, FeatureFile featureFile) {
        Step step = new Step();
        StepDescription stepDescription = new StepDescription();
        Details details = new Details();
        details.addDetail("glue_code", testStep.getCodeLocation());
        details.addDetail("pattern", testStep.getPattern());
        details.addDetail("uri", testStep.getUri());
        stepDescription.setTitle(featureFile.getLine(testStep.getStep().getLine()).trim());
        stepDescription.setDetails(details);
        step.setStepDescription(stepDescription);
        return step;
    }
}
