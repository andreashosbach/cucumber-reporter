package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestScenario;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import com.github.andreashosbach.cucumber_reporter.dictionary.DictionaryFileReader;
import com.github.andreashosbach.cucumber_reporter.dictionary.DomainDictionary;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import com.sun.xml.internal.stream.writers.XMLDOMWriterImpl;
import cucumber.api.formatter.NiceAppendable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CucumberHTMLFormatter implements CucumberFormatter{

    private NiceAppendable out;
    private List<TestFeature> features = new ArrayList<>();

    public CucumberHTMLFormatter(NiceAppendable out){
        this.out=out;
    }

    @Override
    public void addFeature(TestFeature feature){
        features.add(feature);
    }

    @Override
    public void writeOutput() {
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

        // scripts
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
        double total = 0;
        double failed = 0;
        for (TestFeature feature : features) {
            total += feature.totalScenarios();
            failed += feature.failedScenarios();
        }

        out.append("<div class=\"navigation\">\n");
        addNavigationOverviewHTML(total, failed);
        if(failed > 0) {
            addNavigationFailedHTML();
        }
        addNavigationFeatureTree();
        addNavigationTagTree();
        out.append("</div>\n");
    }

    private void addNavigationTagTree() {
        out.append("<h3>Tags</h3>\n");
    }

    private void addNavigationFailedHTML() {
        out.append("<h3>Failed Scenarios</h3>\n");
    }

    private void addNavigationFeatureTree() {
        out.append("<h3>Features</h3>\n");
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
    }

    private void addNavigationOverviewHTML(double total, double failed) {
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
