# Locust Load Tests

These load tests will stress the Beachfront API endpoint with a bulk of shoreline detection job requests. The API will be instructed to not check for redundant jobs with each request, so one request will indeed translate to one execution of the shoreline algorithm.

# Running

Update the `EV.py` file with the requested values.

`pip install` the requirements from `requirements.txt`

Run Locust Python using the command `locust -f beachfront.py --host=http://the.beachfront.url --no-web -n 10 -r 1 -c 10`

Parameters for the above command line are as follows:

	*	`c`: Concurrent users
	*	`r`: Additional users per second (ramping)
	*       `n`: The number of requests to make

# Results

Results are saved to the `results.csv` file after every job completion.
