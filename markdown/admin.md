## 新增员工


**接口地址**:`/admin/staff/add`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "companyName": "",
  "departmentName": "",
  "position": "",
  "managerName": "",
  "phone": "",
  "email": ""
}
```


**请求参数**:


**请求参数**:


| 参数名称                   | 参数说明      | 请求类型 | 是否必须 | 数据类型      | schema        |
| -------------------------- | ------------- | -------- | -------- | ------------- | ------------- |
| addStaffParam              | AddStaffParam | body     | true     | AddStaffParam | AddStaffParam |
| &emsp;&emsp;name           |               |          | false    | string        |               |
| &emsp;&emsp;companyName    |               |          | false    | string        |               |
| &emsp;&emsp;departmentName |               |          | false    | string        |               |
| &emsp;&emsp;position       |               |          | false    | string        |               |
| &emsp;&emsp;managerName    |               |          | false    | string        |               |
| &emsp;&emsp;phone          |               |          | false    | string        |               |
| &emsp;&emsp;email          |               |          | false    | string        |               |


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

## 删除员工


**接口地址**:`/admin/staff/delete`


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

## 修改员工信息


**接口地址**:`/admin/staff/change`


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

## 分页查看员工信息


**接口地址**:`/admin/staff/page`


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

## 所有员工


**接口地址**:`/admin/staff/all`


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

## 新增部门


**接口地址**:`/admin/department/add`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "parentDepartmentName": "",
  "companyName": "",
  "leaderName": ""
}
```


**请求参数**:


**请求参数**:


| 参数名称                         | 参数说明           | 请求类型 | 是否必须 | 数据类型           | schema             |
| -------------------------------- | ------------------ | -------- | -------- | ------------------ | ------------------ |
| addDepartmentParam               | AddDepartmentParam | body     | true     | AddDepartmentParam | AddDepartmentParam |
| &emsp;&emsp;name                 |                    |          | false    | string             |                    |
| &emsp;&emsp;parentDepartmentName |                    |          | false    | string             |                    |
| &emsp;&emsp;companyName          |                    |          | false    | string             |                    |
| &emsp;&emsp;leaderName           |                    |          | false    | string             |                    |


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

## 查看某个公司的所有部门


**接口地址**:`/admin/department/all`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


**请求参数**:


| 参数名称    | 参数说明 | 请求类型 | 是否必须 | 数据类型 | schema |
| ----------- | -------- | -------- | -------- | -------- | ------ |
| companyName |          | query    | true     | string   |        |


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

## 新增公司


**接口地址**:`/admin/company/add`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "parentCompanyName": "",
  "level": 0
}
```


**请求参数**:


**请求参数**:


| 参数名称                      | 参数说明        | 请求类型 | 是否必须 | 数据类型        | schema          |
| ----------------------------- | --------------- | -------- | -------- | --------------- | --------------- |
| addCompanyParam               | AddCompanyParam | body     | true     | AddCompanyParam | AddCompanyParam |
| &emsp;&emsp;name              |                 |          | false    | string          |                 |
| &emsp;&emsp;parentCompanyName |                 |          | false    | string          |                 |
| &emsp;&emsp;level             |                 |          | false    | integer(int32)  |                 |


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

## 所有公司


**接口地址**:`/admin/company/all`


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