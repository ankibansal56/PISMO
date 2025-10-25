# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-10-25

### Added
- Initial release of Pismo Account Service
- REST API for account management
  - POST /accounts - Create new account
  - GET /accounts/:id - Retrieve account by ID
- REST API for transaction management
  - POST /transactions - Create new transaction
- Support for 4 operation types:
  1. Purchase (negative amount)
  2. Installment Purchase (negative amount)
  3. Withdrawal (negative amount)
  4. Payment (positive amount)
- Automatic amount sign handling based on operation type
- Spring Boot 3.3.2 with Java 21
- H2 in-memory database for development
- PostgreSQL support for production
- OpenAPI/Swagger documentation
- Comprehensive error handling
- Request validation
- Docker support with Dockerfile and docker-compose.yml
- Health check endpoints via Spring Actuator
- Unit tests for services and controllers
- Integration tests with full application context
- API examples and documentation
- Postman collection for API testing
- README with detailed setup instructions
- Contributing guidelines

### Technical Stack
- Java 21
- Spring Boot 3.3.2
- Spring Data JPA
- Spring Validation
- H2 Database (dev)
- PostgreSQL (prod)
- Lombok
- SpringDoc OpenAPI
- JUnit 5
- Mockito
- RestAssured
- Maven
- Docker

[1.0.0]: https://github.com/yourusername/pismo/releases/tag/v1.0.0
