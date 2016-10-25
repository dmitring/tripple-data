package com.dmitring.trippledata.domain;

public enum HashingAlgorithm {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256");

    private String algorithmName;

    public String getAlgorithmName() {
        return algorithmName;
    }

    HashingAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }
}
