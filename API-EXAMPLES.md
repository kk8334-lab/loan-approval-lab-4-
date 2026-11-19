# Mock credit-assessment API examples

The API is hosted by the Spring Boot application in this project.

## Endpoint
- Method: `POST`
- URL: `http://localhost:8081/api/credit-assessment`
- Content-Type: `application/json`

## Successful request
```json
{
  "applicantId": "SYNTH-1001",
  "applicantName": "Sample Applicant",
  "monthlyIncome": 60000,
  "requestedAmount": 900000,
  "existingMonthlyDebt": 10000,
  "declaredCreditScore": 720,
  "simulateFailure": false
}
```

## Successful response (HTTP 200)
```json
{
  "status": "SUCCESS",
  "applicantId": "SYNTH-1001",
  "applicantName": "Sample Applicant",
  "creditScore": 720,
  "assessmentReference": "CREDIT-SIM-001",
  "message": "Synthetic credit assessment completed"
}
```

The REST Connector stores the HTTP response wrapper in `creditCheckResponse`; the JSON response fields are under `creditCheckResponse.body`.

## Failure request
Use the same request with:
```json
"simulateFailure": true
```

## Failure response (HTTP 503)
```json
{
  "status": "ERROR",
  "message": "Simulated credit assessment service outage"
}
```

## PowerShell test
Run this from a PowerShell window while the Spring Boot app is running:
```powershell
$body = @{
  applicantId = "SYNTH-1001"
  applicantName = "Sample Applicant"
  monthlyIncome = 60000
  requestedAmount = 900000
  existingMonthlyDebt = 10000
  declaredCreditScore = 720
  simulateFailure = $false
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/credit-assessment" -ContentType "application/json" -Body $body
```
