package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

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
        scenario.setDescription(featureFile.getScenarioDescription(testCase.getLine()));
        Details details = new Details();
        details.addDetail("id", testCase.getId());
        details.addDetail("uri", testCase.getUri());
        details.addDetail("line", testCase.getLine());
        details.addDetail("keyword", testCase.getKeyword());
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
