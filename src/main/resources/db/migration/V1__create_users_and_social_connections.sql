-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(1024),
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    refresh_token VARCHAR(512),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- Create index on email
CREATE INDEX idx_user_email ON users (email);

-- User social connections table
CREATE TABLE user_social_connections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    access_token VARCHAR(1024),
    refresh_token VARCHAR(1024),
    token_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_social_connections_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_provider_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT uk_user_provider UNIQUE (user_id, provider)
);

-- Create indexes
CREATE INDEX idx_user_social_connections_user_id ON user_social_connections (user_id);
CREATE INDEX idx_user_social_connections_provider ON user_social_connections (provider);
