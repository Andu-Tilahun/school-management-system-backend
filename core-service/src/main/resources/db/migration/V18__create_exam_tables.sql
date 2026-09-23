CREATE TABLE IF NOT EXISTS tbl_terms (
    id UUID PRIMARY KEY,
    academic_year_id UUID NOT NULL REFERENCES tbl_academic_years (id),
    semester VARCHAR(20) NOT NULL CHECK (semester IN (
        'SEMESTER_1',
        'SEMESTER_2',
        'SEMESTER_3',
        'SEMESTER_4'
    )),
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
    CONSTRAINT uk_terms_academic_year_semester UNIQUE (academic_year_id, semester)
);

CREATE INDEX IF NOT EXISTS idx_terms_school_id ON tbl_terms (school_id);
CREATE INDEX IF NOT EXISTS idx_terms_academic_year_id ON tbl_terms (academic_year_id);

CREATE TABLE IF NOT EXISTS tbl_enrollment_terms (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES tbl_enrollments (id),
    term_id UUID NOT NULL REFERENCES tbl_terms (id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'COMPLETED', 'WITHDRAWN')),
    registered_at DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_enrollment_terms_enrollment_term UNIQUE (enrollment_id, term_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollment_terms_school_id ON tbl_enrollment_terms (school_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_terms_enrollment_id ON tbl_enrollment_terms (enrollment_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_terms_term_id ON tbl_enrollment_terms (term_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_terms_status ON tbl_enrollment_terms (status);

CREATE TABLE IF NOT EXISTS tbl_student_marks (
    id UUID PRIMARY KEY,
    enrollment_term_id UUID NOT NULL REFERENCES tbl_enrollment_terms (id),
    subject_id UUID NOT NULL REFERENCES tbl_subjects (id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('MIDTERM', 'FINAL', 'QUIZ', 'ASSIGNMENT')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('REGISTERED', 'GRADED', 'ABSENT', 'WITHDRAWN')),
    stud_mark DOUBLE PRECISION,
    total_mark_weight DOUBLE PRECISION NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_student_marks_enrollment_term_subject_type UNIQUE (enrollment_term_id, subject_id, type)
);

CREATE INDEX IF NOT EXISTS idx_student_marks_school_id ON tbl_student_marks (school_id);
CREATE INDEX IF NOT EXISTS idx_student_marks_enrollment_term_id ON tbl_student_marks (enrollment_term_id);
CREATE INDEX IF NOT EXISTS idx_student_marks_subject_id ON tbl_student_marks (subject_id);
CREATE INDEX IF NOT EXISTS idx_student_marks_status ON tbl_student_marks (status);

CREATE TABLE IF NOT EXISTS tbl_subject_totals (
    id UUID PRIMARY KEY,
    enrollment_term_id UUID NOT NULL REFERENCES tbl_enrollment_terms (id),
    subject_id UUID NOT NULL REFERENCES tbl_subjects (id),
    total_mark DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PASS', 'FAIL')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_subject_totals_enrollment_term_subject UNIQUE (enrollment_term_id, subject_id)
);

CREATE INDEX IF NOT EXISTS idx_subject_totals_school_id ON tbl_subject_totals (school_id);
CREATE INDEX IF NOT EXISTS idx_subject_totals_enrollment_term_id ON tbl_subject_totals (enrollment_term_id);
CREATE INDEX IF NOT EXISTS idx_subject_totals_subject_id ON tbl_subject_totals (subject_id);
CREATE INDEX IF NOT EXISTS idx_subject_totals_status ON tbl_subject_totals (status);
