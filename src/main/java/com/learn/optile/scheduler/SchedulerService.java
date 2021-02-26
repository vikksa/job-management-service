package com.learn.optile.scheduler;

import com.learn.optile.exceptions.JobServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    private static final Log logger = LogFactory.getLog(SchedulerService.class);

    private final Scheduler scheduler;

    @Autowired
    public SchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleJob(ScheduledJob job) {

        //If Job Already exists the delete it
        removeScheduledJob(job.getId().toString());


        // Create the job detail
        logger.info(String.format("Creating New Job of type : <%s>", job.getClass()));

        JobBuilder jobBuilder = JobBuilder.newJob(job.getClass())
                .withIdentity(job.getId().toString()).ofType(job.getClass());
        if (job.getPropertiesMap() != null) {
            job.getPropertiesMap().forEach(jobBuilder::usingJobData);
        }
        JobDetail jobDetail = jobBuilder.build();

        // Create the trigger to schedule the job
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getId().toString())
                .startAt(job.getTriggerAtDateTime())
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(1))
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info(String.format("Scheduled a new Job : %s", job));
        } catch (SchedulerException e) {
            logger.error(e, e);
            throw new JobServiceException("Error in task scheduler ", e);
        }
    }


    public void removeScheduledJob(String jobId) {
        JobKey jobKey = new JobKey(jobId);
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            logger.error(e, e);
            throw new JobServiceException(String.format(
                    "Error in task scheduler remove job <%s>",
                    jobId), e);
        }
    }

}
