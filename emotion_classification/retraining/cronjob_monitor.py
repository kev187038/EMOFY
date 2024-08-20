from kubernetes import client, config
from kubernetes.client.rest import ApiException

class CronJobMonitor:
    def __init__(self, namespace, cronjob_name):
        self.namespace = namespace
        self.cronjob_name = cronjob_name
        
        # Load the Kubernetes configuration
        config.load_incluster_config()
        self.batch_v1 = client.BatchV1Api()

    def last_execution(self):
        try:
            # Fetch the specified cronjob
            cronjob = self.batch_v1.read_namespaced_cron_job(self.cronjob_name, self.namespace)
            
            # Get the last scheduled time from the cronjob's status
            last_schedule_time = cronjob.status.last_schedule_time
            
            if last_schedule_time is None:
                return None if last_schedule_time is None else last_schedule_time.strftime('%Y-%m-%d %H:%M:%S')
        
        except ApiException as e:
            raise Exception(f'Error fetching cronjob: {e}')

# Example usage:
# monitor = CronJobMonitor('default', 'retrain-cronjob')
# print(monitor.last_execution())
