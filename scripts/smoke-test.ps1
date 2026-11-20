$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host 'Checking service readiness...'
Invoke-WebRequest http://localhost:8080/v3/api-docs | Out-Null
Invoke-WebRequest http://localhost:4200 | Out-Null

function Login([string]$Email) {
  $body = @{ email = $Email; password = 'Password123!' } | ConvertTo-Json -Compress
  Invoke-RestMethod -Uri 'http://localhost:8080/auth/login' -Method Post -ContentType 'application/json' -Body $body
}

$claimant = Login 'alice@example.com'
$headers = @{ Authorization = "Bearer $($claimant.token)"; 'X-User-Id' = $claimant.user.id }
$claims = Invoke-RestMethod -Uri 'http://localhost:8080/claims' -Headers $headers
Write-Host "Claimant claims returned: $($claims.Count)"

# Keep the smoke fixture inside the seeded policy window even when the host clock
# and the Docker/Postgres clock differ by a day around midnight.
$newClaimBody = @{ policyId = '10000000-0000-0000-0000-000000000001'; claimType = 'MOTOR'; market = 'SG'; incidentDate = '2026-11-20'; description = 'Smoke-test claim'; claimedAmount = 1000; currency = 'SGD' } | ConvertTo-Json -Compress
$created = Invoke-RestMethod -Uri 'http://localhost:8080/claims' -Method Post -Headers $headers -ContentType 'application/json' -Body $newClaimBody
Write-Host "Created smoke claim: $($created.id)"

$officer = Login 'officer1@chx.com'
$officerHeaders = @{ Authorization = "Bearer $($officer.token)"; 'X-User-Id' = $officer.user.id }
$queue = Invoke-RestMethod -Uri 'http://localhost:8080/officer/queue' -Headers $officerHeaders
Write-Host "Officer queue returned: $($queue.Count)"

Invoke-RestMethod -Uri "http://localhost:8080/officer/claims/$($created.id)/pick-up" -Method Post -Headers $officerHeaders | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/officer/claims/$($created.id)/info-requests?question=Please%20provide%20a%20repair%20invoice" -Method Post -Headers $officerHeaders | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/claims/$($created.id)/info-responses?answer=Invoice%20attached%20for%20review" -Method Post -Headers $headers | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/officer/claims/$($created.id)/approve?amount=900" -Method Post -Headers $officerHeaders | Out-Null
Write-Host 'Completed smoke lifecycle: submit -> pickup -> info request -> response -> approve'

$notifications = Invoke-RestMethod -Uri 'http://localhost:8081/notifications'
Write-Host "Notifications currently recorded: $($notifications.Count)"
Write-Host 'Smoke checks completed. Continue lifecycle actions through the UI or API using the IDs returned above.'
