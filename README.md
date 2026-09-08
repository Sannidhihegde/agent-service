# agent-service

An AI agent, exposed as a Spring Boot microservice, that answers natural-
language questions by deciding which downstream services to call as tools.
The core piece of this project: AI Agents as Microservice Collaborators.

## Responsibilities
- Exposes `POST /agent/ask` — accepts a plain-text question, returns the
  LLM's answer
- Uses Spring AI's `ChatClient` with two `@Tool`-annotated methods:
  `getInventoryItems` (calls inventory-service) and `getPricing` (calls
  pricing-service)
- Calls both downstream services via Eureka-resolved, load-balanced
  `RestClient`s — no hardcoded hosts/ports
- Hand-rolled retry with exponential backoff, distinguishing retryable
  (5xx, connection failure) from non-retryable (404, bad request) errors
- Publishes `AgentInteractionEvent` to Kafka (`agent-interactions` topic)
  after each request — fire-and-forget, does not block the response
- Fetches most of its config from `config-server` at startup

## Run
    mvn spring-boot:run
Runs on port 9090 (from `config-repo/agent-service.yml`). Requires
`config-server`, `eureka-server`, `kafka-local`, `inventory-service`,
`pricing-service` running. Requires a local Ollama instance (`llama3.1`
pulled) for the LLM call itself — see Status note below.

## Tech
Spring Boot 4.1, Spring AI 2.0 (Ollama provider), Spring Cloud 2025.1.2
(Eureka client, LoadBalancer, Config client), Spring Kafka.

## Status notes
- REST-calling, retry logic, Eureka resolution, and Kafka publishing are all
  verified working end-to-end via isolated smoke tests (bypassing the LLM
  layer).
- The full live LLM tool-calling loop via Ollama has not yet been run
  end-to-end in this environment — implemented and wired, pending Ollama
  setup on a separate machine.
- Distributed tracing (Micrometer + Brave + Zipkin) was attempted but
  parked: hit multiple confirmed open Spring Boot 4.1.x upstream bugs
  (spring-projects/spring-boot#48249, #49276) in the Zipkin/Brave
  auto-configuration. Tracer beans activate correctly; span export to
  Zipkin not yet confirmed reliable in this Boot version.

## Related services
- Calls: [inventory-service](https://github.com/Sannidhihegde/inventory-service), [pricing-service](https://github.com/Sannidhihegde/pricing-service)
- Publishes events to: [audit-service](https://github.com/Sannidhihegde/audit-service)
