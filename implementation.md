# CLI服务与后端改造实现计划

## 概述

本计划基于 [CLI_architecture.md](file:///d:/JavaCode/-WorkOrder/CLI_architecture.md) 技术架构文档，采用**共享Service层jar包 + Spring Cloud OpenFeign RPC**方案，拆解为三个主要部分：

1. **workorder-api模块**：提取Service接口、Param、VO等作为共享模块
2. **后端服务改造**：依赖workorder-api模块，添加TraceID拦截器，新增鉴权验证接口
3. **CLI服务实现**：依赖workorder-api模块，使用OpenFeign RPC调用后端

---

## 第一部分：workorder-api模块

### 阶段目标

创建独立的workorder-api模块，包含Service接口、Param、VO等，供backend和cli-service共同依赖。

### 任务列表

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| A1 | 创建Maven模块 | `workorder-api/pom.xml` | 创建纯Java模块，仅依赖lombok | 无 |
| A2 | 创建WorkOrderService接口 | `workorder-api/src/main/java/com/example/workorder/api/service/WorkOrderService.java` | 工单服务接口 | A1 |
| A3 | 创建DashboardService接口 | `workorder-api/src/main/java/com/example/workorder/api/service/DashboardService.java` | 数据看板服务接口 | A1 |
| A4 | 创建FlowService接口 | `workorder-api/src/main/java/com/example/workorder/api/service/FlowService.java` | 流程服务接口 | A1 |
| A5 | 创建AuthService接口 | `workorder-api/src/main/java/com/example/workorder/api/service/AuthService.java` | 鉴权服务接口 | A1 |
| A6 | 创建ValidateTokenResult | `workorder-api/src/main/java/com/example/workorder/api/dto/ValidateTokenResult.java` | Token验证结果DTO | A1 |
| A7 | 提取Param类 | `workorder-api/src/main/java/com/example/workorder/api/param/` | 从backend复制WorkOrderPageParam、WorkOrderDetailParam等 | A1 |
| A8 | 提取VO类 | `workorder-api/src/main/java/com/example/workorder/api/vo/` | 从backend复制WorkOrderPageVO、WorkOrderDetailVO等 | A1 |
| A9 | 提取枚举类 | `workorder-api/src/main/java/com/example/workorder/api/enums/` | 从backend复制WorkOrderStatusEnum等 | A1 |
| A10 | 安装到本地Maven仓库 | - | `mvn install` 将api模块安装到本地仓库 | A1-A9 |

### 详细设计

#### A1. 创建Maven模块

**pom.xml关键配置**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>workorder-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-extension</artifactId>
            <version>3.5.5</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>io.swagger.core.v3</groupId>
            <artifactId>swagger-annotations</artifactId>
            <version>2.2.20</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

#### A2-A5. 创建Service接口

从backend的Service接口提取查询方法：

**WorkOrderService**：
```java
public interface WorkOrderService {
    IPage<WorkOrderPageVO> pageWorkOrder(WorkOrderPageParam param);
    WorkOrderDetailVO detail(WorkOrderDetailParam param);
    SearchResult<WorkOrder> searchWorkOrders(String keyword, int pageNum, int pageSize) throws IOException;
}
```

**DashboardService**：
```java
public interface DashboardService {
    DashboardDataVO getData();
    List<WeekHandleVO> getWeekHandleQuantity();
    IPage<MessageVO> pageMessages(MessageParam param);
}
```

**FlowService**：
```java
public interface FlowService {
    FlowVO getById(FlowIdParam param);
    IPage<FlowVO> page(FlowPageParam param);
}
```

**AuthService**：
```java
public interface AuthService {
    ValidateTokenResult validateToken(String token);
}
```

**ValidateTokenResult**：
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResult {
    private boolean valid;
    private String userId;
    private String role;
    private String message;
}
```

---

## 第二部分：后端服务改造

### 阶段目标

1. 依赖workorder-api模块，重构Service实现类
2. 添加TraceID追踪能力，支持从CLI服务传递的TraceID进行全链路日志追踪
3. 新增鉴权验证接口，供CLI服务通过RPC调用

### 任务列表

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| B1 | 修改pom.xml依赖workorder-api | `backend/pom.xml` | 添加workorder-api依赖 | A10 |
| B2 | 重构Service实现类 | `backend/src/main/java/com/example/spring_vue_demo/service/impl/` | 修改ServiceImpl实现workorder-api接口 | B1 |
| B3 | 创建TraceIdInterceptor | `backend/src/main/java/com/example/spring_vue_demo/interceptor/TraceIdInterceptor.java` | 创建TraceID拦截器 | 无 |
| B4 | 注册TraceID拦截器 | `backend/src/main/java/com/example/spring_vue_demo/config/WebConfig.java` | 在WebConfig中注册TraceIdInterceptor | B3 |
| B5 | 更新日志格式配置 | `backend/src/main/resources/application.yaml` | 添加[%X{traceId}]到日志输出格式 | 无 |
| B6 | 创建AuthController | `backend/src/main/java/com/example/spring_vue_demo/controller/AuthController.java` | 新增鉴权验证接口 `/auth/validate` | B1 |

### 详细设计

#### B1. 修改pom.xml

添加workorder-api依赖：
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>workorder-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### B2. 重构Service实现类

修改 `WorkOrderServiceImpl`、`DashboardServiceImpl`、`FlowServiceImpl`：
- 实现 `com.example.workorder.api.service.WorkOrderService` 接口
- 保持原有业务逻辑不变

#### B3. 创建TraceIdInterceptor

**功能**：
- 从请求头 `X-Trace-ID` 获取TraceID
- 若未提供，自动生成UUID作为TraceID
- 将TraceID放入MDC，便于日志打印
- 将TraceID写入响应头，便于下游追踪

**代码示例**：
```java
@Component
public class TraceIdInterceptor implements HandlerInterceptor {
    
    public static final String TRACE_ID_HEADER = "X-Trace-ID";
    public static final String TRACE_ID_KEY = "traceId";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(TRACE_ID_KEY);
    }
}
```

#### B4. 注册TraceID拦截器

**修改WebConfig.java**：
- 添加 `TraceIdInterceptor` 的注入
- 在 `addInterceptors` 方法中注册拦截器

#### B5. 更新日志格式配置

**修改application.yaml**：
```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId}] - %msg%n"
```

#### B6. 创建AuthController

**功能**：提供RPC鉴权验证接口，供CLI服务通过OpenFeign调用

**代码示例**：
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private TokenUtil tokenUtil;
    
    @PostMapping("/validate")
    public ValidateTokenResult validateToken(@RequestHeader("Authorization") String token) {
        ValidateTokenResult result = new ValidateTokenResult();
        try {
            if (tokenUtil.verifyToken(token)) {
                Claims claims = tokenUtil.parseToken(token);
                result.setValid(true);
                result.setUserId(claims.get("userId", String.class));
                result.setRole(claims.get("role", String.class));
                result.setMessage("Token valid");
            } else {
                result.setValid(false);
                result.setMessage("Token invalid");
            }
        } catch (Exception e) {
            result.setValid(false);
            result.setMessage("Token parse error: " + e.getMessage());
        }
        return result;
    }
}
```

---

## 第三部分：CLI服务实现

### 阶段目标

新建基于Spring Boot的CLI服务，依赖workorder-api模块，使用OpenFeign RPC调用后端，提供以下能力：
- 鉴权：通过OpenFeign调用后端鉴权验证接口
- TraceID：生成并传递TraceID
- 统一查询：通过dataCode代理查询后端数据

### 任务列表

#### 3.1 项目初始化

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C1 | 创建Maven项目 | `cli-service/pom.xml` | 创建Spring Boot Maven项目，依赖workorder-api + OpenFeign | A10 |
| C2 | 创建启动类 | `cli-service/src/main/java/com/example/workorder/cli/CliApplication.java` | Spring Boot启动类（@EnableFeignClients） | C1 |
| C3 | 创建配置文件 | `cli-service/src/main/resources/application.yaml` | 配置服务端口、后端地址、Feign配置等 | C1 |

#### 3.2 配置类

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C4 | 创建FeignConfig | `cli-service/src/main/java/com/example/workorder/cli/config/FeignConfig.java` | OpenFeign配置 | C2 |
| C5 | 创建Web配置 | `cli-service/src/main/java/com/example/workorder/cli/config/WebConfig.java` | 配置跨域、拦截器注册等 | C2 |

#### 3.3 OpenFeign客户端

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C6 | 创建WorkOrderFeignClient | `cli-service/src/main/java/com/example/workorder/cli/feign/WorkOrderFeignClient.java` | OpenFeign客户端，调用工单接口 | C2, A2 |
| C7 | 创建DashboardFeignClient | `cli-service/src/main/java/com/example/workorder/cli/feign/DashboardFeignClient.java` | OpenFeign客户端，调用看板接口 | C2, A3 |
| C8 | 创建FlowFeignClient | `cli-service/src/main/java/com/example/workorder/cli/feign/FlowFeignClient.java` | OpenFeign客户端，调用流程接口 | C2, A4 |
| C9 | 创建AuthFeignClient | `cli-service/src/main/java/com/example/workorder/cli/feign/AuthFeignClient.java` | OpenFeign客户端，调用鉴权接口 | C2, A5 |

#### 3.4 拦截器

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C10 | 创建AuthInterceptor | `cli-service/src/main/java/com/example/workorder/cli/interceptor/AuthInterceptor.java` | 验证Authorization头中的Token，调用AuthService | C5 |
| C11 | 创建TraceIdInterceptor | `cli-service/src/main/java/com/example/workorder/cli/interceptor/TraceIdInterceptor.java` | 生成或传递TraceID | C5 |

#### 3.5 DTO定义

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C12 | 创建ApiResponse | `cli-service/src/main/java/com/example/workorder/cli/dto/response/ApiResponse.java` | 统一响应封装 | C2 |
| C13 | 创建QueryRequest | `cli-service/src/main/java/com/example/workorder/cli/dto/request/QueryRequest.java` | 查询请求DTO | C2 |
| C14 | 创建DataCodeDTO | `cli-service/src/main/java/com/example/workorder/cli/dto/response/DataCodeDTO.java` | dataCode信息DTO | C2 |
| C15 | 创建SchemaDTO | `cli-service/src/main/java/com/example/workorder/cli/dto/response/SchemaDTO.java` | Schema信息DTO | C2 |

#### 3.6 枚举与配置

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C16 | 创建DataCodeEnum | `cli-service/src/main/java/com/example/workorder/cli/enums/DataCodeEnum.java` | dataCode枚举定义，包含8个查询类型 | C2 |
| C17 | 创建Schema配置文件 | `cli-service/src/main/resources/data-schemas.json` | dataCode的Schema定义JSON文件（8个） | C1 |

#### 3.7 服务层

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C18 | 创建AuthService | `cli-service/src/main/java/com/example/workorder/cli/service/AuthService.java` | Token验证服务（调用AuthFeignClient） | C9 |
| C19 | 创建TraceService | `cli-service/src/main/java/com/example/workorder/cli/service/TraceService.java` | TraceID生成与管理服务 | C11 |
| C20 | 创建SchemaService | `cli-service/src/main/java/com/example/workorder/cli/service/SchemaService.java` | Schema管理服务，加载和查询Schema定义 | C15, C17 |
| C21 | 创建QueryService | `cli-service/src/main/java/com/example/workorder/cli/service/QueryService.java` | 查询代理服务，根据dataCode调用Feign客户端并归一化响应 | C6, C7, C8, C16 |

#### 3.8 控制器

| 序号 | 任务名称 | 文件路径 | 描述 | 依赖 |
|------|----------|----------|------|------|
| C22 | 创建ApiController | `cli-service/src/main/java/com/example/workorder/cli/controller/ApiController.java` | 统一API控制器，提供三个接口 | C12, C14, C15, C20, C21 |

---

## 详细设计

### C1. 创建Maven项目

**pom.xml关键依赖**：
```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.3.0</version>
    </dependency>
    
    <!-- Spring Cloud OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
        <version>4.1.0</version>
    </dependency>
    
    <!-- Apache HttpClient for Feign -->
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
        <version>5.2.1</version>
    </dependency>
    
    <!-- WorkOrder API (共享模块) -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>workorder-api</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### C3. 创建配置文件

**application.yaml**：
```yaml
server:
  port: 5000

workorder:
  backend:
    url: http://localhost:8080

logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId}] - %msg%n"

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: BASIC
  httpclient:
    enabled: true
```

### C4. 创建FeignConfig

**代码示例**：
```java
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}
```

### C6-C9. 创建OpenFeign客户端

以 `WorkOrderFeignClient` 为例：
```java
@FeignClient(name = "workorder-backend", url = "${workorder.backend.url}")
public interface WorkOrderFeignClient {

    @PostMapping("/workOrder/page")
    ResponseEntity<IPage<WorkOrderPageVO>> pageWorkOrder(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId,
            @RequestBody WorkOrderPageParam param);

    @PostMapping("/workOrder/detail")
    ResponseEntity<WorkOrderDetailVO> detail(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId,
            @RequestBody WorkOrderDetailParam param);

    @GetMapping("/workOrder/search")
    ResponseEntity<SearchResult<WorkOrder>> searchWorkOrders(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize);
}
```

**AuthFeignClient**：
```java
@FeignClient(name = "workorder-backend", url = "${workorder.backend.url}")
public interface AuthFeignClient {

    @PostMapping("/auth/validate")
    ResponseEntity<ValidateTokenResult> validateToken(
            @RequestHeader("Authorization") String token);
}
```

### C18. 创建AuthService

**功能**：通过AuthFeignClient调用后端鉴权接口

**代码示例**：
```java
@Service
public class AuthService {
    
    @Autowired
    private AuthFeignClient authFeignClient;
    
    public ValidateTokenResult validateToken(String token) {
        ResponseEntity<ValidateTokenResult> response = authFeignClient.validateToken(token);
        return response.getBody();
    }
}
```

### C21. 创建QueryService

**核心逻辑**：
1. 根据dataCode查找对应的Feign客户端方法
2. 调用Feign客户端（已自动处理HTTP请求和响应）
3. 将结果包装为统一格式响应

---

## 实施顺序

```
阶段一：workorder-api模块
├── A1 创建Maven模块及pom.xml
├── A2 创建WorkOrderService接口
├── A3 创建DashboardService接口
├── A4 创建FlowService接口
├── A5 创建AuthService接口
├── A6 创建ValidateTokenResult
├── A7 提取Param类
├── A8 提取VO类
├── A9 提取枚举类
└── A10 安装到本地Maven仓库

阶段二：后端改造
├── B1 修改pom.xml依赖workorder-api
├── B2 重构Service实现类
├── B3 创建TraceIdInterceptor
├── B4 注册TraceID拦截器到WebConfig
├── B5 更新日志格式配置
└── B6 创建AuthController（新增鉴权验证接口）

阶段三：CLI服务项目初始化
├── C1 创建Maven项目及pom.xml（依赖workorder-api + OpenFeign）
├── C2 创建启动类（@EnableFeignClients）
└── C3 创建配置文件（包含Feign配置）

阶段四：配置类与Feign客户端
├── C4 创建FeignConfig
├── C5 创建Web配置
├── C6 创建WorkOrderFeignClient
├── C7 创建DashboardFeignClient
├── C8 创建FlowFeignClient
└── C9 创建AuthFeignClient

阶段五：拦截器与数据结构
├── C10 创建AuthInterceptor
├── C11 创建TraceIdInterceptor
├── C12 创建ApiResponse
├── C13 创建QueryRequest
├── C14 创建DataCodeDTO
├── C15 创建SchemaDTO
├── C16 创建DataCodeEnum（8个）
└── C17 创建data-schemas.json（8个）

阶段六：服务层实现
├── C18 创建AuthService（调用AuthFeignClient）
├── C19 创建TraceService
├── C20 创建SchemaService
└── C21 创建QueryService（调用Feign客户端）

阶段七：控制器与测试
└── C22 创建ApiController
```

---

## 关键依赖关系图

```
ApiController
    ├── AuthService (调用AuthFeignClient → 后端/auth/validate)
    ├── SchemaService (查询Schema)
    └── QueryService (调用Feign客户端)
            ├── WorkOrderFeignClient (RPC调用工单接口)
            ├── DashboardFeignClient (RPC调用看板接口)
            ├── FlowFeignClient (RPC调用流程接口)
            └── DataCodeEnum (dataCode映射)

AuthInterceptor
    └── AuthService (RPC鉴权)

TraceIdInterceptor
    └── TraceService

WebConfig
    ├── AuthInterceptor
    └── TraceIdInterceptor

FeignConfig
    └── RequestInterceptor (设置Content-Type)

WorkOrderFeignClient
    └── workorder-api.WorkOrderPageVO, WorkOrderPageParam

AuthFeignClient
    └── workorder-api.ValidateTokenResult
```

---

## 测试验证计划

### workorder-api模块验证

1. **编译验证**：`mvn compile` 成功
2. **安装验证**：`mvn install` 成功，本地仓库包含jar包

### 后端改造验证

1. **编译验证**：`mvn compile` 成功
2. **TraceID传递验证**：
   - 请求携带X-Trace-ID头，验证响应头返回相同的TraceID
   - 查看日志是否包含TraceID

3. **鉴权验证接口**：
   ```bash
   curl -X POST http://localhost:8080/auth/validate \
     -H "Authorization: <token>"
   ```
   预期：返回 `{"valid": true, "userId": "...", "role": "...", "message": "Token valid"}`

4. **日志格式验证**：
   - 确认日志输出包含 `[traceId]` 字段

### CLI服务验证

1. **编译验证**：`mvn compile` 成功
2. **dataCodes接口**：
   ```bash
   curl http://localhost:5000/api/dataCodes
   ```
   预期：返回8个dataCode列表

3. **schema接口**：
   ```bash
   curl http://localhost:5000/api/schema/work_order_page
   ```
   预期：返回work_order_page的入参出参Schema

4. **query接口**（需要有效Token）：
   ```bash
   curl -X POST http://localhost:5000/api/query \
     -H "Authorization: <token>" \
     -H "X-Trace-ID: test-trace-id" \
     -H "Content-Type: application/json" \
     -d '{"dataCode": "work_order_page", "params": {"pageNum": 1, "pageSize": 10}}'
   ```
   预期：返回工单分页数据，响应中包含traceId字段

5. **鉴权验证**：
   - 无Token请求 → 返回401错误
   - 无效Token请求 → 返回401错误（通过RPC鉴权验证）

6. **RPC调用验证**：
   - CLI服务通过OpenFeign调用后端接口，验证返回数据正确

---

## 风险与注意事项

1. **API模块兼容性**：workorder-api模块的接口变更会影响所有依赖模块，需谨慎管理版本
2. **Token验证**：CLI服务通过RPC调用后端鉴权接口，确保验证逻辑一致
3. **响应归一化**：需处理后端不同返回格式（Result/IPage/VO/List）
4. **接口兼容性**：后端接口路径和参数可能变化，需保持Feign客户端同步
5. **性能考虑**：OpenFeign作为HTTP客户端，需考虑请求超时和重试机制
6. **日志追踪**：确保TraceID在CLI服务和后端服务之间正确传递
7. **Maven依赖**：workorder-api需先安装到本地仓库，否则backend和cli-service无法编译
8. **OpenFeign版本**：确保Spring Boot版本与OpenFeign版本兼容（Spring Boot 3.3+ → OpenFeign 4.1.0）
