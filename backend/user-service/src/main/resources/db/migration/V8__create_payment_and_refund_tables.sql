-- 创建支付流水表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID，主键自增',
    payment_no VARCHAR(32) UNIQUE NOT NULL COMMENT '支付系统流水号',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    payment_type VARCHAR(20) NOT NULL COMMENT '支付类型: wechat, alipay, balance',
    payment_channel VARCHAR(50) NOT NULL COMMENT '支付渠道: miniapp, app, h5, native',
    payment_amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-成功, 2-失败, 3-关闭, 4-处理中',
    third_party_no VARCHAR(64) COMMENT '第三方支付平台流水号',
    payer_id VARCHAR(100) COMMENT '支付方标识（如微信openid）',
    payment_time DATETIME COMMENT '支付时间',
    callback_time DATETIME COMMENT '支付回调时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_no (order_no),
    INDEX idx_payment_no (payment_no),
    INDEX idx_third_party_no (third_party_no),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付流水表';

-- 创建订单退款表
CREATE TABLE IF NOT EXISTS order_refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款ID，主键自增',
    refund_no VARCHAR(32) UNIQUE NOT NULL COMMENT '退款单号',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    refund_type TINYINT NOT NULL COMMENT '退款类型: 1-全额退款, 2-部分退款',
    refund_amount DECIMAL(10, 2) NOT NULL COMMENT '退款金额',
    refund_reason VARCHAR(200) NOT NULL COMMENT '退款原因',
    refund_evidence JSON COMMENT '退款凭证图片等，JSON格式',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-审核通过, 2-审核拒绝, 3-退款中, 4-成功, 5-失败',
    auditor_id BIGINT COMMENT '审核人ID',
    audited_at DATETIME COMMENT '审核时间',
    audit_note VARCHAR(500) COMMENT '审核备注',
    payment_refund_no VARCHAR(64) COMMENT '支付渠道退款流水号',
    payment_refund_at DATETIME COMMENT '支付渠道退款时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_refund_no (refund_no),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单退款申请与处理表';
