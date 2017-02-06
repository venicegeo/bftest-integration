#!/bin/bash -ex
echo start
#mail settings
SUBJ= $BUILDURL

pushd `dirname $0` > /dev/null
base=$(pwd -P)
popd > /dev/null

[ -z "$space" ] && space=int

# Initialize "bigLatch".  If anything fails, this should be set to 1,
# so that the overall job exists with failure.
bigLatch=0

# Get the "spaces" environment variable, the spcae that the tests will be run against.
# If it is "test", that should mean that a change was made to the pztest-integration repo,
# and all spaces should be tested.
if [ "$PCF_SPACE" == "test" ]; then
	echo "test case"
#	spaces="stage"
	spaces="int stage prod"
	cd ci/Selenium
	Xvfb :99 2>/dev/null &
	export DISPLAY=:99
	export browser_path=$(which google-chrome)
	npm install chromedriver
	export driver_path=node_modules/chromedriver/lib/chromedriver/chromedriver
	export bf_url=https://beachfront.stage.geointservices.io/
	export GX_url=https://bf-api.stage.geointservices.io/login/geoaxis
	mvn test -e -X
else
	spaces=$PCF_SPACE
fi

# Selenium Configurations:

# cd ci/Selenium
# Xvfb :99 2>/dev/null &
# export DISPLAY=:99
# npm install geckodriver
# export driver_path=node_modules\chromedriver\lib\chromedriver\chromedriver

for space in $spaces; do
	# Reinitialize "latch" for the tests against the current space.
	latch=0
	
	# # Build the beachfront url, to be used in the Selenium tests.
	export bf_url=https://beachfront.$space.geointservices.io/
	export GX_url=https://bf-api.$space.geointservices.io/login/geoaxis
	# Run the Selenium tests.  
	# mvn test -e -X || { latch=1; }
	
	# Remember that there was an overall failure, if a single iteration has a failure.
	if [ "$latch" -eq "1" ]; then
		bigLatch=1
	fi
done


# Return an overall error if any collections failed.
exit $bigLatch