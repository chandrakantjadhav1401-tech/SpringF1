# ⭐ Rating Service (`RATING-SERVICE`)

`RatingService` is an independent microservice responsible for handling user reviews, star ratings, and feedback across hotels. It stores rating information linked to both `userId` and `hotelId`, acting as the analytical feedback layer for the hotel reservation and review platform.

It is built with **Java 21**, **Spring Boot**, and **MySQL**, and registers dynamically with **Eureka Service Registry** to serve inter-service calls initiated by **UserService**.

---

## 🏗️ Architecture & Ecosystem Interaction

`RatingService` pulls centralized configuration from Config Server, registers its network location with Eureka, and persists feedback records in a dedicated table inside MySQL (`college` database).

```
                          +--------------------------------+
                          |   Config Server (:8085)        |
                          |   (Central Git Repository)     |
                          +---------------+----------------+
                                          |
                                          v
+-----------------------+     +--------------------------------+
| API Gateway (:8084)   | --> |  Service Registry (:8761)      |
| Route: /rating/**     |     |  (Eureka Service Discovery)    |
+-----------+-----------+     +---------------+----------------+
            |                                 |
            v                                 v
+--------------------------------------------------------------+
|                    RATING SERVICE (:8083)                    |
|                                                              |
|  1. Records star ratings (1-10) and user comments            |
|  2. Fetches ratings by userId (for UserService aggregation)  |
|  3. Fetches ratings by hotelId (for hotel analytics)         |
|  4. Persists records to MySQL (`college` DB)                 |
+------------------------------+-------------------------------+
                               |
                               v
                  [(MySQL DB: `college`)]
```

---

## 🔄 Inter-Service Execution Workflow

Here is how `RatingService` functions during user profile aggregation:

```
[ Client / Postman ]
         │
         │ 1. GET /users/user/{userId}
         ▼
[ UserService (:8081) ]
         │
         │ 2. Queries MySQL for user profile details
         │ 3. Resolves `RATING-SERVICE` via Eureka Registry (:8761)
         ▼
[ Load-Balanced RestTemplate / FeignClient ]
         │
         │ 4. GET http://RATING-SERVICE/rating/userId/{userId}
         ▼
+--------------------------------------------------------------+
| RatingController.getRatingByUserId() (:8083)                 |
|                                                              |
|  - Queries MySQL table `user_ratings`                        |
|  - Retrieves all rating entities matching the userId         |
|  - Returns HTTP 200 OK with List<Rating>                     |
+------------------------------+-------------------------------+
                               │
                               │ 5. Returns rating list to UserService
                               ▼
[ UserService loops through ratings to fetch Hotel details for each hotelId ]
```

---

## 🗄️ Database Details

- **Database Engine:** MySQL
- **Database Name:** `college`
- **Table:** `user_ratings` (or `ratings`)

### Table Schema Definition

```sql
CREATE DATABASE IF NOT EXISTS college;
USE college;

CREATE TABLE IF NOT EXISTS user_ratings (
    rating_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ⚙️ Configuration (`application.yml`)

```yaml
server:
  port: 8083

spring:
  application:
    name: RATING-SERVICE
  datasource:
    url: jdbc:mysql://localhost:3306/college?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
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
        format_sql: true
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
```

---

## 🔌 API Reference

### 1. Save New Rating
- **Method:** `POST`
- **Direct URL:** `http://localhost:8083/rating/saveRating`
- **Via Gateway:** `http://localhost:8084/rating/saveRating`
- **Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "userId": "3f93a924-0d97-4236-a487",
  "hotelId": "80fbd12b-3f2b-4e07-a143",
  "rating": 8,
  "feedback": "excellent"
}
```

#### Response (`201 Created`)
```json
{
  "ratingId": "90f2f03b-7c28-42b3-bc01",
  "userId": "3f93a924-0d97-4236-a487",
  "hotelId": "80fbd12b-3f2b-4e07-a143",
  "rating": 8,
  "feedback": "excellent"
}
```

---

### 2. Get All Ratings
- **Method:** `GET`
- **Direct URL:** `http://localhost:8083/rating/getAll`
- **Via Gateway:** `http://localhost:8084/rating/getAll`

#### Response (`200 OK`)
```json
[
  {
    "ratingId": "90f2f03b-7c28-42b3-bc01",
    "userId": "3f93a924-0d97-4236-a487",
    "hotelId": "80fbd12b-3f2b-4e07-a143",
    "rating": 8,
    "feedback": "excellent"
  },
  {
    "ratingId": "e330e2b1-45f0-47fb-9061",
    "userId": "3f93a924-0d97-4236-a487",
    "hotelId": "4112b5d8-0d78-4d1e-ada0",
    "rating": 6,
    "feedback": "this is good for friends"
  }
]
```

---

### 3. Get Ratings by User ID
- **Method:** `GET`
- **Direct URL:** `http://localhost:8083/rating/userId/{userId}`
- **Via Gateway:** `http://localhost:8084/rating/userId/{userId}`

#### Sample Call
```http
GET http://localhost:8083/rating/userId/3f93a924-0d97-4236-a487
```

#### Response (`200 OK`)
```json
[
  {
    "ratingId": "90f2f03b-7c28-42b3-bc01",
    "userId": "3f93a924-0d97-4236-a487",
    "hotelId": "80fbd12b-3f2b-4e07-a143",
    "rating": 8,
    "feedback": "excellent"
  },
  {
    "ratingId": "e330e2b1-45f0-47fb-9061",
    "userId": "3f93a924-0d97-4236-a487",
    "hotelId": "4112b5d8-0d78-4d1e-ada0",
    "rating": 6,
    "feedback": "this is good for friends"
  }
]
```

---

### 4. Get Ratings by Hotel ID
- **Method:** `GET`
- **Direct URL:** `http://localhost:8083/rating/hotelId/{hotelId}`
- **Via Gateway:** `http://localhost:8084/rating/hotelId/{hotelId}`

#### Sample Call
```http
GET http://localhost:8083/rating/hotelId/80fbd12b-3f2b-4e07-a143
```

#### Response (`200 OK`)
```json
[
  {
    "ratingId": "90f2f03b-7c28-42b3-bc01",
    "userId": "3f93a924-0d97-4236-a487",
    "hotelId": "80fbd12b-3f2b-4e07-a143",
    "rating": 8,
    "feedback": "excellent"
  }
]
```

---

## 📸 Testing & Verification Screenshots

Save your test screenshots inside the `/screenshots` directory in your repo:

### 1. Create Rating (`POST /rating/saveRating`)
![Save Rating](./screenshots/save_rating.png)

### 2. Fetch Ratings by User ID (`GET /rating/userId/{userId}`)
![Get Ratings By User](./screenshots/get_ratings_by_user.png)

### 3. Fetch Ratings by Hotel ID (`GET /rating/hotelId/{hotelId}`)
![Get Ratings By Hotel](./screenshots/get_ratings_by_hotel.png)

### 4. Eureka Registration Status (`RATING-SERVICE` marked UP)
![Eureka Dashboard](./screenshots/eureka_rating_service.png)

---

## 🚀 Running RatingService Locally

### Prerequisites
1. **Java 21 JDK** installed.
2. **Maven** installed.
3. **MySQL Server** running with database `college`.
4. **Service Registry** (`Eureka Server` on port `8761`) running.
5. **Config Server** (port `8085`) running.

### Build and Run Commands
```bash
# Clone the repository
git clone https://github.com/<your-username>/RatingService.git
cd RatingService

# Clean and package
mvn clean package -DskipTests

# Run the Spring Boot microservice
mvn spring-boot:run
```