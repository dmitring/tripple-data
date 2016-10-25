package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.Job;

import java.util.function.Supplier;

public interface JobProcessor {
    String execute(Job job, Supplier<Boolean> jobStopCondition);
}
