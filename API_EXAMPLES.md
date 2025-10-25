# API Examples

## Base URL
```
http://localhost:8080
```

## 1. Create Account

### Request
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "document_number": "12345678900"
  }'
```

### Response (201 Created)
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

---

## 2. Get Account by ID

### Request
```bash
curl -X GET http://localhost:8080/accounts/1
```

### Response (200 OK)
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

### Error Response (404 Not Found)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with ID: 999",
  "path": "/accounts/999"
}
```

---

## 3. Create Transaction - Purchase (Negative Amount)

### Request
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "operation_type_id": 1,
    "amount": 50.00
  }'
```

### Response (201 Created)
```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 1,
  "amount": -50.00
}
```

**Note:** Amount is automatically converted to negative for purchase transactions.

---

## 4. Create Transaction - Installment Purchase (Negative Amount)

### Request
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "operation_type_id": 2,
    "amount": 23.50
  }'
```

### Response (201 Created)
```json
{
  "transaction_id": 2,
  "account_id": 1,
  "operation_type_id": 2,
  "amount": -23.50
}
```

---

## 5. Create Transaction - Withdrawal (Negative Amount)

### Request
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "operation_type_id": 3,
    "amount": 18.70
  }'
```

### Response (201 Created)
```json
{
  "transaction_id": 3,
  "account_id": 1,
  "operation_type_id": 3,
  "amount": -18.70
}
```

---

## 6. Create Transaction - Payment (Positive Amount)

### Request
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "operation_type_id": 4,
    "amount": 60.00
  }'
```

### Response (201 Created)
```json
{
  "transaction_id": 4,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 60.00
}
```

**Note:** Amount remains positive for payment transactions.

---

## Operation Type IDs

| ID | Description | Amount Sign |
|----|-------------|-------------|
| 1  | PURCHASE | Negative |
| 2  | INSTALLMENT PURCHASE | Negative |
| 3  | WITHDRAWAL | Negative |
| 4  | PAYMENT | Positive |

---

## Error Scenarios

### Invalid Account ID (404)
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 999,
    "operation_type_id": 1,
    "amount": 50.00
  }'
```

### Invalid Operation Type ID (404)
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "operation_type_id": 999,
    "amount": 50.00
  }'
```

### Duplicate Account (409)
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "document_number": "12345678900"
  }'
```

### Validation Error (400)
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "document_number": ""
  }'
```

Response:
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid request parameters",
  "path": "/accounts",
  "validationErrors": {
    "documentNumber": "Document number is required"
  }
}
```

---

## PowerShell Examples (Windows)

### Create Account
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"document_number": "12345678900"}'
```

### Get Account
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/accounts/1" `
  -Method GET
```

### Create Transaction
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/transactions" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"account_id": 1, "operation_type_id": 4, "amount": 123.45}'
```

---

## Health Check

### Request
```bash
curl -X GET http://localhost:8080/actuator/health
```

### Response
```json
{
  "status": "UP"
}
```
