ALTER TABLE tbl_attendances
    ADD COLUMN IF NOT EXISTS enrollment_term_id UUID;

UPDATE tbl_attendances a
SET enrollment_term_id = (
    SELECT et.id
    FROM tbl_enrollment_terms et
    WHERE et.enrollment_id = a.enrollment_id
      AND et.status = 'ACTIVE'
      AND et.active = TRUE
    ORDER BY et.registered_at DESC
    LIMIT 1
)
WHERE a.enrollment_term_id IS NULL;

UPDATE tbl_attendances a
SET enrollment_term_id = (
    SELECT et.id
    FROM tbl_enrollment_terms et
    WHERE et.enrollment_id = a.enrollment_id
    ORDER BY et.registered_at DESC
    LIMIT 1
)
WHERE a.enrollment_term_id IS NULL;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM tbl_attendances WHERE enrollment_term_id IS NULL) THEN
        RAISE EXCEPTION 'Cannot migrate tbl_attendances: some rows have no enrollment term for their enrollment';
    END IF;
END $$;

ALTER TABLE tbl_attendances
    ALTER COLUMN enrollment_term_id SET NOT NULL;

ALTER TABLE tbl_attendances
    DROP CONSTRAINT IF EXISTS fk_attendances_enrollment_term_id;

ALTER TABLE tbl_attendances
    ADD CONSTRAINT fk_attendances_enrollment_term_id
        FOREIGN KEY (enrollment_term_id) REFERENCES tbl_enrollment_terms (id);

DROP INDEX IF EXISTS idx_attendances_enrollment_id;

ALTER TABLE tbl_attendances
    DROP COLUMN IF EXISTS enrollment_id;

CREATE INDEX IF NOT EXISTS idx_attendances_enrollment_term_id ON tbl_attendances (enrollment_term_id);
