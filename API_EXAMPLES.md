# API 调用示例文档

本文档提供 EnterpriseX Framework 各个接口的调用示例。

## 📋 目录

- [认证接口](#认证接口)
- [用户管理](#用户管理)
- [角色管理](#角色管理)
- [在线用户管理](#在线用户管理)
- [缓存管理](#缓存管理)
- [文件管理](#文件管理)

---

## 认证接口

### 1. 用户登录

**接口地址：** `POST /auth/login`

**请求示例：**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "avatar": null
    }
  }
}
```

### 2. 获取用户信息

**接口地址：** `GET /auth/info`

**请求示例：**
```bash
curl -X GET http://localhost:8080/auth/info \
  -H "Authorization: Bearer {accessToken}"
```

### 3. 刷新Token

**接口地址：** `POST /auth/refresh`

**请求示例：**
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

### 4. 用户登出

**接口地址：** `POST /auth/logout`

**请求示例：**
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer {accessToken}"
```

---

## 用户管理

### 1. 查询用户列表

**接口地址：** `GET /system/user/list`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/system/user/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer {accessToken}"
```

**查询参数：**
- `username` - 用户名（模糊查询）
- `nickName` - 昵称（模糊查询）
- `phoneNumber` - 手机号
- `status` - 状态（1=正常，0=停用）
- `deptId` - 部门ID
- `pageNum` - 页码
- `pageSize` - 每页数量

### 2. 新增用户

**接口地址：** `POST /system/user`

**请求示例：**
```bash
curl -X POST http://localhost:8080/system/user \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "nickName": "张三",
    "password": "123456",
    "email": "zhangsan@example.com",
    "phoneNumber": "13800138000",
    "sex": 1,
    "status": 1,
    "deptId": 100,
    "postIds": [1],
    "roleIds": [2]
  }'
```

### 3. 修改用户

**接口地址：** `PUT /system/user`

**请求示例：**
```bash
curl -X PUT http://localhost:8080/system/user \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "nickName": "张三三",
    "email": "zhangsan@example.com",
    "phoneNumber": "13800138000",
    "status": 1
  }'
```

### 4. 删除用户

**接口地址：** `DELETE /system/user/{userIds}`

**请求示例：**
```bash
# 删除单个用户
curl -X DELETE http://localhost:8080/system/user/2 \
  -H "Authorization: Bearer {accessToken}"

# 批量删除用户
curl -X DELETE http://localhost:8080/system/user/2,3,4 \
  -H "Authorization: Bearer {accessToken}"
```

### 5. 导出用户数据

**接口地址：** `GET /system/user/export`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/system/user/export" \
  -H "Authorization: Bearer {accessToken}" \
  -o users.xlsx
```

---

## 角色管理

### 1. 查询角色列表

**接口地址：** `GET /system/role/list`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/system/role/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer {accessToken}"
```

### 2. 新增角色

**接口地址：** `POST /system/role`

**请求示例：**
```bash
curl -X POST http://localhost:8080/system/role \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "测试角色",
    "roleKey": "test",
    "roleSort": 3,
    "dataScope": 1,
    "status": 1,
    "remark": "测试角色",
    "menuIds": [1, 100, 101, 102]
  }'
```

---

## 在线用户管理

### 1. 查询在线用户列表

**接口地址：** `GET /monitor/online/list`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/monitor/online/list?username=admin" \
  -H "Authorization: Bearer {accessToken}"
```

**查询参数：**
- `username` - 用户名（模糊查询）
- `ipaddr` - IP地址（模糊查询）

### 2. 强退用户

**接口地址：** `DELETE /monitor/online/{tokenId}`

**请求示例：**
```bash
curl -X DELETE http://localhost:8080/monitor/online/{tokenId} \
  -H "Authorization: Bearer {accessToken}"
```

### 3. 批量强退用户

**接口地址：** `DELETE /monitor/online/batch/{tokenIds}`

**请求示例：**
```bash
curl -X DELETE "http://localhost:8080/monitor/online/batch/token1,token2,token3" \
  -H "Authorization: Bearer {accessToken}"
```

---

## 缓存管理

### 1. 获取缓存监控信息

**接口地址：** `GET /monitor/cache/info`

**请求示例：**
```bash
curl -X GET http://localhost:8080/monitor/cache/info \
  -H "Authorization: Bearer {accessToken}"
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dbSize": 156,
    "cacheStats": {
      "login_tokens:": 10,
      "online_tokens:": 8,
      "sys_config:": 25,
      "sys_dict:": 50
    }
  }
}
```

### 2. 获取缓存名称列表

**接口地址：** `GET /monitor/cache/names`

**请求示例：**
```bash
curl -X GET http://localhost:8080/monitor/cache/names \
  -H "Authorization: Bearer {accessToken}"
```

### 3. 获取缓存键名列表

**接口地址：** `GET /monitor/cache/keys`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/monitor/cache/keys?cacheName=login_tokens:" \
  -H "Authorization: Bearer {accessToken}"
```

### 4. 清除指定缓存

**接口地址：** `DELETE /monitor/cache/clear`

**请求示例：**
```bash
curl -X DELETE "http://localhost:8080/monitor/cache/clear?cacheKey=login_tokens:admin" \
  -H "Authorization: Bearer {accessToken}"
```

### 5. 清除指定前缀的缓存

**接口地址：** `DELETE /monitor/cache/clearPrefix`

**请求示例：**
```bash
curl -X DELETE "http://localhost:8080/monitor/cache/clearPrefix?cachePrefix=login_tokens:" \
  -H "Authorization: Bearer {accessToken}"
```

### 6. 清空所有缓存

**接口地址：** `DELETE /monitor/cache/clearAll`

**请求示例：**
```bash
curl -X DELETE http://localhost:8080/monitor/cache/clearAll \
  -H "Authorization: Bearer {accessToken}"
```

---

## 文件管理

### 1. 文件上传

**接口地址：** `POST /system/file/upload`

**请求示例：**
```bash
curl -X POST http://localhost:8080/system/file/upload \
  -H "Authorization: Bearer {accessToken}" \
  -F "file=@/path/to/your/file.jpg"
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "fileName": "file.jpg",
    "filePath": "2024/01/15/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "fileSize": "102400",
    "fileType": "image/jpeg"
  }
}
```

### 2. 文件下载

**接口地址：** `GET /system/file/download`

**请求示例：**
```bash
curl -X GET "http://localhost:8080/system/file/download?filePath=2024/01/15/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" \
  -H "Authorization: Bearer {accessToken}" \
  -o downloaded_file.jpg
```

### 3. 文件删除

**接口地址：** `DELETE /system/file/delete`

**请求示例：**
```bash
curl -X DELETE "http://localhost:8080/system/file/delete?filePath=2024/01/15/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" \
  -H "Authorization: Bearer {accessToken}"
```

---

## 📝 注意事项

1. **认证方式**
   - 除登录接口外，所有接口都需要在请求头中携带 `Authorization: Bearer {accessToken}`
   - Token有效期为2小时，过期后需要使用 refreshToken 刷新或重新登录

2. **分页参数**
   - 分页接口通常支持 `pageNum`（页码，从1开始）和 `pageSize`（每页数量）参数
   - 默认 `pageNum=1, pageSize=10`

3. **批量操作**
   - 批量删除接口通过逗号分隔ID：`/system/user/1,2,3`

4. **文件上传限制**
   - 默认最大文件大小：10MB
   - 支持的文件类型：可在配置文件中自定义

5. **错误码说明**
   - `200` - 操作成功
   - `401` - 未认证或Token过期
   - `403` - 无权限
   - `404` - 资源不存在
   - `500` - 服务器内部错误

---

## 🔗 相关文档

- [README.md](README.md) - 项目说明
- [FEATURES.md](FEATURES.md) - 功能清单
- [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - 开发指南
- Swagger文档: http://localhost:9201/doc.html
