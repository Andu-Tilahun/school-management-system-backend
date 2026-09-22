CREATE TABLE IF NOT EXISTS tbl_attendances (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES tbl_enrollments (id),
    penalty_trigger VARCHAR(40) NOT NULL CHECK (penalty_trigger IN (
        'ABSENCE',
        'TARDINESS',
        'EARLY_DEPARTURE',
        'UNEXCUSED_ABSENCE'
    )),
    date_occurred DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'REJECTED')),
    remark VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE INDEX IF NOT EXISTS idx_attendances_school_id ON tbl_attendances (school_id);
CREATE INDEX IF NOT EXISTS idx_attendances_enrollment_id ON tbl_attendances (enrollment_id);
CREATE INDEX IF NOT EXISTS idx_attendances_status ON tbl_attendances (status);
CREATE INDEX IF NOT EXISTS idx_attendances_penalty_trigger ON tbl_attendances (penalty_trigger);
CREATE INDEX IF NOT EXISTS idx_attendances_date_occurred ON tbl_attendances (date_occurred);
