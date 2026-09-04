CREATE TABLE IF NOT EXISTS tbl_students (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    sex VARCHAR(20) NOT NULL CHECK (sex IN ('MALE', 'FEMALE')),
    nationality VARCHAR(100) NOT NULL,
    sub_city VARCHAR(100) NOT NULL,
    kebele INTEGER,
    house_number VARCHAR(50),
    mobile_number VARCHAR(20) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_students_mobile_number ON tbl_students (mobile_number);
CREATE INDEX IF NOT EXISTS idx_students_last_first_name ON tbl_students (last_name, first_name);
