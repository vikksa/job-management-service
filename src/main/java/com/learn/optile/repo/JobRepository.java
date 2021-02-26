package com.learn.optile.repo;

import com.learn.optile.entities.Job;
import com.learn.optile.entities.values.JobState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findAllByStateOrderByPriorityAsc(JobState jobState, Pageable pageable);
}
