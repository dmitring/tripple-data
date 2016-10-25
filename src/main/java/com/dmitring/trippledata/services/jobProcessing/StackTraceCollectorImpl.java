package com.dmitring.trippledata.services.jobProcessing;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

@Component
public class StackTraceCollectorImpl implements StackTraceCollector {
    @Override
    public String getStackTrace(Throwable throwable) {
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        return ExceptionUtils.getStackTrace(throwable);
    }
}
