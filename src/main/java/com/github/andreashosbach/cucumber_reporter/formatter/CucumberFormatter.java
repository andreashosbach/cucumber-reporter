package com.github.andreashosbach.cucumber_reporter.formatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestScenario;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import com.github.andreashosbach.cucumber_reporter.dictionary.DictionaryFileReader;
import com.github.andreashosbach.cucumber_reporter.dictionary.DomainDictionary;
import cucumber.api.HookTestStep;
import cucumber.api.PickleStepTestStep;
import cucumber.api.event.EmbedEvent;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventListener;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestCaseStarted;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestRunStarted;
import cucumber.api.event.TestSourceRead;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.api.event.WriteEvent;
import cucumber.api.formatter.NiceAppendable;

public final class CucumberFormatter implements EventListener {

    private final NiceAppendable out;
    private Map<String, List<String>> testSources = new HashMap<>();
    private Map<String, String> featureNames = new HashMap<>();

    private List<String> currentTestSource;
    private TestFeature currentFeature;
    private TestScenario currentScenario;
    private TestStep currentStep;

    private List<TestFeature> features = new ArrayList<>();

    private EventHandler<TestSourceRead> testSourceReadHandler = new EventHandler<TestSourceRead>() {
        @Override
        public void receive(TestSourceRead event) {
            handleTestSourceRead(event);
        }
    };
    private EventHandler<TestCaseStarted> caseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            handleTestCaseStarted(event);
        }
    };
    private EventHandler<TestCaseFinished> caseFinishedHandler = new EventHandler<TestCaseFinished>() {
        @Override
        public void receive(TestCaseFinished event) {
            handleTestCaseFinished(event);
        }
    };

    private EventHandler<TestStepStarted> stepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            handleTestStepStarted(event);
        }
    };
    private EventHandler<TestStepFinished> stepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            handleTestStepFinished(event);
        }
    };
    private EventHandler<TestRunStarted> runStartedHandler = new EventHandler<TestRunStarted>() {
        @Override
        public void receive(TestRunStarted event) {
            startReport();
        }
    };

    private EventHandler<TestRunFinished> runFinishedHandler = new EventHandler<TestRunFinished>() {
        @Override
        public void receive(TestRunFinished event) {
            finishReport();
        }
    };
    private EventHandler<WriteEvent> writeEventhandler = new EventHandler<WriteEvent>() {
        @Override
        public void receive(WriteEvent event) {
            handleWrite(event);
        }
    };
    private EventHandler<EmbedEvent> embedEventhandler = new EventHandler<EmbedEvent>() {
        @Override
        public void receive(EmbedEvent event) {
            handleEmbed(event);
        }
    };

    @SuppressWarnings("WeakerAccess") // Used by PluginFactory
    public CucumberFormatter(Appendable out) {
        this.out = new NiceAppendable(out);
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);

        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);

        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);

        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);
        publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);

        publisher.registerHandlerFor(TestRunStarted.class, runStartedHandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
    }

    private void handleTestSourceRead(TestSourceRead event) {
        List<String> lines = new ArrayList<>();
        for (String line : event.source.split("\n")) {
            lines.add(line.trim());
        }
        testSources.put(event.uri, lines);
        int featureStart = event.source.indexOf("Feature:");
        int featureEnd = event.source.indexOf("\n", featureStart);
        String featureName = event.source.substring(featureStart + 8, featureEnd).trim();
        featureNames.put(event.uri, featureName);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        currentTestSource = testSources.get(event.testCase.getUri());
        if (currentFeature == null || !event.testCase.getUri().equals(currentFeature.getUri())) {
            TestFeature feature = new TestFeature();
            feature.setName(featureNames.get(event.testCase.getUri()));
            feature.setUri(event.testCase.getUri());
            features.add(feature);
            currentFeature = feature;
        }

        TestScenario scenario = new TestScenario();
        scenario.setName(event.testCase.getName());

        //event.testCase.getTags().forEach((t) -> scenario.addTag(t.getName()));
        currentFeature.addScenario(scenario);
        currentScenario = scenario;
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
    }

    private void handleTestStepStarted(TestStepStarted event) {
        TestStep step = new TestStep();
        if (event.testStep instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep) event.testStep;
            step.setSource(currentTestSource.get(testStep.getStepLine() - 1));
            currentScenario.addStep(step);
        } else if (event.testStep instanceof HookTestStep) {
            HookTestStep hookTestStep = (HookTestStep) event.testStep;
            step.setSource(hookTestStep.getHookType().name());
        } else {
            throw new IllegalStateException();
        }
        currentStep = step;
    }

    private void handleWrite(WriteEvent event) {
    }

    private void handleEmbed(EmbedEvent event) {
    }

    private void handleTestStepFinished(TestStepFinished event) {
        currentStep.setResult(event.result.getStatus().name());
        currentStep.setError(event.result.getErrorMessage());
    }

    private void startReport() {
    }

    private void finishReport() {
        out.append("<html>\n");
        out.append("<head>\n");
        out.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cucumberhtml.css\">\n");
        out.append("</head>\n");
        out.append("<body>\n");

        // header
        addHeaderHTML();

        // navigation
        addNavigationHTML();

        // content
        out.append("<div class=\"content\">\n");
        for (TestFeature feature : features) {
            addFeatureHTML(feature);
        }
        out.append("</div>\n");

        out.append("<script>\n");
        out.append("var coll = document.getElementsByClassName(\"collapsible_error\");\n");
        out.append("var i;\n");
        out.append("for (i = 0; i < coll.length; i++) {\n");
        out.append("coll[i].addEventListener(\"click\", function () {\n");
        out.append("this.classList.toggle(\"active\");\n");
        out.append("var content = this.nextElementSibling;\n");
        out.append("if (content.style.display === \"block\") {\n");
        out.append("content.style.display = \"none\";\n");
        out.append("} else {\n");
        out.append("content.style.display = \"block\";\n");
        out.append("}\n");
        out.append("});\n");
        out.append("}\n");
        out.append("</script>\n");
        out.append("</body>\n");
        out.append("</html>\n");
        out.close();
    }

    private void addFeatureHTML(TestFeature feature) {
        out.append("<div class=\"feature\">\n");
        out.append("<a id=\"" + feature.getID() + "\"/>\n");
        out.append("<h2>" + feature.getName() + "</h2>\n");
        for (int j = 0; j < feature.getScenarios().size(); j++) {
            TestScenario scenario = feature.getScenarios().get(j);
            addScenarioHTML(scenario);
        }
        out.append("</div>\n");
    }

    private void addScenarioHTML(TestScenario scenario) {
        if (scenario.getFailedSteps() == 0) {
            out.append("<div class=\"scenario\">\n");
        } else {
            out.append("<div class=\"scenario_failed\">\n");
        }
        out.append("<a id=\"" + scenario.getID() + "\"/>\n");
        out.append("<h3>" + scenario.getName());
        out.append("<span class=\"statistics\">");
        out.append("steps passed: " + scenario.getPassedSteps());
        out.append(" skipped: " + scenario.getSkippedSteps());
        out.append(" failed: " + scenario.getFailedSteps());
        out.append("</span>\n");
        for (String tag : scenario.getTags()) {
            out.append("<span class=\"tag\">" + tag + "</span>");
        }
        out.append("</h3>\n");
        for (TestStep step : scenario.getSteps()) {
            addStepHTML(step);
        }
        out.append("</div>\n");
    }

    private void addStepHTML(TestStep step) {
        switch (step.getResult()) {
            case "PASSED":
                out.append("<div class=\"step_passed\">\n");
                out.append("<p>" + formatStep(step.getSource()) + "</p>\n");
                break;
            case "FAILED":
                out.append("<div class=\"step_failed\">\n");
                out.append("<button class=\"collapsible_error\">" + formatStep(step.getSource()) + "</button>\n");
                break;
            case "SKIPPED":
                out.append("<div class=\"step_skipped\">\n");
                out.append("<p>" + formatStep(step.getSource()) + "</p>\n");
                break;
            default:
                throw new IllegalStateException("Unknow result " + step.getResult());
        }

        if (step.getError() != null && !step.getError().equals("null")) {
            String error = step.getError();
            error = error.replace("\n", "<br/>\n");
            out.append("<div class=\"error\"><p>" + error + "</p></div>\n");
        }
        out.append("</div>\n");
    }

    private void addHeaderHTML() {
        out.append("<div class=\"header\">\n");
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        out.append("<h1>" + "Test-Report <span class=\"date\">" + format.format(new Date()) + "</span></h1>\n");
        out.append("</div>\n");
    }

    private void addNavigationHTML() {
        out.append("<div class=\"navigation\">\n");

        addNavigationOverviewHTML();

        out.append("<ul>\n");
        for (TestFeature feature : features) {
            out.append("<li><a href=\"#" + feature.getID() + "\">" + feature.getName() + "</a></li>\n");
            out.append("<ul>\n");
            for (TestScenario scenario : feature.getScenarios()) {
                out.append("<li><a href=\"#" + scenario.getID() + "\">" + scenario.getName() + "</a></li>\n");
            }
            out.append("</ul>\n");
        }
        out.append("</ul>\n");
        out.append("</div>\n");
    }

    private void addNavigationOverviewHTML() {
        double total = 0;
        double failed = 0;
        for (TestFeature feature : features) {
            total += feature.totalScenarios();
            failed += feature.failedScenarios();
        }
        long angle = (long) (failed / total * 360d);

        out.append("<div class=\"overview\">\n");
        out.append("<div class=\"pieContainer\">\n");
        out.append("<div class=\"pieBackground\"></div>\n");
        out.append("<div id=\"pieSlice1\" class=\"hold\"><div class=\"pie\"></div></div>\n");
        if (angle > 180) {
            out.append("<div id=\"pieSlice2\" class=\"hold\"><div class=\"pie\"></div></div>\n");
        }
        out.append("<div class=\"innerCircle\"/>\n");
        out.append("</div>\n");
        out.append("</div>\n");

        out.append("<style>\n");
        if (angle <= 180) {
            out.append("#pieSlice1 .pie {\n");
            out.append("background-color: red;\n");
            out.append("transform:rotate(" + angle + "deg);\n");
            out.append("}\n");
        } else {
            out.append("#pieSlice1 .pie {\n");
            out.append("background-color: red;\n");
            out.append("transform:rotate(180deg);\n");
            out.append("}\n");
            out.append("#pieSlice2 {\n");
            out.append("transform:rotate(180deg);\n");
            out.append("}\n");
            out.append("#pieSlice2 .pie {\n");
            out.append("background-color: red;\n");
            out.append("transform:rotate(" + (angle - 180) + "deg);\n");
            out.append("}\n");
        }
        out.append("</style>\n");

        out.append("<p>Scenarios</p>");
        out.append("<p>total: " + (long) total + "</p>");
        out.append("<p>passed: " + (long) (total - failed) + "</p>");
        out.append("<p>failed: " + (long) failed + "</p>");
        out.append("</div>");
    }

    private String formatStep(String source) {
        String[] tokens = source.split(" ");
        StringBuilder output = new StringBuilder();

        String firstWord = tokens[0];
        switch (firstWord.toLowerCase()) {
            case "given":
            case "when":
            case "then":
                //   case "and":
                //   case "but":
                output.append("<span class=\"keyword\">" + firstWord + "</span>");
                break;
            default:
                output.append(firstWord);
        }

        DomainDictionary glossary = DomainDictionary.create(new DictionaryFileReader("/domainDictionary.txt"));
        for (int i = 1; i < tokens.length; i++) {
            output.append(" ");
            if (glossary.containsWord(tokens[i])) {
                output.append("<span class=\"domainword\">" + tokens[i] + "</span>");
            } else {
                output.append(tokens[i]);
            }
        }

        return output.toString();
    }
}
