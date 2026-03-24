# Shorty Pro

Shorty Pro is a distributed URL shortener built as a small event-driven system. It splits the write and redirect workload from analytics processing so the main redirect path stays fast while click events are captured asynchronously.

## Overview

The project consists of two Spring Boot services and a supporting local infrastructure stack:

- `url-shortener-service` creates short codes, resolves original URLs, serves redirects, and manages Redis-backed caching.
- `analytics-service` consumes click events from Kafka and stores them in a separate analytics database.
- PostgreSQL is used for persistent storage.
- Redis is used to accelerate read-heavy redirect and lookup operations.
- Kafka transports click events from the redirect flow to the analytics pipeline.
- Prometheus, Grafana, Loki, Alloy, Kafka UI, and RedisInsight provide local observability and operations tooling.

At a high level, the flow is:

1. A client creates a short URL through `url-shortener-service`.
2. The mapping is stored in PostgreSQL and can be read back directly or served through Redis cache.
3. A redirect request hits `url-shortener-service`.
4. The service returns `302 Found` and publishes a click event to Kafka.
5. `analytics-service` consumes the event and persists it into the analytics PostgreSQL database.

## Supported Operations

### URL Shortener Service

Supported operations:

- Create short URLs.
- Resolve a short code back to its original URL.
- Redirect a short code to its destination URL.
- Inspect cache status.
- Clear all caches or a specific cache.
- Trigger asynchronous cache warmup.

Implementation details:

- Built with Spring Boot 3.5 and Java 21.
- Uses Spring Data JDBC with Flyway migrations against PostgreSQL.
- Uses Redis through Spring Cache for hot-path reads.
- Publishes click events to Kafka.
- Exposes Actuator health and Prometheus metrics endpoints.
- Exposes Swagger/OpenAPI UI for interactive API inspection.

### Analytics Service

Supported operations:

- Consume URL click events from Kafka.
- Persist click events into a dedicated analytics database.
- Ignore malformed or duplicate events safely.

Implementation details:

- Event-driven service; there is currently no business REST controller for analytics reads.
- Uses Spring Kafka consumers with explicit topic and DLT configuration.
- Uses PostgreSQL plus Flyway migrations for event persistence.
- Treats duplicate deliveries as safe by relying on a unique `event_id`.
- Exposes Actuator health and Prometheus metrics endpoints.
- Also exposes Swagger/OpenAPI infrastructure, although the business API surface is currently event-based.

### Infrastructure

The repository includes two local execution modes:

- Docker Compose for a full local stack with databases, messaging, applications, and observability.
- Kubernetes manifests under [`k8n/README.md`](/Users/qnocks/IdeaProjects/shorty-pro/k8n/README.md) for Minikube-based local deployment.

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Validation
- Spring Data JDBC
- Spring Data Redis
- Spring Cache
- Spring Kafka
- PostgreSQL 15
- Redis 7
- Apache Kafka with ZooKeeper
- Flyway
- Micrometer + Prometheus registry
- Grafana
- Loki
- Alloy
- Docker / Docker Compose
- Kubernetes / Minikube
- Gradle

## How To Run

### Prerequisites

- Java 21
- Docker and Docker Compose
- Make

### Run With Docker Compose

From the repository root:

```bash
make build
```

Useful commands:

```bash
make start
make logs
make status
make down
```

Service ports:

- `url-shortener-service`: `8082`
- `analytics-service`: `8081`
- `kafka-ui`: `8080`
- `grafana`: `3000`
- `prometheus`: `9090`
- `redisinsight`: `5540`
- `url-shortener-postgres`: `5434`
- `analytics-postgres`: `5435`
- `redis`: `6379`
- `kafka`: `29092`

### Run With Minikube

Kubernetes manifests are already prepared under `k8n`.

Start here:

- [`k8n/README.md`](/Users/qnocks/IdeaProjects/shorty-pro/k8n/README.md)

That guide covers:

- starting Minikube
- building service images into the Minikube image cache
- deploying with `kubectl apply -k k8n`
- exposing services

### Local Health Checks

Once the stack is up:

```bash
curl http://localhost:8082/actuator/health
curl http://localhost:8081/actuator/health
```

## API

### URL Shortener Service

Base URL:

```text
http://localhost:8082
```

Swagger UI:

```text
http://localhost:8082/swagger
```

OpenAPI document:

```text
http://localhost:8082/api-docs
```

Endpoints:

#### Create Short URL

`POST /api/v1/urls`

Request body:

```json
{
  "url": "https://example.com/some/long/path"
}
```

Response:

```json
"abc123"
```

#### Resolve Original URL

`GET /api/v1/urls?shortUrl=abc123`

Response:

```json
"https://example.com/some/long/path"
```

#### Redirect

`GET /api/v1/redirect/{shortCode}`

Behavior:

- Returns `302 Found`
- Sets the `Location` header to the original URL
- Publishes a click event to Kafka for downstream analytics processing

#### Cache Status

`GET /api/v1/admin/caches/status`

#### Clear All Caches

`POST /api/v1/admin/caches/clear-all`

#### Clear Specific Cache

`POST /api/v1/admin/caches/clear/{cacheName}`

#### Warm Cache

`POST /api/v1/admin/caches/warmup`

#### Warm Cache With Limit

`POST /api/v1/admin/caches/warmup/{limit}`

### Analytics Service

Base URL:

```text
http://localhost:8081
```

Swagger UI:

```text
http://localhost:8081/swagger
```

OpenAPI document:

```text
http://localhost:8081/api-docs
```

Current behavior:

- Consumes Kafka topic `url-click-events-topic`
- Stores click events in the analytics database
- Exposes operational endpoints such as `/actuator/health` and `/actuator/prometheus`

There is currently no public analytics query REST endpoint implemented in this module.
