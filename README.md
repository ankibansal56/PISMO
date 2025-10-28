# Pismo Account Service

REST API for managing customer accounts and transactions built with Spring Boot 3.3.2 and Java 21.

## Features

- ✅ Create customer accounts with unique document numbers
- ✅ Retrieve account information by ID
- ✅ Create transactions with automatic amount sign handling
- ✅ Support for multiple operation types (Purchase, Installment Purchase, Withdrawal, Payment)
- ✅ **JWT-based authentication and authorization**
- ✅ **Role-based access control (USER, ADMIN)**
- ✅ **Secure password encryption with BCrypt**
- ✅ Comprehensive error handling and validation
- ✅ OpenAPI/Swagger documentation
- ✅ H2 in-memory database
- ✅ Docker support
- ✅ Unit and integration tests

## Technologies

- **Java 21** - Programming language
- **Spring Boot 3.3.2** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **H2 Database** - In-memory database
- **Maven** - Build tool
- **Lombok** - Boilerplate code reduction
- **SpringDoc OpenAPI** - API documentation
- **Docker** - Containerization

## Prerequisites

### Option 1: Docker (Recommended) ⭐
- **Windows 10/11 with WSL2**
- **Docker Desktop** for Windows with WSL2 backend
- Git

### Option 2: Local Development
- **Java 21** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- Your favorite IDE

## Quick Start

### 🐳 Running with Docker (Windows + WSL2)

```powershell
# 1. Navigate to project directory
cd "C:\path\to\PISMO\PISMO"

# 2. Build Docker image
wsl docker build -t pismo-app:latest .

# 3. Run the application
wsl docker run -d --name pismo-app -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev pismo-app:latest

# 4. Open Swagger UI
start http://localhost:8080/swagger-ui.html
```

**Manage the container:**
```powershell
# View logs
wsl docker logs pismo-app -f

# Stop and remove
wsl docker stop pismo-app
wsl docker rm pismo-app
```

### 💻 Running Locally

```bash
# Clone the repository
git clone https://github.com/ankibansal56/PISMO.git
cd PISMO/PISMO

# Build and run
./mvnw spring-boot:run
```

**Windows PowerShell:**
```powershell
.\mvnw.cmd spring-boot:run
```

## Access the Application

Once running:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:pismo_db`
  - Username: `sa`
  - Password: (empty)

## Default Credentials

| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `password123` | ROLE_USER, ROLE_ADMIN |
| `user` | `password123` | ROLE_USER |

⚠️ **Change these in production!**

## How to Use the API

### Step 1: Login to Get JWT Token

**Using Swagger UI (Easiest):**
1. Open http://localhost:8080/swagger-ui.html
2. Expand **"Authentication"** → **"POST /api/auth/login"**
3. Click **"Try it out"**
4. Enter: `{"username":"admin","password":"password123"}`
5. Click **"Execute"**
6. Copy the `token` from response
7. Click **"Authorize"** button (🔓 top right)
8. Enter: `Bearer YOUR_TOKEN`
9. Click **"Authorize"** then **"Close"**

**Using PowerShell:**
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method Post `
  -Body (@{username="admin";password="password123"} | ConvertTo-Json) `
  -ContentType "application/json"

$token = $response.token
```

**Using curl:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### Step 2: Create an Account

**Using Swagger UI:**
- All requests now automatically include your token!
- Expand **"Accounts"** → **"POST /accounts"**
- Click **"Try it out"**
- Enter: `{"document_number":"12345678900"}`
- Click **"Execute"**

**Using PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts" `
  -Method Post `
  -Headers @{Authorization="Bearer $token"} `
  -Body (@{document_number="12345678900"} | ConvertTo-Json) `
  -ContentType "application/json"
```

**Using curl:**
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"document_number":"12345678900"}'
```

### Step 3: Create a Transaction

**Using Swagger UI:**
- Expand **"Transactions"** → **"POST /transactions"**
- Click **"Try it out"**
- Enter:
  ```json
  {
    "account_id": 1,
    "operation_type_id": 4,
    "amount": 100.00
  }
  ```
- Click **"Execute"**

**Using PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/transactions" `
  -Method Post `
  -Headers @{Authorization="Bearer $token"} `
  -Body (@{account_id=1;operation_type_id=4;amount=100.00} | ConvertTo-Json) `
  -ContentType "application/json"
```

**Note:** Amounts are automatically adjusted:
- Operation types 1, 2, 3 (Purchase, Installment, Withdrawal): **negative**
- Operation type 4 (Payment): **positive**

### Step 4: Get Account Details

**Using Swagger UI:**
- Expand **"Accounts"** → **"GET /accounts/{accountId}"**
- Enter account ID: `1`
- Click **"Execute"**

**Using PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts/1" `
  -Headers @{Authorization="Bearer $token"}
```

## API Endpoints

### Authentication (Public - No Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Accounts (Protected - Requires JWT Token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/accounts` | Create new account |
| GET | `/accounts/{id}` | Get account by ID |

### Transactions (Protected - Requires JWT Token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transactions` | Create new transaction |

## Database Schema

### Operation Types (Pre-configured)

| ID | Description |
|----|-------------|
| 1 | PURCHASE |
| 2 | INSTALLMENT PURCHASE |
| 3 | WITHDRAWAL |
| 4 | PAYMENT |

### Accounts Table

| Column | Type | Constraints |
|--------|------|-------------|
| account_id | BIGINT | Primary Key, Auto-increment |
| document_number | VARCHAR(50) | Unique, Not Null |

### Transactions Table

| Column | Type | Constraints |
|--------|------|-------------|
| transaction_id | BIGINT | Primary Key, Auto-increment |
| account_id | BIGINT | Foreign Key → accounts |
| operation_type_id | BIGINT | Foreign Key → operation_types |
| amount | DECIMAL(19,2) | Not Null |
| event_date | TIMESTAMP | Not Null, Default: Current Time |

## Testing

Run tests:
```bash
# All tests
./mvnw test

# Windows
.\mvnw.cmd test
```

Test coverage includes:
- ✅ Unit tests for services and controllers
- ✅ Integration tests with full application context
- ✅ Authentication and authorization tests
- ✅ Error handling tests
- ✅ Validation tests

## Error Handling

Standardized error responses:

```json
{
  "timestamp": "2024-10-28T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with ID: 999",
  "path": "/accounts/999"
}
```

**HTTP Status Codes:**
- `200` - Success (GET)
- `201` - Created (POST)
- `400` - Bad Request (validation error)
- `401` - Unauthorized (missing/invalid token)
- `404` - Not Found
- `409` - Conflict (duplicate resource)
- `500` - Internal Server Error

## Project Structure

```
PISMO/
├── src/
│   ├── main/
│   │   ├── java/com/pismo/account/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── domain/
│   │   │   │   ├── entity/       # JPA entities
│   │   │   │   └── enums/        # Enums
│   │   │   ├── dto/
│   │   │   │   ├── request/      # Request DTOs
│   │   │   │   └── response/     # Response DTOs
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── security/         # Security & JWT
│   │   │   └── service/          # Business logic
│   │   └── resources/
│   │       ├── application.yml   # Configuration
│   │       ├── data.sql         # Initial data
│   │       └── schema.sql       # Database schema
│   └── test/                     # Tests
├── Dockerfile                    # Docker image definition
├── docker-compose.yml           # Docker Compose config
├── pom.xml                      # Maven configuration
└── README.md
```

## Additional Documentation

- **[AUTHENTICATION.md](AUTHENTICATION.md)** - Detailed authentication guide
- **[AUTH_FLOW.md](AUTH_FLOW.md)** - Visual authentication flow
- **[API_EXAMPLES.md](API_EXAMPLES.md)** - More API examples
- **[INTERVIEW_NOTES.md](INTERVIEW_NOTES.md)** - Interview preparation

## License

This project is part of the Pismo Code Assessment.
