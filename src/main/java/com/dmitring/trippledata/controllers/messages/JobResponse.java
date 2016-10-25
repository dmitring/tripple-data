package com.dmitring.trippledata.controllers.messages;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import com.dmitring.trippledata.domain.JobStatus;
import lombok.Value;

import java.util.UUID;

@Value
public class JobResponse {
    private UUID id;
    private UUID clientId;
    private String sourceUri;
    private HashingAlgorithm hashingAlgorithm;
    private JobStatus status;
    private long arrivedTime;
    private long startProcessingTime;
    private long endProcessingTime;
    private long totalWaitTime;
    private long processingTime;
    private String hexHash;
    private String stackTrace;
}
