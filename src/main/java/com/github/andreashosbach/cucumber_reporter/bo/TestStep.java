package com.github.andreashosbach.cucumber_reporter.bo;

public class TestStep {
    private String source;
    private String text;
    private String result;
    private String error;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public String getKeyword(){
        return source.split(" ")[0].trim();
    }
}
