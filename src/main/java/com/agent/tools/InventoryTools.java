package com.agent.tools;

import com.agent.client.InventoryClient;
import com.agent.dto.InventoryItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InventoryTools {

    private final InventoryClient inventoryClient;

    public InventoryTools(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Tool(description = "Look up inventory items by their IDs, returning name and quantity in stock")
    public List<InventoryItemDto> getInventoryItems(
            @ToolParam(description = "List of inventory item IDs to look up") List<String> ids) {

        log.info("Agent invoking inventory tool for ids: {}", ids);
        return inventoryClient.fetchInventory(ids);
    }
}