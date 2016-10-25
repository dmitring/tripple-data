package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class JobHashCalculatorProviderImpl implements JobHashCalculatorProvider {
    @Override
    @SneakyThrows
    public MessageDigest getJobHashCalculator(HashingAlgorithm hashingAlgorithm) {
        return MessageDigest.getInstance(hashingAlgorithm.getAlgorithmName());
    }
}
