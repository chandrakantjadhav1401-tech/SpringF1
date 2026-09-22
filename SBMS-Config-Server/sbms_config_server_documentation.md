# ⚙️ SBMS Config Server (`SBMS-Config-server`)

`SBMS-Config-server` is the centralized, externalized configuration hub for this hotel & rating microservices ecosystem, built with **Spring Cloud Config Server** and **Java 21**.

Instead of distributing database credentials, service URLs, and Resilience4j thresholds inside each microservice's local properties, all microservices fetch their environment-specific configurations dynamically from an external Git repository upon boot.

---

## 🏛️ Centralized Configuration Architecture

```
                      +-------------------------------------------------------+
                      |                 Remote Git Repository                 |
                      | https://github.com/chandrakantjadhav1401-tech/        |
                      |              Microservices-Config-Server              |
                      +---------------------------+---------------------------+
                                                  |
                                                  | Git Clone / Fetch
                                                  v
                      +-------------------------------------------------------+
                      |         SBMS Config Server (:8085)                    |
                      |          (@EnableConfigServer)                        |
                      +---------------------------+---------------------------+
                                                  |
                     HTTP REST Config Endpoints   | (/{application}/{profile})
                                                  |
            +--------------------+----------------+--------------------+
            |                    |                                     |
            v                    v                                     v
+-----------------------+ +--------------------+             +--------------------+
| UserService (:8081)   | |RatingService(:8083)|             | HotelService(:8082)|
| - MySQL `college` DB  | | - MySQL `college`  |             | - Postgres DB      |
| - Resilience4j limits | | - App Port configs |             | - App Port configs |
+-----------------------+ +--------------------+             +--------------------+
```

---

## 🔄 Configuration Loading Lifecycle

1. **Config Server Boot:** `SBMS-Config-server` initializes on port `8085` and establishes a connection with the remote GitHub repository.
2. **Client Boot (e.g., UserService):** During bootstrap, `UserService` queries `http://localhost:8085/USER-SERVICE/default`.
3. **Property Resolution:** Config Server reads the remote repository, parses `USER-SERVICE.yml`, and returns the properties as JSON.
4. **Context Injection:** Spring Boot injects these properties into the client's `ApplicationContext` before initializing database connections, Eureka registration, or Resilience4j beans.

---

## ⚙️ Tech Stack & Dependencies

- **Language & Framework:** Java 21, Spring Boot
- **Spring Cloud Module:** Spring Cloud Config Server (`spring-cloud-config-server`)
- **Port:** `8085`
- **External Git Repository:** [Microservices-Config-Server](https://github.com/chandrakantjadhav1401-tech/Microservices-Config-Server)

### Maven Dependency (`pom.xml`)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

---

## 💻 Main Application Class

The server is enabled using the `@EnableConfigServer` annotation:

```java
package com.microservices.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SbmsConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbmsConfigServerApplication.class, args);
    }
}
```

---

## 📄 Server Configuration (`application.yml`)

```yaml
server:
  port: 8085

spring:
  application:
    name: CONFIG-SERVER
  cloud:
    config:
      server:
        git:
          uri: https://github.com/chandrakantjadhav1401-tech/Microservices-Config-Server
          clone-on-start: true
          default-label: main
          timeout: 5

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

---

## 📁 Remote Git Repository Structure

In your GitHub repository ([Microservices-Config-Server](https://github.com/chandrakantjadhav1401-tech/Microservices-Config-Server)), properties files are organized by application name:

```text
Microservices-Config-Server/
├── application.yml          # Global properties shared across all services
├── USER-SERVICE.yml         # UserService DB, Feign, and Resilience4j configs
├── RATING-SERVICE.yml       # RatingService MySQL and endpoint configs
├── HOTEL-SERVICE.yml        # HotelService PostgreSQL and JPA configs
└── API-GATEWAY.yml          # Route predicates and filter mappings
```

---

## 🔌 Config Inspection Endpoints

You can verify loaded configuration profiles using standard Spring Cloud Config REST endpoints:

| Endpoint Pattern | Description |
|---|---|
| `GET http://localhost:8085/{application}/{profile}` | Fetch properties for application under specific profile |
| `GET http://localhost:8085/{application}-{profile}.yml` | Fetch raw YAML format |
| `GET http://localhost:8085/{application}/{profile}/{label}` | Fetch configuration from specific Git branch/label |

### Example Verification URLs:
- `http://localhost:8085/USER-SERVICE/default`
- `http://localhost:8085/HOTEL-SERVICE/default`
- `http://localhost:8085/RATING-SERVICE/default`

---

## 📸 Verification & Screenshots

Place your testing screenshots inside a `/screenshots` folder within this repository:

### 1. Config Server Startup Log
Shows successful connection and clone from the remote GitHub repository:
![Config Server Git Clone](./screenshots/config_server_boot_clone.png)

### 2. Browser Verification of Raw JSON Properties (`/USER-SERVICE/default`)
Shows injected configuration keys, database settings, and Resilience4j parameters:
![Config REST Endpoint](./screenshots/config_endpoint_verification.png)

### 3. Eureka Dashboard Registration (`:8761`)
Shows `CONFIG-SERVER` registered and in `UP` status:
![Config Server Eureka](./screenshots/config_server_eureka.png)

---

## 🚀 Running Locally

### Startup Order
> **Critical:** `SBMS-Config-server` must be started **before** the client microservices (`UserService`, `RatingService`, `HotelService`, `API-GATEWAY`) so they can read their configurations during boot.

```bash
# Clone the repository
git clone <your-config-server-repo-url>
cd SBMS-Config-server

# Build the project
mvn clean install

# Run the Config Server
mvn spring-boot:run
```