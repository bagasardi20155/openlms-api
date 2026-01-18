# OpenLMS

## Overview
This is a Spring Boot RESTful API application built with Java.

## Prerequisites
Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

- **Maven**: Version 3.6 or higher (or use the included Maven Wrapper)
  - Download from [Apache Maven](https://maven.apache.org/download.cgi)
  - Verify installation: `mvn -version`

- **Git**: For cloning the repository
  - Download from [git-scm.com](https://git-scm.com/)

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/bagasardi20155/openlms-api.git
cd openlms-api
```

### 2. Configure the Application
Edit the `application.properties` or `application.yml` file located in `src/main/resources/` to configure:
- Database connection settings
- Server port (default is 8080)
- Any environment-specific configurations

Example `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Install Dependencies
Using Maven:
```bash
mvn clean install
```

Or using Maven Wrapper (if available):
```bash
./mvnw clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

### 5. Verify the Application
Access the API at: `http://localhost:8080`

## Project Structure
```
openlms
    api
    ├── admin
    │   ├── controllers
    │   ├── dtos
    │   ├── entities
    │   ├── helpers
    │   ├── repositories
    │   └── services
    ├── ApiApplication.java
    ├── auth
    │   ├── controllers
    │   │   └── AuthController.java
    │   ├── domains
    │   │   ├── OtpPurpose.java
    │   │   ├── Role.java
    │   │   ├── User.java
    │   │   ├── UserOtp.java
    │   │   └── VipStatus.java
    │   ├── dtos
    │   │   ├── requests
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── SignUpRequest.java
    │   │   │   └── VerifyOtpRequest.java
    │   │   └── responses
    │   │       └── AuthResponse.java
    │   ├── repositories
    │   │   ├── UserOtpRepository.java
    │   │   └── UserRepository.java
    │   └── services
    │       ├── AuthService.java
    │       ├── OtpGenerator.java
    │       └── SendOtpService.java
    ├── chat
    │   ├── controllers
    │   ├── domains
    │   ├── dtos
    │   ├── repositories
    │   └── services
    ├── classroom
    │   ├── controllers
    │   │   ├── ClassController.java
    │   │   ├── EnrollmentController.java
    │   │   └── MaterialController.java
    │   ├── domains
    │   │   ├── ClassEntity.java
    │   │   ├── Enrollment.java
    │   │   ├── EnrollmentStatus.java
    │   │   ├── Material.java
    │   │   ├── MaterialProgress.java
    │   │   └── MaterialType.java
    │   ├── dtos
    │   │   ├── requests
    │   │   │   ├── CreateClassRequest.java
    │   │   │   ├── CreateMaterialRequest.java
    │   │   │   ├── PublishClassRequest.java
    │   │   │   ├── PublishMaterialRequest.java
    │   │   │   └── UpdateMaterialRequest.java
    │   │   └── responses
    │   │       ├── ClassResponse.java
    │   │       ├── EnrollmentResponse.java
    │   │       ├── MaterialResponse.java
    │   │       └── ProgressResponse.java
    │   ├── helpers
    │   │   ├── JwtHelper.java
    │   │   └── RequireRole.java
    │   ├── repositories
    │   │   ├── ClassRepository.java
    │   │   ├── EnrollmentRepository.java
    │   │   ├── MaterialProgressRepository.java
    │   │   └── MaterialRepository.java
    │   └── services
    │       ├── ClassService.java
    │       ├── EnrollmentService.java
    │       └── MaterialService.java
    ├── commons
    │   ├── apis
    │   │   ├── ApiResponse.java
    │   │   └── PageResponse.java
    │   ├── configs
    │   │   ├── CorsConfig.java
    │   │   ├── JacksonConfig.java
    │   │   └── WebConfig.java
    │   ├── exceptions
    │   │   ├── DomainException.java
    │   │   ├── ErrorCode.java
    │   │   ├── ErrorResponse.java
    │   │   ├── ExceptionHandler.java
    │   │   └── NotFoundException.java
    │   ├── securities
    │   │   ├── JwtAuthConverter.java
    │   │   ├── JwtService.java
    │   │   ├── OAuth2LoginSuccessHandler.java
    │   │   └── SecurityConfig.java
    │   └── utils
    │       ├── DateUtil.java
    │       ├── JwtClaimsUtil.java
    │       └── TimeProvider.java
    └── payment
        ├── clients
        ├── controllers
        ├── domains
        ├── dtos
        ├── repositories
        └── services
```