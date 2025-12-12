-- 添加支付密码字段
ALTER TABLE users
ADD COLUMN payment_password VARCHAR(255) COMMENT '支付密码(加密)';