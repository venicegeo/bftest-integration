from locust import HttpLocust, TaskSet, task, events

import time, csv

import EV

class UserBehavior(TaskSet):

    test_start_time = time.time()

    @task(1)
    def check_job_status(self):
        with self.client.get("/v0/job/%s" % self.job_id, auth = (EV.BF_API_KEY, ""), catch_response = True) as response:
            status = response.json()["job"]["properties"]["status"]
            if status not in self.status_times:
                self.status_times[status] = time.time() - self.test_start_time

            if status in ["Pending", "Running"]:
                response.success()
            elif status == "Success":
                response.success()
                self.job_complete.fire()
                self.create_job()
            else:
                response.failure("Failed.  The status was '" + status +"'.")
                self.job_complete.fire()
                self.create_job()

    def create_job(self):
        job_request_payload = {
            'algorithm_id': EV.ALGORITHM_ID,
            'scene_id': EV.SCENE_ID,
            'name': 'LOCUST SWARM!',
            'planet_api_key': EV.PLANET_API_KEY,
            'compute_mask': False
        }

        self.job_start_time = time.time() - self.test_start_time

        with self.client.post("/v0/job", auth = (EV.BF_API_KEY, ""), json = job_request_payload, catch_response = True) as response:
            print("Response status code:", response.status_code)
            try:
                self.job_id = response.json()["job"]["id"]
            except Exception as e:
                response.failure("Failed.  The job_id could not be parsed from response.")
                print(response.request)
                time.sleep(5)
                self.create_job()
            else:
                print("NOW TESTING NEW JOB ID: %s" % self.job_id)
                self.status_times = {}

    def on_start(self):
        self.create_job()

        self.job_complete = events.EventHook()
        self.job_complete += self.on_job_complete

        with open("results.csv", "w+") as results_file:
            writer = csv.writer(results_file)
            writer.writerow(["Job Id", "Start Time", "Time at Pending", "Time at Running", "Time at Success", "Time at Error", "Time at Failed"])

    def on_job_complete(self):
        job_time = round(time.time() - self.job_start_time)
        data = [self.job_id, self.job_start_time]
        for possible_status in ["Pending", "Running", "Success", "Error", "Failed"]:
            if possible_status in self.status_times:
                data.append(self.status_times[possible_status])
            else:
                data.append("")
        with open("results.csv", "a") as results_file:
            writer = csv.writer(results_file)
            writer.writerow(data)
        print "The job_id %s took %d seconds to complete" % (self.job_id, job_time)

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 2000
    max_wait = 2000

