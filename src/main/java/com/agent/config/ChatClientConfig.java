package com.agent.config;

import com.agent.tools.InventoryTools;
import com.agent.tools.PricingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            InventoryTools inventoryTools,
            PricingTools pricingTools) {

        return builder
                .defaultSystem("""
                        You are a shopping assistant. Use the available tools to answer
                        questions about stock levels, availability, and pricing. If asked
                        for both, use both tools before answering.
                        """)
                .defaultTools(inventoryTools, pricingTools)
                .build();
    }
}