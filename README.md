# Spring OTEL Demo

Spring Boot + Thymeleaf + OpenTelemetry (Micrometer) + Grafana/Tempo/Prometheus + Graylog — orchestrated via Docker Compose.

## What This Project Does

**Traces**: Micrometer Tracing → OTLP/HTTP → OTel Collector → Tempo → Grafana (Explore).

**Metrics**: Actuator /actuator/prometheus → Prometheus → Grafana.

**Logs**: Logback GELF → Graylog, with traceId/spanId injected for correlation.

## Stack

Java 24, Spring Boot 3.3.x • Grafana 11.x, Tempo 2.4.x, Prometheus 2.53.x • Graylog 5.2, OpenSearch 2.13, MongoDB 6.0 • OTel Collector 0.99.0

## Quick Start

```bash
# Build & run
docker compose up -d --build

# App
open http://localhost:8080

# Grafana / Prom / Graylog
open http://localhost:3000
open http://localhost:9090
open http://localhost:9000
```

### Important: Create GELF UDP 12201 Input in Graylog

1. Open Graylog: http://localhost:9000 (admin / admin).

2. Go to System → Inputs.

3. Choose GELF UDP and click Launch new input.

4. Recommended settings:

   - Bind address: 0.0.0.0
   - Port: 12201
   - Allow overriding date: ✓
   - Recv Buffer Size: 262144 (optional)
   - Store full message: ✓ (optional, useful for demo)

5. Click Save to start the input.

After this, app logs (spring-otel-demo) should appear in Search (filter `facility:spring-otel-demo`). You can search by `trace_id:<ID>` to correlate with a Tempo trace.

## Configuration

### Spring (src/main/resources/application.properties)

```properties
spring.application.name=spring-otel-demo
management.endpoints.web.exposure.include=health,info,prometheus
management.otlp.tracing.endpoint=http://otel-collector:4318/v1/traces
management.tracing.sampling.probability=1.0
```

### Prometheus (prometheus/prometheus.yml)

```yaml
global: { scrape_interval: 10s }
scrape_configs:
  - job_name: spring-app
    metrics_path: /actuator/prometheus
    static_configs: [{ targets: ['app:8080'] }]

  - job_name: otel-collector
    static_configs: [{ targets: ['otel-collector:9464'] }]
```

### Logback (src/main/resources/logback-spring.xml)

Micrometer MDC keys: `traceId` and `spanId`

Example (console pattern + GELF):

```xml
%msg trace_id=%X{traceId:-} span_id=%X{spanId:-}
<additionalFields>service=spring-otel-demo,trace_id=%X{traceId},span_id=%X{spanId}</additionalFields>
```

## Application Demo

- **GET /** : Thymeleaf home.html page (calls DemoService#doWork()).
- **POST /trace** : "Generate trace" button → creates a custom span (@Observed or ObservationRegistry).

### Quick Verification

- **Prometheus** (Grafana > Explore): `up{job="spring-app"} = 1`, then RPS/latency via `http_server_requests_*`.
- **Tempo** : service `spring-otel-demo`, span `ui.button.click` (and `ui.inner.step`).
- **Graylog** : logs with trace_id/span_id.

## Troubleshooting

- No /actuator/prometheus → add `micrometer-registry-prometheus`.
- No traces → check `micrometer-tracing-bridge-otel` + `opentelemetry-exporter-otlp` and OTLP endpoint.
- Empty traceId/spanId → use camelCase MDC keys in Logback (not trace_id/span_id).
- No Graylog logs → verify the GELF UDP 12201 input as described above.
