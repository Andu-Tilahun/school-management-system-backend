CREATE TABLE IF NOT EXISTS tbl_emergency_contacts (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES tbl_students (id),
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    nationality VARCHAR(100) NOT NULL,
    sub_city VARCHAR(100) NOT NULL,
    kebele INTEGER,
    house_number VARCHAR(50),
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_emergency_contacts_student_mobile UNIQUE (student_id, mobile_number)
);

CREATE INDEX IF NOT EXISTS idx_emergency_contacts_student_id ON tbl_emergency_contacts (student_id);
CREATE INDEX IF NOT EXISTS idx_emergency_contacts_school_id ON tbl_emergency_contacts (school_id);
