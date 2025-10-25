# Test login endpoint
$body = @{
    username = "admin"
    password = "password123"
} | ConvertTo-Json

$headers = @{
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $body -Headers $headers
    Write-Host "✅ Login successful!" -ForegroundColor Green
    Write-Host "Token: $($response.token)" -ForegroundColor Cyan
    Write-Host "Username: $($response.username)" -ForegroundColor Cyan
    Write-Host "Roles: $($response.roles -join ', ')" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Login failed!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Yellow
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    $responseBody = $reader.ReadToEnd()
    Write-Host "Response: $responseBody" -ForegroundColor Yellow
}
