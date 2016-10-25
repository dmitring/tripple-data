package com.dmitring.trippledata.services;

import com.dmitring.trippledata.controllers.messages.JobResponse;
import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.domain.JobStatus;
import com.dmitring.trippledata.repositories.JobRepository;
import com.dmitring.trippledata.services.jobProcessing.JobTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobDisplayServiceImpl implements JobDisplayService {
    private final JobRepository jobRepository;
    private final JobTimer jobTimer;

    @Autowired
    public JobDisplayServiceImpl(JobRepository jobRepository, JobTimer jobTimer) {
        this.jobRepository = jobRepository;
        this.jobTimer = jobTimer;
    }

    @Override
    @Transactional
    public Collection<JobResponse> getJobs(UUID clientId, Collection<JobStatus> alloweedStatuses) {
        return jobRepository.findByClientIdAndStatusIn(clientId, alloweedStatuses)
                .map(this::getJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJobResponse(Job job) {
        long processingTime;
        long totalWaitTime;

        if (job.getEndProcessingTime() != 0) {
            processingTime = job.getEndProcessingTime() - job.getStartProcessingTime();
            totalWaitTime = job.getEndProcessingTime() - job.getArrivedTime();
        } else {
            final long currentTime = jobTimer.getCurrentTime();
            if (job.getStartProcessingTime() == 0)
                processingTime = 0;
            else {
                processingTime = currentTime - job.getStartProcessingTime();
            }
            totalWaitTime = currentTime - job.getArrivedTime();
        }

        return new JobResponse(
                job.getId(),
                job.getClientId(),
                job.getSourceUri(),
                job.getHashingAlgorithm(),
                job.getStatus(),
                job.getArrivedTime(),
                job.getStartProcessingTime(),
                job.getEndProcessingTime(),
                totalWaitTime,
                processingTime,
                job.getHexHash(),
                job.getStackTrace());
    }
}
