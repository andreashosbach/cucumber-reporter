package com.github.andreashosbach.cucumber_scenarioo_plugin.model;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class DescriptionFormatter {

    public static String convertMarkdownToHtml(String markdown){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static String getShortDescription(String description){
        return description.split("\n")[0];
    }
}
