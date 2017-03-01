#!/bin/bash -ex
echo start

pushd `dirname $0` > /dev/null
base=$(pwd -P)
popd > /dev/null

# Initialize "bigLatch".  If anything fails, this should be set to 1,
# so that the overall job exists with failure.
bigLatch=0

# Get the "spaces" environment variable, the spcae that the tests will be run against.
# If it is "test", that should mean that a change was made to the pztest-integration repo,
# and all spaces should be tested.
if [ "$PCF_SPACE" == "test" ]; then
	echo "test case"
	spaces="int stage"
else
	spaces=$PCF_SPACE
fi

# Selenium Configurations:

cd ci/Selenium

echo "RUN TESTS ON CHROME"

browsers="chrome firefox"

for space in $spaces; do
	for browser in $browsers; do
		# Reinitialize "latch" for the tests against the current space.
		latch=0
		
		# # Build the beachfront url, to be used in the Selenium tests.
		export bf_url=https://beachfront.$space.geointservices.io/
		export GX_url=https://bf-api.$space.geointservices.io/login/geoaxis
		export browser
		export space
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