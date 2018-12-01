package com.github.andreashosbach.cucumber_reporter;

import com.github.andreashosbach.cucumber_reporter.formatter.CucumberHTMLFormatter;
import com.github.andreashosbach.cucumber_reporter.formatter.CucumberFormatterEventHandler;
import cucumber.api.event.*;
import cucumber.api.formatter.NiceAppendable;

public final class CucumberReportPlugin implements EventListener {
    private final CucumberFormatterEventHandler eventHandler;

    private EventHandler<TestSourceRead> testSourceReadHandler = new EventHandler<TestSourceRead>() {
        @Override
        public void receive(TestSourceRead event) {
            eventHandler.handleTestSourceRead(event);
        }
    };
    private EventHandler<TestCaseStarted> caseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            eventHandler.handleTestCaseStarted(event);
        }
    };
    private EventHandler<TestCaseFinished> caseFinishedHandler = new EventHandler<TestCaseFinished>() {
        @Override
        public void receive(TestCaseFinished event) {
            eventHandler.handleTestCaseFinished(event);
        }
    };

    private EventHandler<TestStepStarted> stepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            eventHandler.handleTestStepStarted(event);
        }
    };
    private EventHandler<TestStepFinished> stepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            eventHandler.handleTestStepFinished(event);
        }
    };
    private EventHandler<TestRunStarted> runStartedHandler = new EventHandler<TestRunStarted>() {
        @Override
        public void receive(TestRunStarted event) {
            eventHandler.startReport();
        }
    };

    private EventHandler<TestRunFinished> runFinishedHandler = new EventHandler<TestRunFinished>() {
        @Override
        public void receive(TestRunFinished event) {
            eventHandler.finishReport();
        }
    };
    private EventHandler<WriteEvent> writeEventhandler = new EventHandler<WriteEvent>() {
        @Override
        public void receive(WriteEvent event) {
            eventHandler.handleWrite(event);
        }
    };
    private EventHandler<EmbedEvent> embedEventhandler = new EventHandler<EmbedEvent>() {
        @Override
        public void receive(EmbedEvent event) {
            eventHandler.handleEmbed(event);
        }
    };

    @SuppressWarnings("WeakerAccess") // Used by PluginFactory
    public CucumberReportPlugin(Appendable out) {
        eventHandler = new CucumberFormatterEventHandler(new CucumberHTMLFormatter(new NiceAppendable(out)));
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
}
