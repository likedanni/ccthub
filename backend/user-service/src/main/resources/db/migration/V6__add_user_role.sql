-- 添加用户角色字段
ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色: ADMIN-管理员, USER-普通用户'
AFTER status;
-- 更新测试管理员为ADMIN角色
UPDATE users
SET role = 'ADMIN'
WHERE id = 10;