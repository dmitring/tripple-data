package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.Job;

import java.util.function.Supplier;

public interface JobProcessingManager {
    String process(Job sourceJob, Supplier<Boolean> jobStopCondition);
}
