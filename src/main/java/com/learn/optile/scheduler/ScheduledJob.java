package com.learn.optile.scheduler;

import com.learn.optile.services.JobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * To Trigger a Job at particular time
 */
public class ScheduledJob implements Job {

    private static final Log logger = LogFactory.getLog(ScheduledJob.class);

    private UUID id;
    private Long triggerAt;


    public ScheduledJob(UUID id, Date triggerAt) {
        this.id = id;
        this.triggerAt = triggerAt.getTime();
    }


    public ScheduledJob() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getPropertiesMap() {
        HashMap<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("id", this.getId().toString());
        propertiesMap.put("triggerAt", String.valueOf(this.triggerAt));
        return propertiesMap;
    }

    public Long getTriggerAt() {
        return triggerAt;
    }

    public Date getTriggerAtDateTime() {
        return new Date(this.triggerAt);
    }

    public void setTriggerAt(Long triggerAt) {
        this.triggerAt = triggerAt;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            ApplicationContext applicationContext =
                    (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
            logger.info("Job triggered" + context.getJobDetail().getKey().getName());
            applicationContext.getBean(JobService.class).addJobsToQueue(this.getId());
        } catch (SchedulerException e) {
            logger.error("Error while running the job", e);
        }
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "id=" + id +
                ", triggerAt=" + triggerAt +
                '}';
    }
}
