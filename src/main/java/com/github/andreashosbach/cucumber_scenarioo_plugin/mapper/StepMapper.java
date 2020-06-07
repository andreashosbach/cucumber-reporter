package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import io.cucumber.plugin.event.PickleStepTestStep;
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
        details.addDetail("line", testStep.getStep().getLine());
        details.addDetail("keyword", testStep.getStep().getKeyWord());
        stepDescription.setTitle(testStep.getStep().getKeyWord() + testStep.getStep().getText());
        stepDescription.setDetails(details);
        step.setStepDescription(stepDescription);
        return step;
    }
}
