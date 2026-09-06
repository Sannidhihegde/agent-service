package com.agent.client;

import com.agent.dto.PriceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;

@Slf4j
@Component
public class PricingClient {

    private final RestClient restClient;

    public PricingClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://pricing-service")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<PriceInfoDto> fetchPricing(List<String> ids) {
        return RetryExecutor.executeWithRetry(
                () -> callPricingService(ids), 3, 500, 2.0);
    }

    private List<PriceInfoDto> callPricingService(List<String> ids) {
        log.info("Calling pricing-service for ids: {}", ids);
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pricing/allDetails")
                            .queryParam("itemIds", ids)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PriceInfoDto>>() {});

        } catch (HttpClientErrorException.NotFound ex) {
            throw new PricingUnavailableException("Pricing not found: " + ids, ex, false);
        } catch (HttpClientErrorException ex) {
            throw new PricingUnavailableException("Pricing service rejected request", ex, false);
        } catch (HttpServerErrorException ex) {
            throw new PricingUnavailableException("Pricing service errors", ex, true);
        } catch (ResourceAccessException ex) {
            throw new PricingUnavailableException("Pricing service unreachable", ex, true);
        }
    }
}