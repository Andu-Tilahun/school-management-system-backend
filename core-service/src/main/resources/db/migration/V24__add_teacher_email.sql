ALTER TABLE tbl_teachers
    ADD COLUMN IF NOT EXISTS email VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS uk_teachers_email ON tbl_teachers (email) WHERE email IS NOT NULL;
