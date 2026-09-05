package com.agent.config;

import com.agent.tools.InventoryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, InventoryTools inventoryTools) {
        return builder
                .defaultSystem("""
                        You are an inventory assistant. Use the available tools to answer
                        questions about stock levels and item availability. If an item
                        cannot be found, say so clearly rather than guessing.
                        """)
                .defaultTools(inventoryTools)
                .build();
    }
}