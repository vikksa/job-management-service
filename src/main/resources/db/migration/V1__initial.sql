create table job
(
    id binary(16)  not null primary key,
    job_type     varchar(255) null,
    priority     int          null,
    raw_job_data varchar(255) null,
    scheduled_at timestamp null,
    last_modified_at timestamp null,
    created_at timestamp null,
    state        varchar(255) null,
    fail_reason        varchar(255) null
)
