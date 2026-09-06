package com.agent.tools;

import com.agent.client.PricingClient;
import com.agent.dto.PriceInfoDto;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PricingTools {

    private final PricingClient pricingClient;

    public PricingTools(PricingClient pricingClient) {
        this.pricingClient = pricingClient;
    }

    @Tool(description = "Get the final price (after discount) for given item IDs")
    public List<PriceInfoDto> getPricing(
            @ToolParam(description = "List of item IDs to price") List<String> ids) {
        return pricingClient.fetchPricing(ids);
    }
}