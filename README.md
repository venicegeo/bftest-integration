# bftest-integration
Repository holding Beachfront tests

## Layout
In the **ci** folder, there are four subfolders, each with a different kind of test:
- **Jmeter:**     The load tests against Beachfron APIs, jmx files that run with Apache Jmeter
- **Postman:**    The integration tests against Beachfront APIs, json files that run with Postman (and newman, the cli)
- **Selenium:**   The UI tests against the Beachfront front-end, java files that use Selenium and JUnit
- **Shell:**      More integration tests against Beachfront APIs, sh scripts with `jq` as the only external requirement

The **ci** folder also contains the shell scripts that are called to start tests.  These scripts typically point to another script located in one of the subfolders.

## Process
### API Integration Tests
Integration tests are perfomed by Jenkins calling either `beachfront_integration_tests.sh` or `beachfront_integration_tests_stage.sh`.  These scripts will then call the script within the **Postman** folder, `beachfront.sh` which cycles through the integration tests written agianst the Beachfront API
These tests call the newman cli, with:
- *-c:*     a collection file in the **collections/all** folder; this contains a suite of tests, typically targeting a particular endpoint
- *-e:*     an enviornment file in the **environments** folder; this contains variables needed to run the tests specific to the environment being tested (`int` or `stage`)
- *-g:*     a global file (similar to the environment file); this stores more variables needed for the test.  Contains passwords and the like, and is not available in this repository
- *-o:*     a filename telling newman where to output the results of the test
- *-x:*     this tag causes newman to continue testing after a failure.  If a failure occurs, the exit code is set to 1.
- *-requestTimeout:*     the time (in ms) to wait for a response before declaring the test a failure.  Currently set to 16 minutes.

Example call:
`newman -o results.json --requestTimeout 960000 -x -e /jslave/workspace/venice/beachfront/bftest-integration/beachfront/ci/Postman/environments/int.postman_environment -g **** -c /jslave/workspace/venice/beachfront/bftest-integration/beachfront/ci/Postman/collections/all/BF_API.postman_collection`
