package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import org.scenarioo.model.docu.entities.Branch;
import org.scenarioo.model.docu.entities.generic.Details;

import java.util.Map;

public class BranchMapper {
    public static Branch mapBranch(String name, String branchDescription, Map<String, String> detailsMap) {
        Branch branch = new Branch();
        branch.setName(name);
        branch.setDescription(branchDescription);
        if (detailsMap != null) {
            Details details = new Details();
            for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
                details.addDetail(entry.getKey(), entry.getValue());
            }
            branch.setDetails(details);
        }
        return branch;
    }
}
