package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import io.cucumber.plugin.event.Result;
import org.scenarioo.model.docu.entities.Status;

public class StatusMapper {
    public static Status mapStatus(Result result) {
        switch (result.getStatus()) {
            case PASSED:
                return Status.SUCCESS;
            case SKIPPED:
            case PENDING:
            case UNDEFINED:
            case AMBIGUOUS:
            case FAILED:
                return Status.FAILED;
            default:
                throw new IllegalStateException("Unknown result status " + result.getStatus());
        }
    }
}
