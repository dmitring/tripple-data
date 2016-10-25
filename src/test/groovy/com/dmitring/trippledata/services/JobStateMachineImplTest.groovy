package com.dmitring.trippledata.services

import com.dmitring.trippledata.domain.Job
import com.dmitring.trippledata.domain.JobStatus
import com.dmitring.trippledata.domain.TestJobsFactory
import com.dmitring.trippledata.repositories.JobRepository
import com.dmitring.trippledata.services.jobProcessing.JobTimer
import com.dmitring.trippledata.services.jobProcessing.StackTraceCollector
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class JobStateMachineImplTest {

    TestJobsFactory testJobsFactory = new TestJobsFactory()

    JobRepository mockJobRepository
    JobTimer mockJobTime
    long currentTime;
    StackTraceCollector mockCollector

    JobStateMachine jobStateMachine

    @Before
    void setUp() {
        mockJobRepository = mock(JobRepository.class)

        mockJobTime = mock(JobTimer.class)
        currentTime = 48230;
        when(mockJobTime.getCurrentTime()).thenReturn(currentTime)

        mockCollector = mock(StackTraceCollector.class)

        jobStateMachine = new JobStateMachineImpl(mockJobRepository, mockJobTime, mockCollector)
    }

    @Test
    void testInitWaitingJob() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.WAITING)

        // act
        Job resultJob = jobStateMachine.initWaitingJob(job.id, job.clientId, job.sourceUri, job.hashingAlgorithm)

        // assert
        assertEquals(job.id, resultJob.id)
        assertEquals(job.clientId, resultJob.clientId)
        assertEquals(job.hashingAlgorithm, resultJob.hashingAlgorithm)
        assertEquals(JobStatus.WAITING, resultJob.status)
        assertEquals(job.sourceUri, resultJob.sourceUri)
        assertEquals(currentTime, resultJob.arrivedTime)
        verify(mockJobRepository).save(eq(resultJob))
    }

    @Test
    void testTurnToProcessing() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.WAITING)
        when(mockJobRepository.findOne(eq(job.id))).thenReturn(job)
        when(mockJobRepository.save(eq(job))).thenAnswer({invocation -> invocation.getArguments()[0]})

        // act
        Job resultJob = jobStateMachine.turnToProcessing(job.id)

        // assert
        assertEquals(job.id, resultJob.id)
        assertEquals(job.clientId, resultJob.clientId)
        assertEquals(job.hashingAlgorithm, resultJob.hashingAlgorithm)
        assertEquals(JobStatus.PROCESSING, resultJob.status)
        assertEquals(job.sourceUri, resultJob.sourceUri)
        assertEquals(currentTime, resultJob.startProcessingTime)
        verify(mockJobRepository).save(eq(resultJob))
    }

    @Test
    void testTurnToCompleted() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.PROCESSING)
        job.hexHash = "some hexed hash"
        when(mockJobRepository.findOne(eq(job.id))).thenReturn(job)
        when(mockJobRepository.save(eq(job))).thenAnswer({invocation -> invocation.getArguments()[0]})

        // act
        Job resultJob = jobStateMachine.turnToCompleted(job.id, job.hexHash)

        // assert
        assertEquals(job.id, resultJob.id)
        assertEquals(job.clientId, resultJob.clientId)
        assertEquals(job.hashingAlgorithm, resultJob.hashingAlgorithm)
        assertEquals(JobStatus.COMPLETED, resultJob.status)
        assertEquals(job.sourceUri, resultJob.sourceUri)
        assertEquals(job.hexHash, resultJob.hexHash)
        assertEquals(currentTime, resultJob.endProcessingTime)
        verify(mockJobRepository).save(eq(resultJob))
    }

    @Test
    void testTurnToFailed() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.PROCESSING)
        Throwable someError = new RuntimeException()
        job.stackTrace = "some stack trace"
        when(mockCollector.getStackTrace(eq(someError))).thenReturn(job.stackTrace)

        when(mockJobRepository.findOne(eq(job.id))).thenReturn(job)
        when(mockJobRepository.save(eq(job))).thenAnswer({invocation -> invocation.getArguments()[0]})

        // act
        Job resultJob = jobStateMachine.turnToFailed(job.id, someError)

        // assert
        assertEquals(job.id, resultJob.id)
        assertEquals(job.clientId, resultJob.clientId)
        assertEquals(job.hashingAlgorithm, resultJob.hashingAlgorithm)
        assertEquals(JobStatus.FAILED, resultJob.status)
        assertEquals(job.sourceUri, resultJob.sourceUri)
        assertEquals(job.stackTrace, resultJob.stackTrace)
        assertEquals(currentTime, resultJob.endProcessingTime)
        verify(mockJobRepository).save(eq(resultJob))
    }

    @Test
    void testTurnToCancelled() {
        // arrange
        Job job = testJobsFactory.createJobWithStatus(JobStatus.PROCESSING)
        when(mockJobRepository.findOne(eq(job.id))).thenReturn(job)
        when(mockJobRepository.save(eq(job))).thenAnswer({invocation -> invocation.getArguments()[0]})

        // act
        Job resultJob = jobStateMachine.turnToCancelled(job.id,)

        // assert
        assertEquals(job.id, resultJob.id)
        assertEquals(job.clientId, resultJob.clientId)
        assertEquals(job.hashingAlgorithm, resultJob.hashingAlgorithm)
        assertEquals(JobStatus.CANCELED, resultJob.status)
        assertEquals(job.sourceUri, resultJob.sourceUri)
        assertEquals(job.hexHash, resultJob.hexHash)
        assertEquals(currentTime, resultJob.endProcessingTime)
        verify(mockJobRepository).save(eq(resultJob))
    }
}
