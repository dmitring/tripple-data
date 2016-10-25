package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.services.JobStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class JobProcessingManagerImpl implements JobProcessingManager {
    private final JobProcessor jobProcessor;
    private final JobStateMachine jobStateMachine;

    @Autowired
    public JobProcessingManagerImpl(JobProcessor jobProcessor, JobStateMachine jobStateMachine) {
        this.jobProcessor = jobProcessor;
        this.jobStateMachine = jobStateMachine;
    }

    @Override
    public String process(Job job, Supplier<Boolean> jobStopCondition) {
        job = jobStateMachine.turnToProcessing(job.getId());
        if (jobStopCondition.get())
            return null;

        return jobProcessor.execute(job, jobStopCondition);
    }
}
