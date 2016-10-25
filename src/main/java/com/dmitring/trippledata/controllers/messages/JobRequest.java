package com.dmitring.trippledata.controllers.messages;

import com.dmitring.trippledata.domain.HashingAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JobRequest {
    @NotNull
    private UUID id;

    @NotNull
    private UUID clientId;

    @NotEmpty
    @URL
    private String sourceUri;

    @NotNull
    private HashingAlgorithm hashingAlgorithm;
}
