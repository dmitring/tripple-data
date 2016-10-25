package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.services.jobProcessing.JobProcessingManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class JobProcessServiceImpl implements JobProcessService {

    @Data
    @AllArgsConstructor
    private static class InnerProcessingJob {
        private volatile boolean stopRequired;
        private CompletableFuture<Job> jobFuture;

        private InnerProcessingJob(CompletableFuture<Job> jobFuture) {
            this.stopRequired = false;
            this.jobFuture = jobFuture;
        }
    }

    private final ConcurrentMap<UUID, InnerProcessingJob> processingJobs;
    private final JobProcessingManager jobProcessingManager;
    private final JobStateMachine jobStateMachine;

    @Autowired
    public JobProcessServiceImpl(JobProcessingManager jobProcessingManager, JobStateMachine jobStateMachine) {
        this.jobProcessingManager = jobProcessingManager;
        this.jobStateMachine = jobStateMachine;
        this.processingJobs = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<Job> processJob(Job job) {
        return processingJobs.compute(getJobId(job), (jobId, oldJobValue) -> {
            if (oldJobValue != null)
                throw new RuntimeException("JobProcessServiceImpl.processingJobs already consists job, jobId = " + jobId);

            return new InnerProcessingJob(
                    CompletableFuture.supplyAsync(
                            () -> jobProcessingManager.process(job, () -> isJobCanceled(job) ))
                            .handle((jobResult, throwable) -> handleJobCompletion(job, jobResult, throwable)));
        }).getJobFuture();
    }

    @Override
    public CompletableFuture<Job> cancelJob(Job job) {
        final InnerProcessingJob innerProcessingJob = processingJobs.get(getJobId(job));
        if (innerProcessingJob == null)
            return null;
        innerProcessingJob.setStopRequired(true);
        return innerProcessingJob.getJobFuture();
    }

    private Job handleJobCompletion(Job job, String jobResult, Throwable throwable) {
        final InnerProcessingJob innerProcessingJob = processingJobs.remove(getJobId(job));
        final boolean canceled = innerProcessingJob.isStopRequired();
        final UUID jobId = job.getId();
        final Job result;
        if (canceled) {
            result = jobStateMachine.turnToCancelled(jobId);
        } else if (throwable != null) {
            result = jobStateMachine.turnToFailed(jobId, throwable);
        } else {
            result = jobStateMachine.turnToCompleted(jobId, jobResult);
        }

        return result;
    }

    private UUID getJobId(Job job) {
        return job.getId();
    }

    private Boolean isJobCanceled(Job job) {
        return processingJobs.get(getJobId(job)).isStopRequired();
    }
}
