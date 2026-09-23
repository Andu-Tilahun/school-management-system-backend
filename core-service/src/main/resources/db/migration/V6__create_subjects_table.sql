CREATE TABLE IF NOT EXISTS tbl_subjects
(
    id           UUID PRIMARY KEY,
    subject_code VARCHAR(20)  NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    grade_id     UUID         NOT NULL REFERENCES tbl_grades (id),
    status       VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id    UUID         NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_subjects_school_grade_subject_code UNIQUE (school_id, grade_id, subject_code)
);

CREATE INDEX IF NOT EXISTS idx_subjects_subject_name ON tbl_subjects (school_id, subject_name);
CREATE INDEX IF NOT EXISTS idx_subjects_school_id ON tbl_subjects (school_id);
CREATE INDEX IF NOT EXISTS idx_subjects_grade_id ON tbl_subjects (grade_id);
