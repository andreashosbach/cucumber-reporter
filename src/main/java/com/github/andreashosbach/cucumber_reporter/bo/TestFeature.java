package com.github.andreashosbach.cucumber_reporter.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestFeature {
    private String uri;
    private String name;
    private List<TestScenario> scenarios = new ArrayList<>();
    private String uID = UUID.randomUUID().toString();


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addScenario(TestScenario scenario){
        this.scenarios.add(scenario);
    }

    public List<TestScenario> getScenarios(){
        return scenarios;
    }

    public String getID() {
        return uID;
    }

    public long totalScenarios(){
        return scenarios.size();
    }

    public long failedScenarios(){
        return scenarios.stream().filter((s) -> !s.hasPassed()).count();
    }
}
