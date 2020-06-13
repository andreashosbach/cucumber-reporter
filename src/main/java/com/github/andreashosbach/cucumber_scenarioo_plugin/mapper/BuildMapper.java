package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import org.scenarioo.model.docu.entities.Build;
import org.scenarioo.model.docu.entities.generic.Details;

import java.util.Date;
import java.util.Map;

import static com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin.configuration;

public class BuildMapper {
    public static Build mapBuild(String name){
        Build build = new Build();
        build.setDate(new Date());
        build.setName(name);
        build.setRevision(configuration().revision);
        if (configuration().buildDetails != null) {
            Details details = new Details();
            for (Map.Entry<String, String> entry : configuration().buildDetails.entrySet()) {
                details.addDetail(entry.getKey(), entry.getValue());
            }
            build.setDetails(details);
        }
        return build;
    }
}
