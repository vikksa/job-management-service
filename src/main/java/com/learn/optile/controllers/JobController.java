package com.learn.optile.controllers;

import com.learn.optile.dtos.JobDto;
import com.learn.optile.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobDto createJob(@RequestBody JobDto jobDto) {
        return new JobDto(jobService.createJob(jobDto));
    }

    @GetMapping("/{jobId}")
    public JobDto getJob(@PathVariable UUID jobId) {
        return new JobDto(jobService.getJob(jobId));
    }

    @PostMapping("/{jobId}/cancel")
    public void cancelJob(@PathVariable UUID jobId) {
        jobService.cancelJob(jobId);
    }

    @GetMapping
    public Page<JobDto> getAllJobs(@RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return jobService.getAllJobs(page, size).map(JobDto::new);
    }

}
