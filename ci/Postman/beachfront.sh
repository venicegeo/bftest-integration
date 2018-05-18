#!/bin/bash -ex
source ./ci/Selenium/run_sel_tests.sh
echo start
#mail settings
SUBJ= $BUILDURL

#awm
cd ..
cd ..
pushd `dirname $0` > /dev/null
base=$(pwd -P)
popd > /dev/null
[ -z "$space" ] && space=int

# Initialize "bigLatch".  If anything fails, this should be set to 1,
# so that the overall job exists with failure.
bigLatch=0
latch=0
# Get the "spaces" environment variable, the spcae that the tests will be run against.
# If it is "test", that should mean that a change was made to the pztest-integration repo,
# and all spaces should be tested.


## GEOPACKAGE TESTS

# (Add back in once bf api key is added to all bf jobs)
# cd ./ci/ets-gpkg12-bf/
# sh test.sh || bigLatch=1
# cd ../..


spaces=int


# Selenium Configurations:

# cd ci/Selenium
# Xvfb :99 2>/dev/null &
# export DISPLAY=:99
# npm install geckodriver
# export driver_path=node_modules\chromedriver\lib\chromedriver\chromedriver
for space in $spaces; do
	# Reinitialize "latch" for the tests against the current space.
	latch=0
	case $space in
			"int")
			bfGenApiKey=$bfGenApiKeyPzInt
			echo $bfGenApiKeyPzInt
			;;
			"test")
			bfGenApiKey=$bfGenApiKeyPzTest
			echo $bfGenApiKeyPzTest
			;;
			"stage")
			bfGenApiKey=$bfGenApiKeyPzStage
			echo $bfGenApiKeyPzStage
			;;
			"prod")
			bfGenApiKey=$bfGenApiKeyPzProd
			echo $bfGenApiKeyPzProd
			;;
			*)
			# # Build the beachfront url, to be used in the Selenium tests.
			bfGenApiKey="no space specified"
			;;
		esac
	echo $bfGenApiKey
	# # Build the beachfront url, to be used in the Selenium tests.
	# export bf_url=https://beachfront.$space.geointservices.io/
	# export GX_url=https://bf-api.$space.geointservices.io/login/geoaxis
	# # Run the Selenium tests.  
	# mvn test -e -X || [[ "$PCF_SPACE" == "stage" ]] || { latch=1; }
	# Postman / Newman configuration.
	envfile=$base/environments/L2-$space.postman_environment
	[ -f $envfile ] || { echo "no tests configured for this environment"; exit 0; }

	newmancmd=" node --max-old-space-size=8192 ./node_modules/newman/bin/newman.js"
  
	$newmancmd --version
	$newmancmd -h
	which $newmancmd

	# cmd="./node_modules/newman/bin/newman -o results.json --requestTimeout 960000 -x -e $envfile -g $POSTMAN_FILE -c" -----old newman v2 call-------

	cmd="$newmancmd run"
	cmd2="--timeout-request 960000 --timeout-script 300000 -e $envfile -g $POSTMAN_FILE --global-var \"bf_key_gen=$bfGenApiKey\""
	
	# Run all generic tests.
	for f in $(ls -1 $base/collections/all/*postman_collection); do
		# Run the newman test.  If it fails, latch.
		$cmd $f $cmd2 || { latch=1; }
		
		# (skipping curl) Send a POST request to the bug dahsboard with the JSON output of the newman test.
		# curl -H "Content-Type: application/json" -X POST -d @- http://dashboard.venicegeo.io/cgi-bin/beachfront/$space/load.pl < results.json
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
