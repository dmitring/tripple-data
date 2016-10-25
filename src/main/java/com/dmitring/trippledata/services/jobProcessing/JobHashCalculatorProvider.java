package com.dmitring.trippledata.services.jobProcessing;

import com.dmitring.trippledata.domain.HashingAlgorithm;

import java.security.MessageDigest;

public interface JobHashCalculatorProvider {
    MessageDigest getJobHashCalculator(HashingAlgorithm hashingAlgorithm);
}
