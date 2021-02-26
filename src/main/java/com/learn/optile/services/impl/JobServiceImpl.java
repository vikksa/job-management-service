package com.learn.optile.services.impl;

import com.learn.optile.dtos.JobDto;
import com.learn.optile.entities.Job;
import com.learn.optile.entities.values.JobPriority;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.exceptions.BadRequestException;
import com.learn.optile.exceptions.NotFoundException;
import com.learn.optile.jobs.EmailJob;
import com.learn.optile.jobs.ExecutableJob;
import com.learn.optile.jobs.LoggingJob;
import com.learn.optile.repo.JobRepository;
import com.learn.optile.runner.JobRunner;
import com.learn.optile.scheduler.ScheduledJob;
import com.learn.optile.scheduler.SchedulerService;
import com.learn.optile.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobRunner jobRunner;
    private final ApplicationContext applicationContext;
    private final SchedulerService schedulerService;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, JobRunner jobRunner, SchedulerService schedulerService,
                          ApplicationContext applicationContext) {
        this.jobRepository = jobRepository;
        this.jobRunner = jobRunner;
        this.schedulerService = schedulerService;
        this.applicationContext = applicationContext;
    }

    @Override
    public Job createJob(JobDto jobDto) {
        Job job = new Job();
        if (jobDto.getJobType() == null) {
            throw new BadRequestException("Job type can't be empty");
        }
        job.setJobType(jobDto.getJobType());
        if (jobDto.getPriority() != null) {
            job.setPriority(jobDto.getPriority());
        }
        job.setRawJobData(jobDto.getRawJobData());
        if (jobDto.getScheduledAt() != null) {
            if (jobDto.getScheduledAt().isBefore(Instant.now())) {
                throw new BadRequestException("Job schedule time can't be of past");
            }
            job.setScheduledAt(jobDto.getScheduledAt());
            job.setState(JobState.SCHEDULED);
        }
        Job save = jobRepository.save(job);
        if (save.getState() == JobState.SCHEDULED) {
            schedulerService.scheduleJob(new ScheduledJob(save.getId(), Date.from(save.getScheduledAt())));
        }
        //If Job Priority high then add in queue immediately
        if (save.getState() != JobState.SCHEDULED && save.getPriority() == JobPriority.HIGH) {
            submitToRunner(save);
        }
        return save;
    }

    @Override
    public Job getJob(UUID jobId) {
        return jobRepository.findById(jobId).orElseThrow(() ->
                new NotFoundException(String.format("Job not found with id <%s>", jobId)));
    }

    @Override
    public Page<Job> getAllJobs(Integer page, Integer size) {
        return jobRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public void cancelJob(UUID jobId) {
        Job job = getJob(jobId);
        if (job.getState() == JobState.SUBMITTED || job.getState() == JobState.SCHEDULED ||
                job.getState() == JobState.QUEUED) {
            job.setState(JobState.CANCELED);
            jobRepository.save(job);
        } else {
            throw new BadRequestException(String.format("Job can't be canceled now. Already in state %s", job.getState()));
        }
    }

    @Override
    public void updateJobStatus(UUID jobId, JobState jobState) {
        Job job = getJob(jobId);
        job.setState(jobState);
        jobRepository.save(job);
    }

    @Override
    public void setJobFailed(UUID jobId, String failReason) {
        Job job = getJob(jobId);
        job.setState(JobState.FAILED);
        job.setFailReason(failReason);
        jobRepository.save(job);
    }

    @Override
    public void addJobsToQueue() {
        Page<Job> unPickedJobs = jobRepository.findAllByStateOrderByPriorityAsc(JobState.SUBMITTED, PageRequest.of(0, 10));
        unPickedJobs.forEach(this::submitToRunner);
    }

    @Override
    public void addJobsToQueue(UUID jobId) {
        Job job = getJob(jobId);
        if (job.getState() != JobState.SCHEDULED) {
            return;
        }
        job.setState(JobState.SUBMITTED);
        Job save = jobRepository.save(job);
        submitToRunner(save);
    }


    private void submitToRunner(Job job) {
        ExecutableJob executableJob = null;
        switch (job.getJobType()) {
            case EMAIL:
                executableJob = new EmailJob(applicationContext, job);
                break;
            case LOGGING:
                executableJob = new LoggingJob(applicationContext, job);
                break;
        }
        jobRunner.queueTask(executableJob);
    }
}
