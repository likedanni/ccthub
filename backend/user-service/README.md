# user-service (示例)

最小的 Spring Boot 用户服务示例，用于演示用户注册、读取 `AI_DEFAULT_MODEL` 环境变量并返回给客户端。

运行（本地）：

```bash
cd backend/user-service
mvn -q -DskipTests package
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

使用 Docker：

```bash
docker build -t ccthub/user-service:local .
docker run -e AI_DEFAULT_MODEL=claude-haiku-4.5 -p 8080:8080 ccthub/user-service:local
```

使用 docker-compose（包含 MySQL 示例）：

```bash
cd backend/user-service
docker-compose up -d

# 或（单独运行 db 容器后运行服务）：
docker run --name mysql-ccthub -e MYSQL_ROOT_PASSWORD=12345678 -e MYSQL_DATABASE=cct-hub -p 3307:3306 -d mysql:8.0 --default-authentication-plugin=mysql_native_password
docker build -t ccthub/user-service:local .
docker run --rm \
	-e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/cct-hub \
	-e SPRING_DATASOURCE_USERNAME=root \
	-e SPRING_DATASOURCE_PASSWORD=12345678 \
	# 推荐使用一个长度足够的 secret（HS512 需要 >= 512 bits，即 >=64 字节）
	# 生成示例：
	#   openssl rand -base64 64
	# 在容器中通过环境变量注入（示例变量名 `JWT_SECRET` 或 Spring 属性 `jwt.secret` 均可）
	-e JWT_SECRET=your-64-byte-base64-secret-here \
	-e AI_DEFAULT_MODEL=claude-haiku-4.5 \
	-p 8080:8080 ccthub/user-service:local
```

注意：

- Flyway 会在应用启动时对配置的数据库执行迁移，请确保 `cct-hub` 数据库已存在且账号有权限。
- 不要在长期分支中提交明文密码；生产或 CI 环境请使用 secrets。

API 示例：

POST http://localhost:8080/api/users/register
Content-Type: application/json

{
"phone": "13800000000",
"password": "p@ssw0rd"
}

返回示例：

{
"userId": 1,
"status": "ok",
"aiDefaultModel": "claude-haiku-4.5"
}

测试：

```bash
cd backend/user-service
mvn test
```
