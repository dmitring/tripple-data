package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import com.dmitring.trippledata.domain.Job;

import java.util.UUID;

public interface JobStateMachine {
    Job initWaitingJob(UUID jobId, UUID clientId, String sourceUrl, HashingAlgorithm hashingAlgorithm);
    Job turnToProcessing(UUID jobId);
    Job turnToCompleted(UUID jobId, String hexHash);
    Job turnToFailed(UUID jobId, Throwable throwable);
    Job turnToCancelled(UUID jobId);
}
