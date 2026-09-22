# 🏨 Hotel Service (`HOTEL-SERVICE`)

`HotelService` is an autonomous catalog and inventory microservice in the hotel and rating distributed ecosystem. It is responsible for creating, managing, and serving hotel profiles, location details, and service metadata. 

It is built with **Java 21**, **Spring Boot**, and **PostgreSQL**, and serves as a core downstream dependency consumed by **UserService** via **Spring Cloud OpenFeign**.

---

## 🏗️ Architecture & Component Interaction

`HotelService` registers with Eureka for discovery, loads external configuration dynamically, and provides catalog data to the orchestrator service.

```
                          +--------------------------------+
                          |   Config Server (:8085)        |
                          |   (Pulls central git config)   |
                          +---------------+----------------+
                                          |
                                          v
+-----------------------+     +--------------------------------+
| API Gateway (:8084)   | --> |  Service Registry (:8761)      |
| Route: /hotels/**     |     |  (Eureka Service Discovery)    |
+-----------+-----------+     +---------------+----------------+
            |                                 |
            v                                 v
+--------------------------------------------------------------+
|                     HOTEL SERVICE (:8082)                    |
|                                                              |
|  1. Manages hotel catalog data in PostgreSQL                 |
|  2. Exposes REST endpoints for CRUD operations               |
|  3. Registers dynamically with Eureka as `HOTEL-SERVICE`     |
|  4. Consumed by UserService Feign Client                     |
+------------------------------+-------------------------------+
                               |
                               v
               [(PostgreSQL DB: `microservice`)]
```

---

## 🔄 Inter-Service Execution Workflow

Here is how `HotelService` participates when a user's complete profile is requested:

```
[ Client / Postman ]
         │
         │ 1. GET /users/user/{userId}
         ▼
[ UserService (:8081) ]
         │
         │ 2. Queries local MySQL for User
         │ 3. Calls RatingService -> receives list of ratings with hotelIds
         ▼
[ Spring Cloud OpenFeign ]
         │
         │ 4. Resolves `HOTEL-SERVICE` via Eureka Registry (:8761)
         │ 5. GET /hotels/byId/{hotelId}
         ▼
+--------------------------------------------------------------+
| HotelController.getHotelById() (:8082)                       |
|                                                              |
|  - Queries PostgreSQL table `hotels`                         |
|  - Retrieves Hotel entity (id, name, location, about)        |
|  - Returns 200 OK with Hotel payload                         |
+------------------------------+-------------------------------+
                               │
                               │ 6. Response returned to UserService
                               ▼
[ UserService embeds Hotel inside Rating and delivers final payload ]
```

---

## 🗄️ Database Details

* **Database Engine:** PostgreSQL
* **Database Name:** `microservice`
* **Table:** `hotels`

### Table Schema Definition

```sql
CREATE DATABASE microservice;

\c microservice;

CREATE TABLE IF NOT EXISTS hotels (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(150) NOT NULL,
    about TEXT
);
```

---

## ⚙️ Configuration (`application.yml`)

```yaml
server:
  port: 8082

spring:
  application:
    name: HOTEL-SERVICE
  datasource:
    url: jdbc:postgresql://localhost:5432/microservice
    username: postgres
    password: your_postgres_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
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

### 1. Save New Hotel

* **Method:** `POST`
* **Direct URL:** `http://localhost:8082/hotels/saveHotel`
* **Via Gateway:** `http://localhost:8084/hotels/saveHotel`
* **Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "name": "hoti mahal",
  "location": "sambhajinagar",
  "about": "this is good place"
}
```

#### Response (`201 Created`)
```json
{
  "id": "80fbd12b-3f2b-4e07-a143",
  "name": "hoti mahal",
  "location": "sambhajinagar",
  "about": "this is good place"
}
```

---

### 2. Get Hotel By ID

* **Method:** `GET`
* **Direct URL:** `http://localhost:8082/hotels/byId/{hotelId}`
* **Via Gateway:** `http://localhost:8084/hotels/byId/{hotelId}`

#### Sample Call
```http
GET http://localhost:8082/hotels/byId/80fbd12b-3f2b-4e07-a143
```

#### Response (`200 OK`)
```json
{
  "id": "80fbd12b-3f2b-4e07-a143",
  "name": "hoti mahal",
  "location": "sambhajinagar",
  "about": "this is good place"
}
```

---

### 3. Get All Hotels

* **Method:** `GET`
* **Direct URL:** `http://localhost:8082/hotels/allHotels`
* **Via Gateway:** `http://localhost:8084/hotels/allHotels`

#### Response (`200 OK`)
```json
[
  {
    "id": "80fbd12b-3f2b-4e07-a143",
    "name": "hoti mahal",
    "location": "sambhajinagar",
    "about": "this is good place"
  },
  {
    "id": "4112b5d8-0d78-4d1e-ada0",
    "name": "shri ganesha",
    "location": "pune",
    "about": "24x7 services"
  }
]
```

---

## 📸 Testing & Verification Screenshots

Store your testing verification images inside the `/screenshots` folder:

### 1. Hotel Creation (`POST /hotels/saveHotel`)
![Create Hotel](./screenshots/postman_save_hotel.png)

### 2. Get Hotel By ID (`GET /hotels/byId/{hotelId}`)
![Get Hotel By ID](./screenshots/postman_get_hotel_by_id.png)

### 3. Fetch All Hotels (`GET /hotels/allHotels`)
![Get All Hotels](./screenshots/postman_get_all_hotels.png)

### 4. Eureka Registration (`HOTEL-SERVICE` status UP)
![Eureka Registration](./screenshots/eureka_hotel_service_up.png)

---

## 🚀 Running HotelService Locally

### Prerequisites
1. **Java 21 JDK** installed.
2. **Maven** installed.
3. **PostgreSQL Server** running on port `5432` with database `microservice`.
4. **Service Registry** (`Eureka Server` on port `8761`) started.
5. **Config Server** (port `8085`) running.

### Build and Run Commands
```bash
# Clone the repository
git clone https://github.com/<your-username>/HotelService.git
cd HotelService

# Clean and compile
mvn clean package -DskipTests

# Run the Spring Boot microservice
mvn spring-boot:run
```