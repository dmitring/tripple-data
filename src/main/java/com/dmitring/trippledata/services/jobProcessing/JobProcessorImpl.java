package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.Job;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.function.Supplier;

@Component
public class JobProcessorImpl implements JobProcessor {
    private final JobInputStreamProvider jobInputStreamProvider;
    private final JobHashCalculatorProvider jobHashCalculatorProvider;
    private final int bufferSize;

    public JobProcessorImpl(
            JobInputStreamProvider jobInputStreamProvider,
            JobHashCalculatorProvider jobHashCalculatorProvider,
            @Value("${com.dmitring.trippledata.inputBufferSize}") int bufferSize) {
        this.jobInputStreamProvider = jobInputStreamProvider;
        this.jobHashCalculatorProvider = jobHashCalculatorProvider;
        this.bufferSize = bufferSize;

        if (bufferSize <= 0)
            throw new IllegalArgumentException("bufferSize must be greater than 0");
    }

    @Override
    @SneakyThrows
    public String execute(Job job, Supplier<Boolean> jobStopCondition) {
        if (jobStopCondition.get())
            return null;

        try (final InputStream inputStream = jobInputStreamProvider.getInputStream(job.getSourceUri())) {
            final MessageDigest jobHashCalculator = jobHashCalculatorProvider.getJobHashCalculator(job.getHashingAlgorithm());
            return executeRoutine(inputStream, jobHashCalculator, jobStopCondition);
        }
    }

    @SneakyThrows
    private String executeRoutine(InputStream inputStream, MessageDigest jobHashCalculator, Supplier<Boolean> jobStopCondition) {
        byte[] buffer = new byte[bufferSize];

        for (int writtenBytes = 0; writtenBytes >= 0; writtenBytes = inputStream.read(buffer))
        {
            jobHashCalculator.update(buffer, 0, writtenBytes);
            if (jobStopCondition.get())
                return null;
        }

        if (jobStopCondition.get())
            return null;

        return DatatypeConverter.printHexBinary(jobHashCalculator.digest());
    }
}
