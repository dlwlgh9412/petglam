CREATE TABLE tb_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE ,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(1024),
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    refresh_token VARCHAR(512)
);

CREATE INDEX idx_user_email ON tb_users (email);

CREATE TABLE tb_user_social_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    access_token VARCHAR(1024),
    refresh_token VARCHAR(1024),
    token_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_provider_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT uk_user_provider UNIQUE (user_id, provider)
);

-- Create indexes
CREATE INDEX idx_user_social_connections_user_id ON tb_user_social_accounts (user_id);
CREATE INDEX idx_user_social_connections_provider ON tb_user_social_accounts (provider);
