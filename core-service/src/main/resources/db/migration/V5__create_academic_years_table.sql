CREATE TABLE IF NOT EXISTS tbl_academic_years (
    id UUID PRIMARY KEY,
    ac_year VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_academic_years_school_year UNIQUE (school_id, ac_year)
);

CREATE INDEX IF NOT EXISTS idx_academic_years_school_id ON tbl_academic_years (school_id);
CREATE INDEX IF NOT EXISTS idx_academic_years_school_active ON tbl_academic_years (school_id, active);
