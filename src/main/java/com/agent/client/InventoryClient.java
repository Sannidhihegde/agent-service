package com.agent.client;

import com.agent.dto.InventoryItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;

@Slf4j
@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://inventory-service")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<InventoryItemDto> fetchInventory(List<String> ids) {
        return RetryExecutor.executeWithRetry(() -> callInventoryService(ids), 3, 500, 2.0);
    }

    private List<InventoryItemDto> callInventoryService(List<String> ids) {
        log.info("Calling inventory-service for ids: {}", ids);
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/inventory/allDetails")
                            .queryParam("inventoryIds", ids)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<InventoryItemDto>>() {});
        } catch (HttpClientErrorException.NotFound ex) {
            throw new InventoryUnavailableException("Not found: " + ids, ex, false);
        } catch (HttpClientErrorException ex) {
            throw new InventoryUnavailableException("Client error", ex, false);
        } catch (HttpServerErrorException ex) {
            throw new InventoryUnavailableException("Server error", ex, true);
        } catch (ResourceAccessException ex) {
            throw new InventoryUnavailableException("Unreachable", ex, true);
        }
    }
}