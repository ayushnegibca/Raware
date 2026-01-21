# Raware

Raware is a distributed, event-driven inventory management system built with Spring Boot microservices. It demonstrates how large-scale e-commerce platforms manage warehouse inventory under high traffic using asynchronous messaging, decoupled services, and eventual consistency.

## Description

Raware manages product SKUs, inventory levels, reservations, releases, and reconciliation across microservices. It uses Apache Kafka for inter-service messaging and MySQL for persistent storage. Services are built with Spring Boot and Maven.

## Key features

- Inventory reservation and commit flows for order processing
- Event-driven communication with Apache Kafka
- Idempotent event handling and deduplication patterns
- Modular microservice design for independent development and deployment
- Unit and integration testing examples (JUnit, Mockito)

## Architecture overview

Typical components (adjust to match the repository):

- Order Service — accepts orders and publishes OrderCreated events
- Inventory Service — maintains stock, reserves/releases inventory and publishes inventory events
- Product Service — product and SKU metadata
- Apache Kafka — message broker (topics such as `orders.created`, `inventory.reserved`, `inventory.released`)
- MySQL — persistent storage (per-service schema or shared DB depending on setup)

## Prerequisites

- Java 17+ (or the Java version used by the project)
- Maven 3.8+
- Apache Kafka (local install or hosted)
- MySQL 8+ (local install or hosted)
- Optional: MySQL client, Kafka CLI tools for debugging

## Configuration

Each microservice uses Spring Boot configuration (application.yml or application.properties) and environment variables. Common variables to set:

- `SPRING_DATASOURCE_URL` (e.g. `jdbc:mysql://localhost:3306/raware?useSSL=false&allowPublicKeyRetrieval=true`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` (e.g. `localhost:9092`)
- `SERVER_PORT`
- `SPRING_PROFILES_ACTIVE`

Example minimal `application.yml` for a service:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/raware
    username: raware
    password: rawarepass
  kafka:
    bootstrap-servers: localhost:9092
server:
  port: 8081

## Running the system locally (no Docker)

1. Start MySQL
   - Install and start MySQL locally.
   - Create database and user (example):
     ```sql
     CREATE DATABASE raware;
     CREATE USER 'raware'@'localhost' IDENTIFIED BY 'rawarepass';
     GRANT ALL PRIVILEGES ON raware.* TO 'raware'@'localhost';
     ```
   - If the project includes Flyway or Liquibase, migrations can initialize the schema automatically.

2. Start Kafka
   - Install and start Zookeeper and Kafka locally (or use a hosted Kafka).
   - Quick start using Kafka binaries:
     - Start Zookeeper:
       `bin/zookeeper-server-start.sh config/zookeeper.properties`
     - Start Kafka:
       `bin/kafka-server-start.sh config/server.properties`
   - Create topics if needed, e.g.:
     `bin/kafka-topics.sh --create --topic orders.created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1`

3. Configure services
   - Update each service’s `application.yml` or set environment variables for MySQL and Kafka bootstrap servers.

4. Build and run
   - Build all modules:
     `mvn -T 1C clean package`
   - Run with Maven (example module):
     `mvn spring-boot:run -pl order-service`
   - Or run the packaged JAR:
     `java -jar order-service/target/order-service-0.0.1-SNAPSHOT.jar`

## Event topics and flows

Recommended topics:
- `orders.created`
- `orders.cancelled`
- `inventory.reserved`
- `inventory.released`
- `inventory.reservation.failed`

Typical order flow:
1. Client posts an order to Order Service.
2. Order Service publishes `orders.created` with order details.
3. Inventory Service consumes `orders.created` and attempts to reserve stock:
   - On success -> publish `inventory.reserved`.
   - On failure -> publish `inventory.reservation.failed`.
4. Order Service continues payment/fulfillment or cancels the order based on the reservation result.
5. On cancellation -> Order Service publishes `orders.cancelled`; Inventory Service releases stock.

## API examples

- Create an order:
  ```bash
  curl -X POST http://localhost:8082/api/orders \
    -H "Content-Type: application/json" \
    -d '{"userId":"user-1","items":[{"sku":"sku-1","quantity":2}]}'
  ```

- Check inventory:
  ```bash
  curl http://localhost:8081/api/inventory/sku-1
  ```

## Testing

- Unit tests:
  `mvn test -pl <module-path>`
- Integration tests:
  - Locally you can run integration tests against local MySQL and Kafka.
  - For CI, consider using Testcontainers to spin up Kafka and MySQL dynamically.

## Operational concerns & recommendations

- Idempotency: include unique event IDs and persist processed event IDs to avoid duplicate processing.
- Retries & dead-lettering: implement retry/backoff and dead-letter topics for poison messages.
- Schema migrations: use Flyway or Liquibase for DB changes.
- Observability: enable Spring Boot Actuator for health and metrics. Add logging and distributed tracing (Zipkin/Jaeger) as needed.
- Security: do not commit credentials. Use environment variables or a secrets manager for production.

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feat/my-feature`.
3. Add tests and documentation.
4. Open a pull request with a clear description.

## License

Add a `LICENSE` file to the repository (e.g., MIT or Apache-2.0). Let the maintainers choose or add the preferred license.

---