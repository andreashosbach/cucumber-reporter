package com.github.andreashosbach.cucumber_reporter.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestScenario {
    private String name;
    private List<TestStep> steps = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private String uID = UUID.randomUUID().toString();

    public long getFailedSteps(){
        return steps.stream().filter((s) -> s.getResult().equals("FAILED")).count();
    }

    public long getSkippedSteps(){
        return steps.stream().filter((s) -> s.getResult().equals("SKIPPED")).count();
    }

    public long getPassedSteps(){
        return steps.stream().filter((s) -> s.getResult().equals("PASSED")).count();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public void addStep(TestStep step) {
        steps.add(step);
    }

    public String getID() {
        return uID;
    }

    public boolean hasPassed(){
        return getFailedSteps() == 0;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public List<String> getTags(){
        return tags;
    }
}
