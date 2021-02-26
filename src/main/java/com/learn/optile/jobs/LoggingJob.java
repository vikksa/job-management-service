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
        logger.info("Logging Job Started");
        logger.info("Logging Job completed");
    }

    @Override
    public void rollback() {
        logger.info("Logging Job roll backed");

    }
}
