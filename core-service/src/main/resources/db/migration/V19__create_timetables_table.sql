CREATE TABLE IF NOT EXISTS tbl_timetables (
    id UUID PRIMARY KEY,
    class_section_id UUID NOT NULL REFERENCES tbl_class_sections (id),
    teacher_subject_assignment_id UUID NOT NULL REFERENCES tbl_teacher_subject_assignments (id),
    day VARCHAR(20) NOT NULL CHECK (day IN (
        'MONDAY',
        'TUESDAY',
        'WEDNESDAY',
        'THURSDAY',
        'FRIDAY'
    )),
    period VARCHAR(20) NOT NULL CHECK (period IN (
        'PERIOD_1',
        'PERIOD_2',
        'PERIOD_3',
        'PERIOD_4',
        'PERIOD_5',
        'PERIOD_6',
        'PERIOD_7',
        'PERIOD_8'
    )),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_timetables_class_section_day_period_active
    ON tbl_timetables (class_section_id, day, period)
    WHERE active = TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_timetables_assignment_day_period_active
    ON tbl_timetables (teacher_subject_assignment_id, day, period)
    WHERE active = TRUE;

CREATE INDEX IF NOT EXISTS idx_timetables_school_id ON tbl_timetables (school_id);
CREATE INDEX IF NOT EXISTS idx_timetables_class_section_id ON tbl_timetables (class_section_id);
CREATE INDEX IF NOT EXISTS idx_timetables_teacher_subject_assignment_id
    ON tbl_timetables (teacher_subject_assignment_id);
