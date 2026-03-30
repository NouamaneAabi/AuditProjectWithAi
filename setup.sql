-- Setup script for Audit Backend Database
-- Run this after creating the PostgreSQL database 'audit_db'

-- Create the database (run this in psql or pgAdmin)
-- CREATE DATABASE audit_db;

-- Connect to audit_db and run the following inserts after the app has created tables

-- Insert a test admin user
-- Password: 'password' (BCrypt encoded)
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'admin@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'Admin',
    'User',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert a test auditor user
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'auditeur@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'Auditeur',
    'Test',
    'AUDITEUR',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert a test client user
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'client@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'Client',
    'Test',
    'CLIENT',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);