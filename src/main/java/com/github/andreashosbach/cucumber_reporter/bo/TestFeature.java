package com.github.andreashosbach.cucumber_reporter.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestFeature {
    private String uri;
    private String name;
    private String description;
    private List<TestCase> testCases = new ArrayList<>();
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

    public void addTestCase(TestCase testCase){
        this.testCases.add(testCase);
    }

    public List<TestCase> getTestCases(){
        return testCases;
    }

    public String getID() {
        return uID;
    }

    public long totalTestCases(){
        return testCases.size();
    }

    public long failedTestCases(){
        return testCases.stream().filter((s) -> !s.hasPassed()).count();
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
