package com.learn.optile.runner;

import com.learn.optile.jobs.ExecutableJob;

public interface JobRunner {

    void queueTask(ExecutableJob executableJob);

}
