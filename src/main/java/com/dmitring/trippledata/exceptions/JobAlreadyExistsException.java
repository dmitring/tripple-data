package com.dmitring.trippledata.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper=false)
public class JobAlreadyExistsException extends RuntimeException {
    UUID jobId;
}
