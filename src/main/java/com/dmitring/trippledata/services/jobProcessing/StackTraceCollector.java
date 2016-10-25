package com.dmitring.trippledata.services.jobProcessing;

public interface StackTraceCollector {
    String getStackTrace(Throwable throwable);
}
