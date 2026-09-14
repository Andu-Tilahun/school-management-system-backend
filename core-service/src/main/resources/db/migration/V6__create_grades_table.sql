CREATE TABLE IF NOT EXISTS tbl_grades (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_grades_school_name UNIQUE (school_id, name)
);

CREATE INDEX IF NOT EXISTS idx_grades_school_id ON tbl_grades (school_id);
CREATE INDEX IF NOT EXISTS idx_grades_school_name ON tbl_grades (school_id, name);
