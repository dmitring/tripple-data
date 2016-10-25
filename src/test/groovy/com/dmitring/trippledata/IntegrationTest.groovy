package com.dmitring.trippledata

import com.dmitring.trippledata.controllers.messages.JobRequest
import com.dmitring.trippledata.controllers.messages.JobResponse
import com.dmitring.trippledata.domain.HashingAlgorithm
import com.dmitring.trippledata.domain.JobStatus
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.assertEquals

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    private final String nonExistingCorrectUrl = "ftp://localhost:31212/segfiwgh87qwdghw4iry3i/sdvfsdcv"

    @Autowired
    private TestRestTemplate restTemplate

    @Test
    void testRegisterClient() {
        UUID sentClientId = UUID.randomUUID()

        ResponseEntity<UUID> responseEntity =
                restTemplate.postForEntity("/clients/register", sentClientId, UUID.class)

        UUID givenClientId = responseEntity.getBody()

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        assertEquals(sentClientId, givenClientId)
    }

    @Test
    void testRegisterSameClient() {
        UUID firstSentClientId = registerClient()
        UUID secondSentClientId = firstSentClientId

        ResponseEntity<String> secondResponseEntity =
                restTemplate.postForEntity("/clients/register", secondSentClientId, String.class)

        assertEquals(HttpStatus.BAD_REQUEST, secondResponseEntity.getStatusCode())
    }

    @Test
    void testCancelNonExistingJob() {
        UUID nonExistedJobId = UUID.randomUUID()

        ResponseEntity<String> jobCancelResponse =
                restTemplate.postForEntity("/jobs/cancel", nonExistedJobId, String.class)

        assertEquals(HttpStatus.NOT_FOUND, jobCancelResponse.getStatusCode())
    }

    @Test
    void testAddJobFromUnknownClient() {
        JobRequest jobRequest = new JobRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                nonExistingCorrectUrl,
                HashingAlgorithm.SHA1
        )

        ResponseEntity<String> jobAddResponse =
                restTemplate.postForEntity("/jobs/add", jobRequest, String.class)

        assertEquals(HttpStatus.NOT_FOUND, jobAddResponse.getStatusCode())
    }

    @Test
    void testAddJob() {
        JobRequest jobRequest = new JobRequest(
                UUID.randomUUID(),
                registerClient(),
                nonExistingCorrectUrl,
                HashingAlgorithm.SHA1
        )

        ResponseEntity<JobResponse> jobAddResponse =
                restTemplate.postForEntity("/jobs/add", jobRequest, JobResponse.class)
        JobResponse jobResponse = jobAddResponse.getBody()

        assertEquals(HttpStatus.OK, jobAddResponse.getStatusCode())
        assertEquals(jobRequest.id, jobResponse.id)
        assertEquals(jobRequest.clientId, jobResponse.clientId)
        assertEquals(jobRequest.hashingAlgorithm, jobResponse.hashingAlgorithm)
        assertEquals(jobRequest.sourceUri, jobResponse.sourceUri)
    }

    @Test
    void testCreateJobWithKnownId() {
        JobRequest firstRequest = new JobRequest(
                UUID.randomUUID(),
                registerClient(),
                nonExistingCorrectUrl,
                HashingAlgorithm.SHA1
        )
        JobRequest secondRequest = firstRequest;

        restTemplate.postForEntity("/jobs/add", firstRequest, JobResponse.class)
        ResponseEntity<String> secondResponse =
                restTemplate.postForEntity("/jobs/add", secondRequest, String.class)

        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode())
    }

    @Test
    void addAndGetCompletedTest() {
        UUID firstClientId = registerClient()
        UUID secondClientId = registerClient()

        File file = new ClassPathResource("testFile.txt").getFile();
        String fileUri = file.toURI()

        Map<HashingAlgorithm, JobRequest> requests = [:]
        HashingAlgorithm.values().each {algorithm ->
            requests.put(algorithm, new JobRequest(
                    UUID.randomUUID(),
                    secondClientId,
                    fileUri,
                    algorithm
            ))
        }

        Map<String, String> etalonHashes = [:]
        etalonHashes.put(HashingAlgorithm.MD5.toString(), "D4F95DE86B75859D61957BC89E5C2073")
        etalonHashes.put(HashingAlgorithm.SHA1.toString(), "3AD6B6D033A04BF18088DC4E573DA1A79DE921BC")
        etalonHashes.put(HashingAlgorithm.SHA256.toString(), "351FF800AE8D7CE01B46F188191FADDA1D1EBFBE63648136AF27521A5795BBD4")

        // act
        requests.each {alg, req ->
            [alg, restTemplate.postForEntity("/jobs/add", req, JobRequest.class)]
        }

        // wait processing for 1 second
        Thread.sleep(1000)

        Collection<JobResponse> jobs = restTemplate.getForEntity("/jobs?clientId={clientId}&alloweedStatuses={alloweedStatuses}", List.class,
                ["clientId": secondClientId, "alloweedStatuses": JobStatus.values()]).body

        // assert
        jobs.each {jobResponse ->
            assertEquals(etalonHashes.get(jobResponse.hashingAlgorithm), jobResponse.hexHash)
            assertEquals(JobStatus.COMPLETED.toString(), jobResponse.status)
        }
    }

    @Test
    void addAndGetFailedAndCancelled() {
        UUID firstClientId = registerClient()
        UUID secondClientId = registerClient()

        UUID firstJobId = UUID.randomUUID();
        UUID secondJobId = UUID.randomUUID();
        Map<UUID, JobRequest> requestMap = [firstJobId, secondJobId].collectEntries { jobId ->
            [jobId: new JobRequest(jobId, secondClientId, nonExistingCorrectUrl, HashingAlgorithm.SHA256)]
        }

        // act
        requestMap.each { jobId, req ->
            restTemplate.postForEntity("/jobs/add", req, JobRequest.class)
        }
        restTemplate.postForEntity("/jobs/cancel", secondJobId, JobResponse.class)
        // wait processing for 1 second
        Thread.sleep(1000)
        Collection<JobResponse> jobs = restTemplate.getForEntity("/jobs?clientId={clientId}&alloweedStatuses={alloweedStatuses}", List.class,
                ["clientId": secondClientId, "alloweedStatuses": JobStatus.values()]).body

        // assert
        jobs.each { jobResponse ->
            if (jobResponse.id == firstJobId)
                assertEquals(JobStatus.FAILED.toString(), jobResponse.status)
            else
                assertEquals(JobStatus.CANCELED.toString(), jobResponse.status)
        }
    }

    UUID registerClient() {
        UUID sentClientId = UUID.randomUUID()
        return restTemplate.postForEntity("/clients/register", sentClientId, UUID.class).getBody()
    }
}
