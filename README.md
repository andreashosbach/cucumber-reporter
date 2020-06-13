# cucumber-scenarioo-plugin

*Generate Scenarioo documentation from cucumber tests.*

## Using the plugin

### Add the dependency to your pom.xml

Currently you have to download and compile the sources locally first.

	<dependency>
    	<groupId>com.github.andreashosbach</groupId>
		<artifactId>cucumber-scenarioo-plugin</artifactId>
		<version>0.3.0</version>
	</dependency>

### Create a configuration file

Create a file in json format with the following data:
 
    {
        "outputDirectory": "target/scenarioo/selenium",
        "branchName": "My Branch Name",
        "branchDescription":"My branch description",
        "branchDetails": {
            "My key": "My value"
        },
        "revision": "1.0"
        "buildDetails": {
            "My Key": "My value"
        },
        "stepDetailKeys" : ["Glue Code", "Pattern", "URI", "Line", "Keyword"],
        "useCaseDetailKeys" : ["Long Description"],
        "scenarioDetailKeys" : ["Id", "URI", "Line", "Keyword", "Long Description"],
        "buildName" : {
            "prefix" : "Build-",
            "datetimeFormat" : "yyyy-MM-dd-hh-mm-ss",
            "postfix": ""
        }
    } 
    
The field *outputDirectory* is mandatory the other field can be omitted. For the necessary fields like branchName, 
revision, and build name default values are filled in.
If the *...DetailKeys* fields are not given, all keys are added.

Within the branch details any number of key value pairs can be added freely.

On Use Case, Scenario and Step level the lists are configuring which details are included.

Possible Values
* Use Case
    * Long Description
* Scenario
    * Id
    * URI
    * Line
    * Keyword
    * Long Description
* Step
    * Glue Code
    * Pattern
    * URI
    * Line
    * Keyword

In *buildName* it is possible to give prefix, date format and postfix for build names.

### Add the plugin to the Cucumber test runner

    @RunWith(Cucumber.class)
    @CucumberOptions(  ...
        plugin = "com.github.andreashosbach.cucumber_scenarioo_plugin.CucumberScenariooPlugin:resources/cucumber_scenarioo_config.json"
    ...)

The path to the configuration file must follow the  *:* in the plugin configuration.

### Define an after step hook where you take a screenshot and save it with Screenshot.save(...)
  
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

### Configuration

By default the branch name is *branch* and build names are *Build-yyyy-MM-dd-hh-mm-ss* generated by the default name generators.
This behaviour can be changed by implementing custom name generators.


## Feature Files
Guidelines on how to write the *feature files* 

### Description
The descriptions of *features* and *scenarios* are formatted as following:
* The first line of a description is the *short description* and will be displayed in lists
* The whole description (including the first line) is formatted as markdown and visible in the detail under *Long Description*

## Scenarioo

The results can be displayed with [Scenarioo](http://scenarioo.org/)

To run Scenarioo in Docker the [Scenarioo documentation](http://scenarioo.org/docs/master/tutorial/Scenarioo-Viewer-Docker-Image.html)
