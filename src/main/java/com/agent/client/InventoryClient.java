package com.agent.client;

import com.agent.dto.InventoryItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class InventoryClient {

    private final RestClient inventoryRestClient;

    public InventoryClient(RestClient inventoryRestClient) {
        this.inventoryRestClient = inventoryRestClient;
    }

    public List<InventoryItemDto> fetchInventory(List<String> ids) {
        return RetryExecutor.executeWithRetry(
                () -> callInventoryService(ids),
                3,      // maxAttempts
                500,    // initialDelayMillis
                2.0     // backoffMultiplier
        );
    }

    private List<InventoryItemDto> callInventoryService(List<String> ids) {
        log.info("Calling inventory-service for ids: {}", ids);

        try {
            return inventoryRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/inventory/allDetails")
                            .queryParam("inventoryIds", ids)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<InventoryItemDto>>() {});

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Inventory items not found for ids {}: {}", ids, ex.getMessage());
            throw new InventoryUnavailableException(
                    "One or more inventory items not found: " + ids, ex, false);

        } catch (HttpClientErrorException ex) {
            log.warn("Client error calling inventory-service: {}", ex.getStatusCode());
            throw new InventoryUnavailableException(
                    "Inventory service rejected the request", ex, false);

        } catch (HttpServerErrorException ex) {
            log.error("Server error from inventory-service: {}", ex.getStatusCode());
            throw new InventoryUnavailableException(
                    "Inventory service is experiencing errors", ex, true);

        } catch (ResourceAccessException ex) {
            log.error("Could not reach inventory-service", ex);
            throw new InventoryUnavailableException(
                    "Inventory service is unreachable", ex, true);
        }
    }
}