package com.agent.event;

import java.time.Instant;

public record AgentInteractionEvent(
        String question,
        String response,
        Instant timestamp) {
}