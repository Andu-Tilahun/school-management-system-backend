CREATE TABLE IF NOT EXISTS tbl_enrollments (
    id UUID PRIMARY KEY,
    class_section_id UUID NOT NULL REFERENCES tbl_class_sections (id),
    student_id UUID NOT NULL REFERENCES tbl_students (id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'TERMINATE')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_enrollments_school_student UNIQUE (school_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollments_student_id ON tbl_enrollments (student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_class_section_id ON tbl_enrollments (class_section_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_school_id ON tbl_enrollments (school_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_status ON tbl_enrollments (status);
