# cucumber-scenarioo-plugin
*Generate Scenarioo documentation from cucumber tests.*

## Using the plugin
####Add the dependency to your pom.xml
Currently you have to download and compile the sources locally first.

	<dependency>
    	<groupId>com.github.andreashosbach</groupId>
		<artifactId>cucumber-scenarioo-plugin</artifactId>
		<version>0.0.1</version>
	</dependency>

####Add the plugin to the Cucumber test runner: 

    @RunWith(Cucumber.class)
    @CucumberOptions(  ...
        plugin = "com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberReportPlugin:target/scenarioo"
    ...)

####Define an after step hook where you take a screenshot and save it with *Screenshot.save(...)*.
  
Example with Selenium:
  
    @AfterStep
    public void afterStep() {
        TakesScreenshot scrShot = ((TakesScreenshot) webDriver);
        Screenshot.save(webDriver.getTitle(), scrShot.getScreenshotAs(OutputType.BYTES));
    }

Currently you can  have only one after step hook, otherwise the steps are written twice.
(While you can also take the screenshot in the step itself, you have to make sure that for every step there is a screenshot 
saved in the same order as the steps are executed. But there has to be an after step event to trigger the writing of the 
step in the plugin.)

## Scenarioo
The results can be displayed with [Scenarioo](http://scenarioo.org/)

To run Scenarioo in Docker the [Scenarioo documentation](http://scenarioo.org/docs/master/tutorial/Scenarioo-Viewer-Docker-Image.html)
