package selenium_stepdefs;

import org.openqa.selenium.WebDriver;

public class StepDefinitionBase {
    private static WebDriver webDriver;

    public static WebDriver getWebDriver() {
        return webDriver;
    }

    public static void setWebDriver(WebDriver pwebDriver){
        webDriver = pwebDriver;
    }
}
