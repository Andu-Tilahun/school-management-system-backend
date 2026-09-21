CREATE TABLE IF NOT EXISTS tbl_penalties (
    id UUID PRIMARY KEY,
    penalty_rule_id UUID NOT NULL REFERENCES tbl_penalty_rules (id),
    enrollment_id UUID NOT NULL REFERENCES tbl_enrollments (id),
    occurrence_count_at_trigger INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE INDEX IF NOT EXISTS idx_penalties_school_id ON tbl_penalties (school_id);
CREATE INDEX IF NOT EXISTS idx_penalties_penalty_rule_id ON tbl_penalties (penalty_rule_id);
CREATE INDEX IF NOT EXISTS idx_penalties_enrollment_id ON tbl_penalties (enrollment_id);

CREATE TABLE IF NOT EXISTS tbl_penalty_source_attendances (
    id UUID PRIMARY KEY,
    penalty_id UUID NOT NULL REFERENCES tbl_penalties (id),
    attendance_id UUID NOT NULL REFERENCES tbl_attendances (id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE INDEX IF NOT EXISTS idx_penalty_source_attendances_school_id ON tbl_penalty_source_attendances (school_id);
CREATE INDEX IF NOT EXISTS idx_penalty_source_attendances_penalty_id ON tbl_penalty_source_attendances (penalty_id);
CREATE INDEX IF NOT EXISTS idx_penalty_source_attendances_attendance_id ON tbl_penalty_source_attendances (attendance_id);

CREATE TABLE IF NOT EXISTS tbl_penalty_source_offence_records (
    id UUID PRIMARY KEY,
    penalty_id UUID NOT NULL REFERENCES tbl_penalties (id),
    offence_record_id UUID NOT NULL REFERENCES tbl_offence_records (id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE INDEX IF NOT EXISTS idx_penalty_source_offence_records_school_id ON tbl_penalty_source_offence_records (school_id);
CREATE INDEX IF NOT EXISTS idx_penalty_source_offence_records_penalty_id ON tbl_penalty_source_offence_records (penalty_id);
CREATE INDEX IF NOT EXISTS idx_penalty_source_offence_records_offence_record_id ON tbl_penalty_source_offence_records (offence_record_id);
