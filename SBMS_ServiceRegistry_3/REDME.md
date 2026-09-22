
# 🧭 Service Registry (`ServiceRegistry_3` / Eureka Server)

`ServiceRegistry_3` is the central service discovery engine for this microservices ecosystem, powered by **Netflix Eureka Server** and **Spring Cloud Netflix Eureka Server**. 

In this distributed architecture, microservices do not hardcode target IP addresses or port numbers. Instead, every service registers itself here upon startup, reports regular heartbeats, and queries the registry dynamically to locate peer instances for client-side load balancing.

---

## 🏛️ Architecture & Role in Ecosystem

The Service Registry acts as the phonebook for all services:



                             +--------------------------------+
                             |  Service Registry (:8761)      |
                             |     (Eureka Server)            |
                             +---------------+----------------+
                                             ^
        +--------------------+---------------+--------------------+
        | Register &         | Register &    | Register &         | Register &
        | Heartbeat          | Heartbeat     | Heartbeat          | Heartbeat
        v                    v               v                    v


+-----------------------+ +--------------------+ +--------------------+ +--------------------+
|  API Gateway (:8084)  | | UserService (:8081)| |RatingService(:8083)| | HotelService(:8082)|
|  (Discovers Routes)   | | (Discovers Rating  | | (Registered        | | (Registered        |
|                       | |  & Hotel Service)  | |  Microservice)     | |  Microservice)     |
+-----------------------+ +--------------------+ +--------------------+ +--------------------+



---

## 🔄 How Eureka Service Discovery Works

1. **Service Registration:** When `UserService`, `RatingService`, `HotelService`, or `API-GATEWAY` boots up, it sends a `POST` request to Eureka with its instance metadata (service ID, IP address, port, health check URL).
2. **Heartbeats / Renewal:** Every 30 seconds, registered clients send heartbeat signals to Eureka to keep their lease active.
3. **Registry Fetching:** When `UserService` needs to call `RatingService` via `RestTemplate` or `FeignClient`, it asks Eureka: *"Where is RATING-SERVICE running?"* Eureka returns the active host and port dynamically.
4. **Self-Preservation Mode:** If network glitches cause Eureka to stop receiving heartbeats, it avoids abruptly dropping instances to protect the network from false evictions.

---

## ⚙️ Tech Stack & Dependencies

- **Language & Framework:** Java 21, Spring Boot
- **Spring Cloud Version:** Spring Cloud Netflix Eureka Server
- **Build Tool:** Maven
- **Port:** `8761`
- **Configuration Server:** Integrated with `SBMS-Config-server` (`:8085`)

### Maven Dependency (`pom.xml`)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

```

---

## 💻 Main Application Class

The server is enabled using the `@EnableEurekaServer` annotation:

java
package com.microservices.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}


---

## 📄 Configuration (`application.yml`)

Because this application **is** the discovery server, it disables registering with itself:

yaml
server:
  port: 8761

spring:
  application:
    name: SERVICE-REGISTRY

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    wait-time-in-ms-when-sync-empty: 0
    enable-self-preservation: true

 

---

## 📊 Expected Registered Instances Matrix

When the complete ecosystem is started, the Eureka Web Dashboard at `http://localhost:8761` displays:

| Application Name | Port | Description |
| --- | --- | --- |
| `API-GATEWAY` | `8084` | Edge routing entry point |
| `USER-SERVICE` | `8081` | Core business logic & orchestrator |
| `HOTEL-SERVICE` | `8082` | Hotel catalog service (PostgreSQL) |
| `RATING-SERVICE` | `8083` | Rating and review service (MySQL) |

---

## 📸 Verification & Screenshots

Add your verification screenshots to the `/screenshots` folder in this repository:

### 1. Eureka Web Dashboard (`http://localhost:8761`)

Displays all active instances registered with state `UP`:


### 2. Service Instance Health & Metadata

Shows detailed JSON representation of registered microservices:


---

## 🚀 Running Locally

### Startup Order

> **Important:** `ServiceRegistry_3` must be started **before** `UserService`, `RatingService`, `HotelService`, and `API-GATEWAY`.

 bash
# Clone the repository
git clone <your-eureka-repo-url>
cd ServiceRegistry_3

# Build the project
mvn clean install

# Run the Eureka Server
mvn spring-boot:run

 

Once started, open your browser and navigate to:

 
http://localhost:8761


