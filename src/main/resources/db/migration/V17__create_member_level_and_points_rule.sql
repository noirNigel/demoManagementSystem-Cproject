CREATE TABLE IF NOT EXISTS member_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    min_points INT NOT NULL DEFAULT 0,
    discount_rate DECIMAL(10,2) DEFAULT 1.00,
    points_multiplier DECIMAL(10,2) DEFAULT 1.00,
    birthday_benefit VARCHAR(255),
    benefits JSON NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 兼容已有 member_level 表缺少字段的情况
ALTER TABLE member_level
    ADD COLUMN IF NOT EXISTS benefits JSON NULL AFTER birthday_benefit,
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0 AFTER benefits,
    ADD COLUMN IF NOT EXISTS status TINYINT NOT NULL DEFAULT 1 AFTER sort_order;

CREATE TABLE IF NOT EXISTS points_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    earn_per_yuan INT NOT NULL DEFAULT 10,
    redeem_points INT NOT NULL DEFAULT 100,
    redeem_yuan DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO member_level(name, min_points, sort_order, status)
VALUES ('普通', 0, 1, 1),
       ('黄金', 2000, 2, 1),
       ('铂金', 4000, 3, 1),
       ('钻石', 6000, 4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO points_rule(earn_per_yuan, redeem_points, redeem_yuan, status)
VALUES (10, 100, 1.00, 1)
ON DUPLICATE KEY UPDATE earn_per_yuan = VALUES(earn_per_yuan);
