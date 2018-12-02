package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestScenario;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import com.github.andreashosbach.cucumber_reporter.dictionary.DictionaryFileReader;
import com.github.andreashosbach.cucumber_reporter.dictionary.DomainDictionary;
import cucumber.api.formatter.NiceAppendable;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static j2html.TagCreator.*;

public class CucumberJ2HTMLFormatter implements CucumberFormatter {

    private NiceAppendable out;
    private List<TestFeature> features = new ArrayList<>();
    private DomainDictionary glossary = DomainDictionary.create(new DictionaryFileReader("/domainDictionary.txt"));

    public CucumberJ2HTMLFormatter(NiceAppendable out) {
        this.out = out;
    }

    @Override
    public void addFeature(TestFeature feature) {
        features.add(feature);
    }

    @Override
    public void writeOutput() {
        out.append(html(addHeadHTML(), addBodyHTML()).renderFormatted());
        out.close();

    }

    private ContainerTag addHeadHTML() {
        return head();
    }

    private ContainerTag addBodyHTML() {
        // style
        String styleText;
        try {
            styleText = new String(Files.readAllBytes(Paths.get(getClass().getResource("/cucumberhtml.css").toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return body(addHeader(), addNavigationHTML(), addContentHTML(), script(rawHtml(
                "var coll = document.getElementsByClassName(\"collapsible_error\");\n" +
                        "var i;\n" +
                        "for (i = 0; i < coll.length; i++) {" +
                        "coll[i].addEventListener(\"click\", function () {" +
                        "this.classList.toggle(\"active\");" +
                        "var content = this.nextElementSibling;" +
                        "if (content.style.display === \"block\") {" +
                        "content.style.display = \"none\";" +
                        "} else {" +
                        "content.style.display = \"block\";" +
                        "}" +
                        "});" +
                        "}")), style(rawHtml(styleText)));


    }

    private ContainerTag addHeader() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        return div(h1(join("Test-Report", span(dateFormat.format(new Date())).withClass("date")))).withClass("header");
    }

    private ContainerTag addNavigationHTML() {
        double total = 0;
        double failed = 0;
        for (TestFeature feature : features) {
            total += feature.totalScenarios();
            failed += feature.failedScenarios();
        }

        return div(addNavOverview(total, failed), addNavFailed(), addNavFeatures(), addNavTags()).withClass("navigation");
    }

    private ContainerTag addNavTags() {
        return h3("Tags");
    }

    private ContainerTag addNavFailed() {
        return div(h3("Failed"), ul(li(addFailedFeatureList())));
    }

    private ContainerTag addFailedFeatureList() {
        return ul(getScenarios().stream()
                .filter(s -> !s.hasPassed())
                .map(s -> li(join(a(s.getName()).withHref("#" + s.getID()))))
                .toArray(ContainerTag[]::new));
    }

    private List<TestScenario> getScenarios() {
        List<TestScenario> scenarios = new ArrayList<>();
        for (TestFeature feature : features) {
            scenarios.addAll(feature.getScenarios());
        }
        return scenarios;
    }

    private ContainerTag addNavFeatures() {
        return div(h3("Features"), addFeatureList());
    }

    private ContainerTag addFeatureList() {
        return ul(each(features, feature ->
                li(join(a(feature.getName()).withHref("#" + feature.getID()), addScenarioList(feature)))
        ));

    }

    private ContainerTag addScenarioList(TestFeature feature) {
        return ul(each(feature.getScenarios(), scenario ->
                li(a(scenario.getName()).withHref("#" + scenario.getID()))));
    }

    private ContainerTag addNavOverview(double total, double failed) {
        long angle = (long) (failed / total * 360d);
        return div(addPie(angle),
                p("Scenarios"),
                p("total: " + (long) total),
                p("passed: " + (long) (total - failed)),
                p("failed: " + (long) failed))
                .withClass("overview");
    }

    private ContainerTag addPie(long angle) {
        return div(div().withClass("pieBackground"), addPieSlices(angle)).withClass("pieContainer");
    }

    private ContainerTag addPieSlices(long angle) {
        if (angle <= 180) {
            String style = "#pieSlice1 .pie {" +
                    "background-color: red;" +
                    "transform:rotate(" + angle + "deg);" +
                    "}";
            return div(div().withClass("pie"), style(style)).withClass("hold").withId("pieSlice1");
        } else {
            String style = "#pieSlice1 .pie {" +
                    "background-color: red;" +
                    "transform:rotate(180deg);" +
                    "}" +
                    "#pieSlice2 {" +
                    "transform:rotate(180deg);" +
                    "}" +
                    "#pieSlice2 .pie {" +
                    "background-color: red;" +
                    "transform:rotate(" + (angle - 180) + "deg);" +
                    "}";
            return div(
                    div(div().withClass("pie"), style(style)).withClass("hold").withId("pieSlice1"),
                    div(div().withClass("pie"), style(style)).withClass("hold").withId("pieSlice2"));
        }
    }

    private ContainerTag addContentHTML() {
        return div(each(features, this::addTestFeature)).withClass("content");
    }

    private ContainerTag addTestFeature(TestFeature feature) {
        return div(a().withId(feature.getID()),
                h2(feature.getName()),
                each(feature.getScenarios(), this::addScenario))
                .withClass("feature");
    }

    private ContainerTag addScenario(TestScenario scenario) {
        ContainerTag scenarioDiv = div(a().withId(scenario.getID()),
                h3(join(scenario.getName(),
                        addScenarioStatistics(scenario),
                        addScenarioTagsHTML(scenario),
                        each(scenario.getSteps(), this::addStep))));

        if (scenario.getFailedSteps() == 0) {
            scenarioDiv.withClass("scenario");
        } else {
            scenarioDiv.withClass("scenario_failed");
        }

        return scenarioDiv;
    }

    private ContainerTag addScenarioStatistics(TestScenario scenario) {
        return span("steps passed: " + scenario.getPassedSteps() +
                " skipped: " + scenario.getSkippedSteps() +
                " failed: " + scenario.getFailedSteps()).withClass("statistics");
    }

    private ContainerTag addScenarioTagsHTML(TestScenario scenario) {
        return span(each(scenario.getTags(), tag -> span(tag).withClass("tag")));
    }

    private ContainerTag addStep(TestStep step) {

        switch (step.getResult()) {
            case "PASSED": {
                return div(formatStep(step)).withClass("step_passed");
            }
            case "SKIPPED": {
                return div(formatStep(step)).withClass("step_skipped");
            }
            case "FAILED": {
                return div(button(formatStep(step)).withClass("collapsible_error"),
                        div(formatText(step.getError())).withClass("error"))
                        .withClass("step_failed");
            }
            default:
                throw new IllegalStateException("Unknown result type " + step.getResult());
        }
    }

    private UnescapedText formatText(String text) {
        List<String> lines = Arrays.asList(text.split("\n"));
        return join(each(lines, line -> join(line, br())));
    }

    private ContainerTag formatStep(TestStep step) {
        String source = step.getSource();
        List<String> words = Arrays.asList(source.split(" "));
        String firstWord = words.get(0);

        return p(join(formatKeyWord(firstWord), " ",
                each(words.subList(1, words.size()), this::formatDomainWord)));
    }

    private DomContent formatKeyWord(String word) {
        switch (word.toLowerCase()) {
            case "given":
            case "when":
            case "then":
                //   case "and":
                //   case "but":
                return span(word).withClass("keyword");
            default:
                return rawHtml(word);
        }
    }

    private DomContent formatDomainWord(String word) {
        if (glossary.containsWord(word)) {
            return span(word + " ").withClass("domainword");
        } else {
            return rawHtml(word + " ");
        }
    }
}
