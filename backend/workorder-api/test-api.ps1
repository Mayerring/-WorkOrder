Add-Type -AssemblyName System.Net.Http
Add-Type -AssemblyName System.Text.Encoding

$baseUrl = "http://localhost:8081"
$authToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLlvKDkuIkiLCJpZCI6MiwibmFtZSI6IuW8oOS4iSIsImNvbXBhbnkiOiLmgLvlhazlj7giLCJkZXBhcnRtZW50Ijoi5oC75Yqh6YOoIiwicG9zaXRpb24iOiLmgLvnu4_nkIYiLCJzdGF0dXMiOjAsInBob25lIjoiMTM4MTIzNDU2NzgiLCJlbWFpbCI6InpoYW5nc2FuQGV4YW1wbGUuY29tIiwicm9sZSI6InVzZXIiLCJpYXQiOjE3ODM5OTIwMDcsImV4cCI6MTc4NDA3ODQwN30.GAdxrOGNigZ6gCIzSopnM9v2hPuuzHS0KO96xXKpLDM"

function Test-ChatEndpoint {
    param(
        [string]$message,
        [long]$memoryId,
        [string]$token,
        [string]$testName
    )
    
    Write-Host "`n=== 测试用例: $testName ==="
    Write-Host "输入: $message"
    
    $body = @{
        memoryId = $memoryId
        message = $message
    } | ConvertTo-Json
    
    $headers = @{}
    
    if ($token) {
        $headers["Authorization"] = $token
    }
    
    try {
        $httpClient = New-Object System.Net.Http.HttpClient
        $httpRequest = New-Object System.Net.Http.HttpRequestMessage([System.Net.Http.HttpMethod]::Post, "$baseUrl/assistant/chat")
        $content = New-Object System.Net.Http.StringContent($body, [System.Text.Encoding]::UTF8, "application/json")
        $httpRequest.Content = $content
        
        foreach ($key in $headers.Keys) {
            $httpRequest.Headers.Add($key, $headers[$key])
        }
        
        $mediaType = New-Object System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("text/event-stream")
        $httpRequest.Headers.Accept.Add($mediaType)
        
        $response = $httpClient.SendAsync($httpRequest).Result
        
        if ($response.IsSuccessStatusCode) {
            $stream = $response.Content.ReadAsStreamAsync().Result
            $streamReader = New-Object System.IO.StreamReader($stream)
            
            $fullResponse = ""
            while (-not $streamReader.EndOfStream) {
                $line = $streamReader.ReadLine()
                if ($line) {
                    $fullResponse += $line
                }
            }
            
            Write-Host "响应状态码: $($response.StatusCode)"
            Write-Host "响应长度: $($fullResponse.Length) 字符"
            Write-Host "状态: 成功"
            $httpClient.Dispose()
            return $true
        } else {
            $errorContent = $response.Content.ReadAsStringAsync().Result
            Write-Host "错误状态码: $($response.StatusCode)"
            Write-Host "错误内容: $errorContent"
            Write-Host "状态: 失败"
            $httpClient.Dispose()
            return $false
        }
    } catch {
        Write-Host "错误: $_"
        Write-Host "状态: 失败"
        return $false
    }
}

function Test-ChatEndpointNoToken {
    param(
        [string]$message,
        [long]$memoryId,
        [string]$testName
    )
    
    Write-Host "`n=== 测试用例: $testName ==="
    Write-Host "输入: $message"
    Write-Host "说明: 不携带Authorization Token"
    
    $body = @{
        memoryId = $memoryId
        message = $message
    } | ConvertTo-Json
    
    $headers = @{}
    
    try {
        $httpClient = New-Object System.Net.Http.HttpClient
        $httpRequest = New-Object System.Net.Http.HttpRequestMessage([System.Net.Http.HttpMethod]::Post, "$baseUrl/assistant/chat")
        $content = New-Object System.Net.Http.StringContent($body, [System.Text.Encoding]::UTF8, "application/json")
        $httpRequest.Content = $content
        
        foreach ($key in $headers.Keys) {
            $httpRequest.Headers.Add($key, $headers[$key])
        }
        
        $response = $httpClient.SendAsync($httpRequest).Result
        
        if ($response.StatusCode -eq [System.Net.HttpStatusCode]::Unauthorized -or 
            $response.StatusCode -eq [System.Net.HttpStatusCode]::BadRequest) {
            $errorContent = $response.Content.ReadAsStringAsync().Result
            Write-Host "错误状态码: $($response.StatusCode)"
            Write-Host "错误内容: $errorContent"
            Write-Host "状态: 成功(预期的401/400错误)"
            $httpClient.Dispose()
            return $true
        } else {
            Write-Host "响应状态码: $($response.StatusCode)"
            Write-Host "状态: 失败(预期401/400，但收到其他状态码)"
            $httpClient.Dispose()
            return $false
        }
    } catch {
        Write-Host "错误: $_"
        Write-Host "状态: 成功(预期的异常)"
        return $true
    }
}

Write-Host "=========================================="
Write-Host "AiAssistant API 接口测试脚本"
Write-Host "=========================================="
Write-Host "服务地址: $baseUrl"
Write-Host "Token: $(if ($authToken) { '已配置' } else { '未配置' })"
Write-Host "=========================================="

$testResults = @()

Write-Host "`n--- TC-001: 查询工单列表 ---"
$result = Test-ChatEndpoint -message "帮我查一下工单列表" -memoryId 1 -token $authToken -testName "TC-001: 查询工单列表"
$testResults += @{ TestName = "TC-001"; Result = $result }

Write-Host "`n--- TC-002: 查询工单并导出Excel ---"
$result = Test-ChatEndpoint -message "查一下所有处理中的工单，导出成Excel" -memoryId 1 -token $authToken -testName "TC-002: 查询工单并导出Excel"
$testResults += @{ TestName = "TC-002"; Result = $result }

Write-Host "`n--- TC-003: 查询工单并生成Markdown报告 ---"
$result = Test-ChatEndpoint -message "查一下本周完成的工单，生成Markdown报告" -memoryId 1 -token $authToken -testName "TC-003: 查询工单并生成Markdown报告"
$testResults += @{ TestName = "TC-003"; Result = $result }

Write-Host "`n--- TC-004: 读取Excel并分析 ---"
$result = Test-ChatEndpoint -message "读取处理中工单.xlsx，统计各状态数量" -memoryId 1 -token $authToken -testName "TC-004: 读取Excel并分析"
$testResults += @{ TestName = "TC-004"; Result = $result }

Write-Host "`n--- TC-005: 查询消息 ---"
$result = Test-ChatEndpoint -message "帮我查一下我的消息" -memoryId 1 -token $authToken -testName "TC-005: 查询消息"
$testResults += @{ TestName = "TC-005-1"; Result = $result }

Write-Host "`n--- TC-005-2: 保存消息到TXT ---"
$result = Test-ChatEndpoint -message "把这些消息保存到TXT文件" -memoryId 1 -token $authToken -testName "TC-005-2: 保存消息到TXT"
$testResults += @{ TestName = "TC-005-2"; Result = $result }

Write-Host "`n--- TC-006: 状态解释 (RAG检索) ---"
$result = Test-ChatEndpoint -message "工单状态200是什么意思" -memoryId 1 -token $authToken -testName "TC-006: 状态解释"
$testResults += @{ TestName = "TC-006"; Result = $result }

Write-Host "`n--- TC-007: 无Token请求 ---"
$result = Test-ChatEndpointNoToken -message "帮我查一下工单列表" -memoryId 1 -testName "TC-007: 无Token请求"
$testResults += @{ TestName = "TC-007"; Result = $result }

Write-Host "`n--- TC-008: 错误参数请求 ---"
$result = Test-ChatEndpoint -message "查一下状态为999的工单" -memoryId 1 -token $authToken -testName "TC-008: 错误参数请求"
$testResults += @{ TestName = "TC-008"; Result = $result }

Write-Host "`n--- TC-009: 空消息请求 ---"
$result = Test-ChatEndpoint -message "" -memoryId 1 -token $authToken -testName "TC-009: 空消息请求"
$testResults += @{ TestName = "TC-009"; Result = $result }

Write-Host "`n--- TC-010: 特殊字符请求 ---"
$result = Test-ChatEndpoint -message "帮我查一下工单，标题包含@#$%^&*" -memoryId 1 -token $authToken -testName "TC-010: 特殊字符请求"
$testResults += @{ TestName = "TC-010"; Result = $result }

Write-Host "`n=========================================="
Write-Host "测试结果汇总"
Write-Host "=========================================="

$passed = ($testResults | Where-Object { $_.Result }).Count
$total = $testResults.Count

foreach ($result in $testResults) {
    $status = if ($result.Result) { "PASS" } else { "FAIL" }
    Write-Host "$($result.TestName): $status"
}

Write-Host "`n总测试用例: $total"
Write-Host "通过: $passed"
Write-Host "失败: $($total - $passed)"
Write-Host "通过率: $([math]::Round($passed / $total * 100, 2))%"

if ($passed -eq $total) {
    Write-Host "`n所有测试用例通过！" -ForegroundColor Green
} else {
    Write-Host "`n部分测试用例失败，请检查日志" -ForegroundColor Red
}
