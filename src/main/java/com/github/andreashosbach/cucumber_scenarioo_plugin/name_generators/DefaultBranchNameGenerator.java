package com.github.andreashosbach.cucumber_scenarioo_plugin.name_generators;

public class DefaultBranchNameGenerator implements BranchNameGenerator {
    @Override
    public String getBranchName() {
        return "branch";
    }
}
