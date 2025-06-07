CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE otp_config (
    id BOOLEAN PRIMARY KEY CHECK (id),
    code_length INTEGER NOT NULL,
    ttl_seconds INTEGER NOT NULL
);

CREATE TABLE otp_codes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    operation_id TEXT,
    code TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED'))
);
