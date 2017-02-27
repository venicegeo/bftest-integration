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
