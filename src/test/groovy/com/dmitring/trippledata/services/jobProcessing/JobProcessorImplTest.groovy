package com.dmitring.trippledata.services.jobProcessing

import com.dmitring.trippledata.domain.HashingAlgorithm
import com.dmitring.trippledata.domain.Job
import com.dmitring.trippledata.domain.JobStatus
import com.dmitring.trippledata.domain.TestJobsFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNull
import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobProcessorImplTest.class)
class JobProcessorImplTest {

    TestJobsFactory testJobFactory = new TestJobsFactory()

    JobInputStreamProvider mockJobInputStreamProvider
    JobHashCalculatorProvider jobHashCalculatorProvider
    int bufferSize = 4;

    JobProcessor jobProcessor

    @Before
    void setUp() {
        mockJobInputStreamProvider = mock(JobInputStreamProvider.class)
        jobHashCalculatorProvider = new JobHashCalculatorProviderImpl()
        jobProcessor = new JobProcessorImpl(mockJobInputStreamProvider, jobHashCalculatorProvider, bufferSize)
    }

    @Test
    void testHashes() {
        byte[] sourceBuffer = [109, 114, 6, 27, 104, 97, 107, 105, 254, 0, 12, 6, 8, 113, 128, 255]
        Map<HashingAlgorithm, String> testCases = [:]
        testCases.put(HashingAlgorithm.MD5, "8F2224983D6124184AD1727B26B598E2")
        testCases.put(HashingAlgorithm.SHA1, "BC79A89A879EF848E73DF5AE22A269CA28D74629")
        testCases.put(HashingAlgorithm.SHA256, "6E7368D18C86A93AEFF20770579F740E6E4FCDA483903C58A257B065DD5B44C2")

        testCases.each {algorithm, result -> testHash(algorithm, sourceBuffer, result)}
    }

    void testHash(HashingAlgorithm algorithm, byte[] source, String outputHexHash) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(source)
        when(mockJobInputStreamProvider.getInputStream(any(String.class))).thenReturn(inputStream)
        Job job = testJobFactory.createJobWithAlgorithmAndStatus(algorithm, JobStatus.PROCESSING)
        Supplier<Boolean> stopCondition = {false}

        String result = jobProcessor.execute(job, stopCondition)

        assertEquals(outputHexHash, result)
    }

    @Test
    void testStopOnStopCondition() {
        AtomicBoolean stopConditionVariable = new AtomicBoolean(false);
        AtomicBoolean didReadsAfterStopDetected = new AtomicBoolean(false);
        InputStream stubInputStream = new InputStream() {
            volatile int readCount = 0
            @Override
            int read() throws IOException {
                if (readCount == 10)
                    stopConditionVariable.set(true)
                if (readCount == 1010) {
                    didReadsAfterStopDetected.set(true)
                    return -1
                }
                readCount++
                Thread.sleep(1)
                return 100
            }
        }
        when(mockJobInputStreamProvider.getInputStream(any(String.class))).thenReturn(stubInputStream)
        Job someJob = testJobFactory.createJobWithAlgorithmAndStatus(HashingAlgorithm.SHA1, JobStatus.PROCESSING)
        Supplier<Boolean> stopCondition = {stopConditionVariable.get()}

        String result = jobProcessor.execute(someJob, stopCondition)

        assertFalse(didReadsAfterStopDetected.get())
        assertNull(result)
    }
}
