package com.dmitring.trippledata.services.jobProcessing

import com.dmitring.trippledata.domain.Job
import com.dmitring.trippledata.domain.TestJobsFactory
import com.dmitring.trippledata.services.JobStateMachine
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.function.Supplier

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobProcessingManagerImplTest.class)
class JobProcessingManagerImplTest {

    TestJobsFactory testJobsFactory = new TestJobsFactory()

    JobProcessor mockJobProcessor
    JobStateMachine mockJobStateMachine
    JobProcessingManager jobProcessingManager

    @Before
    void setUp() {
        mockJobProcessor = mock(JobProcessor.class)
        mockJobStateMachine = mock(JobStateMachine.class)
        jobProcessingManager = new JobProcessingManagerImpl(mockJobProcessor, mockJobStateMachine)
    }

    @Test
    void testActuallyProcessed() {
        Job job = testJobsFactory.createJob()
        Job processingJob = testJobsFactory.createJob()
        Supplier<Boolean> supplier = {Boolean.FALSE}
        when(mockJobStateMachine.turnToProcessing(any(UUID.class))).thenReturn(processingJob)

        jobProcessingManager.process(job, supplier)

        verify(mockJobStateMachine, times(1)).turnToProcessing(any(UUID.class))
        verify(mockJobStateMachine, times(1)).turnToProcessing(eq(job.id))

        verify(mockJobProcessor, times(1)).execute(any(Job.class), any(Supplier.class))
        verify(mockJobProcessor, times(1)).execute(eq(processingJob), any(Supplier.class))
    }
}


