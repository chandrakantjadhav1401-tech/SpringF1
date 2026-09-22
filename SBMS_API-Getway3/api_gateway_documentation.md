# 🌐 API Gateway (`API-GATEWAY`)

`API-GATEWAY` serves as the single unified entry point (Edge Server / Reverse Proxy) for all incoming client traffic in this hotel and rating microservices ecosystem. Built with **Spring Cloud Gateway** and **Java 21**, it centralizes routing, protocol translation, and client request abstraction.

Clients (web frontends, mobile applications, or Postman) interact strictly with the Gateway on port `8084`, completely isolated from direct internal service ports.

---

## 🏛️ Gateway Architecture & Request Routing

The Gateway queries **ServiceRegistry** (`Eureka Server` on port `8761`) to resolve dynamic service instances using the `lb://` (load balancer) protocol:

```
                            [ External Client / Postman ]
                                         │
                                         │ Requests (Port :8084)
                                         ▼
                      +--------------------------------------+
                      |         API Gateway (:8084)          |
                      |        (Spring Cloud Gateway)        |
                      +------------------+-------------------+
                                         │
                 Queries Discovery Registry for Service Instances
                                         │
                                         ▼
                      +--------------------------------------+
                      |      Eureka Server (:8761)           |
                      +------------------+-------------------+
                                         │
         ┌───────────────────────────────┼───────────────────────────────┐
         │ lb://USER-SERVICE             │ lb://RATING-SERVICE           │ lb://HOTEL-SERVICE
         ▼                               ▼                               ▼
+─────────────────+             +─────────────────+             +─────────────────+
|   UserService   |             |  RatingService  |             |  HotelService   |
|     (:8081)     |             |     (:8083)     |             |     (:8082)     |
+─────────────────+             +─────────────────+             +─────────────────+
```

---

## 🔄 Gateway Execution Workflow

```
1. Client sends request: GET http://localhost:8084/users/user/{userId}
   │
2. API Gateway inspects path against configured Route Predicates:
   Path matches: `/users/**` -> target: `lb://USER-SERVICE`
   │
3. Gateway queries Eureka with Service ID "USER-SERVICE"
   Eureka returns active host and port (e.g., localhost:8081)
   │
4. Gateway forwards request directly to UserService instance
   │
5. UserService processes request, aggregates data, and returns response
   │
6. Gateway streams response back to Client with HTTP 200 OK
```

---

## ⚙️ Tech Stack & Dependencies

- **Language & Framework:** Java 21, Spring Boot
- **Gateway Engine:** Spring Cloud Gateway (Reactive / WebFlux non-blocking engine)
- **Service Discovery Client:** Spring Cloud Netflix Eureka Client
- **Configuration Client:** Spring Cloud Config Client (`SBMS-Config-server` on `:8085`)
- **Port:** `8084`

### Maven Dependencies (`pom.xml`)
```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Eureka Client for Dynamic Service Discovery -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Config Client to pull configs from Config Server (:8085) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
</dependencies>
```

---

## 💻 Main Application Class

```java
package com.microservices.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

---

## 📄 Gateway Configuration (`application.yml`)

```yaml
server:
  port: 8084

spring:
  application:
    name: API-GATEWAY
  config:
    import: optional:configserver:http://localhost:8085
  cloud:
    gateway:
      routes:
        # Route 1: UserService
        - id: USER-SERVICE
          uri: lb://USER-SERVICE
          predicates:
            - Path=/users/**

        # Route 2: RatingService
        - id: RATING-SERVICE
          uri: lb://RATING-SERVICE
          predicates:
            - Path=/rating/**

        # Route 3: HotelService
        - id: HOTEL-SERVICE
          uri: lb://HOTEL-SERVICE
          predicates:
            - Path=/hotels/**

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true
```

---

## 🔀 Route Mapping & Endpoints Matrix

All external calls route through port `8084`:

| Client Request (Gateway Port 8084) | HTTP Method | Routed Microservice | Internal Destination |
|---|---|---|---|
| `http://localhost:8084/users/user/{userId}` | `GET` | `USER-SERVICE` | `http://localhost:8081/users/user/{userId}` |
| `http://localhost:8084/users/saveUser` | `POST` | `USER-SERVICE` | `http://localhost:8081/users/saveUser` |
| `http://localhost:8084/users/getAllUsers` | `GET` | `USER-SERVICE` | `http://localhost:8081/users/getAllUsers` |
| `http://localhost:8084/rating/saveRating` | `POST` | `RATING-SERVICE` | `http://localhost:8083/rating/saveRating` |
| `http://localhost:8084/rating/getAll` | `GET` | `RATING-SERVICE` | `http://localhost:8083/rating/getAll` |
| `http://localhost:8084/rating/userId/{userId}` | `GET` | `RATING-SERVICE` | `http://localhost:8083/rating/userId/{userId}` |
| `http://localhost:8084/rating/hotelId/{hotelId}` | `GET` | `RATING-SERVICE` | `http://localhost:8083/rating/hotelId/{hotelId}` |
| `http://localhost:8084/hotels/saveHotel` | `POST` | `HOTEL-SERVICE` | `http://localhost:8082/hotels/saveHotel` |
| `http://localhost:8084/hotels/byId/{hotelId}` | `GET` | `HOTEL-SERVICE` | `http://localhost:8082/hotels/byId/{hotelId}` |
| `http://localhost:8084/hotels/allHotels` | `GET` | `HOTEL-SERVICE` | `http://localhost:8082/hotels/allHotels` |

---

## 📨 Sample Gateway Requests & Responses

### 1. Retrieve Aggregated User Profile via Gateway
```http
GET http://localhost:8084/users/user/3f93a924-0d97-4236-a487
```

**Response (`200 OK`):**
```json
{
  "userId": "3f93a924-0d97-4236-a487",
  "name": "chandu",
  "email": "chandu@gmail.com",
  "about": "we are expert in computer design i design the business related designs",
  "ratings": [
    {
      "ratingId": "90f2f03b-7c28-42b3-bc01",
      "userId": "3f93a924-0d97-4236-a487",
      "hotelId": "80fbd12b-3f2b-4e07-a143",
      "rating": 8,
      "feedback": "excellent",
      "hotel": {
        "id": "80fbd12b-3f2b-4e07-a143",
        "name": "hoti mahal",
        "location": "sambhajinagar",
        "about": "this is good place"
      }
    }
  ]
}
```

### 2. Save Rating via Gateway
```http
POST http://localhost:8084/rating/saveRating
Content-Type: application/json

{
  "userId": "3f93a924-0d97-4236-a487",
  "hotelId": "80fbd12b-3f2b-4e07-a143",
  "rating": 8,
  "feedback": "excellent"
}
```

---

## 📸 API Gateway Verification & Testing

Place verification screenshots in the `/screenshots` directory:

### 1. Gateway Routing Test (`GET http://localhost:8084/users/user/{userId}`)
![Gateway User Route](./screenshots/gateway_user_route_test.png)

### 2. Gateway Rating Route (`POST http://localhost:8084/rating/saveRating`)
![Gateway Rating Route](./screenshots/gateway_rating_test.png)

### 3. Gateway Hotel Route (`GET http://localhost:8084/hotels/allHotels`)
![Gateway Hotel Route](./screenshots/gateway_hotels_test.png)

### 4. Eureka Dashboard Showing `API-GATEWAY` Registered
![Eureka Gateway Status](./screenshots/gateway_eureka_status.png)

---

## 🚀 Running Locally

### Startup Order
1. **SBMS-Config-Server (`:8085`)**
2. **ServiceRegistry (`:8761`)**
3. **Downstream Services:** `HotelService` (`:8082`), `RatingService` (`:8083`), `UserService` (`:8081`)
4. **API-GATEWAY (`:8084`)**

### Commands
```bash
# Clone the repository
git clone <your-api-gateway-repo-url>
cd API-GATEWAY

# Clean and package
mvn clean package -DskipTests

# Run the Gateway
mvn spring-boot:run
```