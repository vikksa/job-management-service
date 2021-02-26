package com.learn.optile.jobs;

import com.learn.optile.entities.Job;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.services.JobService;
import org.springframework.context.ApplicationContext;

public abstract class ExecutableJob implements Runnable, Comparable<ExecutableJob> {

    private final Job job;
    private final ApplicationContext applicationContext;

    public ExecutableJob(ApplicationContext applicationContext, Job job) {
        this.applicationContext = applicationContext;
        this.job = job;
    }

    public abstract void taskDefinition();

    public abstract void rollback();

    public void run() {
        if (getJobCurrentState() == JobState.CANCELED) {
            return;
        }
        try {
            updateStatus(JobState.RUNNING);
            taskDefinition();
            updateStatus(JobState.SUCCESS);
        } catch (Exception exception) {
            exception.printStackTrace();
            rollback();
            setJobFailed(exception.getMessage());
        }
    }

    public void updateStatus(JobState jobState) {
        JobService jobService = this.applicationContext.getBean(JobService.class);
        jobService.updateJobStatus(this.job.getId(), jobState);
    }

    public void setJobFailed(String failReason) {
        JobService jobService = this.applicationContext.getBean(JobService.class);
        jobService.setJobFailed(this.job.getId(), failReason);
    }

    public JobState getJobCurrentState() {
        JobService jobService = this.applicationContext.getBean(JobService.class);
        return jobService.getJob(this.job.getId()).getState();
    }

    @Override
    public int compareTo(ExecutableJob executableJob) {
        return this.job.getPriority().getValue()
                .compareTo(executableJob.getJob().getPriority().getValue());
    }

    public Job getJob() {
        return job;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
