# 🛒 TechJMarket Microservices Project

This is a full-featured e-commerce microservices application built with **Spring Boot** and **Spring Cloud** made for the frontend  https://jp-market-1944d.firebaseapp.com/

The application is structured around multiple independently deployable services, following microservice architecture best practices.

---

## 📦 Modules & Responsibilities

### 1. **product-service**
Handles product creation and retrieval.

- **Responsibilities**:
    - Expose REST APIs to create and list products.
    - Persist product data using a relational database.

- **Tech Stack**:
    - Spring Web, Spring Data JPA
    - MongoDB

- **Tests Coverage**:
  - Jacoco:
  - Mutation test: 80%

---

### 2. **order-service**
Manages customer orders.

- **Responsibilities**:
    - Accepts order requests.
    - Validates product availability via inventory-service.
    - Sends order placed events to Kafka.

- **Tech Stack**:
    - Spring Web, OpenFeign, Kafka
    - mysql

- **Tests Coverage**:
  - Jacoco: 94%
  - Mutation test: 73%

---

### 3. **inventory-service**
Checks product availability.

- **Responsibilities**:
    - Verifies stock for requested product SKUs.
    - Returns inventory status to order-service.

- **Tech Stack**:
    - Spring Web, Spring Data JPA
    - Kafka, postgresql

- **Tests Coverage**:
  - Jacoco: 100%
  - Mutation test: 100%
---

### 4. **notification-service**
Sends email notifications when an order is placed.

- **Responsibilities**:
    - Listens for order events on Kafka.
    - Sends emails using JavaMailSender.

- **Tech Stack**:
    - Spring Kafka, Spring Mail
    - SMTP mock
- **Tests Coverage**:
  - Jacoco: 91%
  - Mutation test: 85%
---

### 5. **api-gateway**
Routes external HTTP requests to internal microservices.

- **Responsibilities**:
    - Acts as a single entry point.
    - Provides centralized routing and filtering.

- **Tech Stack**:
    - Spring Cloud Gateway

---

### 6. **discovery-server**
Service registry for dynamic discovery.

- **Responsibilities**:
    - Enables service discovery using Netflix Eureka.

- **Tech Stack**:
    - Spring Cloud Netflix Eureka Server, Spring security

---


## 🔐 Security
- Implemented OAuth2-based authentication using **Keycloak**.
- Secured access to endpoints via JWT tokens.

---

## 🧰 Tools & Technologies

| Category | Tools / Libraries |
|---------|-------------------|
| **Languages** | Java 17 |
| **Frameworks** | Spring Boot, Spring Cloud |
| **Communication** | OpenFeign, Kafka |
| **Service Registry** | Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Authentication** | Keycloak (OAuth2 + JWT) |
| **Data** | PostgreSQL / MySQL, MongoDB |
| **Messaging** | Apache Kafka |
| **Configuration** | Spring Cloud Config |
| **Monitoring** | Zipkin, Sleuth, Prometheus, Grafana |
| **Logging** | ELK Stack (optional) |
| **Testing** | JUnit 5, Testcontainers |
| **Build Tool** | Maven |
| **Containers** | Docker, Docker Compose |
| **Deployment** | Kubernetes (optional) |

---

## 🚀 Getting Started

To spin up the services locally with Docker:

```bash
docker-compose up --build
