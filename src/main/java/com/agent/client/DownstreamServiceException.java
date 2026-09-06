package com.agent.client;

public class DownstreamServiceException extends RuntimeException {

    private final boolean retryable;

    public DownstreamServiceException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}