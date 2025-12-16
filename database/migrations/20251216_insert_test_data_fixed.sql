-- ====================================
-- 测试数据插入脚本（修正版）
-- 作者: CCTHub
-- 日期: 2025-12-16
-- 说明: 为支付流水、退款申请、用户钱包、钱包流水、用户积分、积分流水、优惠券列表、用户优惠券表插入测试数据
-- ====================================
USE `cct-hub`;
-- ====================================
-- 1. 支付流水 (payments)
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
        payment_time,
        callback_time,
        create_time
    )
VALUES -- 微信支付成功记录
    (
        'PAY202512160001',
        'T20251216000001',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        196.00,
        1,
        'WX_TXN_001',
        'user_wx_001',
        NOW(),
        NOW(),
        NOW()
    ),
    (
        'PAY202512160002',
        'T20251216000002',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        98.00,
        1,
        'WX_TXN_002',
        'user_wx_002',
        NOW(),
        NOW(),
        NOW()
    ),
    -- 支付宝支付成功记录
    (
        'PAY202512160003',
        'T20251216000003',
        'ALIPAY',
        'ALIPAY_APP',
        294.00,
        1,
        'ALI_TXN_001',
        'user_ali_001',
        NOW(),
        NOW(),
        NOW()
    ),
    -- 待支付记录
    (
        'PAY202512160004',
        'T20251216000004',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        150.00,
        0,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    -- 已退款记录
    (
        'PAY202512160005',
        'T20251216000005',
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        200.00,
        3,
        'WX_TXN_003',
        'user_wx_002',
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    ),
    -- 积分支付记录
    (
        'PAY202512160006',
        'T20251216000006',
        'POINTS',
        'POINTS',
        50.00,
        1,
        NULL,
        '4',
        NOW(),
        NOW(),
        NOW()
    ),
    -- 混合支付记录（微信+积分）
    (
        'PAY202512160007',
        'T20251216000007',
        'MIXED',
        'WECHAT_MINI_PROGRAM',
        180.00,
        1,
        'WX_TXN_004',
        'user_wx_001',
        NOW(),
        NOW(),
        NOW()
    );
-- ====================================
-- 2. 退款申请 (order_refunds)
-- 使用现有用户ID: 9, 10, 11, 12, 13
-- ====================================
INSERT INTO order_refunds (
        refund_no,
        order_no,
        user_id,
        refund_type,
        refund_amount,
        refund_reason,
        status,
        auditor_id,
        audited_at,
        audit_note,
        payment_refund_no,
        payment_refund_at,
        created_at,
        updated_at,
        actual_refund,
        refund_fee
    )
VALUES -- 待审核的全额退款
    (
        'RF202512160001',
        'T20251216000005',
        10,
        1,
        200.00,
        '行程取消，无法前往',
        0,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NULL,
        NULL
    ),
    -- 审核通过待退款
    (
        'RF202512160002',
        'T20251216000008',
        11,
        1,
        150.00,
        '临时有事无法前往',
        1,
        13,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        '符合退款条件',
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 2 HOUR),
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NULL,
        NULL
    ),
    -- 退款成功（全额）
    (
        'RF202512160003',
        'T20251216000009',
        10,
        1,
        196.00,
        '景区关闭',
        3,
        13,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        '景区临时关闭，全额退款',
        'REFUND_WX_123456',
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        196.00,
        0.00
    ),
    -- 退款成功（部分，扣除手续费）
    (
        'RF202512160004',
        'T20251216000010',
        12,
        2,
        98.00,
        '游客临时不能参加',
        3,
        13,
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        '部分退款，扣除手续费',
        'REFUND_WX_123457',
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        88.20,
        9.80
    ),
    -- 审核拒绝
    (
        'RF202512160005',
        'T20251216000011',
        11,
        1,
        98.00,
        '不想去了',
        4,
        13,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        '不符合退款政策：已过退款期限',
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        NULL,
        NULL
    ),
    -- 退款失败
    (
        'RF202512160006',
        'T20251216000012',
        9,
        1,
        120.00,
        '天气原因',
        5,
        13,
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        '退款渠道异常，请联系客服',
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        NULL,
        NULL
    );
-- ====================================
-- 3. 用户钱包 (user_wallet)
-- 使用现有用户ID: 9, 10, 11, 12, 13
-- ====================================
INSERT INTO user_wallet (
        user_id,
        balance,
        frozen_balance,
        total_deposit,
        total_consumption,
        security_level,
        status,
        created_at,
        updated_at,
        version
    )
VALUES -- 正常使用的钱包
    (
        9,
        500.00,
        0.00,
        1500.00,
        1000.00,
        2,
        1,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        NOW(),
        1
    ),
    (
        10,
        280.50,
        20.00,
        500.00,
        219.50,
        2,
        1,
        DATE_SUB(NOW(), INTERVAL 60 DAY),
        NOW(),
        1
    ),
    (
        11,
        1250.00,
        0.00,
        2000.00,
        750.00,
        3,
        1,
        DATE_SUB(NOW(), INTERVAL 90 DAY),
        NOW(),
        1
    ),
    (
        12,
        0.00,
        0.00,
        300.00,
        300.00,
        1,
        1,
        DATE_SUB(NOW(), INTERVAL 15 DAY),
        NOW(),
        1
    ),
    -- 冻结的钱包
    (
        13,
        800.00,
        800.00,
        1000.00,
        200.00,
        2,
        0,
        DATE_SUB(NOW(), INTERVAL 120 DAY),
        NOW(),
        1
    );
-- ====================================
-- 4. 钱包流水 (wallet_transactions)
-- 需要先获取wallet_id，使用现有用户ID: 9, 10, 11, 12, 13
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
        status,
        created_at
    )
SELECT 'WT202512160001',
    9,
    w.id,
    1,
    -- 收入
    200.00,
    500.00,
    'T20251216000013',
    NULL,
    NULL,
    '订单退款到账',
    1,
    DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM user_wallet w
WHERE w.user_id = 9
UNION ALL
SELECT 'WT202512160002',
    9,
    w.id,
    2,
    50.00,
    450.00,
    'T20251216000014',
    NULL,
    NULL,
    '订单消费',
    1,
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM user_wallet w
WHERE w.user_id = 9
UNION ALL
SELECT 'WT202512160003',
    9,
    w.id,
    1,
    100.00,
    550.00,
    NULL,
    NULL,
    NULL,
    '系统充值',
    1,
    DATE_SUB(NOW(), INTERVAL 5 DAY)
FROM user_wallet w
WHERE w.user_id = 9
UNION ALL
SELECT 'WT202512160004',
    10,
    w.id,
    1,
    100.00,
    280.50,
    'T20251216000015',
    NULL,
    'RF202512160003',
    '退款到账',
    1,
    DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM user_wallet w
WHERE w.user_id = 10
UNION ALL
SELECT 'WT202512160005',
    10,
    w.id,
    2,
    98.00,
    180.50,
    'T20251216000016',
    NULL,
    NULL,
    '购买门票',
    1,
    DATE_SUB(NOW(), INTERVAL 7 DAY)
FROM user_wallet w
WHERE w.user_id = 10
UNION ALL
SELECT 'WT202512160006',
    10,
    w.id,
    3,
    20.00,
    280.50,
    NULL,
    NULL,
    NULL,
    '资金冻结',
    1,
    DATE_SUB(NOW(), INTERVAL 1 HOUR)
FROM user_wallet w
WHERE w.user_id = 10
UNION ALL
SELECT 'WT202512160007',
    11,
    w.id,
    1,
    500.00,
    1250.00,
    NULL,
    NULL,
    NULL,
    '充值',
    1,
    DATE_SUB(NOW(), INTERVAL 10 DAY)
FROM user_wallet w
WHERE w.user_id = 11
UNION ALL
SELECT 'WT202512160008',
    11,
    w.id,
    2,
    150.00,
    750.00,
    'T20251216000017',
    NULL,
    NULL,
    '订单支付',
    1,
    DATE_SUB(NOW(), INTERVAL 15 DAY)
FROM user_wallet w
WHERE w.user_id = 11
UNION ALL
SELECT 'WT202512160009',
    12,
    w.id,
    1,
    300.00,
    300.00,
    NULL,
    NULL,
    NULL,
    '新用户奖励',
    1,
    DATE_SUB(NOW(), INTERVAL 15 DAY)
FROM user_wallet w
WHERE w.user_id = 12
UNION ALL
SELECT 'WT202512160010',
    12,
    w.id,
    2,
    300.00,
    0.00,
    'T20251216000018',
    NULL,
    NULL,
    '购票消费',
    1,
    DATE_SUB(NOW(), INTERVAL 14 DAY)
FROM user_wallet w
WHERE w.user_id = 12;
-- ====================================
-- 5. 用户积分 (user_points)
-- ====================================
INSERT INTO user_points (
        user_id,
        change_type,
        source,
        points,
        current_balance,
        order_no,
        activity_id,
        expires_at,
        status,
        created_at,
        remark
    )
VALUES -- 用户1的积分流水
    (
        1,
        1,
        'order',
        100,
        1500,
        'T20251216000001',
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 1 DAY),
        '购票赠送积分'
    ),
    (
        1,
        2,
        'order',
        50,
        1450,
        'T20251216000019',
        NULL,
        NULL,
        1,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        '积分抵扣'
    ),
    (
        1,
        1,
        'sign_in',
        200,
        1650,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        '签到奖励'
    ),
    (
        1,
        3,
        'expire',
        150,
        1500,
        NULL,
        NULL,
        NULL,
        1,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        '积分过期'
    ),
    -- 用户2的积分流水
    (
        2,
        1,
        'order',
        50,
        500,
        'T20251216000002',
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        '消费赠送'
    ),
    (
        2,
        2,
        'exchange',
        100,
        400,
        'T20251216000020',
        NULL,
        NULL,
        1,
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        '积分兑换优惠券'
    ),
    (
        2,
        4,
        'freeze',
        100,
        500,
        NULL,
        NULL,
        NULL,
        1,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        '积分冻结'
    ),
    -- 用户3的积分流水
    (
        3,
        1,
        'order',
        300,
        2800,
        'T20251216000003',
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        '大额消费奖励'
    ),
    (
        3,
        1,
        'activity',
        500,
        2300,
        NULL,
        1001,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        '活动奖励'
    ),
    -- 用户4的积分流水
    (
        4,
        1,
        'register',
        100,
        100,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 60 DAY),
        '新用户注册奖励'
    ),
    -- 用户6的积分流水
    (
        6,
        1,
        'register',
        200,
        200,
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        1,
        NOW(),
        '新用户福利'
    );
-- ====================================
-- 6. 优惠券列表 (coupons)
-- ====================================
INSERT INTO coupons (
        name,
        type,
        value,
        min_spend,
        applicable_type,
        applicable_ids,
        validity_type,
        valid_days,
        starts_at,
        expires_at,
        total_quantity,
        remaining_quantity,
        limit_per_user,
        status,
        created_at
    )
VALUES -- 满减券
    (
        '新用户专享券',
        1,
        20.00,
        100.00,
        1,
        NULL,
        2,
        30,
        NULL,
        NULL,
        1000,
        850,
        1,
        1,
        NOW()
    ),
    (
        '周末出游券',
        1,
        30.00,
        200.00,
        2,
        JSON_ARRAY(1, 2, 3),
        1,
        NULL,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_ADD(NOW(), INTERVAL 60 DAY),
        500,
        320,
        2,
        1,
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        '国庆特惠券',
        1,
        50.00,
        300.00,
        1,
        NULL,
        1,
        NULL,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        2000,
        1200,
        1,
        1,
        DATE_SUB(NOW(), INTERVAL 10 DAY)
    ),
    -- 折扣券（9折 = 0.9）
    (
        '景区通用9折券',
        2,
        0.90,
        50.00,
        2,
        JSON_ARRAY(1, 2, 3, 4),
        2,
        60,
        NULL,
        NULL,
        800,
        600,
        3,
        1,
        NOW()
    ),
    (
        'VIP专享8折券',
        2,
        0.80,
        100.00,
        1,
        NULL,
        1,
        NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_ADD(NOW(), INTERVAL 180 DAY),
        300,
        180,
        1,
        1,
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    -- 即将过期的券
    (
        '限时抢购券',
        1,
        15.00,
        80.00,
        2,
        JSON_ARRAY(1),
        1,
        NULL,
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_ADD(NOW(), INTERVAL 3 DAY),
        500,
        100,
        1,
        1,
        DATE_SUB(NOW(), INTERVAL 7 DAY)
    ),
    -- 已下架的券
    (
        '已过期测试券',
        1,
        10.00,
        50.00,
        1,
        NULL,
        1,
        NULL,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY),
        100,
        0,
        1,
        0,
        DATE_SUB(NOW(), INTERVAL 30 DAY)
    );
-- ====================================
-- 7. 用户优惠券 (user_coupons)
-- ====================================
INSERT INTO user_coupons (
        user_id,
        coupon_id,
        coupon_code,
        status,
        received_at,
        used_at,
        order_no,
        expires_at
    )
VALUES -- 用户1的优惠券
    (
        1,
        1,
        'NEWUSER20251216001',
        0,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        NULL,
        NULL,
        DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 30 DAY)
    ),
    (
        1,
        2,
        'WEEKEND20251216001',
        0,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 5 DAY)
    ),
    (
        1,
        4,
        'VIP90OFF20251216001',
        1,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        'T20251216000001',
        DATE_ADD(
            DATE_SUB(NOW(), INTERVAL 10 DAY),
            INTERVAL 60 DAY
        )
    ),
    -- 用户2的优惠券
    (
        2,
        1,
        'NEWUSER20251216002',
        2,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    (
        2,
        3,
        'NATIONAL20251216001',
        0,
        DATE_SUB(NOW(), INTERVAL 8 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 22 DAY)
    ),
    (
        2,
        5,
        'VIP80OFF20251216001',
        0,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 87 DAY)
    ),
    -- 用户3的优惠券
    (
        3,
        2,
        'WEEKEND20251216002',
        0,
        NOW(),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 7 DAY)
    ),
    (
        3,
        3,
        'NATIONAL20251216002',
        1,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        'T20251216000003',
        DATE_ADD(NOW(), INTERVAL 25 DAY)
    ),
    (
        3,
        4,
        'VIP90OFF20251216002',
        0,
        DATE_SUB(NOW(), INTERVAL 15 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 45 DAY)
    ),
    -- 用户4的优惠券
    (
        4,
        1,
        'NEWUSER20251216003',
        0,
        NOW(),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 30 DAY)
    ),
    (
        4,
        6,
        'FLASH20251216001',
        0,
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 3 DAY)
    ),
    -- 用户5的优惠券
    (
        5,
        3,
        'NATIONAL20251216003',
        0,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 20 DAY)
    ),
    -- 用户6的优惠券
    (
        6,
        1,
        'NEWUSER20251216004',
        0,
        NOW(),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 30 DAY)
    ),
    (
        6,
        2,
        'WEEKEND20251216003',
        0,
        NOW(),
        NULL,
        NULL,
        DATE_ADD(NOW(), INTERVAL 7 DAY)
    );
-- ====================================
-- 验证插入结果
-- ====================================
SELECT '支付流水记录数：' AS info,
    COUNT(*) AS count
FROM payments;
SELECT '退款申请记录数：' AS info,
    COUNT(*) AS count
FROM order_refunds;
SELECT '用户钱包记录数：' AS info,
    COUNT(*) AS count
FROM user_wallet;
SELECT '钱包流水记录数：' AS info,
    COUNT(*) AS count
FROM wallet_transactions;
SELECT '用户积分记录数：' AS info,
    COUNT(*) AS count
FROM user_points;
SELECT '优惠券列表记录数：' AS info,
    COUNT(*) AS count
FROM coupons;
SELECT '用户优惠券记录数：' AS info,
    COUNT(*) AS count
FROM user_coupons;
-- ====================================
-- 数据统计查询
-- ====================================
-- 支付成功总金额
SELECT '支付成功总金额：' AS info,
    IFNULL(SUM(payment_amount), 0) AS total
FROM payments
WHERE status = 1;
-- 退款总金额
SELECT '退款成功总金额：' AS info,
    IFNULL(SUM(actual_refund), 0) AS total
FROM order_refunds
WHERE status = 3;
-- 用户钱包总余额
SELECT '用户钱包总余额：' AS info,
    IFNULL(SUM(balance), 0) AS total
FROM user_wallet
WHERE status = 1;
-- 用户积分总量（需要汇总）
SELECT '用户总可用积分：' AS info,
    IFNULL(
        SUM(
            CASE
                WHEN change_type = 1 THEN points
                WHEN change_type = 2 THEN - points
                WHEN change_type = 3 THEN - points
                ELSE 0
            END
        ),
        0
    ) AS total
FROM user_points
WHERE status = 1;
-- 优惠券发放数量
SELECT '优惠券已发放数量：' AS info,
    COUNT(*) AS count
FROM user_coupons;
-- 优惠券使用率
SELECT '优惠券使用率：' AS info,
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
    ) AS rate
FROM user_coupons;