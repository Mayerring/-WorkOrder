$baseUrl = "http://localhost:5000"
$token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLlvKDkuIkiLCJpZCI6MiwibmFtZSI6IuW8oOS4iSIsImNvbXBhbnkiOiLmgLvlhazlj7giLCJkZXBhcnRtZW50Ijoi5oC75Yqh6YOoIiwicG9zaXRpb24iOiLmgLvnu4_nkIYiLCJzdGF0dXMiOjAsInBob25lIjoiMTM4MTIzNDU2NzgiLCJlbWFpbCI6InpoYW5nc2FuQGV4YW1wbGUuY29tIiwicm9sZSI6InVzZXIiLCJpYXQiOjE3ODM3Nzk3MjQsImV4cCI6MTc4Mzg2NjEyNH0.hJXdJk0N0ipTjA4tuhZAI4UexF0lQeRflFiX6PBKX-Q"
$traceId = "test-trace-id-" + [guid]::NewGuid().ToString()

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "CLI Service API Test Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Send-Request {
    param(
        [string]$method,
        [string]$url,
        [string]$body,
        [bool]$needAuth = $false,
        [string]$description
    )

    Write-Host "[$method] $url" -ForegroundColor Yellow
    Write-Host "Desc: $description" -ForegroundColor Gray

    $httpRequest = [System.Net.HttpWebRequest]::Create($url)
    $httpRequest.Method = $method
    $httpRequest.Headers["X-Trace-ID"] = $traceId

    if ($needAuth) {
        $httpRequest.Headers.Set("Authorization", $token)
    }

    if ($method -eq "POST") {
        $httpRequest.ContentType = "application/json"
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
        $httpRequest.ContentLength = $bytes.Length
        $stream = $httpRequest.GetRequestStream()
        $stream.Write($bytes, 0, $bytes.Length)
        $stream.Close()
    }

    try {
        $response = $httpRequest.GetResponse()
        $streamReader = New-Object System.IO.StreamReader($response.GetResponseStream())
        $responseBody = $streamReader.ReadToEnd()
        $streamReader.Close()
        $response.Close()
        
        Write-Host "Response: $responseBody" -ForegroundColor Green
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errorBody = $streamReader.ReadToEnd()
            $streamReader.Close()
            Write-Host "Error Response: $errorBody" -ForegroundColor Red
        }
    }
    Write-Host ""
}

Write-Host "----------- 1. dataCodes API -----------" -ForegroundColor Cyan
Send-Request -method "GET" -url "$baseUrl/api/dataCodes" -description "Get all data codes" -needAuth $true

Write-Host "----------- 2. schema APIs -----------" -ForegroundColor Cyan
$schemaList = @("work_order_page", "work_order_detail", "work_order_search", "dashboard_data", "dashboard_handle_quantity", "dashboard_messages", "flow_get_by_id", "flow_page")
foreach ($schema in $schemaList) {
    Send-Request -method "GET" -url "$baseUrl/api/schema/$schema" -description "Get schema for $schema" -needAuth $true
}

Write-Host "----------- 3. query - work_order_page -----------" -ForegroundColor Cyan
$body = '{"dataCode": "work_order_page", "params": {"pageNum": 1, "pageSize": 10}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Query work order page"

Write-Host "----------- 4. query - work_order_detail -----------" -ForegroundColor Cyan
$body = '{"dataCode": "work_order_detail", "params": {"id": 1}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Query work order detail"

Write-Host "----------- 5. query - work_order_search -----------" -ForegroundColor Cyan
$body = '{"dataCode": "work_order_search", "params": {"keyword": "test", "pageNum": 1, "pageSize": 10}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Search work orders"

Write-Host "----------- 6. query - dashboard_data -----------" -ForegroundColor Cyan
$body = '{"dataCode": "dashboard_data", "params": {}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Get dashboard data"

Write-Host "----------- 7. query - dashboard_handle_quantity -----------" -ForegroundColor Cyan
$body = '{"dataCode": "dashboard_handle_quantity", "params": {}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Get handle quantity"

Write-Host "----------- 8. query - dashboard_messages -----------" -ForegroundColor Cyan
$body = '{"dataCode": "dashboard_messages", "params": {"pageNum": 1, "pageSize": 10}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Get messages"

Write-Host "----------- 9. query - flow_get_by_id -----------" -ForegroundColor Cyan
$body = '{"dataCode": "flow_get_by_id", "params": {"flowId": 1}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Get flow by id"

Write-Host "----------- 10. query - flow_page -----------" -ForegroundColor Cyan
$body = '{"dataCode": "flow_page", "params": {"pageNum": 1, "pageSize": 10}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $true -description "Get flow page"

Write-Host "----------- 11. Auth Test - No Token -----------" -ForegroundColor Cyan
$body = '{"dataCode": "work_order_page", "params": {"pageNum": 1, "pageSize": 10}}'
Send-Request -method "POST" -url "$baseUrl/api/query" -body $body -needAuth $false -description "Request without token (expect 401)"

Write-Host "----------- 12. Auth Test - Invalid Token -----------" -ForegroundColor Cyan
$invalidToken = "invalid-token-12345"
$httpRequest = [System.Net.HttpWebRequest]::Create("$baseUrl/api/query")
$httpRequest.Method = "POST"
$httpRequest.ContentType = "application/json"
$httpRequest.Headers["X-Trace-ID"] = $traceId
$httpRequest.Headers["Authorization"] = $invalidToken
$bytes = [System.Text.Encoding]::UTF8.GetBytes('{"dataCode": "work_order_page", "params": {"pageNum": 1, "pageSize": 10}}')
$httpRequest.ContentLength = $bytes.Length
$stream = $httpRequest.GetRequestStream()
$stream.Write($bytes, 0, $bytes.Length)
$stream.Close()
try {
    $response = $httpRequest.GetResponse()
    $streamReader = New-Object System.IO.StreamReader($response.GetResponseStream())
    $responseBody = $streamReader.ReadToEnd()
    $streamReader.Close()
    $response.Close()
    Write-Host "Response: $responseBody" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $streamReader.ReadToEnd()
        $streamReader.Close()
        Write-Host "Error Response: $errorBody" -ForegroundColor Red
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Completed" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan