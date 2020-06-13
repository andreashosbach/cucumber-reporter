package com.github.andreashosbach.cucumber_scenarioo_plugin.name_generators;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin.configuration;

public class BuildNameGenerator {
    public String getBuildName() {

        if (configuration().buildName == null) {
            return "Build-" + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
        }

        String prefix = configuration().buildName.prefix;
        String format = configuration().buildName.datetimeFormat;
        String postfix = configuration().buildName.postfix;
        String buildName = prefix;
        if (!format.isEmpty()) {
            buildName += new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
        }
        return buildName + postfix;
    }
}
