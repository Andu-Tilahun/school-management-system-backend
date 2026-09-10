CREATE TABLE IF NOT EXISTS tbl_room_sections (
    id UUID PRIMARY KEY,
    class_section_id UUID NOT NULL REFERENCES tbl_class_sections (id),
    room_id UUID NOT NULL REFERENCES tbl_classrooms (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_room_sections_school_class_section_room UNIQUE (school_id, class_section_id, room_id)
);

CREATE INDEX IF NOT EXISTS idx_room_sections_school_id ON tbl_room_sections (school_id);
CREATE INDEX IF NOT EXISTS idx_room_sections_class_section_id ON tbl_room_sections (class_section_id);
CREATE INDEX IF NOT EXISTS idx_room_sections_room_id ON tbl_room_sections (room_id);
