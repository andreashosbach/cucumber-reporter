package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import org.scenarioo.model.docu.entities.Branch;

public class BranchMapper {
    public static Branch mapBranch(String name) {
        Branch branch = new Branch();
        branch.setName(name);
        return branch;
    }
}
