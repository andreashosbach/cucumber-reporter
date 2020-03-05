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
        while (pos < lines.size() && !lines.get(pos).trim().startsWith("Feature:")) {
            pos++;
        }
        pos++;

        String description = "";
        while (pos < lines.size() && !GherkinUtils.startsWithKeyword(lines.get(pos))) {
            if (!GherkinUtils.isComment(lines.get(pos)) && !lines.get(pos).isEmpty())
                description += lines.get(pos) + "\n";
            pos++;
        }
        return description;
    }

    public String getScenarioDescription(int scenarioTitleLine) {
        int pos = scenarioTitleLine + 1;
        String description = "";
        while(pos < lines.size() && !GherkinUtils.isStep(lines.get(pos))){
            description += lines.get(pos) + "\n";
            pos++;
        }
        return description;
    }

    public String getFeatureName() {
        int pos = 0;
        while (pos < lines.size() && !lines.get(pos).trim().startsWith("Feature:")) {
            pos++;
        }
        return lines.get(pos).replaceFirst("Feature:", "").trim();
    }
}
