-- TimeCapsule Database Initialization Script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS timecapsule;
USE timecapsule;

-- Create user for the application
CREATE USER IF NOT EXISTS 'timecapsule'@'%' IDENTIFIED BY 'timecapsule123';
GRANT ALL PRIVILEGES ON timecapsule.* TO 'timecapsule'@'%';

-- Enable event scheduler for MySQL events
SET GLOBAL event_scheduler = ON;

-- Create tables (will be created by JPA, but keeping for reference)
-- These are commented out as JPA will handle table creation

/*
-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Capsules table
CREATE TABLE IF NOT EXISTS capsules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_status (status)
);

-- Capsule contents table
CREATE TABLE IF NOT EXISTS capsule_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capsule_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    alias VARCHAR(50),
    main_message LONGTEXT NOT NULL,
    FOREIGN KEY (capsule_id) REFERENCES capsules(id) ON DELETE CASCADE,
    UNIQUE KEY uk_capsule_content (capsule_id)
);

-- Capsule themes table
CREATE TABLE IF NOT EXISTS capsule_themes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capsule_id BIGINT NOT NULL,
    theme_type VARCHAR(30) NOT NULL,
    theme_metadata TEXT,
    FOREIGN KEY (capsule_id) REFERENCES capsules(id) ON DELETE CASCADE,
    UNIQUE KEY uk_capsule_theme (capsule_id)
);

-- Capsule recipients table
CREATE TABLE IF NOT EXISTS capsule_recipients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capsule_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    invited_at TIMESTAMP NOT NULL,
    FOREIGN KEY (capsule_id) REFERENCES capsules(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_capsule_user (capsule_id, user_id)
);

-- Attachments table
CREATE TABLE IF NOT EXISTS attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capsule_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (capsule_id) REFERENCES capsules(id) ON DELETE CASCADE,
    INDEX idx_capsule_id (capsule_id)
);

-- Delivery logs table
CREATE TABLE IF NOT EXISTS delivery_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capsule_id BIGINT NOT NULL,
    capsule_status VARCHAR(20) NOT NULL,
    attempted_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (capsule_id) REFERENCES capsules(id) ON DELETE CASCADE,
    INDEX idx_capsule_id (capsule_id),
    INDEX idx_attempted_at (attempted_at)
);
*/

-- Insert sample data for testing (optional)
-- This will be handled by the application

FLUSH PRIVILEGES;