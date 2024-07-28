from kubernetes import client, config
from datetime import datetime

class CronJobMonitor:
    def __init__(self, namespace, cronjob_name):
        # Initialize the Kubernetes client
        config.load_kube_config()
        self.batch_v1 = client.BatchV1Api()
        self.namespace = namespace
        self.cronjob_name = cronjob_name

    def last_execution(self):
        try:
            # Fetch all jobs in the namespace
            jobs = self.batch_v1.list_namespaced_job(self.namespace)

            # Filter jobs created by the specified CronJob
            cronjob_jobs = [
                job for job in jobs.items
                if any(owner_ref.name == self.cronjob_name for owner_ref in job.metadata.owner_references)
            ]

            # Initialize variables to track the last successful job
            last_successful_time = None

            # Iterate through the jobs and find the last successful one
            for job in cronjob_jobs:
                conditions = job.status.conditions
                if conditions:
                    for condition in conditions:
                        if condition.type == 'Complete' and condition.status == 'True':
                            if last_successful_time is None or condition.last_transition_time > last_successful_time:
                                last_successful_time = condition.last_transition_time

            return last_successful_time

        except client.ApiException as e:
            print(f"An error occurred: {e}")
            return None

# Example usage
if __name__ == "__main__":
    # Specify your namespace and CronJob name
    namespace = 'default'
    cronjob_name = 'your-cronjob-name'

    monitor = CronJobMonitor(namespace, cronjob_name)
    last_execution_time = monitor.last_execution()

    if last_execution_time:
        print(f"Last successful execution time: {last_execution_time}")
    else:
        print("No successful execution found or an error occurred.")
