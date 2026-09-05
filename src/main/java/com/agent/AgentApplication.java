package com.agent;

import com.agent.client.InventoryClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    @Bean
    CommandLineRunner testInventoryCall(InventoryClient client) {
        return args -> {
            var items = client.fetchInventory(List.of("1", "2"));
            items.forEach(System.out::println);
        };
    }
}

