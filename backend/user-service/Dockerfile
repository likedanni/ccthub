# ============ 第一阶段：构建应用 ============
# 使用带有 Maven 和 JDK 的官方镜像进行构建
# 选择 -alpine 版本以获得更小的基础镜像
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# 设置工作目录
WORKDIR /build

# 1. 单独复制 pom.xml 文件
# 这一步利用 Docker 缓存层，仅当 pom 变更时才重新解析依赖
COPY pom.xml .

# 2. 下载项目依赖（此层可被缓存）
# 使用 -B 批处理模式， -DskipTests 跳过测试以加速构建
RUN mvn dependency:go-offline -B

# 3. 复制所有源代码
COPY src ./src

# 4. 执行打包，生成可执行 Jar 文件
# 再次指定跳过测试，确保构建产物纯净
RUN mvn clean package -DskipTests

## ============ 运行阶段 ============
# 仅拉取运行时环境（镜像很小，更容易成功）
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 将本地构建好的 jar 包复制到镜像中
COPY target/*.jar app.jar

# 暴露端口（根据你的应用调整）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]