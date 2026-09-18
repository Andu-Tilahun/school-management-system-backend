CREATE TABLE IF NOT EXISTS tbl_emergency_contacts (
    id UUID PRIMARY KEY,
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
    email VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_emergency_contacts_school_email UNIQUE (school_id, email)
);

CREATE INDEX IF NOT EXISTS idx_emergency_contacts_school_id ON tbl_emergency_contacts (school_id);

CREATE TABLE IF NOT EXISTS tbl_student_emergency_contacts (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES tbl_students (id),
    emergency_contact_id UUID NOT NULL REFERENCES tbl_emergency_contacts (id),
    relationship VARCHAR(20) NOT NULL CHECK (relationship IN ('MOTHER', 'FATHER', 'GUARDIAN', 'SIBLING', 'OTHER')),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_student_emergency_contacts_student_contact UNIQUE (student_id, emergency_contact_id)
);

CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_school_id ON tbl_student_emergency_contacts (school_id);
CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_student_id ON tbl_student_emergency_contacts (student_id);
CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_emergency_contact_id ON tbl_student_emergency_contacts (emergency_contact_id);
