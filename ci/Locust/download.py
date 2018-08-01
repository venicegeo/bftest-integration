from locust import HttpLocust, TaskSet, task
import random
import EV

# Behavior that simulates the repeated download of Jobs for a user.
class DownloadBehavior(TaskSet):
    # Obtain the Job reference to download.
    def on_start(self):
        # Get the complete list of Jobs for a user.
        with self.client.get("/job", auth = (EV.BF_API_KEY, "")) as response:
            # Pick a random job from the list.
            rand_job = random.randint(0, len(response.json()["job"])-1)
            # Extract the job's status from the response.
            job = response.json()["job"][rand_job]
            if job["properties"]["status"] == "Success":
                self.job_id = job["properties"]["job_id"]
            else:
                self.interrupt()

    @task(1)
    def download_geopackage(self):
        download_url = "/job/%s.gpkg" % self.job_id
        print("Downloading GeoPackage at %s" % download_url)
        self.client.get(download_url, auth = (EV.BF_API_KEY, ""))

    @task(1)
    def download_shapefile(self):
        download_url = "/job/%s.shp.zip" % self.job_id
        print("Downloading Shapefile at %s" % download_url)
        self.client.get(download_url, auth = (EV.BF_API_KEY, ""))

    @task(1)
    def download_geojson(self):
        download_url = "/job/%s.geojson" % self.job_id
        print("Downloading GeoJson at %s" % download_url)
        self.client.get(download_url, auth = (EV.BF_API_KEY, ""))


# Required for running a locust test.  Defines the user properites.
class BeachfrontUser(HttpLocust):
    task_set = DownloadBehavior

    # Wait times between requests
    min_wait = 1000
    max_wait = 5000
