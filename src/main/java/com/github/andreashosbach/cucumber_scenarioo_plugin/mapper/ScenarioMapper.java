package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.DescriptionFormatter;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.GherkinUtils;
import io.cucumber.plugin.event.TestCase;
import org.scenarioo.model.docu.entities.Scenario;
import org.scenarioo.model.docu.entities.generic.Details;

import java.util.UUID;

public class ScenarioMapper {

    private static int currentExample;
    private static TestCase currentScenarioOutline;

    public static Scenario mapScenario(TestCase testCase, FeatureFile featureFile) {
        Scenario scenario = new Scenario();

        if (testCase.getKeyword().equals("Scenario Outline")) {
            scenario.setName(testCase.getName() + scenarioOutlineCounter(testCase));
        } else {
            scenario.setName(testCase.getName());
        }

        String description = featureFile.getScenarioDescription(testCase.getLine());
        scenario.setDescription(DescriptionFormatter.getShortDescription(description));



        Details details = new Details();
        details.addDetail("Id", testCase.getId());
        details.addDetail("URI", testCase.getUri());
        details.addDetail("Line", testCase.getLine());
        details.addDetail("Keyword", testCase.getKeyword());
        details.addDetail("Long Description", DescriptionFormatter.convertMarkdownToHtml(description));
        scenario.setDetails(details);
        testCase.getTags().forEach(t -> scenario.addLabel(sanitizeTag(t)));
        return scenario;
    }

    private static String scenarioOutlineCounter(TestCase testCase) {
        if (!testCase.equals(testCase)) {
            currentScenarioOutline = testCase;
            currentExample = 0;
        }
        currentExample++;
        return String.format(" (%d)", currentExample);
    }

    private static String sanitizeTag(String tag) {
        return tag.replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
