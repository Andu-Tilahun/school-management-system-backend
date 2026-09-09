CREATE TABLE IF NOT EXISTS tbl_subjects
(
    id           UUID PRIMARY KEY,
    subject_code VARCHAR(20)  NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    school_id    UUID         NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_subjects_school_subject_code UNIQUE (school_id, subject_code)
);

CREATE INDEX IF NOT EXISTS idx_subjects_subject_name ON tbl_subjects (school_id, subject_name);
CREATE INDEX IF NOT EXISTS idx_subjects_school_id ON tbl_subjects (school_id);
