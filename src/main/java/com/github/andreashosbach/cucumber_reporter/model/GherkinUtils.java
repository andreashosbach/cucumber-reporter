package com.github.andreashosbach.cucumber_reporter.model;

public class GherkinUtils {
    public static boolean startsWithKeyword(String line) {
        String l = line.trim();
        return l.startsWith("Scenario:") || l.startsWith("Example:") || l.startsWith("Background:") || l.startsWith("Scenario Outline:") || l.startsWith("@");
    }

    public static boolean isComment(String line) {
        return line.trim().startsWith("#");
    }

    public static boolean isStep(String line) {
        String l = line.trim();
        return !l.startsWith("Given ");
    }
}
