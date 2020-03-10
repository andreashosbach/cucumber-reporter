package com.github.andreashosbach.cucumber_reporter.model;

import java.util.Arrays;

public class GherkinUtils {
    private static final String[] FEATURE_KEYWORDS = {"Feature:"};
    private static final String[] STEP_KEYWORDS = {"Given ", "When ", "Then ", "And ", "But "};
    private static final String[] SCENARIO_KEYWORDS = {"Scenario:", "Example:", "Background:", "Scenario Outline:"};
    private static final String COMMENT_CHAR = "#";
    private static final String TAG_CHAR = "@";

    public static boolean isFeatureTitle(String line){
        final String l = line.trim();
        return  Arrays.stream(FEATURE_KEYWORDS).anyMatch(l::startsWith);
    }

    public static boolean isScenarioTitle(String line) {
        final String l = line.trim();
        return  Arrays.stream(SCENARIO_KEYWORDS).anyMatch(l::startsWith);
    }

    public static boolean startsWithTag(String line){
        return line.trim().startsWith(TAG_CHAR);
    }

    public static boolean isComment(String line) {
        return line.trim().startsWith(COMMENT_CHAR);
    }

    public static boolean isStep(String line) {
        final String l = line.trim();
        return Arrays.stream(STEP_KEYWORDS).noneMatch(l::startsWith);
    }
}
