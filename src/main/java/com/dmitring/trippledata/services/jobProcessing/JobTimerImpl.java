package com.dmitring.trippledata.services.jobProcessing;

import org.springframework.stereotype.Component;

@Component
public class JobTimerImpl implements JobTimer {
    @Override
    public long getCurrentTime() {
        return System.nanoTime();
    }
}
