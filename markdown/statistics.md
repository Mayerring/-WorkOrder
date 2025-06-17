## 待办事项


**接口地址**:`/dashboard/todo`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema          |
| ------ | ---- | --------------- |
| 200    | OK   | WorkOrderTodoVO |


**响应参数**:


| 参数名称   | 参数说明 | 类型           | schema         |
| ---------- | -------- | -------------- | -------------- |
| title      | 标题     | string         |                |
| type       | 类型     | integer(int32) | integer(int32) |
| typeDesc   | 类型desc | string         |                |
| status     | 状态     | integer(int32) | integer(int32) |
| statusDesc | 状态desc | string         |                |
| createTime | 创建时间 | string         |                |


**响应示例**:
```javascript
[
	{
		"title": "",
		"type": 0,
		"typeDesc": "",
		"status": 0,
		"statusDesc": "",
		"createTime": ""
	}
]
```

## 工单状态统计


**接口地址**:`/dashboard/status`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "timeType": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称             | 参数说明                  | 请求类型 | 是否必须 | 数据类型        | schema          |
| -------------------- | ------------------------- | -------- | -------- | --------------- | --------------- |
| statusDataParam      | StatusDataParam           | body     | true     | StatusDataParam | StatusDataParam |
| &emsp;&emsp;timeType | 时间 (1:周，2：月，3：年) |          | true     | integer(int32)  |                 |


**响应状态**:


| 状态码 | 说明 | schema       |
| ------ | ---- | ------------ |
| 200    | OK   | StatusDataVO |


**响应参数**:


| 参数名称   | 参数说明 | 类型           | schema         |
| ---------- | -------- | -------------- | -------------- |
| status     | 状态     | integer(int32) | integer(int32) |
| statusDesc | 状态desc | string         |                |
| quantity   | 数量     | integer(int64) | integer(int64) |


**响应示例**:
```javascript
[
	{
		"status": 0,
		"statusDesc": "",
		"quantity": 0
	}
]
```

## 本周处理数量


**接口地址**:`/dashboard/handleQuantity`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "date": ""
}
```


**请求参数**:


**请求参数**:


| 参数名称                | 参数说明                 | 请求类型 | 是否必须 | 数据类型                | schema                  |
| ----------------------- | ------------------------ | -------- | -------- | ----------------------- | ----------------------- |
| weekHandleQuantityParam | WeekHandleQuantityParam  | body     | true     | WeekHandleQuantityParam | WeekHandleQuantityParam |
| &emsp;&emsp;date        | 日期，采用yyyy-MM-dd格式 |          | true     | string                  |                         |


**响应状态**:


| 状态码 | 说明 | schema       |
| ------ | ---- | ------------ |
| 200    | OK   | WeekHandleVO |


**响应参数**:


| 参数名称         | 参数说明           | 类型           | schema         |
| ---------------- | ------------------ | -------------- | -------------- |
| dailyTotalNum    | 当天工单总数       | integer(int64) | integer(int64) |
| dailyFinishedNum | 当天已完成工单总数 | integer(int64) | integer(int64) |
| date             | 日期               | string         |                |


**响应示例**:
```javascript
{
	"dailyTotalNum": 0,
	"dailyFinishedNum": 0,
	"date": ""
}
```