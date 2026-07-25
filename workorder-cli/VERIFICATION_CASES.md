# workorder-cli 阶段3/4/5 验证用例

## 概述

本验证用例覆盖 Phase 3（HTTP客户端）、Phase 4（Schema获取）、Phase 5（命令实现）的所有功能点。

## 前置条件

1. 后端服务（端口8080）已启动
2. CLI Service（端口5000）已启动
3. 已完成登录：`workorder-cli auth login --phone 13812345678 --password newPassword123!`
4. 已编译workorder-cli可执行文件
    cd d:\JavaCode\-WorkOrder\workorder-cli
    go build -o workorder-cli.exe .
    
## Phase 3：HTTP客户端

### 3.1 API请求封装

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P3-001 | GET请求封装 | `workorder-cli api call --method GET --endpoint /dashboard/data` | 返回数据看板数据，code=0 |
| P3-002 | POST请求封装 | `workorder-cli api call --method POST --endpoint /workOrder/page --body '{"pageNum":1,"pageSize":5}'` | 返回工单列表，code=0 |
| P3-003 | Header设置 | `workorder-cli api call --method GET --endpoint /dashboard/data` | 请求携带Authorization头 |
| P3-004 | 无效Endpoint | `workorder-cli api call --method GET --endpoint /invalid` | 返回错误，code≠0 |

### 3.2 响应解析

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P3-011 | 成功响应解析 | `workorder-cli dashboard data` | 输出JSON格式，包含code/message/data/traceId |
| P3-012 | 错误响应解析 | 使用无效Token执行命令 | 输出JSON格式，code=401，提示认证错误 |
| P3-013 | 响应透传 | `workorder-cli work_order page --page-num 1` | 后端响应完整透传 |

## Phase 4：Schema获取

### 4.1 Schema获取

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P4-001 | 获取所有dataCode | `workorder-cli list` | 返回所有可用命令列表（8个） |
| P4-002 | 获取工单分页Schema | `workorder-cli schema work_order_page` | 返回inputSchema和outputSchema |
| P4-003 | 获取工单详情Schema | `workorder-cli schema work_order_detail` | 返回inputSchema和outputSchema |
| P4-004 | 获取工单搜索Schema | `workorder-cli schema work_order_search` | 返回inputSchema和outputSchema |
| P4-005 | 获取数据看板Schema | `workorder-cli schema dashboard_data` | 返回inputSchema和outputSchema |
| P4-006 | 获取消息中心Schema | `workorder-cli schema dashboard_messages` | 返回inputSchema和outputSchema |
| P4-007 | 获取流程详情Schema | `workorder-cli schema flow_get_by_id` | 返回inputSchema和outputSchema |
| P4-008 | 获取流程分页Schema | `workorder-cli schema flow_page` | 返回inputSchema和outputSchema |
| P4-009 | 无效dataCode | `workorder-cli schema invalid_code` | 返回错误，code≠0 |

### 4.2 dataCode映射验证

| 测试ID | dataCode | 对应命令 | 后端端点 |
|--------|----------|----------|----------|
| P4-011 | work_order_page | `work_order page` | POST /workOrder/page |
| P4-012 | work_order_detail | `work_order detail` | GET /workOrder/detail |
| P4-013 | work_order_search | `work_order search` | POST /workOrder/search |
| P4-014 | dashboard_data | `dashboard data` | GET /dashboard/data |
| P4-015 | dashboard_handle_quantity | `dashboard handle_quantity` | GET /dashboard/handleQuantity |
| P4-016 | dashboard_messages | `dashboard messages` | POST /dashboard/pageMessages |
| P4-017 | flow_get_by_id | `flow getById` | GET /flow/getById |
| P4-018 | flow_page | `flow page` | POST /flow/page |

## Phase 5：命令实现

### 5.1 工单命令

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P5-001 | 工单分页查询（默认参数） | `workorder-cli work_order page` | 返回第1页，10条工单 |
| P5-002 | 工单分页查询（指定参数） | `workorder-cli work_order page --page-num 2 --page-size 5` | 返回第2页，5条工单 |
| P5-003 | 工单分页查询（按标题筛选） | `workorder-cli work_order page --title 服务器` | 返回标题包含"服务器"的工单 |
| P5-004 | 工单分页查询（按编号筛选） | `workorder-cli work_order page --code WO20240101001` | 返回指定编号的工单 |
| P5-005 | 工单分页查询（按类型筛选） | `workorder-cli work_order page --type 0` | 返回需求类工单 |
| P5-006 | 工单详情（按ID） | `workorder-cli work_order detail --id 1` | 返回ID=1的工单详情 |
| P5-007 | 工单详情（按编号） | `workorder-cli work_order detail --code WO20240101001` | 返回指定编号的工单详情 |
| P5-008 | 工单详情（无参数） | `workorder-cli work_order detail` | 返回错误，提示必须指定id或code |
| P5-009 | 工单搜索（关键词） | `workorder-cli work_order search --keyword 故障` | 返回包含"故障"的工单 |
| P5-010 | 工单搜索（无关键词） | `workorder-cli work_order search` | 返回错误，提示keyword不能为空 |

### 5.2 看板命令

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P5-011 | 数据看板 | `workorder-cli dashboard data` | 返回数据统计概览 |
| P5-012 | 本周处理数量 | `workorder-cli dashboard handle_quantity` | 返回本周处理统计 |
| P5-013 | 消息中心（默认参数） | `workorder-cli dashboard messages` | 返回第1页，10条消息 |
| P5-014 | 消息中心（指定参数） | `workorder-cli dashboard messages --page-num 1 --page-size 20` | 返回第1页，20条消息 |

### 5.3 流程命令

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P5-021 | 流程详情 | `workorder-cli flow getById --flow-id 1` | 返回ID=1的流程详情 |
| P5-022 | 流程详情（无参数） | `workorder-cli flow getById` | 返回错误，提示flow-id不能为空 |
| P5-023 | 流程分页（默认参数） | `workorder-cli flow page` | 返回第1页，10条流程 |
| P5-024 | 流程分页（指定参数） | `workorder-cli flow page --page-num 1 --page-size 5` | 返回第1页，5条流程 |

### 5.4 Raw API命令

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P5-031 | GET请求 | `workorder-cli api call --method GET --endpoint /dashboard/data` | 返回数据看板数据 |
| P5-032 | POST请求 | `workorder-cli api call --method POST --endpoint /workOrder/page --body '{"pageNum":1,"pageSize":3}'` | 返回工单列表 |
| P5-033 | 默认GET请求 | `workorder-cli api call --endpoint /dashboard/data` | 返回数据看板数据 |
| P5-034 | 无效JSON body | `workorder-cli api call --method POST --endpoint /workOrder/page --body 'invalid'` | 返回错误，提示JSON无效 |
| P5-035 | 无endpoint | `workorder-cli api call --method GET` | 返回错误，提示endpoint不能为空 |
| P5-036 | 不支持的方法 | `workorder-cli api call --method DELETE --endpoint /workOrder/1` | 返回错误，提示不支持的方法 |

### 5.5 Shortcuts快捷命令

| 测试ID | 测试场景 | 测试命令 | 预期结果 |
|--------|----------|----------|----------|
| P5-041 | 快捷查询工单列表 | `workorder-cli work_order +list` | 返回第1页，10条工单 |
| P5-042 | 快捷搜索工单 | `workorder-cli work_order +search --keyword 需求` | 返回包含"需求"的工单 |
| P5-043 | 数据概览 | `workorder-cli dashboard +overview` | 返回数据统计概览 |

## 认证验证

| 测试ID | 测试场景 | 测试步骤 | 预期结果 |
|--------|----------|----------|----------|
| AUTH-001 | 有效Token | 登录后执行查询命令 | 查询成功，code=0 |
| AUTH-002 | 无效Token | 设置无效TOKEN后执行查询命令 | 返回认证错误，code=401 |
| AUTH-003 | 过期Token | 使用过期Token执行查询命令 | 返回认证错误，code=401 |
| AUTH-004 | 无Token | 未登录状态执行查询命令 | 返回认证错误，code=401 |
| AUTH-005 | 环境变量传递Token | 设置WORKORDER_TOKEN环境变量后执行命令 | Token正确传递，查询成功 |
| AUTH-006 | 命令行参数传递Token | 使用-t参数传递Token执行命令 | Token正确传递，查询成功 |

## 参数映射验证

### 工单分页参数映射

| CLI参数 | API参数 | 类型 | 必填 | 默认值 |
|---------|---------|------|------|--------|
| --page-num | pageNum | int | 否 | 1 |
| --page-size | pageSize | int | 否 | 10 |
| --title | title | string | 否 | "" |
| --code | code | string | 否 | "" |
| --type | type | int | 否 | -1 |
| --content | content | string | 否 | "" |
| --create-time-to | createTimeTo | int64 | 否 | 0 |

### 工单搜索参数映射

| CLI参数 | API参数 | 类型 | 必填 | 默认值 |
|---------|---------|------|------|--------|
| --keyword | keyword | string | 是 | - |
| --page-num | pageNum | int | 否 | 1 |
| --page-size | pageSize | int | 否 | 10 |

### 消息中心参数映射

| CLI参数 | API参数 | 类型 | 必填 | 默认值 |
|---------|---------|------|------|--------|
| --page-num | pageNum | int | 否 | 1 |
| --page-size | pageSize | int | 否 | 10 |

### 流程分页参数映射

| CLI参数 | API参数 | 类型 | 必填 | 默认值 |
|---------|---------|------|------|--------|
| --page-num | pageNum | int | 否 | 1 |
| --page-size | pageSize | int | 否 | 10 |

## 输出格式验证

所有命令输出必须符合统一JSON格式：

```json
{
    "code": 0,
    "message": "success",
    "data": {...},
    "traceId": ""
}
```

| 测试ID | 验证项 | 预期结果 |
|--------|--------|----------|
| OUTPUT-001 | JSON格式 | 输出为有效JSON |
| OUTPUT-002 | code字段 | 成功时为0，失败时非0 |
| OUTPUT-003 | message字段 | 成功时为"success"，失败时为错误信息 |
| OUTPUT-004 | data字段 | 成功时包含数据，失败时为null |
| OUTPUT-005 | traceId字段 | 始终存在，可为空字符串 |

## 退出码验证

| 测试ID | 场景 | 预期退出码 |
|--------|------|------------|
| EXIT-001 | 成功 | 0 |
| EXIT-002 | 通用错误 | 1 |
| EXIT-003 | 认证错误 | 2 |
| EXIT-004 | 参数错误 | 4 |

## 测试脚本

以下是完整的测试脚本（test_cli.ps1）：

```powershell
$cli = "workorder-cli"

Write-Host "=== Phase 3: HTTP客户端 ==="
Write-Host "P3-001: GET请求封装"
& $cli api call --method GET --endpoint /dashboard/data
Write-Host ""

Write-Host "P3-002: POST请求封装"
& $cli api call --method POST --endpoint /workOrder/page --body '{"pageNum":1,"pageSize":5}'
Write-Host ""

Write-Host "=== Phase 4: Schema获取 ==="
Write-Host "P4-001: 获取所有dataCode"
& $cli list
Write-Host ""

Write-Host "P4-002: 获取工单分页Schema"
& $cli schema work_order_page
Write-Host ""

Write-Host "=== Phase 5: 工单命令 ==="
Write-Host "P5-001: 工单分页查询"
& $cli work_order page --page-num 1 --page-size 5
Write-Host ""

Write-Host "P5-006: 工单详情"
& $cli work_order detail --id 1
Write-Host ""

Write-Host "P5-009: 工单搜索"
& $cli work_order search --keyword 故障
Write-Host ""

Write-Host "=== Phase 5: 看板命令 ==="
Write-Host "P5-011: 数据看板"
& $cli dashboard data
Write-Host ""

Write-Host "P5-012: 本周处理数量"
& $cli dashboard handle_quantity
Write-Host ""

Write-Host "=== Phase 5: 流程命令 ==="
Write-Host "P5-023: 流程分页"
& $cli flow page --page-num 1 --page-size 5
Write-Host ""

Write-Host "=== Phase 5: Shortcuts ==="
Write-Host "P5-041: 快捷查询工单列表"
& $cli work_order +list
Write-Host ""

Write-Host "P5-043: 数据概览"
& $cli dashboard +overview
Write-Host ""

Write-Host "=== 认证验证 ==="
Write-Host "AUTH-001: 有效Token"
& $cli auth status
Write-Host ""
```

## 验证结果记录

| 测试ID | 状态 | 备注 |
|--------|------|------|
| P3-001 | ▢ | |
| P3-002 | ▢ | |
| P3-003 | ▢ | |
| P3-004 | ▢ | |
| P3-011 | ▢ | |
| P3-012 | ▢ | |
| P3-013 | ▢ | |
| P4-001 | ▢ | |
| P4-002 | ▢ | |
| P4-003 | ▢ | |
| P4-004 | ▢ | |
| P4-005 | ▢ | |
| P4-006 | ▢ | |
| P4-007 | ▢ | |
| P4-008 | ▢ | |
| P4-009 | ▢ | |
| P5-001 | ▢ | |
| P5-002 | ▢ | |
| P5-003 | ▢ | |
| P5-004 | ▢ | |
| P5-005 | ▢ | |
| P5-006 | ▢ | |
| P5-007 | ▢ | |
| P5-008 | ▢ | |
| P5-009 | ▢ | |
| P5-010 | ▢ | |
| P5-011 | ▢ | |
| P5-012 | ▢ | |
| P5-013 | ▢ | |
| P5-014 | ▢ | |
| P5-021 | ▢ | |
| P5-022 | ▢ | |
| P5-023 | ▢ | |
| P5-024 | ▢ | |
| P5-031 | ▢ | |
| P5-032 | ▢ | |
| P5-033 | ▢ | |
| P5-034 | ▢ | |
| P5-035 | ▢ | |
| P5-036 | ▢ | |
| P5-041 | ▢ | |
| P5-042 | ▢ | |
| P5-043 | ▢ | |
| AUTH-001 | ▢ | |
| AUTH-002 | ▢ | |
| AUTH-003 | ▢ | |
| AUTH-004 | ▢ | |
| AUTH-005 | ▢ | |
| AUTH-006 | ▢ | |
| OUTPUT-001 | ▢ | |
| OUTPUT-002 | ▢ | |
| OUTPUT-003 | ▢ | |
| OUTPUT-004 | ▢ | |
| OUTPUT-005 | ▢ | |
| EXIT-001 | ▢ | |
| EXIT-002 | ▢ | |
| EXIT-003 | ▢ | |
| EXIT-004 | ▢ | |