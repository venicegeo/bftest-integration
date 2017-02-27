# Selenium Pipeline Testing

## Purpose
The Selenium Pipeline Tests serve as a thorough check after each change to the Beachfront User Interface.  These tests ensure that the user experience does not degrade after user interface code change.
The tests currently run twice in the Jenkins pipeline:

1. Between INT and STAGE
  * These tests are run against the INT environment.
  * If these tests pass, then the UI is pushed to STAGE.
2. After push to STAGE
  * These tests are run agianst the STAGE environmnet.
  * These tests are run for FYI purposes; they have no bearing on the pipeline.

## Setup

### Environment Variables
|	Name		|	Description								|
|	----		|	-----------								|
|	bf_username	|	DU Username for GX						|
|	bf_password	|	DU Password for GX						|
|	bf_url		|	URL to main BF page 					|
|	browser		|	Browser to run tests					|
|	GX_url		|	URL to through which to login			|
|	PL_API_KEY	|	Key to access images from Planet		|
|	sauce_user	|	Username for hosting tests through Sauce|
|	sauce_key	|	API Key for hosting tests through Sauce	|
|	space		|	Development space (INT, STAGE, or PROD)	|

### Running
Run `mvn clean install` in the same directory as the pom file.  This installs all of the necessary packages (like junit and selenium) needed to run the tests.
Set the above environment variables.
Run `mvn test` after the above setup to run the tests.

## Impementation
### Test Cases
The table below overviews the different tests files:

|	Name			|	Capability Tested														|
|	----			|	-----------------														|
|	TestGeoAxis		|	Logging in through the GeoAxis portal									|
|	TestBrowseMap	|	Navigating to different map locations and other UI elements				|
|	TestImageSearch	|	Searching for images based on a bounding box, dates, and other options	|
|	TestJobsList	|	Exercising the options available for a completed job					|

### Page Object Model
The Selenium Pipeline Tests utilize a Page Object Model to facilitate finding web elements during the tests.
The page objects are located in `src.test.java.bfui.test.page`.
The following is a list of all Webelements used:
#### GeoAxis

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `disadvantagedLink`       | xpath     | `//*[@id='authmechlinks']/div/ul/li[4]/a` |
| `userField`               | id        | `username`                           |
| `pwField`                 | id        | `password`                           |
| `submitButton`            | css       | `input[type=submit]`                 |

#### Beachfront

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `geoAxisLink`             | className | `Login-button`                       |
| `homeButton`              | className | `Navigation-linkHome`                |
| `helpButton`              | className | `Navigation-linkHelp`                |
| `jobsButton`              | className | `Navigation-linkJobs`                |
| `createJobButton`         | className | `Navigation-linkCreateJob`           |
| `productLinesButton`      | className | `Navigation-linkProductLines`        |
| `createProductLineButton` | className | `Navigation-linkCreateProductLine`   |
| `zoomInButton`            | className | `ol-zoom-in`                         |
| `searchButton`            | className | `PrimaryMap-search`                  |
| `mouseoverCoordinates`    | className | `ol-mouse-position`                  |
| `canvas`                  | className | `ol-unselectable`                    |
| `searchWindow`            | className | `coordinate-dialog`                  |
| `createJobWindow`         | className | `CreateJob-root`                     |
| `jobsWindow`              | className | `JobStatusList-root`                 |
| `featureDetails`          | className | `FeatureDetails-root`                |
| `sessionExpiredOverlay`   | className | `SessionExpired-root`                |
| `banners`                 | className | `ClassificationBanner-root`          |
| `detailTable`             | xpath     | `//div[contains(@class, 'SceneFeatureDetails-root')]/child::dl` |

> Get map coordinates with the javascript: "return primaryMap.props.view.center"
>
> Values found in “detailTable” with “dt” and “dd” tags.

#### Search Window

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `submitButton`            | cs        | `button[type=submit]`                |
| `entry`                   | cs        | `input[placeholder='Enter Coordinates']` |
| `exampleCoordinates`      | xpat      | `/html/body/div/div/main/form/div[2]/dl/dd/code` |
| `errorMessage`            | className | `error-message`                      |

#### Create Job

All elements are children of “createJobWindow” in the Beachfront table.

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `instructionText`         | className | `CreateJob-placeholder`              |
| `invalidDateText`         | className | `CatalogSearchCriteria-invalidDates` |
| `cloudText`               | className | `CatalogSearchCriteria-value`        |
| `loadingMask`             | className | `ImagerySearch-loadingMask`          |
| `errorMessage`            | className | `ImagerySearch-errorMessage`         |
| `clearButton`             | className | `CatalogSearchCriteria-clearBbox`    |
| `apiKeyEntry`             | xpath     | `//label[contains(@class, 'CatalogSearchCriteria-apiKey')]/child::input` |
| `fromDateEntry`           | xpath     | `//label[contains(@class, 'CatalogSearchCriteria-captureDateFrom')]/child::input` |
| `toDateEntry`             | xpath     | `//label[contains(@class, 'CatalogSearchCriteria-captureDateTo')]/child::input` |
| `cloudSlider`             | xpath     | `//label[contains(@class, 'CatalogSearchCriteria-cloudCover')]/child::input` |
| `sourceDropdown`          | xpath     | `//label[contains(@class, 'CatalogSearchCriteria-source')]/child::select` |
| `errorMessageDescription` | xpath     | `//div[contains(@class, 'ImagerySearch-errorMessage')]/child::p` |
| `algorithms`              | xpath     | `//div[contains(@class, 'AlgorithmList-root')]/child::ul/li` |
| `submitButton`            | css       | `button[type=submit]`                |

The algorithm used in the tests is selected from the list of elements in “algorithm” by checking each element’s child (that has the className, “Algorithm-name”) until one is found with text that equals "NDWI_PY".

The current value of “cloudSlider” is found by reading its “value” attribute.

#### Jobs

All elements are children of “jobsWindow” in the Beachfront table.

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `list`                    | tagName   | `ul`                                 |

A job is selected from the child elements of “list” by checking each element’s child (that has the className, “JobStatus-root”) until one is found with text that equals "ForJobTesting".

#### Single Job Info

All elements are children of a job selected from “list” in the Jobs table.

| Element Name              | Method    | Value                                |
|---------------------------|-----------|--------------------------------------|
| `name`                    | xpath     | `//h3[contains(@class, 'JobStatus-title')]/child::span` |
| `forgetButton`            | xpath     | `//div[contains(@class, 'JobStatus-removeToggle')]/child::button` |
| `confirmButton`           | xpath     | `//div[contains(@class, 'JobStatus-removeWarning')]/child::button` |
| `cancelButton`            | xpath     | `(//div[contains(@class, 'JobStatus-removeWarning')]/child::button)[2]` |
| `forgetWarning`           | className | `JobStatus-removeWarning`            |
| `viewLink`                | css       | `a[title=\"View on Map\"]`           |
| `downloadLink`            | css       | `a[title=\"Download\"]`              |
