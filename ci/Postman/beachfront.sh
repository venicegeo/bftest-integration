#!/bin/bash -ex
echo start
#mail settings
SUBJ= $BUILDURL

#awm

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
	
	chmod 700 ./ci/Selenium/run_sel_tests.sh
	./ci/Selenium/run_sel_tests.sh
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
	# export bf_url=https://beachfront.$space.geointservices.io/
	# export GX_url=https://bf-api.$space.geointservices.io/login/geoaxis
	# # Run the Selenium tests.  
	# mvn test -e -X || [[ "$PCF_SPACE" == "stage" ]] || { latch=1; }
	
	# Postman / Newman configuration.
	envfile=$base/environments/$space.postman_environment
	[ -f $envfile ] || { echo "no tests configured for this environment"; exit 0; }
	cmd="newman -o results.json --requestTimeout 960000 -x -e $envfile -g $POSTMAN_FILE -c"
	
	# Run all generic tests.
	for f in $(ls -1 $base/collections/all/*postman_collection); do
		# Run the newman test.  If it fails, latch.
		$cmd $f || [[ "$PCF_SPACE" == "stage" ]] || { latch=1; }
		
		# Send a POST request to the bug dahsboard with the JSON output of the newman test.
		curl -H "Content-Type: application/json" -X POST -d @- http://dashboard.venicegeo.io/cgi-bin/beachfront/$space/load.pl < results.json
		echo $latch
	done
	
	# Run all specific environment tests.
	for f in $(ls -1 $base/collections/$space/*postman_collection); do
		# Run the newman test.  If it fails, latch.
		$cmd $f || [[ "$PCF_SPACE" == "stage" ]] || { latch=1; }
		
		# Send a POST request to the bug dahsboard with the JSON output of the newman test.
		curl -H "Content-Type: application/json" -X POST -d @- http://dashboard.venicegeo.io/cgi-bin/beachfront/$space/load.pl < results.json
		echo $latch
	done
	
	# Remember that there was an overall failure, if a single iteration has a failure.
	if [ "$latch" -eq "1" ]; then
		bigLatch=1
	fi
done


# Return an overall error if any collections failed.
exit $bigLatch
#awm