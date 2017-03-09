# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests


http_get "${http}bf-ia-broker.$domain/"
assert "Should receive a 200" 200 -eq "$code"

http_get "${http}bf-ia-broker.$domain/planet/discover/rapideye?PL_API_KEY=$planet_key"
assert "Should receive a 200" 200 -eq "$code"

selected_image=""
for IMAGE in $(echo $body | jq -r '[.features[] | .id]')
do
	IMAGE=$(strip_jq $IMAGE)
	
	if [ -z "$IMAGE" ]; then
		continue
	fi
	
	http_get "${http}bf-ia-broker.$domain/planet/rapideye/$IMAGE?PL_API_KEY=$planet_key"
	assert "Should receive a 200" 200 -eq "$code"
	image_status="$(echo $body | jq -r '.properties.status')"
	if [ "inactive" == "$image_status" ]; then
		selected_image="$IMAGE"
		break
	fi
done
assert "Inactive rapideye image should be found" ! -z "$selected_image"

http_get "${http}bf-ia-broker.$domain/planet/activate/rapideye/$selected_image?PL_API_KEY=$planet_key"
assert "Should receive a 200" 200 -eq "$code"

for i in {1..20}
do
	http_get "${http}bf-ia-broker.$domain/planet/rapideye/$selected_image?PL_API_KEY=$planet_key"
	assert "Should receive a 200" 200 -eq "$code"
	status="$(echo $body | jq -r '.properties.status')"
	if [ "active" == "$status" ]; then
		i=0 # Pass, in case on last iteration.
		break
	else
		info "Image is $status, trying again.  Attempt $i of 20."
		sleep 10
	fi
done
assert "Rapideye image activation should be successful" 20 -ne "$i"

http_get "${http}bf-ia-broker.$domain/planet/discover/planetscope?PL_API_KEY=$planet_key"
assert "Should receive a 200" 200 -eq "$code"

selected_image=""
for IMAGE in $(echo $body | jq -r '[.features[] | .id]')
do
	IMAGE=$(strip_jq $IMAGE)
	
	if [ -z "$IMAGE" ]; then
		continue
	fi
	
	http_get "${http}bf-ia-broker.$domain/planet/planetscope/$IMAGE?PL_API_KEY=$planet_key"
	assert "Should receive a 200" 200 -eq "$code"
	image_status="$(echo $body | jq -r '.properties.status')"
	if [ "inactive" == "$image_status" ]; then
		selected_image="$IMAGE"
		break
	fi
done
assert "Inactive planetscope image should be found" ! -z "$selected_image"

http_get "${http}bf-ia-broker.$domain/planet/activate/planetscope/$selected_image?PL_API_KEY=$planet_key"
assert "Should receive a 200" 200 -eq "$code"

for i in {1..20}
do
	http_get "${http}bf-ia-broker.$domain/planet/planetscope/$selected_image?PL_API_KEY=$planet_key"
	assert "Should receive a 200" 200 -eq "$code"
	status="$(echo $body | jq -r '.properties.status')"
	if [ "active" == "$status" ]; then
		i=0 # Pass, in case on last iteration.
		break
	else
		info "Image is $status, trying again.  Attempt $i of 20."
		sleep 10
	fi
done
assert "Planetscope image activation should be successful" 20 -ne "$i"



display_result
exit $?