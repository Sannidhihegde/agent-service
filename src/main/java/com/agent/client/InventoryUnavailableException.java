package com.agent.client;

public class InventoryUnavailableException extends DownstreamServiceException {
    public InventoryUnavailableException(String message, Throwable cause, boolean retryable) {
        super(message, cause, retryable);
    }
}