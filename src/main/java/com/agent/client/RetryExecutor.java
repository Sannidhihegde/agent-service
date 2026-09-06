package com.agent.client;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public final class RetryExecutor {

    private RetryExecutor() {
    }

    public static <T> T executeWithRetry(
            Supplier<T> action,
            int maxAttempts,
            long initialDelayMillis,
            double backoffMultiplier) {

        int attempt = 0;
        long delay = initialDelayMillis;

        while (true) {
            attempt++;
            try {
                return action.get();
            } catch (DownstreamServiceException ex) {
                boolean lastAttempt = attempt >= maxAttempts;

                if (!ex.isRetryable() || lastAttempt) {
                    log.warn("Not retrying (retryable={}, attempt={}/{}): {}",
                            ex.isRetryable(), attempt, maxAttempts, ex.getMessage());
                    throw ex;
                }

                log.warn("Attempt {}/{} failed, retrying in {}ms: {}",
                        attempt, maxAttempts, delay, ex.getMessage());

                sleep(delay);
                delay = (long) (delay * backoffMultiplier);
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }
}