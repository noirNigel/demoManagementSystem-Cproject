CREATE TABLE IF NOT EXISTS marketing_banner (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image_url TEXT NOT NULL,
    link_url TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status INT DEFAULT 1,
    sort_order INT DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
