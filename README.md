# bftest-integration
Repository holding Beachfront tests

## Layout
In the **ci** folder, there are four subfolders, each with a different kind of test:
- [**Postman:**](https://github.com/venicegeo/bftest-integration#api-integration-tests-postmannewman)   The integration tests against Beachfront APIs, json files that run with Postman (and newman, the cli)
- [**Selenium:**](https://github.com/venicegeo/bftest-integration#ui-integration-tests-selenium)   The UI tests against the Beachfront front-end, java files that use Selenium and JUnit
- [**Jmeter:**](https://github.com/venicegeo/bftest-integration#api-load-tests)     The load tests against Beachfron APIs, jmx files that run with Apache Jmeter
- [**Shell:**](https://github.com/venicegeo/bftest-integration#api-integration-tests-shell)      More integration tests against Beachfront APIs, sh scripts with `jq` as the only external requirement

The **ci** folder also contains the shell scripts that are called to start tests.  These scripts typically point to another script located in one of the subfolders.

## API Integration Tests (Postman/Newman)
The API Integration tests are perfomed by Jenkins calling either `beachfront_integration_tests.sh` or `beachfront_integration_tests_stage.sh`.  These scripts will then call the script within the **Postman** folder, `beachfront.sh` which cycles through the integration tests written agianst the Beachfront API.  These scripts will fail if any of the tests they run fail; this marks the Jenkins job as a failure and prevents the pipeline from continuing.

### Newman CLI
These tests call the newman cli, with:
- *-c:*     a collection file in the **collections/all** folder; this contains a suite of tests, typically targeting a particular endpoint
- *-e:*     an enviornment file in the **environments** folder; this contains variables needed to run the tests specific to the environment being tested (`int` or `stage`)
- *-g:*     a global file (similar to the environment file); this stores more variables needed for the test.  Contains passwords and the like, and is not available in this repository
- *-o:*     a filename telling newman where to output the results of the test
- *-x:*     this tag causes newman to continue testing after a failure.  If a failure occurs, the exit code is set to 1.
- *-requestTimeout:*     the time (in ms) to wait for a response before declaring the test a failure.  Currently set to 16 minutes.

Example call:
`newman -o results.json --requestTimeout 960000 -x -e /jslave/workspace/venice/beachfront/bftest-integration/beachfront/ci/Postman/environments/int.postman_environment -g **** -c /jslave/workspace/venice/beachfront/bftest-integration/beachfront/ci/Postman/collections/all/BF_API.postman_collection`

### Collection File Organization
Currently, all collection files are in the **collections/all**.  This means that all tests will be run, regardless of the space being tested.  If you need a test run only when a certain space is tested, you can put the collection file in **collections/int** or **collections/stage**, depending on the desired space.

### Custom Functions in Test Scripts
To facilitate the writing of test scripts, some common actions where created into functions.  The functions are stored in the environment files, and then used in test scripts by calling the `eval` function, like so:
`var functionName = eval(postman.getEnvironmentVariable('functionName'));`

These are the currently created functions:
#### testLevels(tests, inputObj, levels, typeCheck, testString):
Test that the provided json object contains the desired key.  If it does, return true and pass a test.  If it does not, return false and fail the test.  This allows the test script to check for the presence of key before trying to access it, which will cause a Postman error.
- *tests:*      (Object)  The javascript object that houses all of the test results.  This is needed so that `testLevels` can add test results as needed.  When writing Postman test scripts, the variable `tests` is used - use that.
- *inputObj:*   (Object)  The json object to check.
- *levels:*     (String)  The key(s) to look for in *inputObj*.  To check for a nested structure, provide the string `"keyName.nestedKeyName.finalKeyName"`
- *typeCheck:*  (String, optional)  Check that the value found at the end of the chain of keys in *levels* is the correct type.
  -  `"string":`   Check that the item is a string.
  -  `"array":`    Check that the item is a non-empty array.
  -  `"number":`   Check that the item can be parsed as a finite float.
  -  `"skip":`     Use this value if you do not want to perform a type check, but need to use later arguments in this function.
- *testString:* (String optional)   Use this argument to add level information to the test name.  Useful if you are checking an *inputObj* that was part of a larger object.
  -  If the test would normally output "foo.bar is present in output", setting *testString* to "baz." will change that output to "baz.foo.bar is present in output"
  
Example Usage:
```javascript
var testLevels = eval(postman.getEnvironmentVariable('testLevels'));
var data = <some JSON object>;
if (testLevels(tests, data, 'some.nested.structure', 'string')) {
    tests["Some check on the data"] = data.some.nested.structure === 'baz';
}
```

#### testElements(testLevels, tests, inputArray, levels, typeCheck):
This function takes an input array and performs a **testLevels** check on each element of that array.


- *testLevels:*   (function)          You must create the **testLevels** function in order to use **testElements**.
- *tests:*        (Object)            Same as **testLevels**.
- *inputArray:*   (Array)             An array to make the **testLevels** check against each element.
- *levels:*       (String)            Same as **testLevels**.
- *typeCheck:*    (String, optional)  Same as **testLevels**.

#### parseJSON(tests, jsonString):
This function returns a JSON object parsed from *jsonString*, passing a test if this is possible.  Otherwise, fail a test and return an empty object.  This function is used because a failing 
- *tests:*      (Object)  Same as **testLevels**.
- *jsonString:* (String)  The string to parse into a JSON object.  Typically, this is the response body returned from an HTTP request.

#### wait(millis):
Don't do anything for the specified amount of time.  This is used to add a wait between postman requests.
- *millis:*     (Number)  The number of milliseconds to wait.

## UI Integration Tests (Selenium)
The UI Integration tests are perfomed by Jenkins calling either `beachfront_iua_tests_int.sh` or `beachfront_ua_tests_stage.sh`.  These scripts will then call the script within the **Selenium** folder, `run_sel_tests.sh` which cycles through the integration tests written agianst the Beachfront UI.  These scripts will fail if any of the tests they run fail; this marks the Jenkins job as a failure and prevents the pipeline from continuing.

This section is designed as a supplement to the comments already in the code.

### Selenium File Organization
The Selenium project starts in the **Selenium** folder, which contains `pom.xml`.  In this directory the testing command, `mvn test` is called, which starts the Junit test.
The test files are located further down, in **src/test/java/bfui/test**:
In this directory, are the suites of Selenium tests, written in java.
Supporting files are located in the **util** folder.
The Page Objects are located in the **page** folder.

### The Page Object Model
These selenium tests use the page object model to separate testing code from interface code.  Information on how the various elements are found are located in the page objects, so that UI changes require as little change to the test code as possible.

### JUnit Rules
Custom JUnit rules were created to facilitate reporting test results.

| Name                | Description                                                                                      |
| ----                | -----------                                                                                      |
| Reporter            | Prints a result summary once a test suite completes, and sends the results to the bug dashboard. |
| SauceResultReporter | Sends test results to sauce labs, so that their dashboard correctly shows passed/failed tests.   |

### Sauce Labs
The tests are run on browsers hosted by Sauce Labs.  Sauce Labs provides many OS / Browser version combinations with which to test, and takes screenshots of the tests, which allows for easy debugging if failures occur.

### Environment Variables
The following environment variables must be set in order for the tests to run properly.  Some of these are handled by `run_sel_tests.sh`, others must be manually set (or set by Jenkins) as they contain information like passwords.

|	Name        |	Description								              |
|	----		    |	-----------								              |
|	bf_username	|	DU Username for GX						          |
|	bf_password	|	DU Password for GX						          |
|	bf_url		  |	URL to main BF page 					          |
|	browser		  |	Browser to run tests					          |
|	GX_url		  |	URL to through which to login			      |
|	PL_API_KEY	|	Key to access images from Planet		    |
|	sauce_user	|	Username for hosting tests through Sauce|
|	sauce_key	  |	API Key for hosting tests through Sauce	|
|	space		    |	Development space (INT, STAGE, or PROD)	|

## API Load Tests
The API Load tests are started manually on an ec2 machine using Apache Jmeter.  These tests make repeated requests against Beachfront's API, logging the amount of time before a response is received.  Other information, like an app's memory and CPU usage is logged as well.

### Installation
Jmeter must be installed on the machine running the tests.
The "Ultimate Thread Group" and the "Stepping Thread Group" plugins also must be installed from https://jmeter-plugins.org/

### Properties
The following properties must be set in order for the tests to run properly.  These can be set when calling the test from the command line.

|	Name              |	Description							                                                    |
|	----		          |	-----------							                                                    |
|	PL_API    	      |	API Key to access Planet Labs.  Needed when creating a job through BF-API.  |
|	auth              |	Auth header for BF-API.                                                     |
|	sample_variables  |	List of additional variables to record in the results file.                 |

### Jmeter CLI
The jmx files are called with a CLI call to jmeter with the following parameters:
- *-n:*                 this tag causes jmeter to run in non-GUI mode (so it uses less resources)
- *-t:*                 the jmx file to run
- *-J[variableName]:*   properties are defined by appending "-J" to the property name, and following it with the desired value.
  - Ex: `-Jauth "abc123"`

Example call:

`jmeter -n -t ./test_files/bf.jmx -Jauth="Basic abc123" -Jsample_variables=someGUID:cpu,someGUID:mem -JPL_API=123abc`

### Logging CPU and Memory Usage
In addition to sending requests to the API under test, the JMX files are also set up to send requests to PCF to get the memory and CPU usage of specified apps.

To log this information:

1. Log in to PCF on the machine running the test.  You must log into the specific org/space that houses the app(s) you want to monitor.
1. Define -Jsample_variables in your jmeter CL call.
   1. Get the GUID of the app(s) you want to monitor from PCF.
   1. Add ":cpu" or ":mem" to the GUID, depending on the stat that you want to monitor.
      - If you need both stats on the same app, you must list the app twice: once with  ":cpu" and with ":mem".
1. Join this list with commas.

Something like this should be added in your command line call:

`-Jsample_variables first-app-guid:cpu,first-app-guid:mem,second-app-guid:cpu,second-app-guid:mem`

### Results:
Each time a test is run, the results will be stored in a new folder.  This is accomplished by creating a timestamp variable at the start of the test, then incorporating that variable in the path for the "Write results to file" filename.

The results are csv files with a line for each request made during the load test.

### Processing Results
As the load tests generate a large amount of data, there are python scripts that can help compile the data.  These scripts are a part of the pztest-integration repository, within the [jmeter](https://github.com/venicegeo/pztest-integration/tree/master/jmeter) folder.

#### compileResults
This script combines the results of several load tests into a single file.

This script adds two columns to the results:
- *Timestamp:*       The name of the folder from which the test orginiated.  Should be the timestamp from when that test started.
- *Elapsed Time:*    For each request, the amount of seconds passed since the start of the test.

The script is called via the command line, as follows:
`python compileResults.py folder target dataFilename`
with the arguments defined as:
- *folder:*         Path to the folder that contains the load tests to compile.  This folder should have subfolder(s) that contain load testing results
- *target:*         Filename of the output, compiled csv file.
  - Should be renamed to something else, to be consistent with other scripts.
- *dataFilemname:*  Filename of the result csv files, should be "raw.csv".  This file needs to be in each subfolder of *folder*.

#### compressResults
This script takes the compiled results (created from `compileResults.py`) and creates a smaller file with averaged data.  The purpose is to create a file that is more manageable for analysis in Excel.

This script reduces the number of lines by averaging together data with the same *Timestamp* and *Elapsed Time* values.

The script is called via the command line, as follows:
`python compressResults.py folder target newName`

with the arguments defined as:
- *folder:*         Path to the folder that contains the compiled results to compress.
- *target:*         Filename of the compiled csv file.
- *newName:*        Filename of the output, compressed csv file.

__NOTE:__ `compressResults.py` must be run using Python 3.

#### cycleResults
This script takes the compiled results (created from `compileResults.py`) and changes the results so that each line displays the total results for a series of requests, rather than individual requests.

Run this script to determine how long it takes users to complete a task involving multiple requests.

The script is called via the command line, as follows:
`python compressResults.py folder target newName`
with the arguments defined as:
- *folder:*         Path to the folder that contains the compiled results to cycle.
- *srcName:*        Filename of the compiled csv file.
- *snkName:*        Filename of the output, cycled csv file.
- *startRequest:*   The same of the step that is the start of the cycle.

## API Integration Tests (Shell)
As an alternative to the Postman / Newman integration tests, these scripts can be run wherever shell is available, if the Newman cli cannot be installed.  These scripts serve the same purpose as the other integration tests, except that they are not currently incorporated with Jenkins.

### Requirements
The shell scripts use [jq](https://stedolan.github.io/jq/) to parse the JSON responses from the APIs under test.

Before running, create the file `vars.sh` in the same directory as our other scripts:
```shell
http="https://"           # Change to "http://" if desired
domain=""                 # Change to match the domain being tested
auth="userName:password"  # Information to create authorization header.  Should be "BF-API-KEY:" for testing Beachfront
planet_key=""             # Planet Labs API Key

info "Variables imported!"
```

`utils.sh` is included in this repo, and contains the code necessary to make assertions, display results, and to send http requests.  The comments in that file explain how to call each function.

### Results
No files are created from this test.  The results are printed in the console marked with passes (✓) or failures (✗).
