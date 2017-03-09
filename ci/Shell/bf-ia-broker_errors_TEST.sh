# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests

known_good_sceneId="rapideye:20170307_004628_5866108_RapidEye-4"

http_get "${http}bf-ia-broker.$domain/"
assert "Should receive a 200" 200 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/discover/foo?PL_API_KEY=$planet_key"
assert "Should receive a 400 when discovering with an invalid item type" 400 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/discover/rapideye"
assert "Should receive a 400 when discovering with no provided PL_API_KEY" 400 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/discover/rapideye?PL_API_KEY=foo"
assert "Should receive a 400 when discovering with an invalid PL_API_KEY" 400 -eq "$code" "skip"

http_get "${http}bf-ia-broker.$domain/planet/rapideye/foo?PL_API_KEY=$planet_key"
assert "Should receive a 404 when looking for an invalid image id" 404 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/planetscope/$known_good_sceneId?PL_API_KEY=$planet_key"
assert "Should receive a 404 when searching planetscope for a rapideye image" 404 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/activate/foo/$known_good_sceneId?PL_API_KEY=$planet_key"
assert "Should receive a 400 when trying to activate using an invalid item type" 400 -eq "$code" "skip"

http_get "${http}bf-ia-broker.$domain/planet/activate/rapideye/foo?PL_API_KEY=$planet_key"
assert "Should receive a 404 when trying to activate using an invalid image id" 404 -eq "$code" "skip"

display_result
exit $?