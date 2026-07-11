# 工单系统CLI查询技能

## 概述

本技能用于通过CLI服务查询工单系统的数据。调用分为三个步骤：鉴权、生成TraceID、调用查询接口。

## 调用流程

### 第一步：设置Token（鉴权）

从浏览器Cookie中提取Token，通过set-token命令设置到CLI服务。

**命令格式**：
```bash
# 设置Token（待Agent CLI脚本实现）
workorder-cli set-token <token>

# 或通过环境变量设置
export WORKORDER_TOKEN=<token>
```

**说明**：
- Token来源：工单系统登录接口返回的JWT Token
- Token格式：纯JWT字符串，不含"Bearer "前缀
- Token有效期：1天，过期需重新获取
- 设置后，后续所有请求自动携带该Token

### 第二步：生成TraceID

执行Python脚本生成随机UUID作为TraceID。

**示例**：
```bash
trace_id=$(python -c "import uuid; print(uuid.uuid4())")
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
      "pageSize": {"type": "Integer", "required": true, "description": "每页大小"}
    },
    "outputSchema": {
      "records": {"type": "List", "description": "工单列表"},
      "total": {"type": "Long", "description": "总数量"}
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

| dataCode | 名称 | 描述 | 权限 |
|----------|------|------|------|
| work_order_page | 工单分页查询 | 分页查询工单列表 | 所有用户 |
| work_order_detail | 工单详情 | 查询工单详细信息 | 所有用户 |
| work_order_search | 工单搜索 | 根据关键词搜索工单 | 所有用户 |
| dashboard_data | 数据看板 | 获取数据统计概览 | 所有用户 |
| dashboard_todo | 待办事项 | 获取待处理工单列表 | 所有用户 |
| dashboard_status | 工单状态统计 | 按状态统计工单数量 | 所有用户 |
| dashboard_type | 工单类型统计 | 按类型统计工单数量 | 所有用户 |
| dashboard_handle_quantity | 本周处理数量 | 获取本周处理统计 | 所有用户 |
| dashboard_messages | 消息中心 | 获取消息列表 | 所有用户 |
| flow_get_by_id | 流程详情 | 查询流程详细信息 | 所有用户 |
| flow_page | 流程分页 | 分页查询流程列表 | 所有用户 |
| user_me | 个人信息 | 查询当前用户信息 | 所有用户 |
| user_organization | 组织架构 | 获取组织架构信息 | 所有用户 |
| user_page | 员工分页 | 分页查询员工列表 | 所有用户 |
| user_all | 所有员工 | 获取所有员工信息 | 所有用户 |
| company_all | 所有公司 | 获取所有公司列表 | ADMIN |
| department_all | 公司部门列表 | 获取指定公司部门 | ADMIN |
| staff_page | 员工分页(Admin) | 管理员分页查询员工 | ADMIN |
| staff_all | 所有员工(Admin) | 管理员获取所有员工 | ADMIN |

## 使用示例

### 示例1：查询工单列表

```bash
# 生成TraceID
trace_id=$(python -c "import uuid; print(uuid.uuid4())")

# 查询工单分页
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
trace_id=$(python -c "import uuid; print(uuid.uuid4())")

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

### 示例3：获取数据看板

```bash
trace_id=$(python -c "import uuid; print(uuid.uuid4())")

curl -X POST http://localhost:5000/api/query \
  -H "Authorization: <token>" \
  -H "X-Trace-ID: $trace_id" \
  -H "Content-Type: application/json" \
  -d '{
    "dataCode": "dashboard_data",
    "params": {}
  }'
```

## 错误处理

### 常见错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 401 | Token无效或未提供 | 检查Authorization头是否正确 |
| 403 | 权限不足 | 检查用户角色是否有权限访问该dataCode |
| 404 | dataCode不存在 | 调用list_data_codes获取正确的dataCode |
| 500 | 服务器内部错误 | 查看响应中的traceId，联系管理员排查 |

### 重试机制

当请求失败时，可根据错误类型决定是否重试：
- **网络错误**：建议重试1-3次
- **401错误**：重新获取Token后重试
- **403错误**：检查权限或更换用户
- **500错误**：记录traceId，联系管理员

## 注意事项

1. **Token有效期**：Token有效期为1天，过期需重新登录获取
2. **TraceID必传**：所有请求必须携带X-Trace-ID头，便于日志追踪
3. **参数校验**：调用前建议先获取Schema确认参数格式
4. **分页参数**：分页查询接口必须传入pageNum和pageSize参数
5. **权限限制**：部分dataCode需要ADMIN角色才能访问