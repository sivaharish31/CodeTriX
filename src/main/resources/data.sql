-- Initial Data for CodeTriX
-- This file runs automatically on startup

-- Insert roles if they don't exist
INSERT INTO roles (name, description)
SELECT 'ADMIN', 'Administrator'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description)
SELECT 'TEAM', 'Competition Team'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'TEAM');

-- Insert admin user if doesn't exist
-- Username: admin
-- Password: admin123
INSERT INTO users (username, password, display_name, role_id, enabled, created_at, updated_at)
SELECT
    'admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4eG5hSaqIxJ5Kpe2',
    'Administrator',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Create default event if doesn't exist
INSERT INTO events (name, status, total_duration_seconds, created_at)
SELECT
    'CodeTriX Competition 2024',
    'NOT_STARTED',
    2700,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events);

-- Create rounds for the event if they don't exist
INSERT INTO rounds (event_id, round_number, round_type, duration_seconds, status)
SELECT
    (SELECT id FROM events LIMIT 1),
    1,
    'CODING',
    900,
    'LOCKED'
WHERE NOT EXISTS (SELECT 1 FROM rounds WHERE round_number = 1);

INSERT INTO rounds (event_id, round_number, round_type, duration_seconds, status)
SELECT
    (SELECT id FROM events LIMIT 1),
    2,
    'DEBUGGING',
    900,
    'LOCKED'
WHERE NOT EXISTS (SELECT 1 FROM rounds WHERE round_number = 2);

INSERT INTO rounds (event_id, round_number, round_type, duration_seconds, status)
SELECT
    (SELECT id FROM events LIMIT 1),
    3,
    'CTF',
    900,
    'LOCKED'
WHERE NOT EXISTS (SELECT 1 FROM rounds WHERE round_number = 3);
