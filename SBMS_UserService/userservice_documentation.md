# 👤 User Service (`USER-SERVICE`)

`UserService` is the core aggregation engine and orchestrator of this hotel and rating microservices ecosystem. It handles user account management and dynamically aggregates distributed data from **RatingService** and **HotelService** using **RestTemplate**, **Spring Cloud OpenFeign**, and **Resilience4j**.

---

## 🏗️ Architecture & Interaction Flow

`UserService` communicates with infrastructure components and downstream business services:

```
                          +-------------------------------+
                          | Config Server (:8085)         |
                          | (Fetches centralized config)  |
                          +---------------+---------------+
                                          |
                                          v
+-----------------------+     +-------------------------------+
| API Gateway (:8084)   | --> | Service Registry (:8761)      |
| Route: /users/**      |     | (Eureka Discovery & Lookup)   |
+-----------+-----------+     +---------------+---------------+
            |                                 |
            v                                 v
+-------------------------------------------------------------+
|                     USER SERVICE (:8081)                    |
|                                                             |
|  1. Reads/Writes local profile data -> MySQL (`college`)    |
|  2. Calls RatingService via Load-Balanced RestTemplate       |
|  3. Calls HotelService via OpenFeign Declarative Client     |
|  4. Fault protected via Resilience4j (Breaker/Retry/Limit) |
+------------------------------+------------------------------+
                               |
            +------------------+------------------+
            | (RestTemplate)                      | (FeignClient)
            v                                     v
+-----------------------+             +-----------------------+
| RatingService (:8083) |             | HotelService (:8082)  |
| DB: MySQL (`college`) |             | DB: PostgreSQL        |
+-----------------------+             +-----------------------+
```

---

## 🔄 Detailed Request Execution Workflow

When `GET /users/user/{userId}` is requested:

```
Client / Postman / Gateway
           │
           │ 1. GET /users/user/{userId}
           ▼
UserController.getSingleUser()
           │
           │ 2. Query MySQL (`college` database, `users` table)
           ▼
[User Profile Retrieved]
           │
           │ 3. Intercepted by Resilience4j (@RateLimiter, @CircuitBreaker, @Retry)
           ▼
RatingService Invocation (RestTemplate -> http://RATING-SERVICE/rating/userId/{userId})
           │
           │ 4. Receives List<Rating>
           ▼
Loop Over Ratings List
           │
           │ 5. For each rating: Call HotelService (FeignClient -> http://HOTEL-SERVICE/hotels/byId/{hotelId})
           ▼
[Hotel Metadata Attached to Each Rating]
           │
           │ 6. Full User payload assembled with complete List<Rating>
           ▼
Return Complete Aggregated JSON Response to Client
```

---

## 🛡️ Resilience4j Fault Tolerance

The primary aggregation endpoint is protected against network delays and downstream service downtime:

```java
@GetMapping("/user/{userId}")
@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
@Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
@RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
public ResponseEntity<User> getSingleUser(@PathVariable String userId) {
    // Aggregation logic calling RatingService and HotelService
}
```

### 1. Circuit Breaker (`ratingHotelBreaker`)
- **CLOSED:** Normal operating state. Outgoing calls to `RatingService` and `HotelService` proceed.
- **OPEN:** If error thresholds are exceeded (e.g. downstream service down), calls fail immediately and route to fallback, avoiding thread hangs.
- **HALF-OPEN:** Allows a limited number of test calls through to check if downstream services have recovered.

### 2. Retry Pattern (`ratingHotelService`)
- Automatically retries transient network interruptions before triggering a failure.

### 3. Rate Limiter (`userRateLimiter`)
- Protects the endpoint from high-traffic spikes and request flooding.

### Fallback Implementation
When downstream services fail or the circuit is open, `ratingHotelFallback(String userId, Exception ex)` handles the failure gracefully by returning the user profile with an empty or default rating list rather than throwing a `500 Internal Server Error`.

---

## 🗄️ Database Details

- **Database Engine:** MySQL
- **Database Name:** `college`
- **Table:** `users`

### Schema Definition
```sql
CREATE DATABASE IF NOT EXISTS college;
USE college;

CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    about TEXT
);
```

---

## ⚙️ Configuration (`application.yml`)

```yaml
server:
  port: 8081

spring:
  application:
    name: USER-SERVICE
  datasource:
    url: jdbc:mysql://localhost:3306/college?useSSL=false&serverTimezone=UTC
    username: root
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
  config:
    import: optional:configserver:http://localhost:8085

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true

# Resilience4j Configuration
resilience4j:
  circuitbreaker:
    instances:
      ratingHotelBreaker:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 6s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
  retry:
    instances:
      ratingHotelService:
        maxAttempts: 3
        waitDuration: 1s
  ratelimiter:
    instances:
      userRateLimiter:
        limitForPeriod: 5
        limitRefreshPeriod: 4s
        timeoutDuration: 1s
```

---

## 🔌 API Reference

### 1. Save New User
- **Method:** `POST`
- **Direct URL:** `http://localhost:8081/users/saveUser`
- **Via Gateway:** `http://localhost:8084/users/saveUser`
- **Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "name": "chandu",
  "email": "chandu@gmail.com",
  "about": "we are expert in computer design i design the business related designs"
}
```

**Response (`201 Created`):**
```json
{
  "userId": "3f93a924-0d97-4236-a487",
  "name": "chandu",
  "email": "chandu@gmail.com",
  "about": "we are expert in computer design i design the business related designs"
}
```

---

### 2. Get All Users
- **Method:** `GET`
- **Direct URL:** `http://localhost:8081/users/getAllUsers`
- **Via Gateway:** `http://localhost:8084/users/getAllUsers`

**Response (`200 OK`):**
```json
[
  {
    "userId": "3f93a924-0d97-4236-a487",
    "name": "chandu",
    "email": "chandu@gmail.com",
    "about": "we are expert in computer design i design the business related designs"
  }
]
```

---

### 3. Get Single User (Aggregated Data)
- **Method:** `GET`
- **Direct URL:** `http://localhost:8081/users/user/3f93a924-0d97-4236-a487`
- **Via Gateway:** `http://localhost:8084/users/user/3f93a924-0d97-4236-a487`

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
    },
    {
      "ratingId": "e330e2b1-45f0-47fb-9061",
      "userId": "3f93a924-0d97-4236-a487",
      "hotelId": "4112b5d8-0d78-4d1e-ada0",
      "rating": 6,
      "feedback": "this is good for friends",
      "hotel": {
        "id": "4112b5d8-0d78-4d1e-ada0",
        "name": "shri ganesha",
        "location": "pune",
        "about": "24x7 services"
      }
    }
  ]
}
```

---

## 📸 Testing & Verification Screenshots

Store testing images inside a `screenshots/` directory within this repository:

### 1. User Creation (`POST /users/saveUser`)
![Save User](./screenshots/postman_save_user.png)

### 2. Aggregated Single User Response (`GET /users/user/{userId}`)
![Get User Aggregated](./screenshots/postman_get_user_aggregated.png)

### 3. API Gateway Routing Test (`GET http://localhost:8084/users/user/{userId}`)
![API Gateway Route](./screenshots/postman_gateway_route.png)

### 4. Eureka Registration Status (`http://localhost:8761`)
![Eureka Status](./screenshots/eureka_registration_status.png)

### 5. Resilience4j Fallback Response
![Fallback Active](./screenshots/resilience4j_fallback_active.png)

---

## 🚀 Running UserService Locally

### Prerequisites
1. **Java 21 JDK** installed.
2. **Maven** installed.
3. **MySQL Server** running with database `college`.
4. **Service Registry** (`Eureka Server` on port `8761`) started.
5. **Config Server** (port `8085`) running with config repository.

### Commands
```bash
# Clone the repository
git clone https://github.com/<your-username>/UserService.git
cd UserService

# Clean and package with Maven
mvn clean package -DskipTests

# Run the Spring Boot application
mvn spring-boot:run
```