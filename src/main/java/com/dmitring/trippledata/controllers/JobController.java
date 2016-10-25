package com.dmitring.trippledata.controllers;

import com.dmitring.trippledata.controllers.messages.JobRequest;
import com.dmitring.trippledata.controllers.messages.JobResponse;
import com.dmitring.trippledata.domain.JobStatus;
import com.dmitring.trippledata.services.JobActionsManagerService;
import com.dmitring.trippledata.services.JobDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobActionsManagerService jobActionsManagerService;
    private final JobDisplayService jobDisplayService;

    @Autowired
    public JobController(JobActionsManagerService jobActionsManagerService, JobDisplayService jobDisplayService) {
        this.jobActionsManagerService = jobActionsManagerService;
        this.jobDisplayService = jobDisplayService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JobResponse addJob(@Valid @RequestBody JobRequest jobRequest) {
        return jobDisplayService.getJobResponse(
                jobActionsManagerService.addJob(jobRequest.getId(), jobRequest.getClientId(), jobRequest.getSourceUri(), jobRequest.getHashingAlgorithm()));
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public JobResponse cancelJob(@RequestBody UUID jobId) {
        return jobDisplayService.getJobResponse(jobActionsManagerService.cancelJob(jobId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<JobResponse> getJobs(@RequestParam UUID clientId, @RequestParam Collection<JobStatus> alloweedStatuses) {
        return jobDisplayService.getJobs(clientId, alloweedStatuses);
    }
}
