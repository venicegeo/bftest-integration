#!/bin/bash -ex
echo start

echo "\\/ \\/ \\/ CHECK FOR ENV VARS HERE \\/ \\/ \\/"
echo "$bf_username"
echo "^  ^  ^  CHECK FOR ENV VARS HERE  ^  ^  ^"

pushd `dirname $0` > /dev/null
base=$(pwd -P)
popd > /dev/null

# Initialize "bigLatch".  If anything fails, this should be set to 1,
# so that the overall job exists with failure.
bigLatch=0
spaces=$PCF_SPACE
# Get the "spaces" environment variable, the spcae that the tests will be run against.
# If it is "test", that should mean that a change was made to the pztest-integration repo,
# and all spaces should be tested.


# Selenium Configurations:

cd ci/Selenium

echo "RUN TESTS ON CHROME"

browsers="chrome firefox"

for space in $spaces; do
	for browser in $browsers; do
		# Reinitialize "latch" for the tests against the current space.
		latch=0
		case $space in
			"pz-int")
			export bf_url=https://beachfront.int.dev.east.paas.geointservices.io
			export GX_url=https://bf-api.int.dev.east.paas.geointservices.io/login/geoaxis
			export browser
			export space
			;;
			"pz-test")
			export bf_url=https://beachfront.test.dev.east.paas.geointservices.io
			export GX_url=https://bf-api.test.dev.east.paas.geointservices.io/login/geoaxis
			export browser
			export space
			;;
			"pz-stage")
			export bf_url=https://beachfront.stage.dev.east.paas.geointservices.io
			export GX_url=https://bf-api.stage.dev.east.paas.geointservices.io/login/geoaxis
			export browser
			export space
			;;
			"pz-prod")
			export bf_url=https://beachfront.prod.dev.east.paas.geointservices.io
			export GX_url=https://bf-api.prod.dev.east.paas.geointservices.io/login/geoaxis
			export browser
			export space
			;;
			*)
			# # Build the beachfront url, to be used in the Selenium tests.
			export bf_url=https://beachfront.$space.geointservices.io
			export GX_url=https://bf-api.$space.geointservices.io/login/geoaxis
			export browser
			export space
			;;
		esac
		# Run the Selenium tests.  
		mvn test || { latch=1; }
		
		# Remember that there was an overall failure, if a single iteration has a failure.
		if [ "$latch" -eq "1" ]; then
			bigLatch=1
		fi
	done
done

# Return an overall error if any collections failed.
exit $bigLatch