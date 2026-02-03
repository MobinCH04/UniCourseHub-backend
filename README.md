# UniCourseHub Backend

##  Deploy
**The project is deployed, and this is the website address:**
- http://uni-course-hub.codeafzaran.ir:9096/login
- 
- **Admins username**: 40173152
- **Admins password**: #Unicoursehub2025

- **Professors username**: 40173100
- **Professors password**: 123

- **Students username**: 40173148
- **Students password**: 123

## API Documentation
**http://uni-course-hub.codeafzaran.ir:9095/api/v1/login**
- **username**: 40173152
- **password**: #Unicoursehub2025

## Project Execution Guide

### Prerequisites
1. **Install Docker Desktop**
   - Download and install Docker Desktop
   - Ensure Docker Desktop is running before proceeding

### Running the Project

1. **Open Command Prompt (CMD)**
   - Navigate to the folder where the `docker-compose.yml` file is located

2. **Run Docker Compose**
   ```bash
   docker compose up -d
   ```

3. **Automatic Setup**
   - The required images, including MariaDB, will be pulled, installed, and started automatically using Docker
   - The backend service will be available on port `9095` (mapped to internal port `8081`)
   - The MariaDB database will be running on port `3306`

### Accessing the Application
- **Backend API**: http://localhost:9095/api/v1
- **API Documentation**: http://localhost:9095/api/v1/swagger-ui.html (if Swagger is configured)

- **The project documentation is available at**:
  http://uni-course-hub.codeafzaran.ir:9095/api/v1/login# 

- **Admin userName**: 40173152
- **Admin password**: #Unicoursehub2025
---

### Dockerhub addresses

- https://hub.docker.com/r/mobinch004/uni-course-hub/tags
- https://hub.docker.com/r/mobinch004/uni-course-hub-front/tags

## Technology Stack

### Core Framework
- **Java**: 21
- **Spring Boot**: 4.0.0
- **Maven**: Build tool and dependency management

### Spring Modules
- **Spring Boot Starter Web MVC**: RESTful web services
- **Spring Boot Starter Data JPA**: Database persistence and ORM
- **Spring Boot Starter Security**: Authentication and authorization
- **Spring Boot Starter Validation**: Input validation

### Database
- **MariaDB**: Relational database management system
- **MariaDB Java Client**: JDBC driver for MariaDB

### Security & Authentication
- **JWT (JSON Web Token)**: 
  - `jjwt-api` (0.11.5)
  - `jjwt-impl` (0.11.5)
  - `jjwt-jackson` (0.11.5)
- **Spring Security**: Security framework

### API Documentation
- **SpringDoc OpenAPI**: 3.0.0 (Swagger/OpenAPI documentation)

### Rate Limiting
- **Bucket4j**: 8.1.0 (Rate limiting and throttling)

### Monitoring & Observability
- **Micrometer Core**: 1.16.0 (Application metrics)
- **Micrometer Tracing Bridge Brave**: Distributed tracing

### Development Tools
- **Lombok**: Reduces boilerplate code (getters, setters, constructors, etc.)

### Testing
- **JUnit**: Unit testing framework
- **Spring Boot Starter Test**: Testing utilities
- **Spring Security Test**: Security testing support
- **Mockito**: 5.20.0 (Mocking framework)
- **TestContainers**: 1.21.3
  - JUnit Jupiter integration
  - MariaDB test container
- **H2 Database**: In-memory database for testing

### Containerization
- **Docker**: Containerization platform
- **Docker Compose**: Multi-container Docker application orchestration

### Build & Deployment
- **Maven Compiler Plugin**: Java compilation
- **Spring Boot Maven Plugin**: Packaging and running Spring Boot applications
