-- Create admin table to support backend authentication
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    status TINYINT DEFAULT 1,
    store_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed a default administrator account for development login
INSERT INTO admin (username, password, role, status)
SELECT 'admin', 'password', 'ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');
