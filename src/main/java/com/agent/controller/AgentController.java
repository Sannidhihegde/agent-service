package com.agent.controller;

import com.agent.event.AgentEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AgentController {

    private final ChatClient chatClient;
    private final AgentEventPublisher eventPublisher;

    public AgentController(ChatClient chatClient, AgentEventPublisher eventPublisher) {
        this.chatClient = chatClient;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/agent/ask")
    public String ask(@RequestBody String question) {
        log.info("Received question: {}", question);

        String response = chatClient.prompt()
                .user(question)
                .call()
                .content();

        eventPublisher.publishInteraction(question, response);

        return response;
    }
}