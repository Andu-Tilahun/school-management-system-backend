CREATE TABLE IF NOT EXISTS tbl_offence_records (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES tbl_enrollments (id),
    penalty_trigger VARCHAR(40) NOT NULL CHECK (penalty_trigger IN (
        'FIGHTING',
        'BULLYING',
        'HARASSMENT',
        'SEXUAL_HARASSMENT',
        'HATE_SPEECH',
        'VANDALISM',
        'THEFT',
        'CHEATING',
        'PLAGIARISM',
        'FORGERY',
        'INSUBORDINATION',
        'DISRUPTIVE_BEHAVIOR',
        'DRESS_CODE_VIOLATION',
        'SKIPPING_CLASS',
        'MOBILE_PHONE_MISUSE',
        'SMOKING',
        'ALCOHOL_POSSESSION',
        'DRUG_POSSESSION',
        'WEAPON_POSSESSION',
        'PROHIBITED_ITEM',
        'GAMBLING',
        'TRESPASSING',
        'EXAM_MISCONDUCT'
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

CREATE INDEX IF NOT EXISTS idx_offence_records_school_id ON tbl_offence_records (school_id);
CREATE INDEX IF NOT EXISTS idx_offence_records_enrollment_id ON tbl_offence_records (enrollment_id);
CREATE INDEX IF NOT EXISTS idx_offence_records_status ON tbl_offence_records (status);
CREATE INDEX IF NOT EXISTS idx_offence_records_penalty_trigger ON tbl_offence_records (penalty_trigger);
CREATE INDEX IF NOT EXISTS idx_offence_records_date_occurred ON tbl_offence_records (date_occurred);
