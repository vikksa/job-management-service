package com.learn.optile.entities;

import com.learn.optile.entities.values.JobPriority;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.entities.values.JobType;
import com.learn.optile.helper.jpa.HashMapConverter;
import com.learn.optile.helper.jpa.PriorityConverter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private JobState state = JobState.SUBMITTED;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Convert(converter = PriorityConverter.class)
    private JobPriority priority = JobPriority.MEDIUM;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> rawJobData;

    private Instant scheduledAt;
    private String failReason;
    @LastModifiedDate
    private Instant lastModifiedAt;
    @CreatedDate
    private Instant createdAt;

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
