package com.dmitring.trippledata.services;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.exceptions.ClientDoesNotExistException;
import com.dmitring.trippledata.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JobActionsManagerServiceImpl implements JobActionsManagerService {
    private final ClientRepository clientRepository;
    private final JobProcessService jobProcessService;
    private final JobStateMachine jobStateMachine;

    @Autowired
    public JobActionsManagerServiceImpl(ClientRepository clientRepository,
                                        JobProcessService jobProcessService,
                                        JobStateMachine jobStateMachine) {
        this.clientRepository = clientRepository;
        this.jobProcessService = jobProcessService;
        this.jobStateMachine = jobStateMachine;
    }

    @Override
    public Job addJob(UUID jobId, UUID clientId, String sourceUrl, HashingAlgorithm hashingAlgorithm) {
        if (!clientRepository.exists(clientId))
            throw new ClientDoesNotExistException(clientId);
        final Job job = jobStateMachine.initWaitingJob(jobId, clientId, sourceUrl, hashingAlgorithm);
        jobProcessService.processJob(job);
        return job;
    }

    @Override
    public Job cancelJob(UUID jobId) {
        final Job job = jobStateMachine.turnToCancelled(jobId);
        jobProcessService.cancelJob(job);
        return job;
    }
}
