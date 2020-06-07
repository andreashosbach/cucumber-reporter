package com.github.andreashosbach.cucumber_scenarioo_plugin.mapper;

import com.github.andreashosbach.cucumber_scenarioo_plugin.model.DescriptionFormatter;
import com.github.andreashosbach.cucumber_scenarioo_plugin.model.FeatureFile;
import org.scenarioo.model.docu.entities.UseCase;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.scenarioo.model.docu.entities.generic.Details;

public class UseCaseMapper {
    public static UseCase mapUseCase(FeatureFile featureFile) {
        UseCase useCase = new UseCase();
        useCase.setName(featureFile.getFeatureName());

        String description = featureFile.getFeatureDescription();
        useCase.setDescription(DescriptionFormatter.getShortDescription(description));
        Details details = new Details();
        details.addDetail("Long Description", DescriptionFormatter.convertMarkdownToHtml(description));
        useCase.setDetails(details);

        return useCase;
    }

}
