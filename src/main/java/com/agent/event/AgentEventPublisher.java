package com.agent.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class AgentEventPublisher {

    private static final String TOPIC = "agent-interactions";

    private final KafkaTemplate<String, AgentInteractionEvent> kafkaTemplate;

    public AgentEventPublisher(KafkaTemplate<String, AgentInteractionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInteraction(String question, String response) {
        AgentInteractionEvent event = new AgentInteractionEvent(question, response, Instant.now());

        kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.warn("Failed to publish interaction event: {}", ex.getMessage());
            } else {
                log.info("Published interaction event to offset {}", result.getRecordMetadata().offset());
            }
        });
    }
}