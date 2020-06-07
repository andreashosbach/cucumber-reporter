package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.DescriptionFormatter;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.StepArgument;
import org.scenarioo.model.docu.entities.Step;
import org.scenarioo.model.docu.entities.StepDescription;
import org.scenarioo.model.docu.entities.generic.Details;

import java.util.stream.Collectors;

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

        StepArgument stepArgument = testStep.getStep().getArgument();
        if (stepArgument != null && (stepArgument instanceof DataTableArgument)) {
            DataTableArgument tableArgument = (DataTableArgument) stepArgument;
            String markdownTable = tableArgument.cells()
                    .stream()
                    .map(l -> l
                            .stream()
                            .collect(Collectors.joining("|", "|", "|")))
                    .collect(Collectors.joining("\n"));
            details.addDetail("Data Table", DescriptionFormatter.convertMarkdownToHtml("```\n" + markdownTable + "\n```"));
            System.out.println(markdownTable);
        }
        return step;
    }
}
