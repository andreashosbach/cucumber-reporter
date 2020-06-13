package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.DescriptionFormatter;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import org.scenarioo.model.docu.entities.UseCase;
import org.scenarioo.model.docu.entities.generic.Details;

import static com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin.configuration;

public class UseCaseMapper {
    public static UseCase mapUseCase(FeatureFile featureFile) {
        UseCase useCase = new UseCase();
        useCase.setName(featureFile.getFeatureName());

        String description = featureFile.getFeatureDescription();
        useCase.setDescription(DescriptionFormatter.getShortDescription(description));
        Details details = new Details();
        addDetail(details,"Long Description", DescriptionFormatter.convertMarkdownToHtml(description));
        useCase.setDetails(details);

        return useCase;
    }

    private static void addDetail(Details details, String key, Object value) {
        if (configuration().useCaseDetailKeys == null || configuration().useCaseDetailKeys.isEmpty() || configuration().useCaseDetailKeys.contains(key)) {
                details.addDetail(key, value);
            }
    }
 }
