package com.learn.optile.services;

import com.learn.optile.dtos.JobDto;
import com.learn.optile.entities.Job;
import com.learn.optile.entities.values.JobState;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface JobService {
    Job createJob(JobDto jobDto);

    Job getJob(UUID jobId);

    Page<Job> getAllJobs(Integer page, Integer size);

    void cancelJob(UUID jobId);

    void updateJobStatus(UUID jobId, JobState jobState);

    void addJobsToQueue();

    void addJobsToQueue(UUID jobId);

    void setJobFailed(UUID id, String failReason);
}
