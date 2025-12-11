CREATE DATABASE IF NOT EXISTS management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE management_db;

-- 员工/用户
CREATE TABLE IF NOT EXISTS sys_user (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL UNIQUE,
                                        password VARCHAR(255) NOT NULL,
                                        fullname VARCHAR(100),
                                        role VARCHAR(50), -- e.g., ADMIN, MANAGER, STAFF
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色权限简表（可扩展）
CREATE TABLE IF NOT EXISTS sys_role (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        role_name VARCHAR(50) NOT NULL UNIQUE,
                                        description VARCHAR(255)
);

-- 商品表
CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(200) NOT NULL,
                                       sku VARCHAR(100),
                                       price DECIMAL(10,2) NOT NULL DEFAULT 0,
                                       cost DECIMAL(10,2) DEFAULT 0,
                                       stock INT DEFAULT 0,
                                       status TINYINT DEFAULT 1, -- 1: 上架, 0: 下架
                                       category_id BIGINT,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 分类
CREATE TABLE IF NOT EXISTS category (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        parent_id BIGINT DEFAULT NULL,
                                        name VARCHAR(100) NOT NULL,
                                        sort_order INT DEFAULT 0
);

-- 订单主表
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_no VARCHAR(64) NOT NULL UNIQUE,
                                      user_openid VARCHAR(128),
                                      total_amount DECIMAL(10,2) DEFAULT 0,
                                      status VARCHAR(30) DEFAULT 'NEW', -- NEW, PAID, CANCELLED, REFUNDED, COMPLETED
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单明细
CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          order_id BIGINT NOT NULL,
                                          product_id BIGINT,
                                          product_name VARCHAR(200),
                                          price DECIMAL(10,2),
                                          quantity INT DEFAULT 1
);

-- 会员表
CREATE TABLE IF NOT EXISTS member (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      openid VARCHAR(128) UNIQUE,
                                      phone VARCHAR(20),
                                      nickname VARCHAR(100),
                                      level INT DEFAULT 1,
                                      total_spent DECIMAL(10,2) DEFAULT 0
);

-- 日志/操作记录
CREATE TABLE IF NOT EXISTS operation_log (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT,
                                             action VARCHAR(255),
                                             ip VARCHAR(50),
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
