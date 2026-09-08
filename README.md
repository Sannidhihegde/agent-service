# agent-service

An AI agent, exposed as a Spring Boot microservice, that answers natural-language
questions by deciding which downstream services to call as tools. The core piece
of this project: AI Agents as Microservice Collaborators.

## Responsibilities
- Exposes `POST /agent/ask` — accepts a plain-text question, returns the LLM's
  answer
- Uses Spring AI's `ChatClient` with two `@Tool`-annotated methods:
  - `getInventoryItems` → calls inventory-service
  - `getPricing` → calls pricing-service
- Calls both downstream services via Eureka-resolved, load-balanced `RestClient`s
  (no hardcoded hosts/ports)
- Hand-rolled retry with exponential backoff for downstream calls, distinguishing
  retryable (5xx, connection failure) from non-retryable (404, bad request) errors
- Publishes an `AgentInteractionEvent` to Kafka (`agent-interactions` topic) after
  each request — fire-and-forget audit logging, does not block the response

## Run
    mvn spring-boot:run
Runs on port 9090. Requires `eureka-server`, `kafka-local`, `inventory-service`,
`pricing-service` running. Requires a local Ollama instance (`llama3.1` pulled)
for the LLM call itself — see note below.

## Tech
Spring Boot 4.1, Spring AI 2.0 (Ollama provider), Spring Cloud 2025.1.2
(Eureka client, LoadBalancer), Spring Kafka.

## Status note
The REST-calling and retry logic (`InventoryClient`, `PricingClient`) and the
Eureka/Kafka wiring are verified working end-to-end via isolated smoke tests.
The full live LLM tool-calling loop (via Ollama) is implemented but not yet run
end-to-end in this environment, pending Ollama setup on a separate machine.

## Related services
- Calls: [inventory-service](https://github.com/Sannidhihegde/inventory-service), [pricing-service](https://github.com/Sannidhihegde/pricing-service)
- Publishes events to: [audit-service](https://github.com/Sannidhihegde/audit-service)
