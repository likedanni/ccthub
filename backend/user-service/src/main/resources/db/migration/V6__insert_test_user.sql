-- 插入测试用户
-- 手机号: 13800138000
-- 密码: test123456 (BCrypt加密后)
-- 用户类型: 管理员测试账号
INSERT INTO users (
        phone,
        password,
        phone_encrypted,
        nickname,
        avatar_url,
        id_card_encrypted,
        real_name,
        member_level,
        growth_value,
        total_points,
        available_points,
        wallet_balance,
        payment_password,
        status,
        register_time,
        last_login_time,
        data_version
    )
VALUES (
        '13800138000',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        -- test123456
        'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        -- SHA-256(13800138000)
        '测试管理员',
        'https://avatars.githubusercontent.com/u/1?v=4',
        NULL,
        '张三',
        4,
        -- 钻石会员
        10000,
        5000,
        3000,
        1888.88,
        '$2a$10$rN9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        -- 支付密码: 888888
        'ACTIVE',
        NOW(),
        NOW(),
        1
    );
-- 插入普通用户测试账号
-- 手机号: 13900139000
-- 密码: user123456
INSERT INTO users (
        phone,
        password,
        phone_encrypted,
        nickname,
        avatar_url,
        member_level,
        growth_value,
        total_points,
        available_points,
        wallet_balance,
        status,
        register_time,
        data_version
    )
VALUES (
        '13900139000',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        -- user123456 (same as test123456)
        'f7c3bc1d808e04732adf679965ccc34ca7ae3441f7b10f3b64a6f59e93b8f1c0',
        '普通用户',
        'https://avatars.githubusercontent.com/u/2?v=4',
        1,
        -- 普通会员
        100,
        200,
        150,
        99.99,
        'ACTIVE',
        NOW(),
        1
    );