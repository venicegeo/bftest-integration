#!/bin/bash
			export bfGenApiKeyPzInt="Int"
			export bfGenApiKeyPzTest="Test"
			export bfGenApiKeyPzStage="Stage"
			export bfGenApiKeyPzProd="Prod"

chmod 700 ./ci/Selenium/run_sel_tests.sh
chmod 700 ./ci/Postman/beachfront.sh
./ci/Selenium/run_sel_tests.sh
./ci/Postman/beachfront.sh

