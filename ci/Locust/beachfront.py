from locust import HttpLocust, TaskSet, task, events

import time, csv

# Environment Variables
import EV

# Required for running a locust test.  Defines the actions that a user will take.
class UserBehavior(TaskSet):
    # A single Locust user will first create_job, then repeatedly check_job_status, until the job is no longer "Pending",
    # or "Running", at which point that user will start the process again, creating a new job.

    # When a job completes, the results are stored in a csv file, detailing when the job's status changed.

    # Run this locust test with the command:
    # locust -f beachfront.py --no-web -c <# users> -r <# users spawned / sec> --host=<bf-api url>

    # Static.  Used as a reference point 
    test_start_time = time.time()

    # Create a job.  This method only runs when called by another method; it is not run automatically by locust.
    def create_job(self):
        # JSON payload for job creation.  This is the same for each request.
        job_request_payload = {
            'algorithm_id': EV.ALGORITHM_ID,
            'scene_id': EV.SCENE_ID,
            'name': 'LOCUST SWARM!',
            'planet_api_key': EV.PLANET_API_KEY,
            'compute_mask': False
        }

        # Record time that the job starts.  Will be logged at job completion.
        self.job_start_time = time.time() - self.test_start_time

        # POST request, creating the job.  
        with self.client.post("/job", auth = (EV.BF_API_KEY, ""), json = job_request_payload, catch_response = True) as response:

            try:
                # Assume that, if job_id can be extracted from the response, the job creation was successful.
                self.job_id = response.json()["job"]["id"]

            except Exception as e:
                # If job creation failed, log the failure.
                response.failure("Failed.  The job_id could not be parsed from response.")
                print(response.request)

                # Then wait 5 seconds before trying again.
                time.sleep(5)
                self.create_job()

            else:
                # If job was successful, clear the status_times dict (allowing new recording to be made)
                print("NOW TESTING NEW JOB ID: %s" % self.job_id)
                self.status_times = {}
                # Then exit this method to allow locust to repeatedly check_job_status

    # Check the status of the user's current job.  The annotation makes locust run this request repeatedly.
    @task(1)
    def check_job_status(self):
        # GET request checking the status of the job, using job_id, which was set in create_job method.
        with self.client.get("/job/%s" % self.job_id, auth = (EV.BF_API_KEY, ""), catch_response = True) as response:

            # Extract the job's status from the response.
            status = response.json()["job"]["properties"]["status"]

            # If the job did not have this status already, record the time this occured.
            if status not in self.status_times:
                self.status_times[status] = time.time() - self.test_start_time

            # Depending on the status:
            # Mark the request as a success, but continue checking.
            if status in ["Pending", "Running", "Submitted"]:
                response.success()

            # Mark the request as a success, fire the job completion event, then create a new job
            elif status == "Success":
                response.success()
                self.job_complete.fire()
                self.create_job()

            # Mark the request as a failure, fire the job completion event, then create a new job
            else:
                response.failure("Failed.  The status was '" + status +"'.")
                self.job_complete.fire()
                self.create_job()

    # This method is called when the locust user is first created.
    def on_start(self):
        # Create a job to generate the first job_id.
        self.create_job()

        # Create an event to fire when a job completes, which will record data.
        self.job_complete = events.EventHook()
        self.job_complete += self.on_job_complete

        # Write the header of the results file.
        with open("results.csv", "w+") as results_file:
            writer = csv.writer(results_file)
            writer.writerow(["Job Id", "Start Time", "Time at Submitted", "Time at Pending", "Time at Running", "Time at Success", "Time at Error", "Time at Failed"])

    # Thsi method is called when a job completes.
    def on_job_complete(self):
        # Print the total time the job took to complete:
        job_time = round(time.time() - (self.job_start_time + self.test_start_time))
        print "The job_id %s took %d seconds to complete" % (self.job_id, job_time)

        # Create a row of data to append to the results file.
        data = [self.job_id, self.job_start_time]
        for possible_status in ["Submitted", "Pending", "Running", "Success", "Error", "Failed"]:
            if possible_status in self.status_times:
                data.append(self.status_times[possible_status])
            else:
                data.append("")

        # Append the data to the results file.
        with open("results.csv", "a") as results_file:
            writer = csv.writer(results_file)
            writer.writerow(data)

# Required for running a locust test.  Defines the user properites.
class WebsiteUser(HttpLocust):
    task_set = UserBehavior

    # No need to send too many status requests.  Send one every 2 seconds.
    min_wait = 4000
    max_wait = 4000