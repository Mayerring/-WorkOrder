# 工单系统CLI查询技能

## 概述

本技能用于通过CLI服务查询工单系统的数据。调用分为三个步骤：鉴权、生成TraceID、调用查询接口。

## 调用流程

### 第一步：鉴权（获取Token）

从工单系统登录接口获取JWT Token。

**登录接口**：
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "zhangsan", "password": "123456"}'
```

**响应示例**：
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

**说明**：
- Token来源：工单系统登录接口返回的JWT Token
- Token格式：纯JWT字符串，不含"Bearer "前缀
- Token有效期：1天，过期需重新获取

### 第二步：生成TraceID

生成随机UUID作为TraceID，用于日志追踪。

**生成方式**：
```bash
trace_id=$(python -c "import uuid; print(uuid.uuid4())")
# 或
trace_id=$(uuidgen)
# 或直接使用固定值用于测试
trace_id="test-trace-id-$(date +%s)"
```

### 第三步：调用查询接口

通过统一查询接口查询数据，传入dataCode和参数。

## 可用工具

### 1. 获取所有可查询数据类型

**工具名称**：list_data_codes

**功能描述**：获取所有可查询的数据类型（dataCode列表）

**调用方式**：
```bash
curl -X GET http://localhost:5000/api/dataCodes \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: <trace_id>"
```

**返回示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "dataCode": "work_order_page",
      "name": "工单分页查询",
      "description": "分页查询工单列表",
      "permission": "all"
    }
  ]
}
```

### 2. 获取数据Schema

**工具名称**：get_data_schema

**功能描述**：获取指定dataCode的入参出参Schema说明

**调用方式**：
```bash
curl -X GET http://localhost:5000/api/schema/<dataCode> \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: <trace_id>"
```

**参数说明**：
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| dataCode | String | 是 | 数据类型标识 |

**返回示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "dataCode": "work_order_page",
    "name": "工单分页查询",
    "inputSchema": {
      "pageNum": {"type": "Integer", "required": true, "description": "当前页数"},
      "pageSize": {"type": "Integer", "required": true, "description": "每页大小"},
      "title": {"type": "String", "required": false, "description": "工单标题"},
      "code": {"type": "String", "required": false, "description": "工单编号"},
      "type": {"type": "Integer", "required": false, "description": "工单类型（0需求，1故障）"},
      "content": {"type": "String", "required": false, "description": "工单关键词"},
      "createTimeTo": {"type": "Long", "required": false, "description": "创建时间止"}
    },
    "outputSchema": {
      "records": {"type": "List", "description": "工单列表"},
      "total": {"type": "Long", "description": "总数量"},
      "current": {"type": "Integer", "description": "当前页码"},
      "pages": {"type": "Integer", "description": "总页数"},
      "size": {"type": "Integer", "description": "每页大小"}
    }
  }
}
```

### 3. 查询数据

**工具名称**：query_data

**功能描述**：通过dataCode查询指定数据

**调用方式**：
```bash
curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: <trace_id>" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "<dataCode>",
    "params": {...}
  }'
```

**参数说明**：
| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| dataCode | String | 是 | 数据类型标识 |
| params | Object | 否 | 查询参数，根据dataCode的Schema定义 |

**返回示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {...},
  "traceId": "<trace_id>"
}
```

## 可用dataCode列表

| dataCode | 名称 | 描述 | 必填参数 | 可选参数 |
|----------|------|------|----------|----------|
| work_order_page | 工单分页查询 | 分页查询工单列表 | pageNum, pageSize | title, code, type, content, createTimeTo |
| work_order_detail | 工单详情 | 查询工单详细信息 | id | - |
| work_order_search | 工单搜索 | 根据关键词搜索工单 | keyword | pageNum, pageSize |
| dashboard_data | 数据看板 | 获取数据统计概览 | 无 | - |
| dashboard_handle_quantity | 本周处理数量 | 获取本周处理统计 | 无 | - |
| dashboard_messages | 消息中心 | 获取消息列表 | pageNum, pageSize | - |
| flow_get_by_id | 流程详情 | 查询流程详细信息 | flowId | - |
| flow_page | 流程分页 | 分页查询流程列表 | pageNum, pageSize | - |

## 使用示例

### 示例1：查询工单列表

```bash
trace_id=$(uuidgen)

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "work_order_page",
    "params": {
      "pageNum": 1,
      "pageSize": 10
    }
  }'
```

### 示例2：查询工单详情

```bash
trace_id=$(uuidgen)

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "work_order_detail",
    "params": {
      "id": 1
    }
  }'
```

### 示例3：关键词搜索工单

```bash
trace_id=$(uuidgen)

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "work_order_search",
    "params": {
      "keyword": "服务器",
      "pageNum": 1,
      "pageSize": 10
    }
  }'
```

### 示例4：获取数据看板

```bash
trace_id=$(uuidgen)

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "dashboard_data",
    "params": {}
  }'
```

### 示例5：获取消息列表

```bash
trace_id=$(uuidgen)

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "dashboard_messages",
    "params": {
      "pageNum": 1,
      "pageSize": 10
    }
  }'
```

## 工单状态说明

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 100 | 未审核 | 工单已提交，等待审核 |
| 200 | 审核中 | 正在审核流程中 |
| 270 | 审核失败 | 审核未通过 |
| 300 | 未派单 | 审核通过，等待分配处理人 |
| 400 | 处理中 | 正在处理中 |
| 410 | 已超时 | 处理超时 |
| 500 | 已完成 | 处理完成，等待确认 |
| 600 | 已确认完成 | 确认通过，流程结束 |
| 670 | 确认失败 | 确认未通过 |
| 700 | 已取消 | 工单被取消 |

## 工单类型说明

| 类型码 | 类型名称 |
|--------|----------|
| 0 | 需求类工单 |
| 1 | 故障类工单 |

## 优先级说明

| 优先级码 | 优先级名称 |
|----------|------------|
| 0 | 高优先级 |
| 1 | 中优先级 |
| 2 | 低优先级 |

## 错误处理

### 常见错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 401 | Token无效或未提供 | 检查Authorization头是否正确，重新登录获取Token |
| 403 | 权限不足 | 检查用户角色是否有权限访问该dataCode |
| 404 | dataCode不存在 | 调用list_data_codes获取正确的dataCode |
| 500 | 服务器内部错误 | 查看响应中的traceId，联系管理员排查 |

### 重试机制

当请求失败时，可根据错误类型决定是否重试：

| 错误类型 | 是否重试 | 重试次数 | 间隔 |
|----------|----------|----------|------|
| 网络超时 | 是 | 3次 | 1s, 2s, 4s（指数退避） |
| 500错误 | 是 | 2次 | 1s, 2s |
| 401错误 | 否 | - | 重新登录获取Token |
| 403错误 | 否 | - | 检查权限或更换用户 |
| 404错误 | 否 | - | 检查dataCode是否正确 |

### 降级策略

当CLI服务不可用时：
1. 返回友好提示，建议稍后重试
2. 提供替代查询方式（如直接访问网页端）
3. 记录错误追踪ID，便于问题排查

## 注意事项

1. **Token有效期**：Token有效期为1天，过期需重新登录获取
2. **TraceID必传**：所有请求必须携带X-Trace-ID头，便于日志追踪
3. **参数校验**：调用前建议先获取Schema确认参数格式
4. **分页参数**：分页查询接口必须传入pageNum和pageSize参数
5. **中文编码**：请求体中的中文参数需要使用UTF-8编码
6. **服务依赖**：CLI服务依赖backend服务（端口8080），需先启动backend