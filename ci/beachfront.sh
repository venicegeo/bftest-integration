#!/bin/bash
			export bfGenApiKeyPzInt="test"
			export bfGenApiKeyPzTest="test"
			export bfGenApiKeyPzStage="test"
			export bfGenApiKeyPzProd="test"

chmod 700 ./ci/Selenium/run_sel_tests.sh
chmod 700 ./ci/Postman/beachfront.sh
./ci/Selenium/run_sel_tests.sh
./ci/Postman/beachfront.sh

