#!/bin/bash

echo "==================================="
echo "CCT-Hub 系统测试脚本"
echo "==================================="
echo ""

# 测试后端是否运行
echo "[1] 检查后端服务状态..."
BACKEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/users/1/profile)
if [ "$BACKEND_STATUS" == "000" ]; then
    echo "❌ 后端服务未启动！"
    echo "   请运行: cd backend/user-service && mvn spring-boot:run"
    exit 1
else
    echo "✅ 后端服务正常运行 (HTTP $BACKEND_STATUS)"
fi
echo ""

# 测试数据库连接
echo "[2] 检查数据库连接..."
DB_CHECK=$(docker exec mysql-ccthub mysql -uroot -p12345678 -e "SELECT COUNT(*) FROM \`cct-hub\`.users;" 2>/dev/null | tail -n 1)
if [ -z "$DB_CHECK" ]; then
    echo "❌ 数据库连接失败！"
    echo "   请检查: docker ps | grep mysql"
    exit 1
else
    echo "✅ 数据库连接正常 (共 $DB_CHECK 个用户)"
fi
echo ""

# 测试登录接口
echo "[3] 测试登录接口..."
echo "   账号: 13800138000"
echo "   密码: test123456"

LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"test123456"}')

echo "   响应: $LOGIN_RESPONSE"

if echo "$LOGIN_RESPONSE" | grep -q "accessToken"; then
    echo "✅ 登录成功！"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    echo "   Token: ${TOKEN:0:50}..."
else
    echo "❌ 登录失败！"
    echo "   可能原因:"
    echo "   1. 数据库中没有测试用户数据"
    echo "   2. 密码加密方式不匹配"
    echo "   3. Spring Security配置问题"
fi
echo ""

# 测试PC管理后台
echo "[4] 检查PC管理后台状态..."
FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000)
if [ "$FRONTEND_STATUS" == "200" ]; then
    echo "✅ PC管理后台正常运行"
    echo "   访问地址: http://localhost:3000"
else
    echo "⚠️  PC管理后台未启动 (HTTP $FRONTEND_STATUS)"
    echo "   请运行: cd frontend/admin-web && npm run dev"
fi
echo ""

echo "==================================="
echo "测试完成！"
echo "==================================="
echo ""
echo "📱 测试账号:"
echo "   管理员: 13800138000 / test123456"
echo "   普通用户: 13900139000 / user123456"
echo ""
echo "🌐 访问地址:"
echo "   后端API: http://localhost:8080"
echo "   PC后台: http://localhost:3000"
echo ""
