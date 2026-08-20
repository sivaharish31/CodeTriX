-- CodeTriX Database Schema
-- PostgreSQL Database Schema

-- Roles table (for authentication)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- Users table (for admin users)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    email VARCHAR(100),
    role_id BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Teams table (for team registration and management)
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    team_code VARCHAR(20) NOT NULL UNIQUE,
    team_name VARCHAR(100) NOT NULL UNIQUE,
    login_pin_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team members table
CREATE TABLE IF NOT EXISTS team_members (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    college VARCHAR(150) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

-- Events table (for event management and timer)
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE,
    end_time TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    total_duration_seconds INTEGER NOT NULL DEFAULT 2700,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rounds table (for round management)
CREATE TABLE IF NOT EXISTS rounds (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    round_number INTEGER NOT NULL,
    round_type VARCHAR(20) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE,
    end_time TIMESTAMP WITH TIME ZONE,
    duration_seconds INTEGER NOT NULL DEFAULT 900,
    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
    CONSTRAINT fk_round_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT uk_event_round UNIQUE (event_id, round_number)
);

-- Coding problems table
CREATE TABLE IF NOT EXISTS coding_problems (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    constraints TEXT,
    input_format TEXT,
    output_format TEXT,
    points INTEGER NOT NULL,
    time_limit_ms INTEGER NOT NULL DEFAULT 2000,
    memory_limit_mb INTEGER NOT NULL DEFAULT 256,
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    display_order INTEGER DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Test cases table
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_sample BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER DEFAULT 0,
    explanation TEXT,
    CONSTRAINT fk_testcase_problem FOREIGN KEY (problem_id) REFERENCES coding_problems(id) ON DELETE CASCADE
);

-- Submissions table
CREATE TABLE IF NOT EXISTS submissions (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    team_code VARCHAR(20) NOT NULL,
    problem_id BIGINT NOT NULL,
    language VARCHAR(10) NOT NULL,
    source_code TEXT NOT NULL,
    submission_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    tests_passed INTEGER DEFAULT 0,
    total_tests INTEGER DEFAULT 0,
    points_earned INTEGER DEFAULT 0,
    execution_time_ms INTEGER,
    memory_used_kb INTEGER,
    compile_output TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_submission_problem FOREIGN KEY (problem_id) REFERENCES coding_problems(id)
);

-- Debugging problems table
CREATE TABLE IF NOT EXISTS debugging_problems (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    buggy_code TEXT NOT NULL,
    language VARCHAR(10) NOT NULL,
    points INTEGER NOT NULL,
    time_limit_ms INTEGER NOT NULL DEFAULT 2000,
    memory_limit_mb INTEGER NOT NULL DEFAULT 256,
    hint TEXT,
    display_order INTEGER DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Debugging test cases table
CREATE TABLE IF NOT EXISTS debugging_test_cases (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_sample BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER DEFAULT 0,
    explanation TEXT,
    CONSTRAINT fk_debug_testcase_problem FOREIGN KEY (problem_id) REFERENCES debugging_problems(id) ON DELETE CASCADE
);

-- Debugging submissions table
CREATE TABLE IF NOT EXISTS debugging_submissions (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    team_code VARCHAR(20) NOT NULL,
    problem_id BIGINT NOT NULL,
    language VARCHAR(10) NOT NULL,
    source_code TEXT NOT NULL,
    submission_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    tests_passed INTEGER DEFAULT 0,
    total_tests INTEGER DEFAULT 0,
    points_earned INTEGER DEFAULT 0,
    execution_time_ms INTEGER,
    memory_used_kb INTEGER,
    compile_output TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_debug_submission_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_debug_submission_problem FOREIGN KEY (problem_id) REFERENCES debugging_problems(id)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_teams_team_code ON teams(team_code);
CREATE INDEX IF NOT EXISTS idx_teams_status ON teams(status);
CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members(team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_roll_number ON team_members(roll_number);
CREATE INDEX IF NOT EXISTS idx_events_status ON events(status);
CREATE INDEX IF NOT EXISTS idx_rounds_event_id ON rounds(event_id);
CREATE INDEX IF NOT EXISTS idx_rounds_status ON rounds(status);
CREATE INDEX IF NOT EXISTS idx_problems_enabled ON coding_problems(enabled);
CREATE INDEX IF NOT EXISTS idx_testcases_problem_id ON test_cases(problem_id);
CREATE INDEX IF NOT EXISTS idx_submissions_team_id ON submissions(team_id);
CREATE INDEX IF NOT EXISTS idx_submissions_problem_id ON submissions(problem_id);
CREATE INDEX IF NOT EXISTS idx_submissions_time ON submissions(submission_time);
CREATE INDEX IF NOT EXISTS idx_submissions_status ON submissions(status);
CREATE INDEX IF NOT EXISTS idx_debug_problems_enabled ON debugging_problems(enabled);
CREATE INDEX IF NOT EXISTS idx_debug_testcases_problem_id ON debugging_test_cases(problem_id);
CREATE INDEX IF NOT EXISTS idx_debug_submissions_team_id ON debugging_submissions(team_id);
CREATE INDEX IF NOT EXISTS idx_debug_submissions_problem_id ON debugging_submissions(problem_id);
CREATE INDEX IF NOT EXISTS idx_debug_submissions_time ON debugging_submissions(submission_time);
