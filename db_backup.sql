-- ============================================================
-- Doctor Appointment App — Database Seed
-- Run: mysql -u root -p docappdb < db_backup.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS docappdb;
USE docappdb;

-- BCrypt hash of "Admin@123"
INSERT IGNORE INTO users (name, email, password, role, enabled, created_at) VALUES
('Admin User',   'admin@docapp.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN',   true, NOW()),
('Dr. Sarah Lee', 'sarah@docapp.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR',  true, NOW()),
('Dr. James Park', 'james@docapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR',  true, NOW()),
('Dr. Aisha Noor', 'aisha@docapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR',  true, NOW()),
('John Patient',  'john@example.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', true, NOW());

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience, consultation_fee, phone, bio, available) VALUES
(2, 'Cardiology',   'MBBS, MD (Cardiology)',    12, 150.00, '+1-555-0101', 'Specialist in heart diseases and cardiovascular conditions with 12 years experience.', true),
(3, 'Neurology',    'MBBS, DM (Neurology)',     8,  180.00, '+1-555-0102', 'Expert in neurological disorders including migraines, epilepsy and stroke management.', true),
(4, 'Pediatrics',   'MBBS, MD (Pediatrics)',    6,  120.00, '+1-555-0103', 'Dedicated to the health and well-being of children from birth through adolescence.', true);

INSERT IGNORE INTO patients (user_id, phone, date_of_birth, gender, blood_group, address) VALUES
(5, '+1-555-9001', '1990-05-15', 'Male', 'O+', '123 Main Street, Springfield, IL');
