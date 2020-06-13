package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import org.scenarioo.model.docu.entities.Branch;
import org.scenarioo.model.docu.entities.generic.Details;

import java.util.Map;

import static com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin.configuration;

public class BranchMapper {
    public static Branch mapBranch() {
        Branch branch = new Branch();
        branch.setName(configuration().branchName);
        branch.setDescription(configuration().branchDescription);
        if (configuration().branchDetails != null) {
            Details details = new Details();
            for (Map.Entry<String, String> entry : configuration().branchDetails.entrySet()) {
                details.addDetail(entry.getKey(), entry.getValue());
            }
            branch.setDetails(details);
        }
        return branch;
    }
}
