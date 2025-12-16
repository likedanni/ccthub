-- ====================================
-- 测试数据插入脚本
-- 作者: CCTHub
-- 日期: 2025-12-16
-- 说明: 为支付流水、退款申请、用户钱包、钱包流水、用户积分、积分流水、优惠券列表、用户优惠券表插入测试数据
-- ====================================
USE `cct-hub`;
-- ====================================
-- 1. 支付流水 (payment_transactions)
-- ====================================
INSERT INTO payment_transactions (
        transaction_no,
        order_no,
        user_id,
        amount,
        payment_method,
        payment_channel,
        transaction_status,
        payment_time,
        refund_amount,
        create_time,
        update_time
    )
VALUES -- 微信支付成功记录
    (
        'PAY202512160001',
        'T20251216000001',
        1,
        196.00,
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        1,
        NOW(),
        0.00,
        NOW(),
        NOW()
    ),
    (
        'PAY202512160002',
        'T20251216000002',
        2,
        98.00,
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        1,
        NOW(),
        0.00,
        NOW(),
        NOW()
    ),
    -- 支付宝支付成功记录
    (
        'PAY202512160003',
        'T20251216000003',
        3,
        294.00,
        'ALIPAY',
        'ALIPAY_APP',
        1,
        NOW(),
        0.00,
        NOW(),
        NOW()
    ),
    -- 待支付记录
    (
        'PAY202512160004',
        'T20251216000004',
        1,
        150.00,
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        0,
        NULL,
        0.00,
        NOW(),
        NOW()
    ),
    -- 已退款记录
    (
        'PAY202512160005',
        'T20251216000005',
        2,
        200.00,
        'WECHAT',
        'WECHAT_MINI_PROGRAM',
        1,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        200.00,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        NOW()
    ),
    -- 积分支付记录
    (
        'PAY202512160006',
        'T20251216000006',
        4,
        50.00,
        'POINTS',
        'POINTS',
        1,
        NOW(),
        0.00,
        NOW(),
        NOW()
    ),
    -- 混合支付记录（微信+积分）
    (
        'PAY202512160007',
        'T20251216000007',
        1,
        180.00,
        'MIXED',
        'WECHAT_MINI_PROGRAM',
        1,
        NOW(),
        0.00,
        NOW(),
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
        refund_status,
        apply_time,
        audit_time,
        refund_time,
        refund_transaction_no,
        reject_reason,
        create_time,
        update_time
    )
VALUES -- 待审核的全额退款
    (
        'RF202512160001',
        'T20251216000005',
        2,
        1,
        200.00,
        0.00,
        '行程取消，无法前往',
        0,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NULL,
        NULL,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        DATE_SUB(NOW(), INTERVAL 1 HOUR)
    ),
    -- 审核通过待退款
    (
        'RF202512160002',
        'T20251216000008',
        3,
        1,
        150.00,
        0.00,
        '临时有事无法前往',
        1,
        DATE_SUB(NOW(), INTERVAL 2 HOUR),
        DATE_SUB(NOW(), INTERVAL 1 HOUR),
        NULL,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 2 HOUR),
        DATE_SUB(NOW(), INTERVAL 1 HOUR)
    ),
    -- 退款成功（全额）
    (
        'RF202512160003',
        'T20251216000009',
        1,
        1,
        196.00,
        0.00,
        '景区关闭',
        3,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        'REFUND_WX_123456',
        NULL,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    -- 退款成功（部分，扣除手续费）
    (
        'RF202512160004',
        'T20251216000010',
        4,
        2,
        88.20,
        9.80,
        '游客临时不能参加',
        3,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        'REFUND_WX_123457',
        NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY)
    ),
    -- 审核拒绝
    (
        'RF202512160005',
        'T20251216000011',
        2,
        1,
        98.00,
        0.00,
        '不想去了',
        4,
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        NULL,
        NULL,
        '不符合退款政策：已过退款期限',
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    -- 退款失败
    (
        'RF202512160006',
        'T20251216000012',
        3,
        1,
        120.00,
        0.00,
        '天气原因',
        5,
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY),
        NULL,
        NULL,
        '退款渠道异常，请联系客服',
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY)
    );
-- ====================================
-- 3. 用户钱包 (user_wallets)
-- ====================================
INSERT INTO user_wallets (
        user_id,
        balance,
        total_income,
        total_expense,
        frozen_amount,
        wallet_status,
        create_time,
        update_time
    )
VALUES -- 正常使用的钱包
    (
        1,
        500.00,
        1500.00,
        1000.00,
        0.00,
        1,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        NOW()
    ),
    (
        2,
        280.50,
        500.00,
        219.50,
        20.00,
        1,
        DATE_SUB(NOW(), INTERVAL 60 DAY),
        NOW()
    ),
    (
        3,
        1250.00,
        2000.00,
        750.00,
        0.00,
        1,
        DATE_SUB(NOW(), INTERVAL 90 DAY),
        NOW()
    ),
    (
        4,
        0.00,
        300.00,
        300.00,
        0.00,
        1,
        DATE_SUB(NOW(), INTERVAL 15 DAY),
        NOW()
    ),
    -- 冻结的钱包
    (
        5,
        800.00,
        1000.00,
        200.00,
        800.00,
        0,
        DATE_SUB(NOW(), INTERVAL 120 DAY),
        NOW()
    ),
    -- 新用户钱包
    (6, 100.00, 100.00, 0.00, 0.00, 1, NOW(), NOW());
-- ====================================
-- 4. 钱包流水 (wallet_transactions)
-- ====================================
INSERT INTO wallet_transactions (
        transaction_no,
        user_id,
        transaction_type,
        amount,
        balance_after,
        related_order_no,
        related_refund_no,
        description,
        create_time
    )
VALUES -- 用户1的钱包流水
    (
        'WT202512160001',
        1,
        1,
        200.00,
        500.00,
        'T20251216000013',
        NULL,
        '订单退款到账',
        DATE_SUB(NOW(), INTERVAL 1 DAY)
    ),
    (
        'WT202512160002',
        1,
        2,
        50.00,
        450.00,
        'T20251216000014',
        NULL,
        '订单消费',
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        'WT202512160003',
        1,
        1,
        100.00,
        550.00,
        NULL,
        NULL,
        '系统充值',
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    -- 用户2的钱包流水
    (
        'WT202512160004',
        2,
        1,
        100.00,
        280.50,
        'T20251216000015',
        'RF202512160003',
        '退款到账',
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    ),
    (
        'WT202512160005',
        2,
        2,
        98.00,
        180.50,
        'T20251216000016',
        NULL,
        '购买门票',
        DATE_SUB(NOW(), INTERVAL 7 DAY)
    ),
    (
        'WT202512160006',
        2,
        3,
        20.00,
        280.50,
        NULL,
        NULL,
        '资金冻结',
        DATE_SUB(NOW(), INTERVAL 1 HOUR)
    ),
    -- 用户3的钱包流水
    (
        'WT202512160007',
        3,
        1,
        500.00,
        1250.00,
        NULL,
        NULL,
        '充值',
        DATE_SUB(NOW(), INTERVAL 10 DAY)
    ),
    (
        'WT202512160008',
        3,
        2,
        150.00,
        750.00,
        'T20251216000017',
        NULL,
        '订单支付',
        DATE_SUB(NOW(), INTERVAL 15 DAY)
    ),
    -- 用户4的钱包流水
    (
        'WT202512160009',
        4,
        1,
        300.00,
        300.00,
        NULL,
        NULL,
        '新用户奖励',
        DATE_SUB(NOW(), INTERVAL 15 DAY)
    ),
    (
        'WT202512160010',
        4,
        2,
        300.00,
        0.00,
        'T20251216000018',
        NULL,
        '购票消费',
        DATE_SUB(NOW(), INTERVAL 14 DAY)
    );
-- ====================================
-- 5. 用户积分 (user_points)
-- ====================================
INSERT INTO user_points (
        user_id,
        available_points,
        total_earned,
        total_spent,
        frozen_points,
        points_level,
        create_time,
        update_time
    )
VALUES -- 活跃用户
    (
        1,
        1500,
        3000,
        1500,
        0,
        3,
        DATE_SUB(NOW(), INTERVAL 180 DAY),
        NOW()
    ),
    (
        2,
        500,
        1200,
        700,
        100,
        2,
        DATE_SUB(NOW(), INTERVAL 150 DAY),
        NOW()
    ),
    (
        3,
        2800,
        5000,
        2200,
        0,
        4,
        DATE_SUB(NOW(), INTERVAL 365 DAY),
        NOW()
    ),
    -- 普通用户
    (
        4,
        100,
        300,
        200,
        0,
        1,
        DATE_SUB(NOW(), INTERVAL 60 DAY),
        NOW()
    ),
    (
        5,
        0,
        500,
        500,
        0,
        1,
        DATE_SUB(NOW(), INTERVAL 90 DAY),
        NOW()
    ),
    -- 新用户
    (6, 200, 200, 0, 0, 1, NOW(), NOW());
-- ====================================
-- 6. 积分流水 (point_transactions)
-- ====================================
INSERT INTO point_transactions (
        transaction_no,
        user_id,
        transaction_type,
        points,
        balance_after,
        related_order_no,
        expire_time,
        description,
        create_time
    )
VALUES -- 用户1的积分流水
    (
        'PT202512160001',
        1,
        1,
        100,
        1500,
        'T20251216000001',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '购票赠送积分',
        DATE_SUB(NOW(), INTERVAL 1 DAY)
    ),
    (
        'PT202512160002',
        1,
        2,
        50,
        1450,
        'T20251216000019',
        NULL,
        '积分抵扣',
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        'PT202512160003',
        1,
        1,
        200,
        1650,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '签到奖励',
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    (
        'PT202512160004',
        1,
        4,
        150,
        1500,
        NULL,
        NULL,
        '积分过期',
        DATE_SUB(NOW(), INTERVAL 10 DAY)
    ),
    -- 用户2的积分流水
    (
        'PT202512160005',
        2,
        1,
        50,
        500,
        'T20251216000002',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '消费赠送',
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    ),
    (
        'PT202512160006',
        2,
        2,
        100,
        400,
        'T20251216000020',
        NULL,
        '积分兑换优惠券',
        DATE_SUB(NOW(), INTERVAL 7 DAY)
    ),
    (
        'PT202512160007',
        2,
        3,
        100,
        500,
        NULL,
        NULL,
        '积分冻结',
        DATE_SUB(NOW(), INTERVAL 1 HOUR)
    ),
    -- 用户3的积分流水
    (
        'PT202512160008',
        3,
        1,
        300,
        2800,
        'T20251216000003',
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '大额消费奖励',
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    (
        'PT202512160009',
        3,
        1,
        500,
        2300,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '活动奖励',
        DATE_SUB(NOW(), INTERVAL 30 DAY)
    ),
    -- 用户4的积分流水
    (
        'PT202512160010',
        4,
        1,
        100,
        100,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '新用户注册奖励',
        DATE_SUB(NOW(), INTERVAL 60 DAY)
    ),
    -- 用户6的积分流水
    (
        'PT202512160011',
        6,
        1,
        200,
        200,
        NULL,
        DATE_ADD(NOW(), INTERVAL 365 DAY),
        '新用户福利',
        NOW()
    );
-- ====================================
-- 7. 优惠券列表 (coupons)
-- ====================================
INSERT INTO coupons (
        coupon_name,
        coupon_type,
        discount_type,
        discount_value,
        min_order_amount,
        max_discount_amount,
        total_quantity,
        remaining_quantity,
        valid_days,
        start_time,
        end_time,
        coupon_status,
        applicable_products,
        description,
        create_time,
        update_time
    )
VALUES -- 满减券
    (
        '新用户专享券',
        1,
        1,
        20.00,
        100.00,
        20.00,
        1000,
        850,
        30,
        NOW(),
        DATE_ADD(NOW(), INTERVAL 90 DAY),
        1,
        'ALL',
        '新用户注册赠送，满100减20',
        NOW(),
        NOW()
    ),
    (
        '周末出游券',
        1,
        1,
        30.00,
        200.00,
        30.00,
        500,
        320,
        7,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_ADD(NOW(), INTERVAL 60 DAY),
        1,
        'TICKET',
        '周末专享，满200减30',
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        NOW()
    ),
    (
        '国庆特惠券',
        1,
        1,
        50.00,
        300.00,
        50.00,
        2000,
        1200,
        14,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        1,
        'ALL',
        '国庆活动，满300减50',
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        NOW()
    ),
    -- 折扣券
    (
        '景区通用9折券',
        2,
        2,
        9.00,
        50.00,
        100.00,
        800,
        600,
        60,
        NOW(),
        DATE_ADD(NOW(), INTERVAL 120 DAY),
        1,
        'TICKET',
        '所有景区门票9折优惠',
        NOW(),
        NOW()
    ),
    (
        'VIP专享8折券',
        2,
        2,
        8.00,
        100.00,
        200.00,
        300,
        180,
        90,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_ADD(NOW(), INTERVAL 180 DAY),
        1,
        'ALL',
        'VIP会员专享8折',
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        NOW()
    ),
    -- 即将过期的券
    (
        '限时抢购券',
        1,
        1,
        15.00,
        80.00,
        15.00,
        500,
        100,
        3,
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_ADD(NOW(), INTERVAL 3 DAY),
        1,
        'TICKET',
        '限时抢购，满80减15',
        DATE_SUB(NOW(), INTERVAL 7 DAY),
        NOW()
    ),
    -- 已下架的券
    (
        '已过期测试券',
        1,
        1,
        10.00,
        50.00,
        10.00,
        100,
        0,
        7,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY),
        0,
        'ALL',
        '已过期的优惠券',
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY)
    );
-- ====================================
-- 8. 用户优惠券 (user_coupons)
-- ====================================
INSERT INTO user_coupons (
        user_id,
        coupon_id,
        coupon_code,
        receive_time,
        expire_time,
        use_status,
        use_time,
        order_no,
        create_time,
        update_time
    )
VALUES -- 用户1的优惠券
    (
        1,
        1,
        'NEWUSER20251216001',
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_ADD(NOW(), INTERVAL 25 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    ),
    (
        1,
        2,
        'WEEKEND20251216001',
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_ADD(NOW(), INTERVAL 5 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        1,
        4,
        'VIP90OFF20251216001',
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_ADD(NOW(), INTERVAL 50 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        'T20251216000001',
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    ),
    -- 用户2的优惠券
    (
        2,
        1,
        'NEWUSER20251216002',
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        2,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 30 DAY),
        NOW()
    ),
    (
        2,
        3,
        'NATIONAL20251216001',
        DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_ADD(NOW(), INTERVAL 22 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)
    ),
    (
        2,
        5,
        'VIP80OFF20251216001',
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_ADD(NOW(), INTERVAL 87 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    ),
    -- 用户3的优惠券
    (
        3,
        2,
        'WEEKEND20251216002',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 7 DAY),
        0,
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        3,
        3,
        'NATIONAL20251216002',
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_ADD(NOW(), INTERVAL 25 DAY),
        1,
        DATE_SUB(NOW(), INTERVAL 2 DAY),
        'T20251216000003',
        DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        3,
        4,
        'VIP90OFF20251216002',
        DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_ADD(NOW(), INTERVAL 45 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 15 DAY)
    ),
    -- 用户4的优惠券
    (
        4,
        1,
        'NEWUSER20251216003',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        0,
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        4,
        6,
        'FLASH20251216001',
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_ADD(NOW(), INTERVAL 3 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY)
    ),
    -- 用户5的优惠券
    (
        5,
        3,
        'NATIONAL20251216003',
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_ADD(NOW(), INTERVAL 20 DAY),
        0,
        NULL,
        NULL,
        DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 10 DAY)
    ),
    -- 用户6的优惠券
    (
        6,
        1,
        'NEWUSER20251216004',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        0,
        NULL,
        NULL,
        NOW(),
        NOW()
    ),
    (
        6,
        2,
        'WEEKEND20251216003',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 7 DAY),
        0,
        NULL,
        NULL,
        NOW(),
        NOW()
    );
-- ====================================
-- 验证插入结果
-- ====================================
SELECT '支付流水记录数：',
    COUNT(*)
FROM payment_transactions;
SELECT '退款申请记录数：',
    COUNT(*)
FROM order_refunds;
SELECT '用户钱包记录数：',
    COUNT(*)
FROM user_wallets;
SELECT '钱包流水记录数：',
    COUNT(*)
FROM wallet_transactions;
SELECT '用户积分记录数：',
    COUNT(*)
FROM user_points;
SELECT '积分流水记录数：',
    COUNT(*)
FROM point_transactions;
SELECT '优惠券列表记录数：',
    COUNT(*)
FROM coupons;
SELECT '用户优惠券记录数：',
    COUNT(*)
FROM user_coupons;
-- ====================================
-- 数据统计查询
-- ====================================
-- 支付成功总金额
SELECT '支付成功总金额：',
    SUM(amount)
FROM payment_transactions
WHERE payment_status = 1;
-- 退款总金额
SELECT '退款成功总金额：',
    SUM(refund_amount)
FROM order_refunds
WHERE refund_status = 3;
-- 用户钱包总余额
SELECT '用户钱包总余额：',
    SUM(balance)
FROM user_wallets
WHERE wallet_status = 1;
-- 用户积分总量
SELECT '用户可用积分总量：',
    SUM(available_points)
FROM user_points;
-- 优惠券发放数量
SELECT '优惠券已发放数量：',
    COUNT(*)
FROM user_coupons;
-- 优惠券使用率
SELECT '优惠券使用率：',
    CONCAT(
        ROUND(
            SUM(
                CASE
                    WHEN use_status = 1 THEN 1
                    ELSE 0
                END
            ) * 100.0 / COUNT(*),
            2
        ),
        '%'
    )
FROM user_coupons;