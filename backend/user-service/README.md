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
