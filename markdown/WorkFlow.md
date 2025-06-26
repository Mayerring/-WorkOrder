## 新增流程


**接口地址**:`/flow/create`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "flowName": "",
  "nodes": [
    {
      "handlerId": 0,
      "handlerName": ""
    }
  ],
  "distributeNode": {
    "handlerId": 0,
    "handlerName": ""
  },
  "checkNode": {
    "handlerId": 0,
    "handlerName": ""
  }
}
```


**请求参数**:


**请求参数**:


| 参数名称                            | 参数说明        | 请求类型 | 是否必须 | 数据类型        | schema          |
| ----------------------------------- | --------------- | -------- | -------- | --------------- | --------------- |
| flowCreateParam                     | FlowCreateParam | body     | true     | FlowCreateParam | FlowCreateParam |
| &emsp;&emsp;flowName                | 流程名          |          | true     | string          |                 |
| &emsp;&emsp;nodes                   | 审核节点        |          | true     | array           | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |
| &emsp;&emsp;distributeNode          |                 |          | true     | FlowNode        | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |
| &emsp;&emsp;checkNode               |                 |          | true     | FlowNode        | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |


**响应状态**:


| 状态码 | 说明 | schema       |
| ------ | ---- | ------------ |
| 200    | OK   | FlowCreateVO |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| flowId   | 流程id   | integer(int64) | integer(int64) |
| success  | 成功     | boolean        |                |


**响应示例**:
```javascript
{
	"flowId": 0,
	"success": true
}
```

## 编辑流程


**接口地址**:`/flow/edit`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "flowName": "",
  "nodes": [
    {
      "handlerId": 0,
      "handlerName": ""
    }
  ],
  "distributeNode": {
    "handlerId": 0,
    "handlerName": ""
  },
  "checkNode": {
    "handlerId": 0,
    "handlerName": ""
  },
  "flowId": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称                            | 参数说明        | 请求类型 | 是否必须 | 数据类型        | schema          |
| ----------------------------------- | --------------- | -------- | -------- | --------------- | --------------- |
| flowUpdateParam                     | FlowUpdateParam | body     | true     | FlowUpdateParam | FlowUpdateParam |
| &emsp;&emsp;flowName                | 流程名          |          | true     | string          |                 |
| &emsp;&emsp;nodes                   | 审核节点        |          | true     | array           | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |
| &emsp;&emsp;distributeNode          |                 |          | true     | FlowNode        | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |
| &emsp;&emsp;checkNode               |                 |          | true     | FlowNode        | FlowNode        |
| &emsp;&emsp;&emsp;&emsp;handlerId   | 处理人id        |          | true     | integer(int64)  |                 |
| &emsp;&emsp;&emsp;&emsp;handlerName | 处理人名        |          | true     | string          |                 |
| &emsp;&emsp;flowId                  | 流程Id          |          | false    | integer(int64)  |                 |


**响应状态**:


| 状态码 | 说明 | schema       |
| ------ | ---- | ------------ |
| 200    | OK   | FlowCreateVO |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| flowId   | 流程id   | integer(int64) | integer(int64) |
| success  | 成功     | boolean        |                |


**响应示例**:
```javascript
{
	"flowId": 0,
	"success": true
}
```

## 删除流程


**接口地址**:`/flow/delete`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "flowId": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称           | 参数说明    | 请求类型 | 是否必须 | 数据类型       | schema      |
| ------------------ | ----------- | -------- | -------- | -------------- | ----------- |
| flowIdParam        | FlowIdParam | body     | true     | FlowIdParam    | FlowIdParam |
| &emsp;&emsp;flowId | 流程id      |          | true     | integer(int64) |             |


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   |        |


**响应参数**:


暂无


**响应示例**:
```text
boolean
```

## 分页查询


**接口地址**:`/flow/page`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "pageNum": 0,
  "pageSize": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称             | 参数说明      | 请求类型 | 是否必须 | 数据类型       | schema        |
| -------------------- | ------------- | -------- | -------- | -------------- | ------------- |
| flowPageParam        | FlowPageParam | body     | true     | FlowPageParam  | FlowPageParam |
| &emsp;&emsp;pageNum  | 当前页数      |          | true     | integer(int32) |               |
| &emsp;&emsp;pageSize | 每页大小      |          | true     | integer(int32) |               |


**响应状态**:


| 状态码 | 说明 | schema     |
| ------ | ---- | ---------- |
| 200    | OK   | PageFlowVO |


**响应参数**:


| 参数名称                                         | 参数说明                     | 类型           | schema         |
| ------------------------------------------------ | ---------------------------- | -------------- | -------------- |
| records                                          |                              | array          | FlowVO         |
| &emsp;&emsp;flowId                               | 流程id                       | integer(int64) |                |
| &emsp;&emsp;flowName                             | 流程名                       | string         |                |
| &emsp;&emsp;nodes                                | 节点                         | array          | FlowNodeVO     |
| &emsp;&emsp;&emsp;&emsp;id                       | 节点id                       | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;nodeType                 | 节点类型:审批2，指派3，验收5 | integer(int32) |                |
| &emsp;&emsp;&emsp;&emsp;nodeTypeDesc             | 节点类型desc                 | string         |                |
| &emsp;&emsp;&emsp;&emsp;handlerId                | 处理人id                     | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;handlerName              | 处理人名                     | string         |                |
| &emsp;&emsp;&emsp;&emsp;isLastNode               | 是否为该流程终止节点         | boolean        |                |
| total                                            |                              | integer(int64) | integer(int64) |
| size                                             |                              | integer(int64) | integer(int64) |
| current                                          |                              | integer(int64) | integer(int64) |
| orders                                           |                              | array          | OrderItem      |
| &emsp;&emsp;column                               |                              | string         |                |
| &emsp;&emsp;asc                                  |                              | boolean        |                |
| optimizeCountSql                                 |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;records                              |                              | array          | FlowVO         |
| &emsp;&emsp;&emsp;&emsp;flowId                   | 流程id                       | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;flowName                 | 流程名                       | string         |                |
| &emsp;&emsp;&emsp;&emsp;nodes                    | 节点                         | array          | FlowNodeVO     |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id           | 节点id                       | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nodeType     | 节点类型:审批2，指派3，验收5 | integer(int32) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nodeTypeDesc | 节点类型desc                 | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;handlerId    | 处理人id                     | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;handlerName  | 处理人名                     | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;isLastNode   | 是否为该流程终止节点         | boolean        |                |
| &emsp;&emsp;total                                |                              | integer(int64) |                |
| &emsp;&emsp;size                                 |                              | integer(int64) |                |
| &emsp;&emsp;current                              |                              | integer(int64) |                |
| &emsp;&emsp;orders                               |                              | array          | OrderItem      |
| &emsp;&emsp;&emsp;&emsp;column                   |                              | string         |                |
| &emsp;&emsp;&emsp;&emsp;asc                      |                              | boolean        |                |
| &emsp;&emsp;optimizeCountSql                     |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;searchCount                          |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;optimizeJoinOfCountSql               |                              | boolean        |                |
| &emsp;&emsp;maxLimit                             |                              | integer(int64) |                |
| &emsp;&emsp;countId                              |                              | string         |                |
| &emsp;&emsp;pages                                |                              | integer(int64) |                |
| searchCount                                      |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;records                              |                              | array          | FlowVO         |
| &emsp;&emsp;&emsp;&emsp;flowId                   | 流程id                       | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;flowName                 | 流程名                       | string         |                |
| &emsp;&emsp;&emsp;&emsp;nodes                    | 节点                         | array          | FlowNodeVO     |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id           | 节点id                       | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nodeType     | 节点类型:审批2，指派3，验收5 | integer(int32) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nodeTypeDesc | 节点类型desc                 | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;handlerId    | 处理人id                     | integer(int64) |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;handlerName  | 处理人名                     | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;isLastNode   | 是否为该流程终止节点         | boolean        |                |
| &emsp;&emsp;total                                |                              | integer(int64) |                |
| &emsp;&emsp;size                                 |                              | integer(int64) |                |
| &emsp;&emsp;current                              |                              | integer(int64) |                |
| &emsp;&emsp;orders                               |                              | array          | OrderItem      |
| &emsp;&emsp;&emsp;&emsp;column                   |                              | string         |                |
| &emsp;&emsp;&emsp;&emsp;asc                      |                              | boolean        |                |
| &emsp;&emsp;optimizeCountSql                     |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;searchCount                          |                              | PageFlowVO     | PageFlowVO     |
| &emsp;&emsp;optimizeJoinOfCountSql               |                              | boolean        |                |
| &emsp;&emsp;maxLimit                             |                              | integer(int64) |                |
| &emsp;&emsp;countId                              |                              | string         |                |
| &emsp;&emsp;pages                                |                              | integer(int64) |                |
| optimizeJoinOfCountSql                           |                              | boolean        |                |
| maxLimit                                         |                              | integer(int64) | integer(int64) |
| countId                                          |                              | string         |                |
| pages                                            |                              | integer(int64) | integer(int64) |


**响应示例**:
```javascript
{
	"records": [
		{
			"flowId": 0,
			"flowName": "",
			"nodes": [
				{
					"id": 0,
					"nodeType": 0,
					"nodeTypeDesc": "",
					"handlerId": 0,
					"handlerName": "",
					"isLastNode": true
				}
			]
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"column": "",
			"asc": true
		}
	],
	"optimizeCountSql": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"searchCount": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"optimizeJoinOfCountSql": true,
	"maxLimit": 0,
	"countId": "",
	"pages": 0
}
```