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
        details.addDetail("Glue Code", testStep.getCodeLocation());
        details.addDetail("Pattern", testStep.getPattern());
        details.addDetail("Uri", testStep.getUri());
        details.addDetail("Line", testStep.getStep().getLine());
        details.addDetail("Keyword", testStep.getStep().getKeyWord());
        String stepText = testStep.getStep().getKeyWord() + testStep.getStep().getText().trim();
        stepDescription.setTitle(stepText.replaceAll("\n", "<br>/"));
        stepDescription.setDetails(details);
        step.setStepDescription(stepDescription);
        return step;
    }
}
