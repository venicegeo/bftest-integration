assert()
{
	# Function Call:
	# assert "Name of Test" "ARG1" "ARG2" "ARG3" "SKIP?"
	#
	# This function will perform the test [ $ARG1$ARG2$ARG3 ], then
	# display the "Name of Test" in either Green (✓) or Red (✗),
	# depending on the result of the test.
	#
	# If the 5th argument is "skip", then the test will not be
	# performed, and the "Name of Test" will be displayed in Blue (☁)
	#
	# Examples:
	# assert "This tests that integers are equal" 200 -eq 200
	# assert "This tests that integers are not equal" 1 -ne 2
	# assert "This tests that strings are equal" "some_string" == "some_string"
	# assert "This tests that strings not equal" "this_string" != "that_string"
	# assert "This tests that a variable is empty" "" -z "$variable"
	# assert "This tests that a variable is not empty" ! -z "$variable"
	
	RED='\033[0;31m'
	GREEN='\033[0;32m'
	BLUE='\033[0;34m'
	NC='\033[0m'
	
	if [ ! -z "$5" ] && [ "skip" == "$5" ]; then
		echo -e "  $BLUE☁ $ASSERTION$NC"
		return 0
	fi
	
	
	ASSERTION="$1"
	
	LEFT_temp=${2%\"}			# Remove trailing and leading double quotes.
	LEFT=${LEFT_temp#\"}
	if [ -n "$LEFT" ]; then		# Unless empty, add a trailing space.
		LEFT="$LEFT "
	fi
	
	OP=$3
	
	RIGHT_temp=${4%\"}			# Remove trailing and leading double quotes.
	RIGHT=${RIGHT_temp#\"}
	if [ -n "$RIGHT" ]; then	# Unless empty, add a leading space.
		RIGHT=" $RIGHT"
	fi
	
	
	if [ $LEFT$OP$RIGHT ]; then
		echo -e "  $GREEN✓ $ASSERTION$NC"
	else
		echo -e "  $RED $ASSERTION$NC"
	fi
}

info()
{
	# This echos the provided text in dark grey.  This uses the
	# same indention as assertions.
	
	NC='\033[0m'
	GREY='\033[0;90m'
	echo -e "$GREY  ★ $1$NC"
}


http_get()
{
	# Function Call:
	# http_get "https://url.to.hit" "username:password"
	#
	# This function sends a GET request to the provided URL, adding 
	# an auth header if provided one.
	#
	# Running this function will display the http request like so:
	# METHOD: https://url.to.hit
	#
	# After this function completes, the variables $body (the response body)
	# and $code (the status code) are available in the calling script.
	
	AUTH="$2"
	if [ -n "$AUTH" ]; then		# Unless empty, add --user tag.
		AUTH="--user $AUTH"
	fi
	
	URL="$1"
	printf "GET: $URL\n"
	
	IFS=✓ read body code <<< $(curl $AUTH -s -w "✓%{http_code}" $URL)

}

# This function sends a get request, and assigns $body & $code variables from the response
http_delete()
{	
	# Function Call:
	# http_get "https://url.to.hit" "username:password"
	#
	# This function sends a DELETE request to the provided URL, adding 
	# an auth header if provided one.
	#
	# Running this function will display the http request like so:
	# METHOD: https://url.to.hit
	#
	# After this function completes, the variables $body (the response body)
	# and $code (the status code) are available in the calling script.
	
	AUTH="$2"
	if [ -n "$AUTH" ]; then		# Unless empty, add --user tag.
		AUTH="--user $AUTH"
	fi
	
	URL="$1"
	printf "DELETE: $URL\n"
	
	IFS=✓ read body code <<< $(curl -X DELETE $AUTH -s -w "✓%{http_code}" $URL)

}

# This function sends a POST request, and assigns $body & $code variables from the response
http_post()
{
	# Function Call:
	# http_get "https://url.to.hit" {"json":"payload"} "username:password"
	#
	# This function sends a POST request to the provided URL, adding 
	# an auth header if provided one.
	#
	# If a payload is provided, that will be sent in the request as well.
	# A 'Content-Type: application/json' header is always sent.
	#
	# Running this function will display the http request like so:
	# METHOD: https://url.to.hit
	#   ★ PAYLOAD: {"json":"payload"}
	#
	# After this function completes, the variables $body (the response body)
	# and $code (the status code) are available in the calling script.

	URL="$1"
	printf "POST: $URL\n"
	
	PAYLOAD="$2"
	if [ -n "$PAYLOAD" ]; then		# Unless empty, add -d tag.
		info "PAYLOAD: $PAYLOAD"
		PAYLOAD_TAG="-d"
	else
		info "NO PAYLOAD"
	fi
	
	AUTH="$3"
	if [ -n "$AUTH" ]; then			# Unless empty, add --user tag.
		AUTH="--user $AUTH"
	fi
	

	response=$(curl -X POST "$PAYLOAD_TAG" "$PAYLOAD" -H 'Content-Type: application/json' $AUTH -s -w "✓%{http_code}" $URL)
	
	IFS=✓ read body code <<< $(echo "$response")

}

assert_jq_array_contains()
{
	# Function Call:
	# assert "Name of Test" '["jq", "array"]' "string2find" "SKIP?"
	#
	# This function asserts that "string2find" is found in an array
	# created by jq.

	ASSERTION="$1"
	JQ_ARRAY=$2
	TARGET="$3"
	
	found=""
	for ITEM in $JQ_ARRAY
	do
		ITEM=$(strip_jq $ITEM)
		
		if [ -z "$ITEM" ]; then
			continue
		fi

		if [ "$TARGET" == "$ITEM" ]; then
			found="Present"
			break
		fi
	done
	
	assert "$ASSERTION" ! -z "$found" "$4"
}

assert_jq_array_lacks()
{
	# Function Call:
	# assert "Name of Test" '["jq", "array"]' "string2find" "SKIP?"
	#
	# This function asserts that "string2find" is NOT found in an array
	# created by jq.
	
	ASSERTION="$1"
	JQ_ARRAY=$2
	TARGET="$3"
	
	found="Missing"
	for ITEM in $JQ_ARRAY
	do
		ITEM=$(strip_jq $ITEM)
		
		if [ -z "$ITEM" ]; then
			continue
		fi
		
		if [ "$TARGET" == "$ITEM" ]; then
			found=""
			break
		fi
	done
	
	assert "$ASSERTION" ! -z "$found" "$4"
}

strip_jq()
{
	# Function Call:
	# VARIABLE=$(stip_jq $VARIABLE)
	#
	# This function removes extra characters that jq adds to make string comparisons
	# easier, particularly when dealing with elements in an array.
	
	OUTPUT_STRING=${1%]}				# Strip jq stuff (quotes, commas, brackets) from array
	OUTPUT_STRING=${OUTPUT_STRING%,}
	OUTPUT_STRING=${OUTPUT_STRING%\"}
	OUTPUT_STRING=${OUTPUT_STRING#[}
	OUTPUT_STRING=${OUTPUT_STRING#\"}
	
	echo "$OUTPUT_STRING"
}


info "Utilities imported!"