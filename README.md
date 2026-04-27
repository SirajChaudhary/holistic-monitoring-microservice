# Holistic Monitoring Microservice

# Overview

This microservice demonstrates **end-to-end observability** in a modern Spring Boot application.

It uses logs, traces, and metrics together to monitor application behavior, diagnose issues, and understand performance in real time.

This microservice includes a `docker-compose.yaml` file that defines all required services (Elasticsearch, Logstash, Kibana, Zipkin, Prometheus, Grafana). These services can be started together using a single command: `docker-compose up -d`

The project reflects how modern microservices are observed and debugged using industry-standard tools.

---

# What This Project Shows
- How logs, traces, and metrics work together
- How to debug real production issues
- How to identify performance bottlenecks
- How to use correlationId and traceId effectively
- How observability tools integrate in a microservice

---

# Key Features

This project demonstrates:

- Request/response logging with **correlationId** for end-to-end tracking
- Distributed tracing using **traceId** and **spanId**
- Centralized logging with **ELK Stack (Logstash → Elasticsearch → Kibana)**
- Structured logging (JSON) for better search and analysis
- Application metrics collection using **Micrometer**
- Custom metrics (e.g., DB latency) for performance monitoring
- Metrics exposure via **Spring Boot Actuator** (`/actuator/prometheus`)
- Metrics scraping and storage using **Prometheus**
- Visualization and dashboards using **Grafana**
- End-to-end request observability (**Logs + Traces + Metrics**)
- Error tracking and debugging using **Kibana (logs)** and **Zipkin (traces)**

```
Logs    → What happened (Kibana)
Traces  → Where time is spent (Zipkin)
Metrics → System behavior over time (Grafana)
```

---

# Architecture Overview

```
Client Request
 → Spring Boot Application
     → Logs    → Logstash → Elasticsearch → Kibana
     → Traces  → Zipkin
     → Metrics → Prometheus → Grafana
```

---

# Technology Stack

### Core
- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**

### Logging
- **SLF4J** (logging facade)
- **Logback** (logging implementation)
- **Logstash** Logback Encoder** (structured JSON logging)

### Monitoring & Metrics
- **Spring Boot Actuator** (exposes metrics & health endpoints)
- **Micrometer** (metrics collection library)
- **Prometheus** (metrics scraping & storage)

### Tracing
- **Micrometer Tracing** (tracing abstraction)
- **OpenTelemetry** (tracing implementation via bridge)
- **Zipkin** (trace visualization)

### Logging Stack (ELK)
- **Logstash** (log processing & forwarding)
- **Elasticsearch** (log storage & indexing)
- **Kibana** (log search & visualization)

### Visualization
- **Grafana** (metrics dashboards & monitoring)

### Containerization
- **Docker Compose** (orchestration of all services)

---

# Servers and Their Purpose

### 1. Spring Boot Application
- Runs business logic
- Generates logs, traces, and metrics
- Exposes Actuator endpoints (/actuator/*)
- URL: `http://localhost:8080`

### 2. Elasticsearch
- Stores logs (from Logstash)
- Acts as a search engine for log data
- URL: `http://localhost:9200`

### 3. Logstash
- Collects logs from application (JSON format via Logback)
- Processes and forwards logs to Elasticsearch
- (No UI)

### 4. Kibana
- UI to search and visualize logs stored in Elasticsearch
- Helps trace a request using correlationId
- URL: `http://localhost:5601`

### 5. Zipkin
- Visualizes distributed traces
- Shows request flow (HTTP → Controller → Service → DB)
- Helps identify latency and bottlenecks
- URL: `http://localhost:9411`

### 6. Prometheus
- Scrapes metrics from /actuator/prometheus
- Stores metrics as time-series data
- Allows querying using PromQL
- URL: `http://localhost:9090`

### 7. Grafana
- Visualizes metrics from Prometheus
- Creates dashboards, charts, and alerts
- URL: `http://localhost:3000`
  - Default Login: admin / admin

## Other Technologies Used and Their Purpose

### Micrometer
- Metrics library used by Spring Boot
- Collects:
  - HTTP metrics (requests, latency, errors)
  - Custom metrics (e.g., DB latency)

- Exposes metrics via: `/actuator/prometheus`

### OpenTelemetry
- Provides distributed tracing implementation
- Used as the underlying tracing system for Micrometer
- Generates:
    - traceId
    - spanId
- Exports traces to Zipkin

- Relationship: `Micrometer (Tracing API) → OpenTelemetry (implementation) → Zipkin (visualization)`

### Micrometer Observation API
- Used to manually create spans
- Required because Micrometer does NOT create:
  - Controller spans
  - Service spans
- Used in code: `Observation.createNotStarted(...)`

### Logback
- Default logging framework in Spring Boot
- Generates logs in JSON format
- Sends logs to Logstash

### Logstash Logback Encoder
- Converts logs into structured JSON
- Adds fields like:
  - correlationId
  - traceId
  - spanId

### MDC (Mapped Diagnostic Context)
- Adds contextual data to logs automatically
- Used for: 
  - correlationId
  - traceId
  - spanId

- Enables request-level tracking in logs

## How Everything Works Together

### Logs Flow
```
Application (Logback + MDC)
→ Logstash
→ Elasticsearch
→ Kibana (search & visualize)
```

### Traces Flow
```
Application (Micrometer + OpenTelemetry)
→ Zipkin
→ Trace timeline visualization
```

### Metrics Flow
```
Application (Micrometer)
→ /actuator/prometheus
→ Prometheus
→ Grafana (dashboards)
```

### Flow Summary
```
Logs      → ELK Stack (Logstash + Elasticsearch + Kibana)
Traces    → Micrometer + OpenTelemetry → Zipkin
Metrics   → Micrometer → Prometheus → Grafana
```

---

# Endpoints

### Application Endpoints
- Create Payment `POST /api/v1/payments`
- Get Payment `GET /api/v1/payments/{id}`
- Simulate Error `GET /api/v1/payments/simulate-error`
- Simulate External Call (Latency) `GET /api/v1/payments/external-call`

### Actuator Endpoints
- Health `/actuator/health` 
- Metrics `/actuator/metrics`
- Prometheus `/actuator/prometheus`

---

# How to Run (End-to-End)

### Step 1: Clone the project
```
git clone https://github.com/SirajChaudhary/holistic-monitoring-microservice.git
cd holistic-monitoring-microservice
```

### Step 2: Start all monitoring containers
```
docker-compose up -d
```
Useful Docker Compose Commands (Just for reference)
- To create and start containers (foreground) `docker-compose up`
- To create and start containers (detached mode) `docker-compose up -d`
- To start existing containers `docker-compose start`
- To stop running containers `docker-compose stop`
- To restart containers `docker-compose restart`
- To view container logs `docker-compose logs`
- To view logs in real-time `docker-compose logs -f`
- To stop containers & remove containers and network `docker-compose down`
- To stop containers & remove containers, network, and volumes `docker-compose down -v`

### Step 3: Verify containers
```
docker ps
```
<img width="3410" height="676" alt="image" src="https://github.com/user-attachments/assets/fc694002-a478-42f9-8808-a1e48794e61a" />

You should see running containers:
- prometheus
- grafana
- zipkin
- logstash
- kibana
- elasticsearch

### Step 4: Create DB & Start Spring Boot Application
```
CREATE DATABASE monitoring_db;
```

```
mvn clean install
mvn spring-boot:run
```

### Step 5: Call APIs

- Import postman_collection and run APIs

- Run SUCCESS API: `POST http://localhost:8080/api/v1/payments`

  <img width="2566" height="450" alt="image" src="https://github.com/user-attachments/assets/a964f289-2b27-4b2a-b30f-38c4a31fd1b7" /><br />

  - **Request header** (correlationId): If correlationId is not provided in header, the [LoggingFilter](src/main/java/com/example/monitoring/logging/LoggingFilter.java) generates a correlationId and adds it to both the request and response.
    <img width="2560" height="290" alt="image" src="https://github.com/user-attachments/assets/cf630d36-9192-411b-9b57-15c4bb3ea44a" /> <br />
  
  - **Response header** (correlationId & traceId): The same correlationId is added to the response by the [LoggingFilter](src/main/java/com/example/monitoring/logging/LoggingFilter.java), and the traceId is generated and added automatically by Micrometer.
    <img width="2590" height="614" alt="image" src="https://github.com/user-attachments/assets/0b59c945-e0ee-43a8-acae-47a0cf2c8f79" />
 
- Run ERROR API: `GET http://localhost:8080/api/v1/payments/simulate-error`

  <img width="2574" height="948" alt="image" src="https://github.com/user-attachments/assets/40c4e3ac-8f02-4749-8849-2f08f91edc50" />

  - **Response body**: In this case, correlationId and traceId are ALSO included in the response body via the GlobalException handler using a custom [ErrorResponse](src/main/java/com/example/monitoring/exception/GlobalExceptionHandler.java). This is optional, as these values are already added to the response headers automatically.
  - **Response headers** (correlationId & traceId): Even for failure API cases, the correlationId is added by the LoggingFilter, and the traceId is generated automatically by Micrometer in the response headers.
    <img width="2588" height="546" alt="image" src="https://github.com/user-attachments/assets/b5004a65-2821-4a19-b9d7-41137d966c97" />

    - Copy from Response Headers:
      - correlationId
      - traceId

### Step 6: Check Application Health & Metrics via Actuator

```
http://localhost:8080/actuator
```
<img width="2720" height="1390" alt="image" src="https://github.com/user-attachments/assets/774fbba6-c3ad-41e2-b712-f180527851b1" />


Spring Boot Actuator exposes multiple endpoints to monitor application health, metrics, and runtime details.

- #### Health Metrics 
  - `http://localhost:8080/actuator/health`

    <img width="2604" height="1132" alt="image" src="https://github.com/user-attachments/assets/b2794b42-d361-40d7-9a39-08a416540b87" />

  - Check overall application health
    - Application status (UP / DOWN)
    - Component health (DB, disk, etc.)
    - Readiness/Liveness (if enabled)

- #### Application Metrics (Prometheus) 
  - `http://localhost:8080/actuator/prometheus`

     <img width="2950" height="2106" alt="image" src="https://github.com/user-attachments/assets/5c1c1c99-997e-48d0-ac6d-9d0358320527" />

  - Exposes all metrics in Prometheus format (Following common metrics you can see here)
    - #### HTTP Metrics
      - `http_server_requests_seconds_count` → total requests
      - `http_server_requests_seconds_sum` → total processing time
      - `http_server_requests_seconds_max` → max latency

    - #### JVM Metrics
      - `jvm_memory_used_bytes` → memory usage
      - `jvm_gc_pause_seconds` → GC pauses
      - `jvm_threads_live_threads` → active threads

    - #### System Metrics
      - `system_cpu_usage` → CPU usage
      - `process_uptime_seconds` → application uptime

    - #### Database Metrics (Custom)

      - `db_operation_time_seconds_sum` → total DB time
      - `db_operation_time_seconds_max` → max DB latency
      - `db_operation_time_seconds_count` → number of DB calls

        <img width="3420" height="2098" alt="image" src="https://github.com/user-attachments/assets/3f67fb12-3772-47f8-bc74-9bc4bd3b3186" />
    
      - [Database Custom metrics](src/main/java/com/example/monitoring/service/impl/PaymentServiceImpl.java) created using Micrometer Timer to measure DB operation performance
    
        ```
        Timer.Sample sample = Timer.start(meterRegistry);
      
        try {
            // DB operation (save / fetch / update)
        } finally {
              sample.stop(
                  Timer.builder("db.operation.time")
                      .tag("operation", "save")
                      .tag("entity", "payment")
                      .register(meterRegistry)
          );
        }
        ```

    - #### Custom Business Metrics (if added)
      - `payment_success_total` → successful transactions
      - `payment_failure_total` → failed transactions

    - #### You can view all available metric names at
      - `http://localhost:8080/actuator/metrics`
        <img width="2682" height="2100" alt="image" src="https://github.com/user-attachments/assets/d1ff668a-f218-4363-b293-336dd3ebb042" />

      - To use them in Prometheus:
        - Replace `.` with `_` in the metric name
        - Then search for it in: `http://localhost:8080/actuator/prometheus`

- #### Other Useful Endpoints
  - Application info `http://localhost:8080/actuator/info` 
  - List of metric names `http://localhost:8080/actuator/metrics`

### Step 7: Check Logs (Kibana)

- #### Generate Logs

  - Call the error API (or any API): `GET http://localhost:8080/api/v1/payments/simulate-error`
    - Copy **correlationId** from response

- #### Verify Logs Reaching Logstash
  - `docker logs -f logstash`

    <img width="3404" height="1988" alt="image" src="https://github.com/user-attachments/assets/83810ba8-4028-4ffc-bcc3-56ee7cafe2f1" />

  - You should see logs flowing in JSON format
  - This confirms: `Spring Boot → Logstash connection is working`

- #### Verify Logs Stored in Elasticsearch
  - `http://localhost:9200/_cat/indices?v`

    <img width="3062" height="642" alt="image" src="https://github.com/user-attachments/assets/7e0b4d76-d07c-4e36-a3f9-ca7e7237ac44" />

  - Look for: `spring-logs-YYYY.MM.DD`
  - Verify: `docs.count increases after each API call`

- #### Open Kibana

  - `http://localhost:5601`
  - Create Data View (First Time Only)
    - Go to: `Stack Management → Data Views → Create data view`
      - Enter:
        - Name: `spring-logs`
        - Index pattern: `spring-logs-*`
      - Click `Save data view to Kibana`

- #### Open Logs (Discover)

  - Go to: `Discover` 
  - Select: `spring-logs`
  - Set time filter (top right): `Last 30 minutes`

- #### Search Logs Using correlationId
  - In search bar: `correlationId: "<your-id>"`

    <img width="3472" height="2092" alt="image" src="https://github.com/user-attachments/assets/51b6daed-0e86-488d-a37e-3d18d066d002" />

- #### Observe Logs

  - You should see:
    - Incoming request log 
    - Processing logs (controller/service)
    - Error logs (from simulate-error)
    - Outgoing response log

  - All logs will contain the same **correlationId**

- #### Kibana Log Flow Summary

    ```
    API Call
     → Logs generated in application
     → Sent to Logstash
     → Stored in Elasticsearch (spring-logs-*)
     → Viewed in Kibana
    ```
  - **correlationId** uniquely identifies a request
  - All logs generated during a request (controller, service, etc.) share the same correlationId
  - **Kibana + correlationId are used to understand what happened during a request**
  - Kibana allows you to search and visualize these logs
  - Enables end-to-end debugging of a single request
  - Helps identify errors, execution flow, and performance issues

### Step 8: Check Traces (Zipkin)

```
http://localhost:9411
```

Search the traceId in Zipkin for the SUCCESS API

<img width="3472" height="2096" alt="image" src="https://github.com/user-attachments/assets/a00c1db4-b17a-4540-8fc4-102c128d7eef" />

Search the traceId in Zipkin for the ERROR/FAILURE API

<img width="3470" height="2104" alt="image" src="https://github.com/user-attachments/assets/a8a7ad2e-0060-4904-9af5-7260ee8ad2ff" />

- #### Trigger a Request

  - Call any API (SUCCESS or ERROR):
    - `POST http://localhost:8080/api/v1/payments`
    - `GET http://localhost:8080/api/v1/payments/simulate-error`
  - This automatically generates a **trace (traceId)**

- #### Open Zipkin
  - Go to: `http://localhost:9411`
  - Click Run Query (to fetch latest traces)
  - Or search using: `traceId: "<your-trace-id>"`
  - You should now see a list of traces
  - Click on any trace → view the **timeline**

- #### Micrometer Default Behavior (IMPORTANT TO UNDERSTAND)

  - By default, Micrometer:
    - Automatically creates:
        - HTTP span (incoming request)
        - DB span (if JDBC instrumentation is enabled)
        - Messaging spans (if applicable)
    - Does NOT create spans for:
      - Controller methods 
      - Service methods

  - So without custom instrumentation, Zipkin will only show: HTTP + DB (sometimes) spans.

  - **How We Fix This:**
    - To see full flow on Zipkin (Controller → Service → DB spans),
      - we manually create spans using Micrometer Observation API: `Observation.createNotStarted(...)`
      - **Expected Span Hierarchy on Zipkin (After Adding Observation)**
          ```
          HTTP POST /api/v1/payments
           └── controller.createPayment
                └── service.createPayment
                     └── db.save.payment
          ```

      - This allows us to visualize business logic layers in Zipkin

      - **Example (Success API)**
          ```
          POST /api/v1/payments
           └── HTTP span
                └── controller.createPayment
                     └── service.createPayment
                          └── db.save.payment
          ```

          What to Check
          - Duration of each span
          - Which layer is taking most time
          - Total request latency

          - 👉 If DB span is highest → DB is bottleneck
          - 👉 If service span is high → business logic issue

      - **Example (Error API)**
          ```
          GET /simulate-error
           └── HTTP span
                └── controller.simulateError
                     └── ERROR
          ```
          What to Check
          - Span marked as **error**
          - Error location in flow
          - Helps identify exact failure point

- #### Trace Flow
    ```
    API Call
     → Incoming HTTP request reaches Spring Boot application
     → HTTP span is created automatically by Micrometer (entry point)
     → Controller logic executes → custom span created via Observation API
     → Service layer executes → custom span created via Observation API
     → Database call (if any) → DB span created automatically
     → All spans are linked using the same traceId
     → Trace is exported to Zipkin
     → Displayed as a timeline showing end-to-end request flow
    ```

    ```
    API → HTTP span → controller/service spans (via Observation API) → DB span → Zipkin → timeline view
    ```

- #### Key Points
  - traceId is auto-generated for every request (Micrometer/OpenTelemetry)
  - traceId is created for both successful and failed requests
  - Same traceId is shared across the entire request flow
  - spanId is generated for each span (HTTP, controller, service, DB, etc.)
  - HTTP spans are created automatically by Micrometer
  - DB spans are created automatically when DB operations are executed (if instrumentation is enabled)
  - Controller and Service spans require Observation API (manual creation)
  - Spans are created for both successful and failed requests
  - Failed spans are marked with error details (visible in Zipkin)

### Step 9: Analyze Metrics Using Prometheus

Prometheus stores and lets you query **time-series metrics** collected from your application.

```
http://localhost:9090
```

<img width="3468" height="928" alt="image" src="https://github.com/user-attachments/assets/ce964e0d-0d22-4dfb-b6c6-1a04c571c307" />

- #### Open Prometheus
  - Go to: `http://localhost:9090`
  - Click on the **Graph** tab
  - Enter a query and click **Execute**

- #### What You Can See in Prometheus

  - You can view different types of metrics such as API performance, JVM stats, system usage, and database performance.

    - **HTTP Metrics (API Performance)**
      - `http_server_requests_seconds_sum` → total time spent processing requests
      - `http_server_requests_seconds_max` → maximum request latency
      - `http_server_requests_seconds_count` → total number of requests

        <img width="3472" height="2106" alt="image" src="https://github.com/user-attachments/assets/3594746d-50f9-4715-8a46-0d27dce3fb84" />

    - **JVM Metrics**
      - `jvm_gc_pause_seconds` → garbage collection time
      - `jvm_threads_live_threads` → active threads
      - `jvm_memory_used_bytes` → memory usage

        <img width="3472" height="2098" alt="image" src="https://github.com/user-attachments/assets/53f0eb24-017a-4737-b3e2-8791a74227ef" />

    - **System Metrics**
      - `process_uptime_seconds` → application uptime
      - `system_cpu_usage` → CPU usage

        <img width="3472" height="1950" alt="image" src="https://github.com/user-attachments/assets/f93edd77-3d97-41cc-b3b7-c9983c174052" />

    - **Database Metrics (DB Performance)**
      - You added a timer in your code to measure DB performance `db.operation.time`. Prometheus exposes it as:
        - `db_operation_time_seconds_sum` → total DB time
        - `db_operation_time_seconds_max` → maximum DB latency
        - `db_operation_time_seconds_count` → number of DB calls

        <img width="3462" height="1928" alt="image" src="https://github.com/user-attachments/assets/5810515e-2090-4623-ac93-fba44caefb5c" />

    - **Filter by Tags**
      - If you added tags like:
        ```
        operation = save
        entity = payment
        ```
        You can filter metrics: 
        ```
        db_operation_time_seconds_count{operation="save", entity="payment"}
        ```
        <img width="3474" height="1930" alt="image" src="https://github.com/user-attachments/assets/6dd8467b-e5db-45b8-8a37-64f364553062" />
      
- #### What to Observe
  - Increasing request count → high traffic
  - Increasing error count → failures in API
  - Increasing DB latency → slow queries
  - Compare API latency vs DB latency → identify bottlenecks

- #### PromQL (Prometheus Query Language)

  - These are PromQL (Prometheus Query Language) queries used to analyze and calculate insights from raw metrics.

    - Requests per second (RPS): `rate(http_server_requests_seconds_count[1m])`
    - Average API latency: `rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])`
    - Error rate (5xx responses) `rate(http_server_requests_seconds_count{status="500"}[1m])`

    ```
    Metrics  → raw data collected from the application  
    PromQL   → used to query and analyze that data  
    ```

- #### What You Can Do in Prometheus
  - Query metrics using **PromQL**
  - Calculate rates (requests per second)
  - Measure latency (average, max)
  - Filter using labels (status, URI, operation, etc.)
  - Analyze trends over time

- #### Difference: Prometheus vs /actuator/prometheus
    ```
    /actuator/prometheus → raw metrics from application (no history)
    Prometheus           → stores and queries metrics over time
    ```

- #### Metrics Flow
    ```
    Application (Micrometer)
    → collects metrics
    
    /actuator/prometheus
    → exposes metrics
    
    Prometheus
    → scrapes and stores metrics
    
    Grafana
    → visualizes metrics
    ```

### Step 10: Visualize Metrics in Grafana
```
http://localhost:3000
```

- #### Open Grafana
  - Go to: `http://localhost:3000`
  - Login (default):
    - Username: admin
    - Password: admin

- #### Configure Prometheus Data Source
  - Navigate to: Connections → Data Sources → Add data source
  - Select: Prometheus
  - Set URL: `http://prometheus:9090`
  - Click Save & Test

- #### Import Dashboard:

  - Go to: Dashboards → New → Import  
    - OR open: http://localhost:3000/dashboard/import

  - Click “Upload JSON file” and select your dashboard file  
    - OR paste the JSON content manually

  - Select Prometheus as Data Source
  - Click Import

    <img width="3466" height="2084" alt="image" src="https://github.com/user-attachments/assets/d9d10a35-3114-46ee-9982-85bd85ef69ea" />

- #### What You Will See in Dashboard

  - API Metrics
    - Request rate (RPS)
    - Error rate (5xx)
    - API latency (average & max)
    - Endpoint-wise performance

  - Database Metrics
    - DB call rate
    - Average DB latency
    - Max DB latency
    - DB operations (save, etc.)

  - Comparison View
    - API latency vs DB latency (same graph)

- #### How to Read the Dashboard
    ```
    High API latency + low DB latency → issue in service/business logic
    High DB latency → database bottleneck
    High error rate → failures (check logs using correlationId)
    High request rate → traffic spike
    ```

- #### Metrics Flow
    ```
    Application (Micrometer)
    → /actuator/prometheus
    → Prometheus (scrapes & stores metrics)
    → Grafana (visualizes dashboards)
    ```

---

# What is correlationId?

- correlationId is a **unique ID generated per request**
- Used to track all logs (e.g. controller/service/repository logs) related to that request
- Helps answer: “Which logs belong to this request?”

Why it is needed
- Multiple users send requests at the same time
- Logs from different requests get mixed
- correlationId tags all logs of a single request with the same ID

Example
- Without correlationId
    ```
    Payment created  
    Fetching payment  
    Error occurred
    ```

- With correlationId
    ```
    [correlationId=abc123] Payment created  
    [correlationId=abc123] Fetching payment  
    [correlationId=abc123] Error occurred
    ```
    - Now you can clearly trace one request

What is MDC?
- MDC (Mapped Diagnostic Context) is part of SLF4J/Logback
- Stores data (like correlationId) per request/thread
- Automatically adds it to every log
- You set it once → all logs include it

correlationId Flow

```
Client
|
| HTTP Request
v
Filter (LoggingFilter)
|
| → Generate correlationId (UUID)
| → Put into MDC
v
Controller
|
Service
|
Repository
|
Logs generated at each layer
|
All logs contain SAME correlationId
|
Logstash → Elasticsearch → Kibana
```

```
Request received
 → correlationId generated
 → correlationId stored in MDC (thread-local)
 → controller/service/repository logs are written
 → all logs include same correlationId from MDC
 → logs go to Kibana (via ELK)
 → response completed
 → MDC cleared
```
```
Example Logs
[correlationId=abc123] Request received  
[correlationId=abc123] Processing payment  
[correlationId=abc123] Payment saved  
```

Where it is created
- Usually in a filter ([LoggingFilter](src/main/java/com/example/monitoring/logging/LoggingFilter.java))
    ```
    String correlationId = UUID.randomUUID().toString();
    
    MDC.put("correlationId", correlationId);
    
    try {
        filterChain.doFilter(request, response);
    } finally {
        MDC.clear();
    }
    ```
What it helps you do
- Identify logs for a specific request
- Track execution flow via logs
- Debug errors easily
- Find which request/user caused an issue

In practice
- Get correlationId from API response
- Search it in **Kibana**
- View complete request logs and errors

```
correlationId → identifies a request  
MDC           → ensures all logs carry that ID
```

---

# What is traceId?
- traceId is a **unique ID generated per request**
- Used to track the **complete execution flow** of that request
- Helps answer: “Where did this request go and how did it execute step by step?”

Why it is needed
- A request flows through multiple layers (controller, service, database)
- Sometimes across multiple services
- traceId links all these steps into a single end-to-end flow

Example
- Without traceId
    ```
    Controller called  
    Service executed  
    DB query executed
    ```

- With traceId
    ```
    [traceId=xyz456] HTTP POST /payments  
    [traceId=xyz456] PaymentController.createPayment  
    [traceId=xyz456] PaymentService.createPayment  
    [traceId=xyz456] DB Insert (payments)
    ```

    - Now you can trace the entire request flow

What it helps you do
- Track request execution **end-to-end**
- Identify which step is slow
- Visualize flow in **Zipkin**

How traceId is generated
- Automatically generated by Micrometer / OpenTelemetry
- Created when the request enters the application (HTTP layer)
- A root span is created at the same time
- No manual handling required
```
traceId → identifies complete request flow (end-to-end)
```

---

# What is spanId?
- spanId represents a **single operation (span)** in a request
- Each layer (HTTP, controller, service, DB) creates its own span
- Multiple spanIds exist under one traceId
- All spans together form the **complete trace**

traceId Flow
```
Client
  |
  | HTTP Request
  v
Spring Boot (Tracing starts)
  |
  | → traceId generated automatically
  | → root span (HTTP) created automatically
  v
Controller (Span - manually created using Observation API)
  |
Service (Span - manually created using Observation API)
  |
Repository / DB (Span - created automatically)
  |
Spans collected
  |
Sent to Zipkin
  |
Visualized as trace tree
```

Example Trace Structure (Zipkin)
```
POST /api/v1/payments
├── PaymentController.createPayment
│    └── PaymentService.createPayment
│         └── jdbc.insert (payments)
```

Example Span IDs
```
traceId: xyz456

Span 1 (HTTP)       → spanId: 111  
Span 2 (Controller) → spanId: 222  
Span 3 (Service)    → spanId: 333  
Span 4 (DB)         → spanId: 444
```
- Same traceId, different spanIds
- You can identify which span is slow (e.g., DB span taking more time)
- You can also identify failures at a specific layer (e.g., service or DB span marked as error)

What traceId helps you do
- Understand where a request travelled
- Identify which step is slow
- Find which layer failed
- Measure how long each step took

How to use traceId
- Call an API
- Copy traceId from response
- Open Zipkin: `http://localhost:9411`
- Search using traceId
- View complete request flow and timing

In practice
- traceId is returned in API response
- Used to debug request flow in Zipkin
- Helps identify performance bottlenecks and failures

Key Points
- traceId → identifies entire request flow (end-to-end)
- spanId → identifies individual steps within the request
- Generated automatically by **Micrometer Tracing / OpenTelemetry**
- Created when HTTP request enters the application
- Same traceId is shared across all spans
- Used in Zipkin to visualize request flow and timing
- No manual handling required

---

# Span Breakdown

How a single request is broken into spans inside your application.
- Controller → Service → DB

Request Flow with Span Breakdown
```
Client
  |
  |  HTTP Request
  v
[Span 1: HTTP POST /api/v1/payments]   ← ROOT SPAN (traceId = abc123)
  |
  v
    [Span 2: PaymentController.createPayment]
    |
    v
        [Span 3: PaymentService.createPayment]
        |
        v
            [Span 4: DB Insert (payments table)]
        |
        v
    [Span 5: Response Processing]
  |
  v
Client Response
```

How This Works Internally
- HTTP Span (Root)
  - Created automatically when request enters the application
- Controller & Service Spans
  - Represent business logic execution
  - Created manually using Micrometer Observation API
- Database Span
  - Created automatically by JDBC instrumentation
  - Captures query execution time and DB latency
  
Example with IDs
```
traceId: abc123

Span 1 (HTTP)       → spanId: 111  
Span 2 (Controller) → spanId: 222  
Span 3 (Service)    → spanId: 333  
Span 4 (DB)         → spanId: 444  
```
- Same traceId, different spanIds
- Helps identify slow spans (e.g., DB taking more time)
- Helps detect failures at a specific layer

Example Timing Breakdown
```
Total Request Time: 120 ms

├── Controller: 5 ms  
├── Service: 40 ms  
└── DB: 75 ms   ← bottleneck  
```
How You See This in Zipkin
```
POST /api/v1/payments
├── PaymentController.createPayment
│    └── PaymentService.createPayment
│         └── jdbc.insert (payments)
```
What You Can Understand from This
- Where time is spent (latency)
- Which layer is slow
- Where failure occurred
- Total request execution time

This Microservice APIs and Expected Behavior
- Success API: `POST /payments`
    - Spans are created for: HTTP → Controller → Service → DB
- Error API: `GET /simulate-error`
    - Spans are created for: HTTP → Controller → error span
- External Call API: `GET /external-call`
    - Spans are created for: HTTP → Controller → Service (includes delay)

---

# Sequence Diagram
Request → Logs → Trace → Metrics
`````
Client
  |
  | HTTP Request
  v
Spring Boot Application
  |
  |--- Generate correlationId (MDC)
  |--- Start Trace (traceId, spanId)
  |
  |--- Execute Business Logic
  |
  |--- Logs → Logback → Logstash → Elasticsearch → Kibana
  |
  |--- Traces → Zipkin
  |
  |--- Metrics → /actuator/prometheus → Prometheus → Grafana
  |
  v
HTTP Response (with traceId & correlationId)
  |
  v
Client
`````
How This Works
#### 1. Request Comes In
- correlationId created
- traceId + spanId created
#### 2. Logs Flow
```
App → Logback → Logstash → Elasticsearch → Kibana
```
- You search using **correlationId**
#### 3. Trace Flow
```   
App → OpenTelemetry → Zipkin
```
- You search using **traceId**
- Shows **timeline + spans**
#### 4. Metrics Flow
```   
App → Micrometer → Prometheus → Grafana
```
- Prometheus scrapes metrics
- Grafana visualizes

---

# Real Debugging Flow
1. Call the error API
2. Copy the traceId
3. Open Zipkin → find the failure
4. Copy the correlationId
5. Open Kibana → search related logs

Result:
- Zipkin → shows **WHERE** the failure occurred
- Kibana → shows **WHY** the failure occurred
- Prometheus → shows **HOW OFTEN** it occurs
- Grafana → shows **TRENDS over time**

---

# Conclusion

This project demonstrates a complete observability pipeline similar to real-world production systems.

You can:

- Monitor application health
- Debug failures using traces and logs
- Analyze performance using metrics
- Visualize system behavior using dashboards

---

# License

Free software, [Siraj Chaudhary](https://www.linkedin.com/in/sirajchaudhary/)
