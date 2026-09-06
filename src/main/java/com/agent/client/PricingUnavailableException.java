package com.agent.client;

public class PricingUnavailableException extends DownstreamServiceException {
    public PricingUnavailableException(String message, Throwable cause, boolean retryable) {
        super(message, cause, retryable);
    }
}