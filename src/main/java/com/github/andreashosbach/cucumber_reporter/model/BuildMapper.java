package com.github.andreashosbach.cucumber_reporter.model;

import org.scenarioo.model.docu.entities.Build;

import java.util.Date;

public class BuildMapper {

    public static Build mapBuild(String name, String revision){
        Build build = new Build();
        build.setDate(new Date());
        build.setName(name);
        build.setRevision(revision);
        return build;
    }
}
