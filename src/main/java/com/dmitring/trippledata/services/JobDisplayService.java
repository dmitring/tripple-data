package com.dmitring.trippledata.services;

import com.dmitring.trippledata.controllers.messages.JobResponse;
import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.domain.JobStatus;

import java.util.Collection;
import java.util.UUID;

public interface JobDisplayService {
    Collection<JobResponse> getJobs(UUID clientId, Collection<JobStatus> alloweedStatuses);
    JobResponse getJobResponse(Job job);
}
