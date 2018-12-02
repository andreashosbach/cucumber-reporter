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
import java.util.*;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class CucumberJ2HTMLFormatter implements CucumberFormatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList("given", "when", "then"));
    private String styleText;
    private String scriptText;

    private final NiceAppendable out;
    private final List<TestFeature> features = new ArrayList<>();
    private final DomainDictionary glossary = DomainDictionary.create(new DictionaryFileReader("/domainDictionary.txt"));
    private Date executionTime;


    public CucumberJ2HTMLFormatter(NiceAppendable out) {
        this.out = out;

        try {
            styleText = new String(Files.readAllBytes(Paths.get(getClass().getResource("/style.css").toURI())));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load style information: " + e.getMessage());
        }

        try {
            scriptText = new String(Files.readAllBytes(Paths.get(getClass().getResource("/scripts.js").toURI())));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load script information: " + e.getMessage());
        }
    }

    @Override
    public void addFeature(TestFeature feature) {
        features.add(feature);
    }

    @Override
    public void writeOutput() {
        executionTime = new Date();
        out.append(html(addHeadHTML(), addBodyHTML()).renderFormatted());
        out.close();
    }

    private ContainerTag addHeadHTML() {
        return head(title("Test-Report " + DATE_FORMAT.format(executionTime)));
    }

    private ContainerTag addBodyHTML() {
        return body(addHeader(), addNavigationHTML(), addContentHTML(), style(rawHtml(styleText)), script(rawHtml(scriptText)));
    }

    private ContainerTag addHeader() {
        return div(h1(join("Test-Report", span(DATE_FORMAT.format(executionTime)).withClass("date")))).withClass("header");
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
        Map<String, List<TestScenario>> tags = new HashMap<>();
        getScenarios().
                forEach(s -> s.getTags()
                        .forEach(t -> {
                            if (!tags.containsKey(t))
                                tags.put(t, new ArrayList<>());
                            tags.get(t).add(s);
                        }));
        if (tags.size() > 0) {
            return div(h3("Tags"),
                    ul(each(tags.keySet(), tag ->
                            li(join(p(tag), addScenarioList(tags.get(tag)))))));
        } else {
            return div();
        }
    }

    private ContainerTag addNavFailed() {
        List<TestScenario> failedScenarios = getScenarios().stream().filter(s -> !s.hasPassed()).collect(Collectors.toList());
        if (failedScenarios.size() > 0) {
            return div(h3("Failed"),
                    ul(li(ul(failedScenarios.stream()
                            .map(s -> li(join(a(s.getName()).withHref("#" + s.getID()))))
                            .toArray(ContainerTag[]::new)))));
        } else {
            return div();
        }
    }

    private List<TestScenario> getScenarios() {
        List<TestScenario> scenarios = new ArrayList<>();
        for (TestFeature feature : features) {
            scenarios.addAll(feature.getScenarios());
        }
        return scenarios;
    }

    private ContainerTag addNavFeatures() {
        return div(h3("Features"),
                ul(each(features, feature ->
                        li(join(a(feature.getName()).withHref("#" + feature.getID()),
                                addScenarioList(feature.getScenarios())))
                )));
    }

    private ContainerTag addScenarioList(List<TestScenario> scenarios) {
        return ul(each(scenarios, scenario ->
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
                        div(formatErrorText(step.getError())).withClass("error"))
                        .withClass("step_failed");
            }
            default:
                throw new IllegalStateException("Unknown result type " + step.getResult());
        }
    }

    private UnescapedText formatErrorText(String text) {
        List<String> lines = Arrays.asList(text.split("\n"));
        return join(each(lines, line -> join(line, br())));
    }

    private ContainerTag formatStep(TestStep step) {
        String source = step.getText();
        List<String> words = Arrays.asList(source.split(" "));

        return p(join(formatKeyWord(step.getKeyword()), " ",
                each(words.subList(1, words.size()), this::formatDomainWord)));
    }

    private DomContent formatKeyWord(String word) {
        if (KEYWORDS.contains(word.toLowerCase())) {
            return span(word).withClass("keyword");
        } else {
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
