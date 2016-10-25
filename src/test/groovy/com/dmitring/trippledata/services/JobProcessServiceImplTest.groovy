package com.dmitring.trippledata.services

import com.dmitring.trippledata.domain.Job
import com.dmitring.trippledata.domain.TestJobsFactory
import com.dmitring.trippledata.services.jobProcessing.JobProcessingManager
import com.dmitring.trippledata.utils.AssertFutureUtil
import com.dmitring.trippledata.utils.ThrowableUtil
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Function
import java.util.function.Supplier

import static org.junit.Assert.*
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class JobProcessServiceImplTest {

    TestJobsFactory testJobFactory = new TestJobsFactory()

    JobProcessingManager mockJobProcessingManager
    JobStateMachine mockJobStateMachine
    JobProcessService jobProcessService

    @Before
    void setUp() {
        mockJobProcessingManager = mock(JobProcessingManager.class)
        mockJobStateMachine = mock(JobStateMachine.class)
        jobProcessService = new JobProcessServiceImpl(mockJobProcessingManager, mockJobStateMachine)
    }

    @Test
    void testJobActuallyProcessed() {
        // arrange
        Job job = testJobFactory.createJob()

        // act
        AssertFutureUtil.get(jobProcessService.processJob(job), 1000)

        // assert
        verify(mockJobProcessingManager, times(1)).process(any(Job.class), any(Function.class))
        verify(mockJobProcessingManager, times(1)).process(eq(job), any(Function.class))
    }

    @Test
    void testSuccessfullyProcessed() {
        // arrange
        Job job = testJobFactory.createJob()

        String someHashResult = "someHash"
        when(mockJobProcessingManager.process(eq(job), any(Function.class))).thenReturn(someHashResult)

        Job completedJob = testJobFactory.createJob()
        when(mockJobStateMachine.turnToCompleted(any(UUID.class), any(String.class))).thenReturn(completedJob)

        // act
        Job resultJob = AssertFutureUtil.get(jobProcessService.processJob(job), 1000)

        // assert
        verify(mockJobStateMachine, times(1)).turnToCompleted(any(UUID.class), any(String.class))
        verify(mockJobStateMachine, times(1)).turnToCompleted(eq(job.id), eq(someHashResult))

        assertEquals(completedJob.id, resultJob.id)
    }

    @Test
    void testJobProcessedFailed() {
        // arrange
        Throwable throwable = new IOException("some exception")
        Job job = testJobFactory.createJob()
        when(mockJobProcessingManager.process(eq(job), any(Function.class))).thenAnswer({throw throwable})

        Job failedJob = testJobFactory.createJob()
        when(mockJobStateMachine.turnToFailed(any(UUID.class), any(Throwable.class))).thenReturn(failedJob)

        // act
        Job resultJob = AssertFutureUtil.get(jobProcessService.processJob(job), 1000)

        // assert
        ArgumentCaptor<Throwable> throwableArgumentCaptor = new ArgumentCaptor<>()
        verify(mockJobStateMachine, times(1)).turnToFailed(any(UUID.class), any(Throwable.class))
        verify(mockJobStateMachine, times(1)).turnToFailed(eq(job.id), throwableArgumentCaptor.capture())
        Throwable presentThrowable = throwableArgumentCaptor.value
        ThrowableUtil.assertCausesConains(throwable, presentThrowable)
        assertEquals(failedJob.id, resultJob.id)
    }

    @Test
    void testCancellationProcessed() {
        // arrange
        Lock waitForCancel = new ReentrantLock()
        waitForCancel.lock()
        Job job = testJobFactory.createJob()

        Job cancelledJob = testJobFactory.createJob()
        when(mockJobStateMachine.turnToCancelled(any(UUID.class))).thenReturn(cancelledJob)

        JobProcessingManager stubJobProcessor = new JobProcessingManager() {
            public volatile Boolean stopConditionResult
            @Override
            String process(Job sourceJob, Supplier<Boolean> jobStopCondition) {
                waitForCancel.lock()
                stopConditionResult = jobStopCondition.get()
                waitForCancel.unlock()
                return null
            }
        }
        jobProcessService = new JobProcessServiceImpl(stubJobProcessor, mockJobStateMachine)

        // act
        CompletableFuture<Job> jobResultFuture = jobProcessService.processJob(job)
        CompletableFuture<Job> jobCancelFuture = jobProcessService.cancelJob(job)
        waitForCancel.unlock()
        AssertFutureUtil.get(jobResultFuture, 1000)
        AssertFutureUtil.get(jobCancelFuture, 0)

        // assert
        assertTrue(stubJobProcessor.stopConditionResult)
        verify(mockJobStateMachine, times(1)).turnToCancelled(any(UUID.class))
        verify(mockJobStateMachine, times(1)).turnToCancelled(eq(job.id))
        assertEquals(jobResultFuture, jobCancelFuture)
        AssertFutureUtil.getAndAssert(jobResultFuture, cancelledJob, 1000)
    }

    @Test
    void testCancelNoeExisting() {
        // arrange
        Job job = testJobFactory.createJob()

        // act
        CompletableFuture<Job> resultFuture = jobProcessService.cancelJob(job)

        // assert
        assertNull(resultFuture)
    }
}
