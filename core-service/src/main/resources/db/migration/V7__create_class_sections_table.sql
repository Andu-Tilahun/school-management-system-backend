CREATE TABLE IF NOT EXISTS tbl_class_sections (
    id UUID PRIMARY KEY,
    grade_id UUID NOT NULL REFERENCES tbl_grades (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_class_sections_school_grade UNIQUE (school_id, grade_id)
);

CREATE INDEX IF NOT EXISTS idx_class_sections_school_id ON tbl_class_sections (school_id);
CREATE INDEX IF NOT EXISTS idx_class_sections_grade_id ON tbl_class_sections (grade_id);
