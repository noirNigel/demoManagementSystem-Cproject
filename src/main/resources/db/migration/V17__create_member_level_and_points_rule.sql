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

CREATE TABLE IF NOT EXISTS points_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    earn_per_yuan INT NOT NULL DEFAULT 10,
    redeem_points INT NOT NULL DEFAULT 100,
    redeem_yuan DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Align existing member_level columns without depending on ADD COLUMN IF NOT EXISTS
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'min_points'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN min_points INT NOT NULL DEFAULT 0'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'discount_rate'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN discount_rate DECIMAL(10,2) DEFAULT 1.00'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'points_multiplier'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN points_multiplier DECIMAL(10,2) DEFAULT 1.00'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'birthday_benefit'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN birthday_benefit VARCHAR(255)'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'benefits'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN benefits JSON NULL'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'sort_order'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN sort_order INT NOT NULL DEFAULT 0'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'status'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN status TINYINT NOT NULL DEFAULT 1'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'created_at'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'member_level' AND column_name = 'updated_at'
        ),
        'SELECT 1',
        'ALTER TABLE member_level ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

-- Align existing points_rule columns
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'earn_per_yuan'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN earn_per_yuan INT NOT NULL DEFAULT 10'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'redeem_points'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN redeem_points INT NOT NULL DEFAULT 100'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'redeem_yuan'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN redeem_yuan DECIMAL(10,2) NOT NULL DEFAULT 1.00'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'status'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN status TINYINT NOT NULL DEFAULT 1'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'created_at'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'points_rule' AND column_name = 'updated_at'
        ),
        'SELECT 1',
        'ALTER TABLE points_rule ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

INSERT INTO member_level(name, min_points, sort_order, status)
VALUES ('普通', 0, 1, 1),
       ('黄金', 2000, 2, 1),
       ('铂金', 4000, 3, 1),
       ('钻石', 6000, 4, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO points_rule(earn_per_yuan, redeem_points, redeem_yuan, status)
VALUES (10, 100, 1.00, 1)
ON DUPLICATE KEY UPDATE earn_per_yuan = VALUES(earn_per_yuan);
