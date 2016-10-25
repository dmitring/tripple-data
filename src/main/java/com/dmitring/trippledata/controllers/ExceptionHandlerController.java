package com.dmitring.trippledata.controllers;

import com.dmitring.trippledata.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class ExceptionHandlerController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ClientAlreadyExistsException.class)
    public String handleBaseException(ClientAlreadyExistsException e){
        return String.format("Client with present id already exists, clientId = %s", e.getClientId().toString());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ClientDoesNotExistException.class)
    public String handleBaseException(ClientDoesNotExistException e){
        return String.format("Client with present id does not exist, clientId = %s", e.getClientId().toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = JobAlreadyExistsException.class)
    public String handleBaseException(JobAlreadyExistsException e){
        return String.format("Job with present id already exists, jobId = %s", e.getJobId().toString());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = JobDoesNotExistException.class)
    public String handleBaseException(JobDoesNotExistException e){
        return String.format("Job with present id does not exist, jobId = %s", e.getJobId().toString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = InvalidJobStatusException.class)
    public String handleBaseException(InvalidJobStatusException e){
        return String.format("Internal server error: Unexpected job status, jobId = %s, jobStatus = %s",
                e.getJobId().toString(), e.getJobStatus().toString());
    }
}
