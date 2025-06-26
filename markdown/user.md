## 获取个人信息


**接口地址**:`/user/me`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```

## 登录


**接口地址**:`/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "staffNumber": "",
  "phone": "",
  "password": ""
}
```


**请求参数**:


**请求参数**:


| 参数名称                | 参数说明   | 请求类型 | 是否必须 | 数据类型   | schema     |
| ----------------------- | ---------- | -------- | -------- | ---------- | ---------- |
| loginParam              | LoginParam | body     | true     | LoginParam | LoginParam |
| &emsp;&emsp;staffNumber | 工号       |          | false    | string     |            |
| &emsp;&emsp;phone       | 手机号码   |          | false    | string     |            |
| &emsp;&emsp;password    | 密码       |          | true     | string     |            |


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```

## 根据查询员工的信息


**接口地址**:`/user/belong`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| staffId  |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```

## 修改个人信息


**接口地址**:`/user/change`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```

## 修改个人信息


**接口地址**:`/user/change`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```

## 分页查询所有员工


**接口地址**:`/user/page`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "pageNum": 0,
  "pageSize": 0,
  "staffNumber": "",
  "name": "",
  "password": "",
  "companyCode": "",
  "company": "",
  "departmentCode": "",
  "department": "",
  "position": "",
  "status": 0,
  "managerNumber": "",
  "managerName": "",
  "phone": "",
  "email": "",
  "createTimeFrom": 0,
  "createTimeTo": 0,
  "id": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称                   | 参数说明                                | 请求类型 | 是否必须 | 数据类型       | schema         |
| -------------------------- | --------------------------------------- | -------- | -------- | -------------- | -------------- |
| staffPageParam             | StaffPageParam                          | body     | true     | StaffPageParam | StaffPageParam |
| &emsp;&emsp;pageNum        | 当前页数                                |          | true     | integer(int32) |                |
| &emsp;&emsp;pageSize       | 每页大小                                |          | true     | integer(int32) |                |
| &emsp;&emsp;staffNumber    | 员工工号                                |          | false    | string         |                |
| &emsp;&emsp;name           | 员工姓名                                |          | false    | string         |                |
| &emsp;&emsp;password       | 密码                                    |          | false    | string         |                |
| &emsp;&emsp;companyCode    | 所属公司代码                            |          | false    | string         |                |
| &emsp;&emsp;company        | 公司                                    |          | false    | string         |                |
| &emsp;&emsp;departmentCode | 部门编码                                |          | false    | string         |                |
| &emsp;&emsp;department     | 部门                                    |          | false    | string         |                |
| &emsp;&emsp;position       | 职位                                    |          | false    | string         |                |
| &emsp;&emsp;status         | 状态（0：正常，1：休假，2：停职 3：离职 |          | false    | integer(int32) |                |
| &emsp;&emsp;managerNumber  | 直属领导编码                            |          | false    | string         |                |
| &emsp;&emsp;managerName    | 直属领导                                |          | false    | string         |                |
| &emsp;&emsp;phone          | 电话号码                                |          | false    | string         |                |
| &emsp;&emsp;email          | 邮箱                                    |          | false    | string         |                |
| &emsp;&emsp;createTimeFrom | 创建时间起                              |          | false    | integer(int64) |                |
| &emsp;&emsp;createTimeTo   | 创建时间止                              |          | false    | integer(int64) |                |
| &emsp;&emsp;id             |                                         |          | false    | integer(int64) |                |


**响应状态**:


| 状态码 | 说明 | schema |
| ------ | ---- | ------ |
| 200    | OK   | Result |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     | 响应码   | integer(int32) | integer(int32) |
| msg      |          | string         |                |
| data     |          | object         |                |


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"data": {}
}
```