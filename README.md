### Job Management Service

#### Introduction: 

Purpose of this service to manage any kind of execution and manage those stats, life cycle.
The Job can be executed on priority basis and or also can be scheduled at particular time.

This service expose REST api to submit the jobs to execute and get the stats.

#### Features:
1. Any kind of job can be handled. Facility to add new jobs with minimum changes.
2. Job can be schedule for future execution
3. Job can be executed on basis of priority
4. Job execution cancellation
5. List all submitted jobs

#### Technical Stack

    Java 1.8
    Maven 3.6+
    Spring boot 2.4.1.RELEASE
    MySql8
    H2 database for test cases
    Quartz for job scheduling

#### Running the application

###### Run app using docker-compose

Job service is configured with docker-compose to run with mysql 8. To run the service run the following command:
```shell script
docker-compose up
```
In docker-compose two service is defined one is for mysql8 and second one is for job service.

###### Run app using maven

```shell script
mvn spring-boot:run
```

#### Predefined Jobs:
1. Email Send Job
2. Logging job

#### REST Apis to Submit a Job:

#### Resources

##### Job Resource
 A resource or dto to exchange data over rest api.

```json
{
  "id": {
    "type": "UUID",
    "nullable": false,
    "readonly": true,
    "description": "Unique ID of the Job."
  },
  "state": {
    "type": "String/enumeration",
    "nullable": false,
    "constraints": ["SUBMITTED","SCHEDULED","QUEUED","RUNNING","SUCCESS","FAILED","CANCELED"],
    "description": "Current state of the job"
  },
  "jobType": {
    "type": "String/enumeration",
    "nullable": false,
    "constraints": ["EMAIL", "LOGGING"],
    "description": "Type of the Job"
  },
  "priority": {
    "type": "String/enumeration",
    "nullable": false,
    "default": "MEDIUM",
    "constraints": ["HIGH", "MEDIUM","LOW"],
    "description": "Priority of the job"
  },
  "rawJobData": {
    "type": "Json",
    "nullable": true,
    "description": "If any jobs need some data to process the task then data can be send via rowJobData"
  },
   "scheduledAt": {
    "type": "Date String",
    "format": "yyyy-MM-dd'T'HH:mm:ss.SSSX",
    "nullable": true,
    "description": "Job can scheduled to execute in the future using this field"
  },
   "createdAt": {
    "type": "Date String",
    "format": "yyyy-MM-dd'T'HH:mm:ss.SSSX",
    "nullable": false,
    "readOnly": true,
    "description": "Creation time of the job"
  },
   "lastModifiedAt": {
    "type": "Date String",
    "format": "yyyy-MM-dd'T'HH:mm:ss.SSSX",
    "nullable": false,
        "readOnly": true,
    "description": "Last update time on the job"
  },
  "failReason": {
    "type": "String",
    "nullable": true,
    "readOnly": true,
    "description": "If job get failed then reason of failure will be here in this field"
  }
}
```

##### 1. Submit a Job API:
To submit a job to service

###### Request
| Name | Value |
| -------- | ------- |
| URL | `/job` |
| Method | `POST` |
| Content-Type | `application/json` |
| Resource | [Job Resource](#job-resource) |

###### Response
| Name | Value |
| -------- | ------- |
| Content-Type | `application/json` |
| Resource | [Job Resource](#job-resource) |
| Success Status Code | `200 OK` |

###### Example Call
`curl --location --request POST 'http://localhost:8080/job' \
 --header 'Content-Type: application/json' \
 --data-raw '{
     "jobType": "EMAIL",
     "priority": "HIGH",
     "rawJobData": {
         "emailBody": "Email body",
         "to":"vikramsewadra@gmail.com"
     }
 }'`
###### Errors
| Status Code | Causes |
| ---------------- | --------- |
| `400 Bad Request` | Invalid request, maybe type of job invalid, missing some other attributes. |

##### 2. Get Job API:
To get a job

###### Request
| Name | Value |
| -------- | ------- |
| URL | `/job/{jobId}` |
| Method | `GET` |

###### Response
| Name | Value |
| -------- | ------- |
| Content-Type | `application/json` |
| Resource | [Job Resource](#job-resource) |
| Success Status Code | `200 OK` |

###### Example Call
`curl --location --request GET 'http://localhost:8080/job/72a24bff-06c1-4e5c-95c1-7d9314a0d13f'`

##### 3. Get All Jobs API:
To get all jobs

###### Request
| Name | Value |
| -------- | ------- |
| URL | `/job` |
| Method | `GET` |

###### Response
| Name | Value |
| -------- | ------- |
| Content-Type | `application/json` |
| Resource | Page of [Job Resource](#job-resource) |
| Success Status Code | `200 OK` |

###### Example Call
`curl --location --request GET 'http://localhost:8080/job'`

##### 4. Cancel a Job API:
To cancel an already submitted job

###### Request
| Name | Value |
| -------- | ------- |
| URL | `/job/{jobId}/cancel` |
| Method | `POST` |

###### Response
| Name | Value |
| -------- | ------- |
| Content-Type | `application/json` |
| Success Status Code | `200 OK` |

###### Example Call
`curl --location --request POST 'http://localhost:8080/job/72a24bff-06c1-4e5c-95c1-7d9314a0d13f/cancel'`

###### Errors
| Status Code | Causes |
| ---------------- | --------- |
| `400 Bad Request` | If job not exist or already started the execution. |


### Add a new job type:

1. Define a job class extending the abstract class com.learn.optile.jobs.ExecutableJob and add definition to two methods:

    --> taskDefinition : Code to perform the job
    
    --> rollback: if job fails in between then to rollback the things and make consistency

2. Add a new job type in com.learn.optile.entities.values.JobType enum.

3. If any job needed then data to be processed then it can be passed and rowJobData and can be used via job property
from ExecutableJob.

4. Update submitToRunner method in JobServiceImpl class to make instance of the new type of job on basis of job type.

##### Example 

```java
package com.learn.optile.jobs;

import com.learn.optile.entities.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Job of simple logging
 */
public class LoggingJob extends ExecutableJob {

    private static final Logger logger = LoggerFactory.getLogger(LoggingJob.class);

    public LoggingJob(ApplicationContext applicationContext, Job job) {
        super(applicationContext, job);
    }

    @Override
    public void taskDefinition() {
        //jobRawData can be access via getJob().getRawJobData()
        //To use any service in the application ApplicationContext can be used to  get the bean of service
        logger.info("Logging Job Started");
        logger.info("Logging Job completed");
    }

    @Override
    public void rollback() {
        logger.info("Logging Job roll backed");
    }
}

```


