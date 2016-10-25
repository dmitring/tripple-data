package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.Job;

import java.util.concurrent.CompletableFuture;

public interface JobProcessService {
    CompletableFuture<Job> processJob(Job job);
    CompletableFuture<Job> cancelJob(Job job);
}
