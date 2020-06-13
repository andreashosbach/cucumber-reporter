package com.github.andreashosbach.cucumber_scenarioo_plugin.name_generators;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildNameGenerator {
    public String getBuildName() {
        return "Build-" + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
    }
}
