package com.github.andreashosbach.cucumber_reporter.formatter;

import com.github.andreashosbach.cucumber_reporter.bo.TestFeature;
import com.github.andreashosbach.cucumber_reporter.bo.TestScenario;
import com.github.andreashosbach.cucumber_reporter.bo.TestStep;
import com.github.andreashosbach.cucumber_reporter.dictionary.DictionaryFileReader;
import com.github.andreashosbach.cucumber_reporter.dictionary.DomainDictionary;
import cucumber.api.formatter.NiceAppendable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface CucumberFormatter {

    abstract void addFeature(TestFeature feature);

    abstract void writeOutput();
}
