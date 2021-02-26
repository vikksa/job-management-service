package com.learn.optile.scheduler;

import com.learn.optile.services.JobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * This job will fetch received jobs from database and put them in the Queue to execute the tasks.
 * This job registered from quartz xml configuration. Configuration is in file under resource/jobs/put-jobs-in-queue-job.xml
 */
public class PutJobsInQueueJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PutJobsInQueueJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        try {
            logger.info("PutJobsInQueueJob task triggered");
            ApplicationContext applicationContext =
                    (ApplicationContext) jobContext.getScheduler().getContext().get("applicationContext");
            applicationContext.getBean(JobService.class).addJobsToQueue();
        } catch (SchedulerException e) {
            logger.error("Error while running the job", e);
        }
    }
}
