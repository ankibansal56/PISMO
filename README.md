# Pismo Account Service

REST API for managing customer accounts and transactions built with Spring Boot 3.3.2 and Java 21.

## Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [Project Structure](#project-structure)

## Features

- ✅ Create customer accounts with unique document numbers
- ✅ Retrieve account information by ID
- ✅ Create transactions with automatic amount sign handling
- ✅ Support for multiple operation types (Purchase, Installment Purchase, Withdrawal, Payment)
- ✅ Automatic negative amounts for debt transactions
- ✅ Automatic positive amounts for credit transactions
- ✅ **JWT-based authentication and authorization**
- ✅ **Role-based access control (USER, ADMIN)**
- ✅ **Secure password encryption with BCrypt**
- ✅ Comprehensive error handling and validation
- ✅ OpenAPI/Swagger documentation
- ✅ Docker support with PostgreSQL
- ✅ H2 in-memory database for development
- ✅ Unit and integration tests

## Technologies

- **Java 21** - Programming language
- **Spring Boot 3.3.2** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **H2 Database** - Development database
- **PostgreSQL** - Production database
- **Maven** - Build tool
- **Lombok** - Boilerplate code reduction
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **RestAssured** - API testing
- **Docker** - Containerization

## Prerequisites

### Option 1: Run with Docker on Windows (Recommended) ⭐
- **Windows 10/11 with WSL2** (Windows Subsystem for Linux)
- **Docker Desktop** for Windows with WSL2 backend enabled
- Git for Windows

### Option 2: Run Locally
- **Java 21** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## Quick Start

### 🐳 Running with Docker (Windows + WSL2)

This is the easiest way to run the application on Windows:

```powershell
# 1. Navigate to project directory
cd "C:\Users\2131hs\OneDrive - BP\Documents\PISMO\PISMO"

# 2. Build Docker image in WSL
wsl docker build -t pismo-app:latest .

# 3. Run the application
wsl docker run -d --name pismo-app -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev pismo-app:latest

# 4. Check if it's running
wsl docker ps

# 5. Open Swagger UI in browser
start http://localhost:8080/swagger-ui.html
```

**Stop the application:**
```powershell
wsl docker stop pismo-app
wsl docker rm pismo-app
```

**View logs:**
```powershell
wsl docker logs pismo-app -f
```

### 💻 Running Locally (Without Docker)

```bash
# 1. Clone the repository
git clone https://github.com/ankibansal56/PISMO.git
cd PISMO/PISMO

# 2. Build the project
./mvnw clean package

# 3. Run the application
./mvnw spring-boot:run

# Or run the JAR file
java -jar target/account-service-0.0.1-SNAPSHOT.jar
```

**For Windows PowerShell:**
```powershell
# Build and run
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

The application will start on **http://localhost:8080**

### 🎯 Access the Application

Once running, you can access:

- **Swagger UI (Interactive API Docs):** http://localhost:8080/swagger-ui.html
- **API Base URL:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health
- **H2 Console (dev mode):** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:pismo_db`
  - Username: `sa`
  - Password: (leave empty)

### 🔑 Default Credentials

For testing purposes, the application comes with pre-configured users:

| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `password123` | ROLE_USER, ROLE_ADMIN |
| `user` | `password123` | ROLE_USER |

⚠️ **Important:** Change these credentials in production!

## How to Use the API

### Step 1: Login to Get JWT Token

```bash
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method Post `
  -Body (@{username="admin";password="password123"} | ConvertTo-Json) `
  -ContentType "application/json"

$token = $response.token
Write-Host "Token: $token"
```

```bash
# Bash/curl
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' \
  | jq -r '.token'
```

You'll receive a response like:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@pismo.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

#### Step 3: Create an Account

```bash
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/accounts" `
  -Method Post `
  -Headers @{Authorization="Bearer $token"} `
  -Body (@{document_number="12345678900"} | ConvertTo-Json) `
  -ContentType "application/json"
```

```bash
# Bash/curl
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"document_number":"12345678900"}'
```

Response:
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### Step 4: Create a Transaction

```bash
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/transactions" `
  -Method Post `
  -Headers @{Authorization="Bearer $token"} `
  -Body (@{account_id=1;operation_type_id=4;amount=100.00} | ConvertTo-Json) `
  -ContentType "application/json"
```

```bash
# Bash/curl
curl -X POST http://localhost:8080/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"account_id":1,"operation_type_id":4,"amount":100.00}'
```

Response:
```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 100.00
}
```

#### Step 5: Get Account Details

```bash
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/accounts/1" `
  -Headers @{Authorization="Bearer $token"}
```

```bash
# Bash/curl
curl -X GET http://localhost:8080/accounts/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd pismo
```

### 2. Run with Maven (Development Mode - H2 Database)

```bash
# Windows
mvn spring-boot:run

# Linux/Mac
./mvn spring-boot:run
```

The application will start on `http://localhost:8080` with an in-memory H2 database.

### 3. Access H2 Console (Development Mode)

Visit `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:pismo_db`
- Username: `sa`
- Password: (leave empty)

### 4. Build the Application

```bash
# Windows
mvnw.cmd clean package

# Linux/Mac
./mvnw clean package
```

### 5. Run Tests

```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

## API Documentation

### Swagger UI (Interactive API Testing)

Once the application is running, access the **interactive Swagger UI** at:

```
http://localhost:8080/swagger-ui.html
```

**How to use Swagger UI with JWT Authentication:**

1. **Start the application** (if not already running):
   ```bash
   mvn spring-boot:run
   ```

2. **Open Swagger UI** in your browser:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Login to get JWT token**:
   - Expand the **"Authentication"** section
   - Click on **"POST /api/auth/login"**
   - Click **"Try it out"**
   - Enter credentials in the request body:
     ```json
     {
       "username": "admin",
       "password": "password123"
     }
     ```
   - Click **"Execute"**
   - Copy the `token` value from the response

4. **Authorize Swagger UI**:
   - Click the **"Authorize" button** 🔓 at the top right
   - In the popup, enter: `Bearer YOUR_TOKEN_HERE`
     - Example: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
   - Click **"Authorize"**
   - Click **"Close"**

5. **Test the APIs**:
   - Now all your requests will include the JWT token automatically
   - Try creating an account under the **"Accounts"** section
   - Try creating transactions under the **"Transactions"** section

**Available API Groups:**
- 🔐 **Authentication** - Login and register (no auth required)
- 👤 **Accounts** - Create and retrieve accounts (requires JWT)
- 💰 **Transactions** - Create transactions (requires JWT)

### OpenAPI Specification

The OpenAPI JSON specification is available at:

```
http://localhost:8080/api-docs
```

### OpenAPI JSON

```
http://localhost:8080/api-docs
```

### API Endpoints

#### Authentication Endpoints (Public)

#### 1. Register User

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["USER"]
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully"
}
```

#### 2. Login User

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

#### Protected Endpoints (Require Authentication)

**Note:** All account and transaction endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### 3. Create Account

**Request:**
```http
POST /accounts
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "document_number": "12345678900"
}
```

**Response (201 Created):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### 4. Get Account

**Request:**
```http
GET /accounts/:accountId
Authorization: Bearer <your-jwt-token>
```

**Response (200 OK):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### 5. Create Transaction

**Request:**
```http
POST /transactions
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

**Response (201 Created):**
```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

**Note:** The amount will be automatically adjusted based on operation type:
- Operation types 1, 2, 3 (Purchase, Installment Purchase, Withdrawal): negative amounts
- Operation type 4 (Payment): positive amounts

## Authentication & Authorization

The API uses **JWT (JSON Web Token)** for authentication. See [AUTHENTICATION.md](AUTHENTICATION.md) for detailed guide.

### Quick Start with Authentication

1. **Register a new user or use default credentials:**
   - Username: `admin`
   - Password: `password123`

2. **Login to get JWT token:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "password123"}'
   ```

3. **Use the token in subsequent requests:**
   ```bash
   curl -X POST http://localhost:8080/accounts \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <your-token>" \
     -d '{"document_number": "12345678900"}'
   ```

### Default Users

| Username | Password | Roles |
|----------|----------|-------|
| admin | password123 | ROLE_USER, ROLE_ADMIN |
| user | password123 | ROLE_USER |

### API Endpoints (LEGACY - Without Auth Context)

#### 1. Create Account
**Request:**
```http
POST /accounts
Content-Type: application/json

{
  "document_number": "12345678900"
}
```

**Response (201 Created):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### 2. Get Account

**Request:**
```http
GET /accounts/:accountId
```

**Response (200 OK):**
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

#### 3. Create Transaction

**Request:**
```http
POST /transactions
Content-Type: application/json

{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

**Response (201 Created):**
```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

**Note:** The amount will be automatically adjusted based on operation type:
- Operation types 1, 2, 3 (Purchase, Installment Purchase, Withdrawal): negative amounts
- Operation type 4 (Payment): positive amounts

## Database Schema

### Accounts Table
| Column | Type | Description |
|--------|------|-------------|
| account_id | BIGINT | Primary key, auto-generated |
| document_number | VARCHAR | Unique document identifier |

### Operation Types Table
| Column | Type | Description |
|--------|------|-------------|
| operation_type_id | BIGINT | Primary key |
| description | VARCHAR | Operation type description |

**Predefined Operation Types:**
1. PURCHASE
2. INSTALLMENT PURCHASE
3. WITHDRAWAL
4. PAYMENT

### Transactions Table
| Column | Type | Description |
|--------|------|-------------|
| transaction_id | BIGINT | Primary key, auto-generated |
| account_id | BIGINT | Foreign key to accounts |
| operation_type_id | BIGINT | Foreign key to operation_types |
| amount | DECIMAL(19,2) | Transaction amount |
| event_date | TIMESTAMP | Transaction timestamp |

## Testing

### Unit Tests

Run unit tests for service and controller layers:

```bash
mvnw test -Dtest=*Test
```

### Integration Tests

Run integration tests:

```bash
mvnw test -Dtest=*IntegrationTest
```

### Test Coverage

The project includes:
- ✅ Service layer unit tests
- ✅ Controller layer unit tests
- ✅ Integration tests with full application context
- ✅ Error handling tests
- ✅ Validation tests

## Docker Support

### Build and Run with Docker Compose

The easiest way to run the application with PostgreSQL:

```bash
docker-compose up --build
```

This will start:
- PostgreSQL database on port 5432
- Application on port 8080

### Build Docker Image Only

```bash
docker build -t pismo-account-service .
```

### Run with Custom PostgreSQL

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=your-postgres-host \
  -e DB_PORT=5432 \
  -e DB_NAME=pismo_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your-password \
  pismo-account-service
```

### Stop Docker Compose

```bash
docker-compose down
```

### Stop and Remove Volumes

```bash
docker-compose down -v
```

## Project Structure

```
pismo/
├── src/
│   ├── main/
│   │   ├── java/com/pismo/account/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── domain/
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   └── enums/           # Enums
│   │   │   ├── dto/
│   │   │   │   ├── request/         # Request DTOs
│   │   │   │   └── response/        # Response DTOs
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── repository/          # Data repositories
│   │   │   ├── service/             # Business logic
│   │   │   └── AccountServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml      # Application configuration
│   │       └── data.sql            # Initial data
│   └── test/
│       └── java/com/pismo/account/
│           ├── controller/          # Controller tests
│           ├── service/             # Service tests
│           └── AccountServiceIntegrationTest.java
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Configuration Profiles

### Development Profile (default)
- In-memory H2 database
- H2 console enabled
- SQL logging enabled
- Auto-create/drop schema

### Production Profile
- PostgreSQL database
- Connection pooling configured
- SQL logging disabled
- Schema validation only

## Environment Variables (Production)

| Variable | Description | Default |
|----------|-------------|---------|
| DB_HOST | PostgreSQL host | localhost |
| DB_PORT | PostgreSQL port | 5432 |
| DB_NAME | Database name | pismo_db |
| DB_USERNAME | Database username | postgres |
| DB_PASSWORD | Database password | postgres |

## Error Handling

The API returns standardized error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with ID: 999",
  "path": "/accounts/999"
}
```

**HTTP Status Codes:**
- `200 OK` - Successful GET request
- `201 Created` - Successful resource creation
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource
- `500 Internal Server Error` - Unexpected error

## AWS EC2 Deployment 🚀

This application can be deployed to AWS EC2 using Docker. See the comprehensive guides:

### Quick Deployment (3 commands)

```bash
# 1. SSH to EC2 and clone
ssh -i "your-key.pem" ec2-user@YOUR-EC2-IP
git clone https://github.com/ankibansal56/PISMO.git && cd PISMO

# 2. Configure environment
cp .env.example .env && nano .env  # Add your secrets

# 3. Deploy with automated script
chmod +x deploy.sh && ./deploy.sh
```

### 📚 Deployment Documentation

- **[AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md)** - Complete step-by-step deployment guide
  - EC2 instance setup and configuration
  - Docker installation on EC2
  - PostgreSQL database setup
  - SSL/HTTPS configuration with Let's Encrypt
  - Nginx reverse proxy setup
  - Security best practices
  
- **[DEPLOYMENT_CHEATSHEET.md](DEPLOYMENT_CHEATSHEET.md)** - Quick reference for common commands
  - Essential Docker commands
  - Database backup/restore
  - Monitoring and troubleshooting
  - Update procedures

### Access Your Deployed API

```bash
# Get your EC2 public IP
curl http://checkip.amazonaws.com

# Your API will be available at:
http://YOUR-EC2-PUBLIC-IP:8080

# Swagger UI (interactive testing):
http://YOUR-EC2-PUBLIC-IP:8080/swagger-ui.html
```

### Test from Local Machine

```powershell
# Windows PowerShell
$ip = "YOUR-EC2-PUBLIC-IP"
Invoke-RestMethod "http://$ip:8080/actuator/health"
```

### Production Features

- ✅ PostgreSQL database with persistent storage
- ✅ Auto-restart on failure
- ✅ Health checks
- ✅ Environment-based configuration
- ✅ Production-ready security settings
- ✅ Automated deployment script

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the Pismo Code Assessment.

## Support

For questions or issues, please open an issue in the repository.
