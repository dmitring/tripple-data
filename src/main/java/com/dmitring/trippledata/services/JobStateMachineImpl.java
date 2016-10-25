package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.domain.JobStatus;
import com.dmitring.trippledata.exceptions.InvalidJobStatusException;
import com.dmitring.trippledata.exceptions.JobAlreadyExistsException;
import com.dmitring.trippledata.exceptions.JobDoesNotExistException;
import com.dmitring.trippledata.repositories.JobRepository;
import com.dmitring.trippledata.services.jobProcessing.JobTimer;
import com.dmitring.trippledata.services.jobProcessing.StackTraceCollector;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class JobStateMachineImpl implements JobStateMachine {

    private final JobRepository jobRepository;
    private final JobTimer jobTimer;
    private final StackTraceCollector stackTraceCollector;

    @Autowired
    public JobStateMachineImpl(JobRepository jobRepository, JobTimer jobTimer, StackTraceCollector stackTraceCollector) {
        this.jobRepository = jobRepository;
        this.jobTimer = jobTimer;
        this.stackTraceCollector = stackTraceCollector;
    }

    @Override
    @Transactional
    public Job initWaitingJob(UUID jobId, UUID clientId, String sourceUrl, HashingAlgorithm hashingAlgorithm) {
        assertInitingWaitingJob(jobId);
        final Job waitingJob = new Job(jobId, clientId, sourceUrl, hashingAlgorithm, JobStatus.WAITING);
        waitingJob.setArrivedTime(jobTimer.getCurrentTime());
        jobRepository.save(waitingJob);
        return waitingJob;
    }

    @Override
    @Transactional
    public Job turnToProcessing(UUID jobId) {
        final Job persistedJob = jobRepository.findOne(jobId);
        assertJobExistsAndStatusIn(jobId, persistedJob, JobStatus.WAITING);
        persistedJob.setStatus(JobStatus.PROCESSING);
        persistedJob.setStartProcessingTime(jobTimer.getCurrentTime());
        return jobRepository.save(persistedJob);
    }

    @Override
    @Transactional
    public Job turnToCompleted(UUID jobId, String hexHash) {
        final Job persistedJob = jobRepository.findOne(jobId);
        assertJobExistsAndStatusIn(jobId, persistedJob, JobStatus.PROCESSING);
        persistedJob.setStatus(JobStatus.COMPLETED);
        persistedJob.setEndProcessingTime(jobTimer.getCurrentTime());
        persistedJob.setHexHash(hexHash);
        return jobRepository.save(persistedJob);
    }

    @Override
    @Transactional
    public Job turnToFailed(UUID jobId, Throwable throwable) {
        final Job persistedJob = jobRepository.findOne(jobId);
        assertJobExistsAndStatusIn(jobId, persistedJob, JobStatus.PROCESSING);
        persistedJob.setStatus(JobStatus.FAILED);
        persistedJob.setEndProcessingTime(jobTimer.getCurrentTime());
        persistedJob.setStackTrace(stackTraceCollector.getStackTrace(throwable));
        return jobRepository.save(persistedJob);
    }

    @Override
    @Transactional
    public Job turnToCancelled(UUID jobId) {
        final Job persistedJob = jobRepository.findOne(jobId);
        assertJobExistsAndStatusIn(jobId, persistedJob, JobStatus.values());
        persistedJob.setStatus(JobStatus.CANCELED);
        persistedJob.setEndProcessingTime(jobTimer.getCurrentTime());
        return jobRepository.save(persistedJob);
    }

    private void assertInitingWaitingJob(UUID jobId) {
        if (jobRepository.exists(jobId))
            throw new JobAlreadyExistsException(jobId);
    }

    private void assertJobExistsAndStatusIn(UUID jobId, Job job, JobStatus... allowedStatuses) {
        if (job == null)
            throwNotFound(jobId);
        if (!ArrayUtils.contains(allowedStatuses, job.getStatus()))
            throwIncorrectStatus(job);
    }

    private void throwIncorrectStatus(Job job) {
        throw new InvalidJobStatusException(job.getId(), job.getStatus());
    }

    private void throwNotFound(UUID jobId) {
        throw new JobDoesNotExistException(jobId);
    }
}
