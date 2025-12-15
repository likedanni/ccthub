#!/bin/bash

# 测试脚本 - 验证前后端功能

echo "========================================="
echo "后端API测试"
echo "========================================="

echo "1. 测试票种列表API..."
curl -s "http://localhost:8080/api/tickets?page=0&size=3" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(f\"总票种数: {data['totalElements']}\")
for item in data['content'][:3]:
    print(f\"  - {item['name']} (ID: {item['id']}, 状态: {item['statusText']})\")
"

echo ""
echo "2. 测试订单列表API..."
curl -s "http://localhost:8080/api/orders" | python3 -c "
import sys, json
data = json.load(sys.stdin)
orders = data.get('data', [])
print(f\"总订单数: {len(orders)}\")
for order in orders[:3]:
    print(f\"  - {order['orderNo']}: {order['contactName']}, ¥{order['actualAmount']}, {order['status']}\")
"

echo ""
echo "========================================="
echo "数据库数据检查"
echo "========================================="

mysql -uroot -p12345678 -e "
USE \\\`cct-hub\\\`;
SELECT '票种数据:' as info;
SELECT id, name, type, status FROM tickets LIMIT 5;

SELECT '票价数据:' as info;
SELECT COUNT(*) as total FROM ticket_prices;

SELECT '订单数据:' as info;
SELECT id, order_no, contact_name, status FROM orders LIMIT 5;
" 2>&1 | grep -v "Warning"

echo ""
echo "========================================="
echo "前端测试说明"
echo "========================================="
echo "请手动执行以下步骤测试前端："
echo ""
echo "1. 启动前端开发服务器："
echo "   cd /Users/like/CCTHub/frontend/admin-web"
echo "   npm run dev"
echo ""
echo "2. 打开浏览器访问: http://localhost:5173"
echo ""
echo "3. 登录后测试以下功能："
echo "   ✓ 点击「票种管理」- 应显示4个票种"
echo "   ✓ 点击「订单管理」- 应显示5个订单"
echo "   ✓ 检查数据是否正常显示"
echo ""
