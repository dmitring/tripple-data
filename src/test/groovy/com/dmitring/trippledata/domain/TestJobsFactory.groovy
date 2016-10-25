package com.dmitring.trippledata.domain

import com.dmitring.trippledata.services.jobProcessing.JobTimer
import com.dmitring.trippledata.services.jobProcessing.JobTimerImpl

public class TestJobsFactory {
    JobTimer jobTimer = new JobTimerImpl()

    public Job createJob() {
        return createJobWithAlgorithmAndStatus(HashingAlgorithm.MD5, JobStatus.PROCESSING);
    }

    public Job createJobWithStatus(JobStatus jobStatus) {
        return createJobWithAlgorithmAndStatus(HashingAlgorithm.MD5, jobStatus);
    }

    public Job createJobWithAlgorithmAndStatus(HashingAlgorithm algorithm, JobStatus status) {
        return new Job(UUID.randomUUID(), UUID.randomUUID(), "http://some_url", algorithm, status)
    }
}
