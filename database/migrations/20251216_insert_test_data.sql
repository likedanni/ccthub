-- ====================================
-- 测试数据插入脚本（本地数据库版本）
-- 作者: CCTHub
-- 日期: 2025-12-16
-- 说明: 为支付流水、退款申请、用户钱包、钱包流水、用户积分、优惠券列表、用户优惠券表插入测试数据
-- 重要: 此脚本用于本地数据库 localhost:3306/cct-hub，不是Docker数据库！
-- ====================================
USE `cct-hub`;
-- 清理旧测试数据
DELETE FROM wallet_transactions
WHERE transaction_no LIKE 'WT20251216%';
DELETE FROM user_coupons
WHERE coupon_code LIKE '%20251216%';
DELETE FROM coupons
WHERE name LIKE '%测试%'
    OR name LIKE '%20251216%';
DELETE FROM user_points
WHERE id > 1000;
-- 保留原有数据
DELETE FROM order_refunds
WHERE refund_no LIKE 'RF20251216%';
DELETE FROM payments
WHERE payment_no LIKE 'PAY20251216%';
-- ====================================
-- 1. 支付流水 (payments表)
-- ====================================
INSERT INTO payments (
        payment_no,
        order_no,
        payment_type,
        payment_channel,
        payment_amount,
        status,
        third_party_no,
        payer_id,
        payment_time
    )
VALUES -- 微信支付成功记录
    (
        'PAY202512160001',
        'ORD202512160001',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        196.00,
        1,
        'WX20251216001',
        'openid_user_001',
        NOW()
    ),
    (
        'PAY202512160002',
        'ORD202512160002',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        98.00,
        1,
        'WX20251216002',
        'openid_user_002',
        NOW()
    ),
    -- 支付宝支付成功记录
    (
        'PAY202512160003',
        'ORD202512160003',
        'ALIPAY',
        'ALIPAY_APP',
        294.00,
        1,
        'ALI20251216001',
        'alipay_user_001',
        NOW()
    ),
    -- 待支付记录
    (
        'PAY202512160004',
        'ORD202512160004',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        150.00,
        0,
        NULL,
        NULL,
        NULL
    ),
    -- 已退款记录
    (
        'PAY202512160005',
        'ORD202512160005',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        200.00,
        3,
        'WX20251216003',
        'openid_user_003',
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    -- 积分支付记录
    (
        'PAY202512160006',
        'ORD202512160006',
        'POINTS',
        'POINTS',
        50.00,
        1,
        NULL,
        NULL,
        NOW()
    ),
    -- 混合支付记录（微信+积分）
    (
        'PAY202512160007',
        'ORD202512160007',
        'MIXED',
        'WECHAT_MINI_PROGRAM',
        180.00,
        1,
        'WX20251216004',
        'openid_user_004',
        NOW()
    );
-- ====================================
-- 2. 退款申请 (order_refunds)
-- ====================================
INSERT INTO order_refunds (
        refund_no,
        order_no,
        user_id,
        refund_type,
        refund_amount,
        refund_fee,
        refund_reason,
        status,
        auditor_id,
        audited_at,
        payment_refund_at,
        actual_refund
    )
VALUES -- 待审核的全额退款
    (
        'RF202512160001',
        'ORD202512160005',
        10,
        1,
        200.00,
        0.00,
        '行程取消，无法前往',
        0,
        NULL,
        NULL,
        NULL,
        0.00
    ),
    -- 审核通过待退款
    (
        'RF202512160002',
        'ORD202512160008',
        11,
        1,
        150.00,
        0.00,
        '临时有事无法前往',
        1,
        10,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NULL,
        0.00
    ),
    -- 退款成功（全额）
    (
        'RF202512160003',
        'ORD202512160009',
        10,
        1,
        196.00,
        0.00,
        '景区关闭',
        3,
        10,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        196.00
    ),
    -- 退款成功（部分，扣除手续费）
    (
        'RF202512160004',
        'ORD202512160010',
        12,
        2,
        98.00,
        9.80,
        '游客临时不能参加',
        3,
        10,
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        88.20
    ),
    -- 审核拒绝
    (
        'RF202512160005',
        'ORD202512160011',
        11,
        1,
        98.00,
        0.00,
        '不想去了',
        4,
        10,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        NULL,
        0.00
    ),
    -- 退款失败
    (
        'RF202512160006',
        'ORD202512160012',
        13,
        1,
        120.00,
        0.00,
        '天气原因',
        5,
        10,
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        NULL,
        0.00
    );
-- ====================================
-- 3. 钱包流水 (wallet_transactions) - 需要先有wallet_id
-- ====================================
INSERT INTO wallet_transactions (
        transaction_no,
        user_id,
        wallet_id,
        transaction_type,
        amount,
        balance_after,
        order_no,
        payment_no,
        refund_no,
        remark,
        status
    )
SELECT 'WT202512160001',
    10,
    id,
    1,
    200.00,
    700.00,
    'ORD202512160013',
    NULL,
    'RF202512160003',
    '订单退款到账',
    1
FROM user_wallet
WHERE user_id = 10
UNION ALL
SELECT 'WT202512160002',
    10,
    id,
    2,
    50.00,
    650.00,
    'ORD202512160014',
    'PAY202512160001',
    NULL,
    '订单消费',
    1
FROM user_wallet
WHERE user_id = 10
UNION ALL
SELECT 'WT202512160003',
    10,
    id,
    1,
    100.00,
    750.00,
    NULL,
    NULL,
    NULL,
    '系统充值',
    1
FROM user_wallet
WHERE user_id = 10
UNION ALL
SELECT 'WT202512160004',
    11,
    id,
    1,
    100.00,
    380.50,
    'ORD202512160015',
    NULL,
    'RF202512160004',
    '退款到账',
    1
FROM user_wallet
WHERE user_id = 11
UNION ALL
SELECT 'WT202512160005',
    11,
    id,
    2,
    98.00,
    282.50,
    'ORD202512160016',
    'PAY202512160002',
    NULL,
    '购买门票',
    1
FROM user_wallet
WHERE user_id = 11
UNION ALL
SELECT 'WT202512160006',
    11,
    id,
    3,
    20.00,
    360.50,
    NULL,
    NULL,
    NULL,
    '资金冻结',
    1
FROM user_wallet
WHERE user_id = 11
UNION ALL
SELECT 'WT202512160007',
    12,
    id,
    1,
    500.00,
    1750.00,
    NULL,
    NULL,
    NULL,
    '充值',
    1
FROM user_wallet
WHERE user_id = 12
UNION ALL
SELECT 'WT202512160008',
    12,
    id,
    2,
    150.00,
    1600.00,
    'ORD202512160017',
    'PAY202512160003',
    NULL,
    '订单支付',
    1
FROM user_wallet
WHERE user_id = 12
UNION ALL
SELECT 'WT202512160009',
    13,
    id,
    1,
    300.00,
    300.00,
    NULL,
    NULL,
    NULL,
    '新用户奖励',
    1
FROM user_wallet
WHERE user_id = 13
UNION ALL
SELECT 'WT202512160010',
    13,
    id,
    2,
    300.00,
    0.00,
    'ORD202512160018',
    'PAY202512160006',
    NULL,
    '购票消费',
    1
FROM user_wallet
WHERE user_id = 13;
-- ====================================
-- 4. 用户积分 (user_points)
-- ====================================
INSERT INTO user_points (
        user_id,
        change_type,
        source,
        points,
        current_balance,
        order_no,
        expires_at,
        status,
        remark
    )
VALUES -- 用户10的积分流水
    (
        10,
        1,
        'order',
        100,
        3100,
        'ORD202512160001',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '购票赠送积分'
    ),
    (
        10,
        2,
        'exchange',
        50,
        3050,
        'ORD202512160019',
        NULL,
        1,
        '积分抵扣'
    ),
    (
        10,
        1,
        'sign_in',
        200,
        3250,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '签到奖励'
    ),
    -- 用户11的积分流水
    (
        11,
        1,
        'order',
        50,
        200,
        'ORD202512160002',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '消费赠送'
    ),
    (
        11,
        2,
        'exchange',
        100,
        100,
        'ORD202512160020',
        NULL,
        1,
        '积分兑换优惠券'
    ),
    -- 用户12的积分流水
    (
        12,
        1,
        'order',
        300,
        300,
        'ORD202512160003',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '大额消费奖励'
    ),
    (
        12,
        1,
        'activity',
        500,
        800,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '活动奖励'
    ),
    -- 用户13的积分流水
    (
        13,
        1,
        'register',
        100,
        100,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        '新用户注册奖励'
    );
-- ====================================
-- 5. 优惠券列表 (coupons)
-- ====================================
INSERT INTO coupons (
        name,
        type,
        value,
        min_spend,
        applicable_type,
        validity_type,
        valid_days,
        starts_at,
        expires_at,
        total_quantity,
        remaining_quantity,
        limit_per_user,
        status
    )
VALUES -- 满减券
    (
        '新用户专享券',
        1,
        20.00,
        100.00,
        0,
        1,
        30,
        NOW(),
        DATE_ADD(NOW(), INTERVAL 90 DAY),
        1000,
        850,
        1,
        1
    ),
    (
        '周末出游券',
        1,
        30.00,
        200.00,
        1,
        1,
        7,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_ADD(NOW(), INTERVAL 60 DAY),
        500,
        320,
        1,
        1
    ),
    (
        '国庆特惠券',
        1,
        50.00,
        300.00,
        0,
        1,
        14,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        2000,
        1200,
        1,
        1
    ),
    -- 折扣券
    (
        '景区通用9折券',
        2,
        0.90,
        50.00,
        1,
        1,
        60,
        NOW(),
        DATE_ADD(NOW(), INTERVAL 120 DAY),
        800,
        600,
        2,
        1
    ),
    (
        'VIP专享8折券',
        2,
        0.80,
        100.00,
        0,
        1,
        90,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_ADD(NOW(), INTERVAL 180 DAY),
        300,
        180,
        3,
        1
    );
-- ====================================
-- 6. 用户优惠券 (user_coupons)
-- ====================================
INSERT INTO user_coupons (
        user_id,
        coupon_id,
        coupon_code,
        status,
        used_at,
        order_no,
        expires_at
    )
VALUES -- 用户10的优惠券
    (
        10,
        1,
        'NEWUSER20251216001',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 25 DAY)
    ),
    (
        10,
        2,
        'WEEKEND20251216001',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 5 DAY)
    ),
    (
        10,
        4,
        'VIP90OFF20251216001',
        1,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        'ORD202512160001',
        DATE_ADD(NOW(), INTERVAL 50 DAY)
    ),
    -- 用户11的优惠券
    (
        11,
        1,
        'NEWUSER20251216002',
        2,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    (
        11,
        3,
        'NATIONAL20251216001',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 22 DAY)
    ),
    (
        11,
        5,
        'VIP80OFF20251216001',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 87 DAY)
    ),
    -- 用户12的优惠券
    (
        12,
        2,
        'WEEKEND20251216002',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 7 DAY)
    ),
    (
        12,
        3,
        'NATIONAL20251216002',
        1,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        'ORD202512160003',
        DATE_ADD(NOW(), INTERVAL 25 DAY)
    ),
    (
        12,
        4,
        'VIP90OFF20251216002',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 45 DAY)
    ),
    -- 用户13的优惠券
    (
        13,
        1,
        'NEWUSER20251216003',
        0,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 30 DAY)
    );
-- ====================================
-- 验证插入结果
-- ====================================
SELECT '支付流水记录数：',
    COUNT(*)
FROM payments
WHERE payment_no LIKE 'PAY20251216%';
SELECT '退款申请记录数：',
    COUNT(*)
FROM order_refunds
WHERE refund_no LIKE 'RF20251216%';
SELECT '钱包流水记录数：',
    COUNT(*)
FROM wallet_transactions
WHERE transaction_no LIKE 'WT20251216%';
SELECT '用户积分记录数：',
    COUNT(*)
FROM user_points
WHERE user_id IN (10, 11, 12, 13)
    AND created_at > DATE_SUB(NOW(), INTERVAL 1 HOUR);
SELECT '优惠券列表记录数：',
    COUNT(*)
FROM coupons
WHERE name LIKE '%券';
SELECT '用户优惠券记录数：',
    COUNT(*)
FROM user_coupons
WHERE coupon_code LIKE '%20251216%';
-- ====================================
-- 数据统计查询
-- ====================================
SELECT '支付成功总金额：',
    SUM(payment_amount)
FROM payments
WHERE status = 1
    AND payment_no LIKE 'PAY20251216%';
SELECT '退款成功总金额：',
    SUM(actual_refund)
FROM order_refunds
WHERE status = 3
    AND refund_no LIKE 'RF20251216%';
SELECT '钱包流水总笔数：',
    COUNT(*)
FROM wallet_transactions
WHERE transaction_no LIKE 'WT20251216%';
SELECT '优惠券使用率：',
    CONCAT(
        ROUND(
            SUM(
                CASE
                    WHEN status = 1 THEN 1
                    ELSE 0
                END
            ) * 100.0 / COUNT(*),
            2
        ),
        '%'
    )
FROM user_coupons
WHERE coupon_code LIKE '%20251216%';