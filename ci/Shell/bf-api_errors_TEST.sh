# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests

known_good_sceneId="rapideye:20170307_004628_5866108_RapidEye-4"

http_get "${http}bf-api.$domain/"
assert "Should receive a 200" 200 -eq "$code"
assert "Uptime field is present" "null" != "$(echo $body | jq '.uptime')"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth"
service_id="$(echo $body | jq -r '.algorithms[0].service_id')"
assert "Should receive a 200" 200 -eq "$code"
assert "An algorithm's service_id found - to use in later tests" "null" != "$service_id"

http_get "${http}bf-api.$domain/v0/algorithm/foo" "$auth"
assert "Should receive a 404" 404 -eq "$code"

http_get "${http}bf-api.$domain/v0/job/foo" "$auth"
assert "Should receive a 404" 404 -eq "$code"

http_delete "${http}bf-api.$domain/v0/job/foo" "$auth"
assert "Should receive a 404" 404 -eq "$code"

payload="{
	\"scene_id\": \"$known_good_sceneId\",
	\"name\": \"Payload WITH Spaces\",
	\"planet_api_key\": \"$planet_key\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for missing 'algorithm_id'" 400 -eq "$code"

payload="{
	\"algorithm_id\": \"$service_id\",
	\"name\": \"Payload WITH Spaces\",
	\"planet_api_key\": \"$planet_key\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for missing 'scene_id'" 400 -eq "$code"

payload="{
	\"scene_id\": \"$known_good_sceneId\",
	\"algorithm_id\": \"$service_id\",
	\"planet_api_key\": \"$planet_key\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for missing 'name'" 400 -eq "$code"

payload="{
	\"scene_id\": \"$known_good_sceneId\",
	\"algorithm_id\": \"$service_id\",
	\"name\": \"Payload WITH Spaces\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for missing 'planet_api_key'" 400 -eq "$code"

payload="{
	\"algorithm_id\": \"foo\",
	\"scene_id\": \"$known_good_sceneId\",
	\"name\": \"Payload WITH Spaces\",
	\"planet_api_key\": \"$planet_key\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for invalid 'algorithm_id'" 400 -eq "$code" "skip"

payload="{
	\"algorithm_id\": \"$service_id\",
	\"scene_id\": \"$known_good_sceneId\",
	\"name\": \"Payload WITH Spaces\",
	\"planet_api_key\": \"foo\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for invalid 'planet_api_key'" 400 -eq "$code" "skip"

payload="{
	\"algorithm_id\" :\"$service_id\",
	\"scene_id\": \"rapideye:foo\",
	\"name\": \"Payload WITH Spaces\",
	\"planet_api_key\": \"$planet_key\"
	}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 400 for invalid 'scene_id'" 400 -eq "$code" "skip"


display_result
exit $?