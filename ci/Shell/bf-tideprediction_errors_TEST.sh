# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests


http_get "${http}bf-tideprediction.$domain/"
assert "Should receive a 200" 200 -eq "$code"

payload="{
	\"lon\": 151,
	\"dtg\": \"2016-05-31-0-1\"
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 400 for missing 'lat'" 400 -eq "$code" "skip"

payload="{
	\"lat\": 100,
	\"lon\": 151,
	\"dtg\": \"2016-05-31-0-1\"
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 400 for a lat of 100" 400 -eq "$code" "skip"

payload="{
	\"lat\": 35.85,
	\"lon\": -200,
	\"dtg\": \"2016-05-31-0-1\"
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 400 for a lon of -200" 400 -eq "$code" "skip"

payload="{
	\"lat\": 35.85,
	\"lon\": 151
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 400 for missing 'dtg'" 400 -eq "$code" "skip"

payload="{
	\"lat\": 35.85,
	\"lon\": 151,
	\"dtg\": \"foo\"
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 400 for a malformed 'dtg'" 400 -eq "$code" "skip"

payload="{
	\"locations\": [
		{
			\"lat\": 100,
			\"lon\": 25,
			\"dtg\": \"2016-05-25-5-25\"
		}
	]
	}"
http_post "${http}bf-tideprediction.$domain/tides" "" "$payload"
assert "Should receive a 400 for a lat of 100" 400 -eq "$code" "skip"

payload="{
	\"locations\": [
		{
			\"lon\": 25,
			\"dtg\": \"2016-05-25-5-25\"
		}
	]
	}"
http_post "${http}bf-tideprediction.$domain/tides" "" "$payload"
assert "Should receive a 400 for missing 'lat'" 400 -eq "$code" "skip"


display_result
exit $?