package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import com.dmitring.trippledata.domain.Job;

import java.util.UUID;

public interface JobActionsManagerService {
    Job addJob(UUID jobId, UUID clientId, String sourceUrl, HashingAlgorithm hashingAlgorithm);
    Job cancelJob(UUID jobId);
}
