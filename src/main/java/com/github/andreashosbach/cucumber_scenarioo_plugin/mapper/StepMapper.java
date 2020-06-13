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

import static com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin.configuration;

public class StepMapper {
    public static Step mapStep(PickleStepTestStep testStep, FeatureFile featureFile) {
        Step step = new Step();
        StepDescription stepDescription = new StepDescription();
        Details details = new Details();
        addDetail(details, "Glue Code", testStep.getCodeLocation());
        addDetail(details, "Pattern", testStep.getPattern());
        addDetail(details, "URI", testStep.getUri());
        addDetail(details, "Line", testStep.getStep().getLine());
        addDetail(details, "Keyword", testStep.getStep().getKeyWord());
        String stepText = testStep.getStep().getKeyWord() + testStep.getStep().getText().trim();
        stepDescription.setTitle(stepText.replaceAll("\n", "<br>/"));
        stepDescription.setDetails(details);
        step.setStepDescription(stepDescription);

        StepArgument stepArgument = testStep.getStep().getArgument();
        if (stepArgument instanceof DataTableArgument) {
            DataTableArgument tableArgument = (DataTableArgument) stepArgument;
            String markdownTable = tableArgument.cells()
                    .stream()
                    .map(l -> l
                            .stream()
                            .collect(Collectors.joining("|", "|", "|")))
                    .collect(Collectors.joining("\n"));
            details.addDetail("Data Table", DescriptionFormatter.convertMarkdownToHtml("```\n" + markdownTable + "\n```"));
        }
        return step;
    }

    private static void addDetail(Details details, String key, Object value) {
        if (configuration().stepDetailKeys == null || configuration().stepDetailKeys.isEmpty() || configuration().stepDetailKeys.contains(key)) {
            details.addDetail(key, value);
        }
    }
}
