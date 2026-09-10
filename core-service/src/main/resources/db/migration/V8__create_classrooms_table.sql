CREATE TABLE IF NOT EXISTS tbl_classrooms (
    id UUID PRIMARY KEY,
    room_number VARCHAR(50) NOT NULL,
    room_size INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_classrooms_school_room_number UNIQUE (school_id, room_number)
);

CREATE INDEX IF NOT EXISTS idx_classrooms_school_id ON tbl_classrooms (school_id);
CREATE INDEX IF NOT EXISTS idx_classrooms_school_room_number ON tbl_classrooms (school_id, room_number);
