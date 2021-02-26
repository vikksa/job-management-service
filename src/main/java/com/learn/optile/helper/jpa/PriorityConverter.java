package com.learn.optile.helper.jpa;

import com.learn.optile.entities.values.JobPriority;

import javax.persistence.AttributeConverter;

public class PriorityConverter implements AttributeConverter<JobPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(JobPriority jobPriority) {
        return jobPriority.getValue();
    }

    @Override
    public JobPriority convertToEntityAttribute(Integer value) {
        return JobPriority.formValue(value);

    }

}
