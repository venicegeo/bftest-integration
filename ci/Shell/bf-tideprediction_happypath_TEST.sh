# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests


http_get "${http}bf-tideprediction.$domain/"
assert "Should receive a 200" 200 -eq "$code"

payload="{
	\"lat\": 35.85,
	\"lon\": 151,
	\"dtg\": \"2016-05-31-0-1\"
	}"
http_post "${http}bf-tideprediction.$domain/" "" "$payload"
assert "Should receive a 200" 200 -eq "$code"
assert "currentTide should be returned" "null" != "$(echo $body | jq -r '.currentTide')"
assert "maximumTide24Hours should be returned" "null" != "$(echo $body | jq -r '.maximumTide24Hours')"
assert "minimumTide24Hours should be returned" "null" != "$(echo $body | jq -r '.minimumTide24Hours')"

payload="{
	\"locations\": [
		{
			\"lat\": 25,
			\"lon\": 25,
			\"dtg\": \"2016-05-25-5-25\"
		},
		{
			\"lat\": 33.85,
			\"lon\": 151,
			\"dtg\": \"2016-05-31-0-1\"
		},
		{
			\"lat\": 67.89,
			\"lon\": 123.45,
			\"dtg\": \"2007-12-3-4-56\"
		}
	]
	}"
http_post "${http}bf-tideprediction.$domain/tides" "" "$payload"
assert "Should receive a 200" 200 -eq "$code"
count=0
for TIDE in $(echo $body | jq -r '[.locations[] | .results.currentTide]')
do
	TIDE=$(strip_jq $TIDE)
	if [ ! -z "$TIDE" ] && [ "null" != "$TIDE" ]; then
		(( count += 1 ))
	fi
done
assert "Three currentTide should be returned" 3 -eq $count
count=0
for MAXTIDE in $(echo $body | jq -r '[.locations[] | .results.maximumTide24Hours]')
do
	MAXTIDE=$(strip_jq $MAXTIDE)
	if [ ! -z "$MAXTIDE" ] && [ "null" != "$MAXTIDE" ]; then
		(( count += 1 ))
	fi
done
assert "Three maximumTide24Hours should be returned" 3 -eq $count
count=0
for MINTIDE in $(echo $body | jq -r '[.locations[] | .results.minimumTide24Hours]')
do
	MINTIDE=$(strip_jq $MINTIDE)
	if [ ! -z "$MINTIDE" ] && [ "null" != "$MINTIDE" ]; then
		(( count += 1 ))
	fi
done
assert "Three minimumTide24Hours should be returned" 3 -eq $count

display_result
exit $?