package com.github.andreashosbach.cucumber_reporter;

import com.github.andreashosbach.cucumber_reporter.event_handler.CucumberEventHandler;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class CucumberReportPlugin implements EventListener {
    private final CucumberEventHandler eventHandler;

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
    public CucumberReportPlugin(String out) {
        String build = "Build-" + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
        eventHandler = new CucumberEventHandler("branch", build, "1.0", out);
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        //Build
        publisher.registerHandlerFor(TestRunStarted.class, runStartedHandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);

        //FeatureFile
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);

        //FeatureElement
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);

        //Step
        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);

        //Hooks
        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);
        publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);
    }
}
