package com.github.andreashosbach.cucumber_reporter.model;

import java.util.Arrays;
import java.util.List;

public class FeatureFile {
    private String uri;
    private List<String> lines;

    public FeatureFile(String uri, String code) {
        this.uri = uri;
        lines = Arrays.asList(code.split("\n"));
    }

    public String getUri() {
        return uri;
    }

    public String getLine(int lineNumber) {
        return lines.get(lineNumber - 1);
    }

    public String getFeatureDescription() {
        int pos = 0;
        while (pos < lines.size() && !GherkinUtils.isFeatureTitle(lines.get(pos))) {
            pos++;
        }
        pos++;

        StringBuilder description = new StringBuilder();
        while (pos < lines.size() && !GherkinUtils.isScenarioTitle(lines.get(pos)) && GherkinUtils.startsWithTag(lines.get(pos))) {
            if (!GherkinUtils.isComment(lines.get(pos)))
                description.append(lines.get(pos)).append("\n");
            pos++;
        }
        return description.toString().trim();
    }

    public String getScenarioDescription(int scenarioTitleLine) {
        StringBuilder description = new StringBuilder();
        int pos = scenarioTitleLine + 1;
        while (pos < lines.size() && !GherkinUtils.isStep(lines.get(pos))) {
            description.append(lines.get(pos)).append("\n");
            pos++;
        }
        return description.toString().trim();
    }

    public String getFeatureName() {
        String titleLine = lines.stream()
                .filter(GherkinUtils::isFeatureTitle)
                .findFirst().orElseThrow(() -> new IllegalStateException("Feature title not found"));
        return titleLine.substring(titleLine.indexOf(":") + 1).trim();
    }
}
