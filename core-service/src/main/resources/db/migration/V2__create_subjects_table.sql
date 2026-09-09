CREATE SCHEMA IF NOT EXISTS public;
CREATE TABLE subjects (
    id            UUID PRIMARY KEY,
    subject_code  VARCHAR(20) NOT NULL UNIQUE,
    subject_name  VARCHAR(100) NOT NULL,
    credit_hours  INTEGER NOT NULL,
    grade_level   INTEGER NOT NULL,
    status        VARCHAR(20) NOT NULL
);