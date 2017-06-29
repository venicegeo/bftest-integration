#!/bin/bash

echo "\\/ \\/ \\/ CHECK FOR ENV VARS HERE \\/ \\/ \\/"
echo "$bf_username"
echo "$sauce_user"
echo "^  ^  ^  CHECK FOR ENV VARS HERE  ^  ^  ^"

chmod 700 ./ci/Selenium/run_sel_tests.sh
./ci/Selenium/run_sel_tests.sh
