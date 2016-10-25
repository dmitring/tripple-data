package com.dmitring.trippledata.services

import com.dmitring.trippledata.controllers.messages.JobResponse
import com.dmitring.trippledata.domain.Job
import com.dmitring.trippledata.domain.JobStatus
import com.dmitring.trippledata.domain.TestJobsFactory
import com.dmitring.trippledata.repositories.JobRepository
import com.dmitring.trippledata.services.jobProcessing.JobTimer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.mockito.Mockito.*
import static org.junit.Assert.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobProcessServiceImplTest.class)
class JobDisplayServiceImplTest {

    TestJobsFactory testJobsFactory = new TestJobsFactory()

    JobRepository jobRepository
    long currentTime = 100000
    JobTimer jobTimer
    JobDisplayService jobDisplayService

    @Before
    void setUp() {
        jobRepository = mock(JobRepository.class)
        jobTimer = mock(JobTimer.class)
        when(jobTimer.getCurrentTime()).thenReturn(currentTime)
        jobDisplayService = new JobDisplayServiceImpl(jobRepository, jobTimer)
    }

    @Test
    void testWaitingJob() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.WAITING)
        job.arrivedTime = 10000

        // act
        JobResponse jobReponse = jobDisplayService.getJobResponse(job)

        // assert
        assertEquals(0, jobReponse.processingTime)
        assertEquals(currentTime - job.arrivedTime, jobReponse.totalWaitTime)
    }

    @Test
    void testProcessingJob() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.PROCESSING)
        job.arrivedTime = 10000
        job.startProcessingTime = 50000

        // act
        JobResponse jobReponse = jobDisplayService.getJobResponse(job)

        // assert
        assertEquals(currentTime - job.startProcessingTime, jobReponse.processingTime)
        assertEquals(currentTime - job.arrivedTime, jobReponse.totalWaitTime)
    }

    @Test
    void testFinishedJob() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.COMPLETED) // also may be FAILED or CANCELED
        job.arrivedTime = 10000
        job.startProcessingTime = 50000
        job.endProcessingTime = 75000

        // act
        JobResponse jobReponse = jobDisplayService.getJobResponse(job)

        // assert
        assertEquals(job.endProcessingTime - job.startProcessingTime, jobReponse.processingTime)
        assertEquals(job.endProcessingTime - job.arrivedTime, jobReponse.totalWaitTime)
    }
}
