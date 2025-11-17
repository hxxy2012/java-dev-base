# 配置文件说明文档

本文档详细说明 EnterpriseX Framework 各个配置文件的用途和配置项。

## 📋 目录

- [网关配置](#网关配置)
- [认证服务配置](#认证服务配置)
- [系统服务配置](#系统服务配置)
- [文件服务配置](#文件服务配置)
- [前端环境配置](#前端环境配置)
- [数据库配置](#数据库配置)
- [Redis配置](#redis配置)
- [JWT配置](#jwt配置)

---

## 网关配置

**配置文件：** `enterprisex-gateway/src/main/resources/application.yml`

### 基本配置

```yaml
server:
  port: 8080  # 网关服务端口
```

### Nacos配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # Nacos服务发现地址
      config:
        server-addr: localhost:8848  # Nacos配置中心地址
        file-extension: yml          # 配置文件扩展名
```

### 路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证服务路由
        - id: enterprisex-auth
          uri: lb://enterprisex-auth      # 负载均衡URI
          predicates:
            - Path=/auth/**               # 路径匹配
          filters:
            - StripPrefix=0               # 不剥离路径前缀

        # 系统服务路由
        - id: enterprisex-system
          uri: lb://enterprisex-system
          predicates:
            - Path=/system/**,/monitor/** # 支持多个路径
          filters:
            - StripPrefix=0

        # 文件服务路由
        - id: enterprisex-file
          uri: lb://enterprisex-file
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=0
```

**说明：**
- `lb://` 前缀表示使用负载均衡，从Nacos获取服务实例
- `StripPrefix=0` 表示不剥离路径前缀，保持原始路径转发

### 跨域配置

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "*"           # 允许的源
            allowed-methods:               # 允许的HTTP方法
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowed-headers: "*"           # 允许的请求头
            allow-credentials: true        # 允许携带凭证
            max-age: 3600                  # 预检请求缓存时间（秒）
```

**生产环境建议：**
- 将 `allowed-origins` 改为具体的前端域名，不要使用 `*`
- 根据实际需要限制 `allowed-methods` 和 `allowed-headers`

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost     # Redis服务器地址
      port: 6379          # Redis端口
      password:           # Redis密码（如有）
      database: 0         # 数据库索引
```

### JWT配置

```yaml
jwt:
  secret: WW91ckJhc2U2NEVuY29kZWRTZWNyZXRLZXlNaW5pbXVtMjU2Qml0c0ZvckNTMjU2QWxnb3JpdGhtU2VjdXJpdHk=
```

**重要说明：**
- 此密钥用于JWT签名验证
- **生产环境必须修改此密钥！**
- 密钥必须是Base64编码的字符串，长度至少256位
- 可以使用以下命令生成新密钥：
  ```bash
  echo -n "YourSecretKeyMinimum256BitsForHS256AlgorithmSecurity" | base64
  ```

---

## 认证服务配置

**配置文件：** `enterprisex-auth/src/main/resources/application.yml`

### 基本配置

```yaml
server:
  port: 9201  # 认证服务端口

spring:
  application:
    name: enterprisex-auth  # 服务名称（注册到Nacos）
```

### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/enterprisex?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password  # 修改为实际密码

    # Druid连接池配置
    druid:
      initial-size: 5              # 初始连接数
      min-idle: 5                  # 最小空闲连接数
      max-active: 20               # 最大活跃连接数
      max-wait: 60000              # 获取连接的最大等待时间（毫秒）
      test-while-idle: true        # 空闲时测试连接
      test-on-borrow: false        # 借用连接时不测试
      test-on-return: false        # 归还连接时不测试
```

### MyBatis-Plus配置

```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml  # Mapper XML文件位置
  type-aliases-package: com.enterprisex.*.domain  # 实体类包路径
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl  # 日志实现
```

### JWT配置

```yaml
jwt:
  secret: WW91ckJhc2U2NEVuY29kZWRTZWNyZXRLZXlNaW5pbXVtMjU2Qml0c0ZvckNTMjU2QWxnb3JpdGhtU2VjdXJpdHk=
  expiration: 7200        # AccessToken过期时间（秒），默认2小时
  refresh-expiration: 604800  # RefreshToken过期时间（秒），默认7天
```

---

## 系统服务配置

**配置文件：** `enterprisex-modules/enterprisex-system/src/main/resources/application.yml`

### 基本配置

```yaml
server:
  port: 9202  # 系统服务端口

spring:
  application:
    name: enterprisex-system
```

### 数据库和MyBatis配置

与认证服务类似，参考上述配置。

### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB       # 单个文件最大大小
      max-request-size: 100MB   # 整个请求最大大小
```

---

## 文件服务配置

**配置文件：** `enterprisex-modules/enterprisex-file/src/main/resources/application.yml`

### 基本配置

```yaml
server:
  port: 9203

spring:
  application:
    name: enterprisex-file
```

### 文件存储配置

```yaml
file:
  upload:
    path: /data/upload/      # 文件上传根目录
    allowed-extensions:      # 允许的文件扩展名
      - jpg
      - jpeg
      - png
      - gif
      - pdf
      - doc
      - docx
      - xls
      - xlsx
    max-size: 10485760       # 最大文件大小（字节），10MB
```

**注意事项：**
- 确保上传目录存在且有写入权限
- Windows系统路径示例：`C:/data/upload/`
- Linux系统路径示例：`/var/data/upload/`

---

## 前端环境配置

**配置文件：** `enterprisex-ui/.env.development` / `.env.production`

### 开发环境配置 (.env.development)

```bash
# API地址
VITE_API_BASE_URL=http://localhost:8080

# 应用标题
VITE_APP_TITLE=EnterpriseX Framework

# 端口
VITE_PORT=3000

# 是否开启Mock
VITE_USE_MOCK=false
```

### 生产环境配置 (.env.production)

```bash
# API地址（修改为实际的生产环境地址）
VITE_API_BASE_URL=https://api.yourcompany.com

# 应用标题
VITE_APP_TITLE=EnterpriseX Framework

# 是否开启Mock
VITE_USE_MOCK=false
```

---

## 数据库配置

### 连接字符串参数说明

```
jdbc:mysql://localhost:3306/enterprisex?参数1&参数2&参数3
```

**常用参数：**

| 参数 | 说明 | 推荐值 |
|------|------|--------|
| useUnicode | 使用Unicode字符集 | true |
| characterEncoding | 字符编码 | utf8 或 utf8mb4 |
| serverTimezone | 服务器时区 | Asia/Shanghai |
| useSSL | 是否使用SSL | false（开发环境） |
| allowPublicKeyRetrieval | 允许客户端从服务器获取公钥 | true（MySQL 8+） |
| rewriteBatchedStatements | 批量操作优化 | true（提升性能） |

### 连接池配置建议

**开发环境：**
- initial-size: 5
- min-idle: 5
- max-active: 20

**生产环境：**
- initial-size: 10
- min-idle: 10
- max-active: 50（根据实际并发调整）

---

## Redis配置

### 基本配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:           # 如有密码则填写
      database: 0         # 数据库索引（0-15）
      timeout: 3000       # 连接超时时间（毫秒）

      # Lettuce连接池配置
      lettuce:
        pool:
          max-active: 8   # 最大连接数
          max-idle: 8     # 最大空闲连接数
          min-idle: 0     # 最小空闲连接数
          max-wait: -1    # 最大等待时间（-1表示无限制）
```

### 缓存键命名规范

项目中使用以下缓存键前缀：

| 前缀 | 用途 | 示例 |
|------|------|------|
| `login_tokens:` | 登录Token | `login_tokens:admin` |
| `online_tokens:` | 在线用户Token | `online_tokens:uuid` |
| `sys_config:` | 系统配置 | `sys_config:sys.user.initPassword` |
| `sys_dict:` | 数据字典 | `sys_dict:sys_user_sex` |
| `captcha_codes:` | 验证码 | `captcha_codes:uuid` |

---

## JWT配置

### 配置项说明

```yaml
jwt:
  # 密钥（Base64编码）- 生产环境必须修改
  secret: WW91ckJhc2U2NEVuY29kZWRTZWNyZXRLZXlNaW5pbXVtMjU2Qml0c0ZvckNTMjU2QWxnb3JpdGhtU2VjdXJpdHk=

  # AccessToken过期时间（秒）
  expiration: 7200        # 2小时

  # RefreshToken过期时间（秒）
  refresh-expiration: 604800  # 7天

  # Token前缀
  token-prefix: Bearer    # HTTP请求头中的Token前缀

  # 请求头名称
  header: Authorization   # Token所在的请求头名称
```

### 安全建议

1. **生产环境必须修改密钥**
2. **密钥长度至少256位**
3. **定期轮换密钥（建议每季度）**
4. **不要将密钥提交到版本控制系统**
5. **使用环境变量或配置中心管理敏感配置**

### 生成新密钥

**方法1：使用OpenSSL**
```bash
openssl rand -base64 64
```

**方法2：使用Java代码**
```java
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateSecret {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String secret = Base64.getEncoder().encodeToString(bytes);
        System.out.println(secret);
    }
}
```

---

## 环境变量配置

### 推荐使用环境变量管理敏感配置

**application.yml示例：**
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/enterprisex}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

jwt:
  secret: ${JWT_SECRET:default_secret_change_in_production}
```

**Docker环境变量示例：**
```bash
docker run -d \
  -e DB_URL=jdbc:mysql://mysql:3306/enterprisex \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=your_base64_encoded_secret \
  enterprisex-auth:latest
```

---

## 配置优先级

Spring Boot配置加载顺序（优先级从高到低）：

1. 命令行参数
2. 操作系统环境变量
3. `application-{profile}.yml` （profile特定配置）
4. `application.yml` （默认配置）
5. Nacos配置中心（如已配置）

---

## 常见问题

### 1. 数据库连接失败

**错误信息：** `Communications link failure`

**解决方案：**
- 检查数据库是否启动
- 检查数据库地址、端口是否正确
- 检查用户名密码是否正确
- 检查防火墙是否开放3306端口

### 2. Redis连接失败

**错误信息：** `Unable to connect to Redis`

**解决方案：**
- 检查Redis是否启动
- 检查Redis地址、端口是否正确
- 检查Redis密码配置
- 检查防火墙是否开放6379端口

### 3. Nacos注册失败

**错误信息：** `Request nacos server failed`

**解决方案：**
- 检查Nacos是否启动
- 检查Nacos地址是否正确
- 检查网络连接
- 检查Nacos命名空间配置

### 4. JWT验证失败

**错误信息：** `Invalid JWT token`

**解决方案：**
- 检查各服务的JWT密钥是否一致
- 检查Token是否过期
- 检查请求头格式是否正确（`Authorization: Bearer {token}`）

---

## 🔗 相关文档

- [README.md](README.md) - 项目说明
- [FEATURES.md](FEATURES.md) - 功能清单
- [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - 开发指南
- [API_EXAMPLES.md](API_EXAMPLES.md) - API调用示例
