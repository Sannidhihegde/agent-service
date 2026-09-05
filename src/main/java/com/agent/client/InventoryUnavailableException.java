package com.agent.client;

import lombok.Getter;

@Getter
public class InventoryUnavailableException extends RuntimeException {

    private final boolean retryable;

    public InventoryUnavailableException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

}