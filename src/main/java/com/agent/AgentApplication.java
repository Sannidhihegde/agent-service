package com.agent;

import com.agent.event.AgentEventPublisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    //test event
//    @Bean
//    CommandLineRunner testEventPublish(AgentEventPublisher publisher) {
//        return args -> {
//            publisher.publishInteraction("Test question", "Test response");
//            System.out.println("Event publish triggered — check audit-service logs/endpoint");
//        };
//    }
}