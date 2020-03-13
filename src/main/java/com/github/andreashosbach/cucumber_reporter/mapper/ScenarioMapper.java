package com.github.andreashosbach.cucumber_reporter.mapper;

import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import io.cucumber.plugin.event.TestCase;
import org.scenarioo.model.docu.entities.Scenario;
import org.scenarioo.model.docu.entities.generic.Details;

public class ScenarioMapper {
    public static Scenario mapScenario(TestCase testCase, FeatureFile featureFile) {
        Scenario scenario = new Scenario();
        scenario.setName(testCase.getName());
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

    private static String sanitizeTag(String tag) {
        return tag.replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
