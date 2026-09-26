CREATE TABLE IF NOT EXISTS tbl_class_section_homerooms (
    id UUID PRIMARY KEY,
    class_section_id UUID NOT NULL REFERENCES tbl_class_sections (id),
    teacher_id UUID NOT NULL REFERENCES tbl_teachers (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_class_section_homerooms_section_active
    ON tbl_class_section_homerooms (class_section_id)
    WHERE active = TRUE;

CREATE INDEX IF NOT EXISTS idx_class_section_homerooms_school_id
    ON tbl_class_section_homerooms (school_id);
CREATE INDEX IF NOT EXISTS idx_class_section_homerooms_class_section_id
    ON tbl_class_section_homerooms (class_section_id);
CREATE INDEX IF NOT EXISTS idx_class_section_homerooms_teacher_id
    ON tbl_class_section_homerooms (teacher_id);
