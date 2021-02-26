package com.learn.optile;

import com.learn.optile.dtos.JobDto;
import com.learn.optile.entities.values.JobPriority;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.entities.values.JobType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class JobTestCases extends AbstractBaseSpec {

    @Test
    @DisplayName("Submit Job Test Case")
    public void submitJobTestCase() {
        JobDto body = new JobDto();
        body.setJobType(JobType.LOGGING);
        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("state", equalTo(JobState.SUBMITTED.toString()))
                .body("priority", equalTo("MEDIUM"))
                .body("jobType", equalTo("LOGGING"));
    }

    @Test
    @DisplayName("Submit a Job Without Job Type")
    public void submitInvalidJob() {
        JobDto body = new JobDto();
        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("timestamp", notNullValue())
                .body("status", equalTo("BAD_REQUEST"));
    }

    @Test
    @DisplayName("Submit a High Priority Job to start immediate execution")
    public void submitHighPriorityJob() {
        JobDto body = new JobDto();
        body.setJobType(JobType.LOGGING);
        body.setPriority(JobPriority.HIGH);
        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("state", equalTo(JobState.QUEUED.toString()))
                .body("priority", equalTo(JobPriority.HIGH.toString()))
                .body("jobType", equalTo("LOGGING"));
    }

    @Test
    @DisplayName("Submit a Scheduled Job")
    public void submitAScheduledJob() {
        JobDto body = new JobDto();
        body.setJobType(JobType.EMAIL);
        body.setPriority(JobPriority.HIGH);
        body.setScheduledAt(Instant.now().plusSeconds(1000L));
        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("state", equalTo(JobState.SCHEDULED.toString()))
                .body("priority", equalTo(JobPriority.HIGH.toString()))
                .body("jobType", equalTo("EMAIL"));
    }

    @Test
    @DisplayName("Check complete execution of a Job")
    public void completeExecutionOfAJob() {
        JobDto body = new JobDto();
        body.setJobType(JobType.EMAIL);
        Map<String,Object> rawJobData = new HashMap<>();
        rawJobData.put("emailBody","Email Body");
        rawJobData.put("emailSubject","Email Subject");
        rawJobData.put("to","test.email@gmail.com");
        body.setRawJobData(rawJobData);
        JobDto response = RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(JobDto.class);

        /**
         * Wait for 1 Minute so Quartz @Link {@link com.learn.optile.scheduler.PutJobsInQueueJob}
         * submit the job to runner
         */
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean jobCompleted = false;

        while (!jobCompleted) {
            //Check status of job until job completed
            JobDto intermediateJobResponse = RestAssured.with()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .log().all()
                    .when()
                    .get("/job/" + response.getId())
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(JobDto.class);
            if (intermediateJobResponse.getState() == JobState.SUCCESS || intermediateJobResponse.getState() == JobState.FAILED) {
                jobCompleted = true;
            } else {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RestAssured.with()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .get("/job/" + response.getId())
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("state", equalTo(JobState.SUCCESS.toString()));

    }

    @Test
    @DisplayName("Cancel a submitted Job")
    public void cancelSubmittedJob() {

        //Setup
        JobDto body = new JobDto();
        body.setJobType(JobType.EMAIL);
        JobDto response = RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(JobDto.class);

        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(JobDto.class);

        //Cancel Job
        RestAssured.with()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .post("/job/" + response.getId() + "/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        //Check Status of job
        RestAssured.with()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when()
                .get("/job/" + response.getId())
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("state", equalTo(JobState.CANCELED.toString()));

    }
}
