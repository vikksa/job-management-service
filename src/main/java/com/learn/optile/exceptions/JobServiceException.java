package com.learn.optile.exceptions;

public class JobServiceException extends RuntimeException {

    public JobServiceException() {
    }

    public JobServiceException(String message) {
        super(message);
    }

    public JobServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JobServiceException(Throwable throwable) {
        super(throwable);
    }

}
