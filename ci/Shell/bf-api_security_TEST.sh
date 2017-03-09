# install jq

. ./utils.sh	# Import functions like assert and http_get
. ./vars.sh		# Import variables needed for these tests


http_get "${http}bf-api.$domain/"
assert "Should receive a 200" 200 -eq "$code"
assert "Uptime field is present" "null" != "$(echo $body | jq '.uptime')"

http_get "http://bf-api.$domain/"
assert "Should receive a 403 for missing HTTPS" 403 -eq "$code"

http_get "http://bf-api.$domain/v0/algorithm" "$auth"
assert "Should receive a 403 for missing HTTPS" 403 -eq "$code"

http_get "http://bf-api.$domain/v0/user" "$auth"
assert "Should receive a 403 for missing HTTPS" 403 -eq "$code"

http_get "http://bf-api.$domain/v0/job" "$auth"
assert "Should receive a 403 for missing HTTPS" 403 -eq "$code"

http_get "http://bf-api.$domain/v0/productline" "$auth"
assert "Should receive a 403 for missing HTTPS" 403 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Origin: https://beachfront.$domain" "Access-Control-Request-Headers:Content-Type,X-Requested-With"
assert "Should receive a 200 for beachfront Origin" 200 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Origin: https://bf-swagger.$domain" "Access-Control-Request-Headers: Content-Type,X-Requested-With"
assert "Should receive a 200 for bf-swagger Origin" 200 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Access-Control-Request-Headers: Content-Type,X-Requested-With" "Referer: https://beachfront.$domain"
assert "Should receive a 403 for a Referer and no Origin" 403 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Origin: https://garbage.$domain" "Access-Control-Request-Headers: Content-Type,X-Requested-With"
assert "Should receive a 403 for garbage Origin" 403 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Origin: https://bf-swagger.$domain.garbage.io" "Access-Control-Request-Headers: Content-Type,X-Requested-With"
assert "Should receive a 403 for phishing Origin" 403 -eq "$code"

http_get "${http}bf-api.$domain/v0/algorithm" "$auth" "Origin: http://bf-swagger.$domain" "Access-Control-Request-Headers: Content-Type,X-Requested-With"
assert "Should receive a 403 for http Origin" 403 -eq "$code"


display_result
exit $?