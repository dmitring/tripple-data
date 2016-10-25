package com.dmitring.trippledata.repositories;

import com.dmitring.trippledata.domain.Job;
import com.dmitring.trippledata.domain.JobStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface JobRepository extends CrudRepository<Job, UUID> {
    Stream<Job> findByClientIdAndStatusIn(UUID clientId, Collection<JobStatus> alloweedStatuses);
}
