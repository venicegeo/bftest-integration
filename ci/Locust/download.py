from locust import HttpLocust, TaskSet, task
import random
import EV

# Behavior that simulates the repeated download of Jobs for a user.
class DownloadBehavior(TaskSet):

    # Obtain the Job List for the chosen API Key
    def on_start(self):
        # Get the complete list of Jobs for a user.
        with self.client.get("/job", auth = (EV.BF_API_KEY, ""), catch_response=True) as response:
            self.jobs_list = response.json()

    # Downloads a random Job from the list
    @task(1)
    def download(self):
        # Get a random Job ID
        job_id = self.get_random_job()
        if job_id is None:
            print("Job %s was not a Success. Skipping." % job_id)
            return
        # Choose a random Data Type
        data_type = random.choice(["gpkg", "geojson", "shp.zip"])
        download_url = "/job/%s.%s" % (job_id, data_type)
        print("Downloading Data %s" % download_url)
        # Download
        with self.client.get(download_url, auth = (EV.BF_API_KEY, ""), catch_response = True) as response:
            # Check HTTP Status
            if response.ok:
                print("Job %s length was %s" % (job_id, response.headers["Content-Length"]))
                response.success()
            else:
                response.failure("The download has failed for Job %s with status %s and content %s" % (job_id, response.status_code, response.text))

    # Pull a random Job from the Jobs list.
    def get_random_job(self):
        job = random.choice(list(self.jobs_list["jobs"]["features"]))
        # Extract the job's status from the response.
        if job["properties"]["status"] == "Success":
            return job["properties"]["job_id"]
        else:
            return None

# Required for running a locust test.  Defines the user properites.
class BeachfrontUser(HttpLocust):
    task_set = DownloadBehavior

    # Wait times between requests
    min_wait = 1000
    max_wait = 3000
