# Authentication & Authorization Guide

## Overview

The Pismo Account Service now includes **JWT-based authentication** with **role-based access control (RBAC)**. All account and transaction endpoints require authentication.

## Security Features

### ✅ Implemented Features
- JWT (JSON Web Token) authentication
- Role-based authorization (USER, ADMIN)
- Password encryption with BCrypt
- Stateless session management
- Token expiration (24 hours)
- Protected endpoints
- Public authentication endpoints

## User Roles

| Role | Description | Permissions |
|------|-------------|-------------|
| `ROLE_USER` | Regular user | Can create accounts and transactions |
| `ROLE_ADMIN` | Administrator | Full access to all resources |

## Authentication Endpoints

### 1. Register New User

**Endpoint:** `POST /api/auth/register`

**Request:**
```json
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

**Notes:**
- If `roles` is not provided, defaults to `ROLE_USER`
- Valid roles: `USER`, `ADMIN`
- Password must be at least 6 characters
- Username must be unique
- Email must be valid and unique

---

### 2. Login (Get JWT Token)

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
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

## Using JWT Token

### Include Token in Requests

After logging in, include the JWT token in the `Authorization` header for all protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

### Example with curl

```bash
# Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'

# Use token to create account
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "document_number": "12345678900"
  }'
```

### Example with PowerShell

```powershell
# Login to get token
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username": "johndoe", "password": "password123"}'

$token = $loginResponse.token

# Use token to create account
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Invoke-RestMethod -Uri "http://localhost:8080/accounts" `
  -Method POST `
  -Headers $headers `
  -Body '{"document_number": "12345678900"}'
```

## Protected Endpoints

### Requires Authentication (USER or ADMIN role)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/accounts` | Create account | USER, ADMIN |
| GET | `/accounts/:id` | Get account | USER, ADMIN |
| POST | `/transactions` | Create transaction | USER, ADMIN |

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | API documentation |
| GET | `/h2-console` | H2 database console (dev only) |

## Default Users

For development and testing, two default users are pre-configured:

### Admin User
- **Username:** `admin`
- **Password:** `password123`
- **Email:** `admin@pismo.com`
- **Roles:** `ROLE_USER`, `ROLE_ADMIN`

### Regular User
- **Username:** `user`
- **Password:** `password123`
- **Email:** `user@pismo.com`
- **Roles:** `ROLE_USER`

## Complete Authentication Flow Example

### Step 1: Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "securePassword123"
  }'
```

### Step 2: Login to Get Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTYzNjQ4MDgwMCwiZXhwIjoxNjM2NTY3MjAwfQ.xxx",
  "type": "Bearer",
  "id": 3,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"]
}
```

### Step 3: Use Token to Access Protected Endpoints

```bash
# Store the token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Create an account
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "document_number": "98765432100"
  }'

# Get account details
curl -X GET http://localhost:8080/accounts/1 \
  -H "Authorization: Bearer $TOKEN"

# Create a transaction
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "account_id": 1,
    "operation_type_id": 4,
    "amount": 100.00
  }'
```

## Error Responses

### 401 Unauthorized (No Token or Invalid Token)

```json
{
  "timestamp": "2024-10-25T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/accounts"
}
```

### 403 Forbidden (Insufficient Permissions)

```json
{
  "timestamp": "2024-10-25T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/admin/users"
}
```

### 409 Conflict (Duplicate Username/Email)

```json
{
  "timestamp": "2024-10-25T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists",
  "path": "/api/auth/register"
}
```

## JWT Configuration

### Default Settings

- **Secret Key:** Configured in `application.yml`
- **Token Expiration:** 24 hours (86400000 ms)
- **Algorithm:** HS256 (HMAC with SHA-256)

### Customize JWT Settings

Edit `src/main/resources/application.yml`:

```yaml
app:
  jwt:
    secret: your-custom-secret-key-at-least-256-bits-long
    expiration: 86400000  # 24 hours in milliseconds
```

**Important:** In production, use environment variables:

```bash
export APP_JWT_SECRET="your-production-secret-key"
export APP_JWT_EXPIRATION="86400000"
```

## Security Best Practices

### ✅ Implemented
- Passwords are encrypted with BCrypt
- JWT tokens are stateless
- CSRF protection disabled (stateless JWT)
- Session management is stateless
- Tokens expire after 24 hours
- Proper CORS configuration

### 🔒 Production Recommendations
1. **Use HTTPS** - Always use TLS/SSL in production
2. **Strong Secret Key** - Use a cryptographically strong secret key
3. **Environment Variables** - Store secrets in environment variables, not in code
4. **Short Token Expiration** - Consider shorter expiration times for sensitive applications
5. **Refresh Tokens** - Implement refresh token mechanism for better UX
6. **Rate Limiting** - Add rate limiting to prevent brute force attacks
7. **Account Lockout** - Implement account lockout after failed login attempts
8. **Password Policy** - Enforce strong password requirements
9. **Audit Logging** - Log all authentication and authorization events

## Testing with Postman

The updated Postman collection includes authentication examples:

1. **Register User** - Create a new user account
2. **Login** - Get JWT token
3. **Create Account (Authenticated)** - Use token to create account
4. **Get Account (Authenticated)** - Use token to retrieve account
5. **Create Transaction (Authenticated)** - Use token to create transaction

Import the collection: `Pismo-API.postman_collection.json`

## Troubleshooting

### Token Expired

**Error:** `401 Unauthorized`

**Solution:** Login again to get a new token

### Invalid Token Format

**Error:** `401 Unauthorized`

**Solution:** Ensure the token is prefixed with `Bearer ` in the Authorization header

### Missing Authorization Header

**Error:** `401 Unauthorized`

**Solution:** Include the Authorization header with Bearer token

### Wrong Credentials

**Error:** `401 Unauthorized`

**Solution:** Verify username and password are correct

## Next Steps

Consider implementing:
- [ ] Refresh token mechanism
- [ ] Password reset functionality
- [ ] Email verification
- [ ] Two-factor authentication (2FA)
- [ ] OAuth2 integration
- [ ] Rate limiting
- [ ] Account lockout mechanism
- [ ] Audit logging
