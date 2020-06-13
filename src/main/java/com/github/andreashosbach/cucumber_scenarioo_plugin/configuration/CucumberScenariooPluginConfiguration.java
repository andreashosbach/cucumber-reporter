package com.github.andreashosbach.cucumber_scenarioo_plugin.configuration;

import java.util.List;
import java.util.Map;

public class CucumberScenariooPluginConfiguration {
    public String outputDirectory;
    public String branchName;
    public String branchDescription;
    public Map<String, String> branchDetails;
    public String revision;
    public Map<String, String> buildDetails;
    public List<String> stepDetailKeys;
    public List<String> useCaseDetailKeys;
    public List<String> scenarioDetailKeys;
}
