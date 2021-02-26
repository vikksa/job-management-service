package com.learn.optile.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.learn.optile.entities.Job;
import com.learn.optile.entities.values.JobPriority;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.entities.values.JobType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobDto {

    private UUID id;
    private JobState state;
    private JobType jobType;
    private JobPriority priority;
    private Map<String, Object> rawJobData;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant scheduledAt;
    private String failReason;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant lastModifiedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant createdAt;

    public JobDto() {
    }

    public JobDto(Job entity) {
        this.id = entity.getId();
        this.state = entity.getState();
        this.jobType = entity.getJobType();
        this.priority = entity.getPriority();
        this.rawJobData = entity.getRawJobData();
        this.scheduledAt = entity.getScheduledAt();
        this.failReason = entity.getFailReason();
        this.createdAt = entity.getCreatedAt();
        this.lastModifiedAt = entity.getLastModifiedAt();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobPriority getPriority() {
        return priority;
    }

    public void setPriority(JobPriority priority) {
        this.priority = priority;
    }

    public Map<String, Object> getRawJobData() {
        return rawJobData;
    }

    public void setRawJobData(Map<String, Object> rawJobData) {
        this.rawJobData = rawJobData;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
