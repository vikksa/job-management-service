package com.learn.optile.runner.impl;

import com.learn.optile.entities.values.JobPriority;
import com.learn.optile.entities.values.JobState;
import com.learn.optile.jobs.ExecutableJob;
import com.learn.optile.runner.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class JobRunnerImpl implements JobRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobRunnerImpl.class);
    private final PriorityBlockingQueue jobQueue;
    private final ExecutorService executorService;
    private final Integer threadPoolSize;

    @Autowired
    public JobRunnerImpl(@Value("${job-runner.thread-pool-size:10}") Integer threadPoolSize) {
        this.jobQueue = new PriorityBlockingQueue<ExecutableJob>(1);
        this.executorService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS,
                this.jobQueue);
        this.threadPoolSize = threadPoolSize;
    }

    public boolean isSpaceInQueue() {
        return this.jobQueue.size() < threadPoolSize;
    }

    public boolean makeSpaceForHighPriorityTask(JobPriority priority) {
        List<ExecutableJob> list = new ArrayList<>();
        this.jobQueue.drainTo(list);
        list.sort(Collections.reverseOrder());
        if (list.get(0).getJob().getPriority().getValue() > priority.getValue()) {
            ExecutableJob executableJob = list.get(0);
            executableJob.updateStatus(JobState.SUBMITTED);
            list.remove(0);
            this.jobQueue.addAll(list);
            return true;
        }
        this.jobQueue.addAll(list);
        return false;
    }

    public void queueTask(ExecutableJob executableJob) {
        if (!isSpaceInQueue()) {
            if (executableJob.getJob().getPriority() == JobPriority.LOW) {
                logger.debug(String.format("Task queue full. Task <%s> not queued", executableJob.getJob().getId()));
                return;
            } else {
                //TODO Improve logic of make space in queue. Remove some low priority tasks and add high priority
                boolean spaceMade = makeSpaceForHighPriorityTask(executableJob.getJob().getPriority());
                if (!spaceMade) {
                    return;
                }
            }
        }
        this.executorService.execute(executableJob);
        executableJob.updateStatus(JobState.QUEUED);
    }

}
