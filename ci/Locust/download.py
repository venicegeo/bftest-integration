from locust import HttpLocust, TaskSet, task
import random
import EV

# Behavior that simulates the repeated download of Jobs for a user.
class DownloadBehavior(TaskSet):
    # Obtain the Job reference to download.
    def on_start(self):
        # Get the complete list of Jobs for a user.
        with self.client.get("/job", auth = (EV.BF_API_KEY, ""), catch_response=True) as response:
	    # Ensure the list has jobs.
	    if len(response.json()["jobs"]["features"]) == 0:
		print("API Key has no associated jobs. Test cannot continue.")
		self.job_id = None
		return
            # Pick a random job from the list.
            job = random.choice(list(response.json()["jobs"]["features"]))
            # Extract the job's status from the response.
            if job["properties"]["status"] == "Success":
                self.job_id = job["properties"]["job_id"]
            else:
		return

    @task(1)
    def download(self):
	if self.job_id is None:
	    print("Task has no Job ID. Test cannot continue.")
	    return
	data_type = random.choice(["gpkg", "geojson", "shp.zip"])
        download_url = "/job/%s.%s" % self.job_id, data_type
        print("Downloading Data %s" % download_url)
	with self.client.get(download_url, auth = (EV.BF_API_KEY, ""), catch_response = True) as response:
	    if response.ok:
		print("Job %s length was %s", self.job_id, response.headers["Content-Length"])
		response.success()
	    else:
		response.failure()


# Required for running a locust test.  Defines the user properites.
class BeachfrontUser(HttpLocust):
    task_set = DownloadBehavior

    # Wait times between requests
    min_wait = 1000
    max_wait = 5000
