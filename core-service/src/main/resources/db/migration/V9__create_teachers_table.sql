CREATE TABLE IF NOT EXISTS tbl_teachers (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    sub_city VARCHAR(100) NOT NULL,
    kebele INTEGER,
    house_number VARCHAR(50),
    mobile_number VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_teachers_school_mobile UNIQUE (school_id, mobile_number)
);

CREATE INDEX IF NOT EXISTS idx_tbl_teachers_last_first_name ON tbl_teachers (school_id, last_name, first_name);
CREATE INDEX IF NOT EXISTS idx_tbl_teachers_school_id ON tbl_teachers (school_id);

CREATE TABLE IF NOT EXISTS tbl_teacher_subject_assignments (
    id UUID PRIMARY KEY,
    teacher_id UUID NOT NULL REFERENCES tbl_teachers (id),
    subject_id UUID NOT NULL REFERENCES tbl_subjects (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_teacher_subject_assignments_teacher_subject UNIQUE (teacher_id, subject_id)
);

CREATE INDEX IF NOT EXISTS idx_teacher_subject_assignments_school_id ON tbl_teacher_subject_assignments (school_id);
CREATE INDEX IF NOT EXISTS idx_teacher_subject_assignments_teacher_id ON tbl_teacher_subject_assignments (teacher_id);
CREATE INDEX IF NOT EXISTS idx_teacher_subject_assignments_subject_id ON tbl_teacher_subject_assignments (subject_id);
