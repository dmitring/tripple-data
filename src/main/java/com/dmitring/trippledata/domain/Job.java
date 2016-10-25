package com.dmitring.trippledata.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(name = "i_client_id", columnList = "clientId")})
public class Job{
    @Id
    private UUID id;

    private UUID clientId;

    @Lob
    private String sourceUri;

    private HashingAlgorithm hashingAlgorithm;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private long arrivedTime;

    private long startProcessingTime;

    private long endProcessingTime;

    private String hexHash;

    @Lob
    private String stackTrace;

    public Job(UUID id, UUID clientId, String sourceUri, HashingAlgorithm hashingAlgorithm, JobStatus jobStatus) {
        this.id = id;
        this.clientId = clientId;
        this.sourceUri = sourceUri;
        this.hashingAlgorithm = hashingAlgorithm;
        this.status = jobStatus;
    }
}
