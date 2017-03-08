# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests


http_get "${http}bf-api.$domain/"
assert "Should receive a 200" 200 -eq "$code"
assert "Uptime field is present" "null" != "$(echo $body | jq '.uptime')"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth"
service_id="$(echo $body | jq -r '.algorithms[0].service_id')"
assert "Should receive a 200" 200 -eq "$code"
assert "An algorithm's service_id found" "null" != "$service_id"

http_get "${http}bf-api.$domain/v0/algorithm/$service_id" "$auth"
assert "Should receive a 200" 200 -eq "$code"
assert "Correct service_id returned" "$service_id" == "$(echo $body | jq -r '.algorithm.service_id')"

http_get "${http}bf-api.$domain/v0/user" "$auth"
assert "Should receive a 200" 200 -eq "$code"
catalog_url="$(echo $body | jq -r '.services.catalog')"
assert "The catalog url should exist" "null" != "$catalog_url"

http_get "$catalog_url"
assert "Should receive a 200" 200 -eq "$code"

http_get "$catalog_url/planet/discover/rapideye?PL_API_KEY=$planet_key"
assert "Should receive a 200" 200 -eq "$code"

selected_image=""
for IMAGE in $(echo $body | jq -r '[.features[] | .id]')
do
	IMAGE=$(strip_jq $IMAGE)
	
	if [ -z "$IMAGE" ]; then
		continue
	fi
	
	http_get "$catalog_url/planet/rapideye/$IMAGE?PL_API_KEY=$planet_key"
	assert "Should receive a 200" 200 -eq "$code"
	image_status="$(echo $body | jq -r '.properties.status')"
	if [ "inactive" == "$image_status" ]; then
		selected_image="$IMAGE"
		break
	fi
done
assert "Inactive image should be found" ! -z "$selected_image"

payload="{\"algorithm_id\":\"$service_id\",\"scene_id\":\"rapideye:$selected_image\",\"name\":\"Payload WITH Spaces\",\"planet_api_key\":\"$planet_key\"}"
http_post "${http}bf-api.$domain/v0/job" "$auth" "$payload"
assert "Should receive a 201" 201 -eq "$code"
job_id="$(echo $body | jq -r '.job.id')"
assert "A job Id should be returned" "null" != "$job_id"

for i in {1..20}
do
	http_get "${http}bf-api.$domain/v0/job/$job_id" "$auth"
	assert "Should receive a 200" 200 -eq "$code"
	status="$(echo $body | jq -r '.job.properties.status')"
	if [ "Success" == "$status" ]; then
		i=0 # Pass, in case on last iteration.
		break
	else
		info "Job is $status, trying again.  Attempt $i of 20."
		sleep 30
	fi
done
assert "Job should be successful" 20 -ne "$i"

http_get "${http}bf-api.$domain/v0/job" "$auth"
assert "Should receive a 200" 200 -eq "$code"
assert_jq_array_contains "Job should be in list" "$(echo $body | jq -r '[.jobs.features[] | .id]')" "$job_id"

http_get "${http}bf-api.$domain/v0/job/by_scene/rapideye:$selected_image" "$auth"
assert "Should receive a 200" 200 -eq "$code"
assert_jq_array_contains "Job should be in jobs listed by scene" "$(echo $body | jq -r '[.jobs.features[] | .id]')" "$job_id"
for IMAGE in $(echo $body | jq -r '[.jobs.features[] | .properties.scene_id]')
do
	IMAGE=$(strip_jq $IMAGE)
	
	if [ -z "$IMAGE" ]; then
		continue
	fi
	
	assert "Image in list has correct scene" "rapideye:$selected_image" == "$IMAGE"
done

http_delete "${http}bf-api.$domain/v0/job/$job_id" "$auth"
assert "Should receive a 200" 200 -eq "$code"

sleep 5

http_get "${http}bf-api.$domain/v0/job" "$auth"
assert "Should receive a 200" 200 -eq "$code"
assert_jq_array_lacks "Deleted job should not be in list" "$(echo $body | jq -r '[.jobs.features[] | .id]')" "$job_id"

http_get "${http}bf-api.$domain/v0/job/$job_id" "$auth"
assert "Should receive a 200" 200 -eq "$code"

sleep 5

http_get "${http}bf-api.$domain/v0/job" "$auth"
assert "Should receive a 200" 200 -eq "$code"
assert_jq_array_contains "Remembered job should be back in list" "$(echo $body | jq -r '[.jobs.features[] | .id]')" "$job_id"
