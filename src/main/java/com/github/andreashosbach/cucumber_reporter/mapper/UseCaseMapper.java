package com.github.andreashosbach.cucumber_reporter.mapper;

import com.github.andreashosbach.cucumber_reporter.model.FeatureFile;
import org.scenarioo.model.docu.entities.UseCase;

public class UseCaseMapper {
    public static UseCase mapUseCase(FeatureFile featureFile) {
        UseCase useCase = new UseCase();
        useCase.setName(featureFile.getFeatureName());
        useCase.setDescription(featureFile.getFeatureDescription());
        return useCase;
    }

}
