package com.dmitring.trippledata.exceptions;

import com.dmitring.trippledata.domain.JobStatus;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper=false)
public class InvalidJobStatusException extends RuntimeException {
    UUID jobId;
    JobStatus jobStatus;
}
