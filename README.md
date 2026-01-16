# Smart Parking System (INSA) — Proof of Concept

A small Spring Boot–based system that manages the full life cycle of a parking spot. It is composed of four microservices and two helpers. Services communicate synchronously over HTTP with clear, narrow REST APIs. A MySQL schema persists spot states, reservations, sessions, and payments. A simple UI aggregates data for a live dashboard.
<p align="center">
<img width="639" height="391" alt="image" src="https://github.com/user-attachments/assets/b5b03e36-c679-4d73-b23f-e67865c3d509" />
</p>

## Services

| Service              | Default port | Base path           | Owns (DB tables)            | Purpose |
|----------------------|--------------|---------------------|-----------------------------|---------|
| Parking Spot         | 8082         | `/api/spots`        | `EMPLACEMENT`               | Source of truth for spot catalogue and state machine (`Libre`, `Réservé`, `Occupé`). Only service allowed to change spot state. |
| Reservation          | 8083         | `/api/reservations` | `RESERVATION`               | Creates and validates bookings for a time window. Never marks a spot `Occupé`. |
| Entry and Exit       | 8084         | `/api/sessions`     | `SESSION_PARKING`           | Gatekeeper. Starts a session at entry, frees the spot at exit, collaborates with Payment. |
| Payment              | (configure)  | `/api/payments`     | `TRANSACTION`               | Quotes and records payment. In the PoC, payment is always accepted. |
| Sensor (simulator)   | (configure)  | —                   | `SENSOR_LECTURE` (optional) | Periodically flips a random spot to `Libre` or `Occupé` by calling the Parking Spot API. |
| Client UI            | 8080         | `/`                 | —                           | Thymeleaf MVC page that shows the live grid and provides “enter” and “exit” helpers. |

## Database

Tables mirror the domain: `EMPLACEMENT` (spots), `RESERVATION` (bookings), `SESSION_PARKING` (sessions), and `TRANSACTION` (payments). Foreign keys keep relationships coherent. Load the DDL used in the report’s “Database schema” section or run the initialiser present in the project if provided by your instructors. Keep `spring.jpa.hibernate.ddl-auto=none` so DDL is controlled.

Useful indexes in practice: on `EMPLACEMENT(status)` for fast free-spot lookups, on `SESSION_PARKING(plaque_immat)` for exit, and on `TRANSACTION(id_session)` for receipts.

## Configuration

Each service uses standard Spring `application.properties`. Use the same JDBC URL for all services in this PoC and keep times in UTC.

Example (adapt per service):

```properties
spring.datasource.url=jdbc:mysql://<DB_HOST>:3306/<DB_NAME>?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASS>
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
server.port=8082   # set per service (8082 spots, 8083 reservations, 8084 entry-exit, 8080 UI)
```
The UI client sets:
```properties
server.port=8080
spring.application.name=client-ui
spring.thymeleaf.cache=false
```
## How to run (development)

### 1 - Start MySQL and load the schema and seed data once.

### 2 - Launch the services in separate terminals from their module directories:
```properties
# Parking Spot (8082)
mvn spring-boot:run

# Reservation (8083)
mvn spring-boot:run

# Entry/Exit (8084)
mvn spring-boot:run

# Payment (configure port in properties)
mvn spring-boot:run

# Sensor (optional simulator; enables @Scheduled and RestTemplate)
mvn spring-boot:run

# Client UI (8080)
mvn spring-boot:run
```
### 3 - Open the dashboard:
```properties
http://localhost:8080/
```
The page auto-refreshes every few seconds. When the Sensor service is running, it flips random spots between `Libre` and `Occupé`, which the Parking Spot service persists and the UI displays.

## Key endpoints
### Parking Spot service (8082)
```properties
GET  /api/spots                              # list all spots
PUT  /api/spots/{id}/status                  # body: "Libre" | "Réservé" | "Occupé" (JSON string or plain text)
```
### Reservation service (8083)
```properties
POST /api/reservations                       # body: { idSpot|null, plaqueImmat, heureDebut, heureFin }
GET  /api/reservations/{id}
POST /api/reservations/{id}/validate         # body: { nowIso }
PUT  /api/reservations/{id}/cancel
```
### Entry and Exit service (8084)
```properties
POST /api/sessions/enter                     # body: { plaqueImmat, idSpot? } returns session + assignedSpotId
POST /api/sessions/exit                      # body: { plaqueImmat } returns receipt and frees the spot
```
### Payment service (port as configured)
```properties
POST /api/payments/quote                     # body: { sessionId }
POST /api/payments/pay                       # body: { sessionId, amount, method }
```

## Using curl
### List spots
```properties
curl -s http://localhost:8082/api/spots | jq
```
### Enter without reservation.
```properties
curl -s -X POST http://localhost:8084/api/sessions/enter \
  -H 'Content-Type: application/json' \
  -d '{ "plaqueImmat": "AB-123-CD" }' | jq
```
### Exit and free the spot.
```properties
curl -s -X POST http://localhost:8084/api/sessions/exit \
  -H 'Content-Type: application/json' \
  -d '{ "plaqueImmat": "AB-123-CD" }' | jq
```
### Create then validate a reservation.
```properties
curl -s -X POST http://localhost:8083/api/reservations \
  -H 'Content-Type: application/json' \
  -d '{ "idSpot": null, "plaqueImmat": "CD-456-EF",
        "heureDebut": "2026-01-13T14:00:00Z", "heureFin":"2026-01-13T16:00:00Z" }' | jq

curl -s -X POST http://localhost:8083/api/reservations/42/validate \
  -H 'Content-Type: application/json' \
  -d '{ "nowIso": "2026-01-13T14:15:00Z" }' | jq
```
## Notes and limitations
This proof of concept keeps the scope narrow. Payment is always accepted. There is no authentication, authorisation, audit trail, or fault injection. A simulator stands in for physical sensors. The design isolates responsibilities per service and keeps endpoints idempotent where appropriate, which makes the system easy to extend.
## Repository map (indicative)
```properties
BE_Archi-main/
  parking-spot-service/
  reservation-service/
  entry-exit-service/
  payment-service/
  sensor-service/
  client-ui/
```
