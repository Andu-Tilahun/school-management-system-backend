ALTER TABLE tbl_offence_records
    ADD COLUMN IF NOT EXISTS enrollment_term_id UUID;

UPDATE tbl_offence_records o
SET enrollment_term_id = (
    SELECT et.id
    FROM tbl_enrollment_terms et
    WHERE et.enrollment_id = o.enrollment_id
      AND et.status = 'ACTIVE'
      AND et.active = TRUE
    ORDER BY et.registered_at DESC
    LIMIT 1
)
WHERE o.enrollment_term_id IS NULL;

UPDATE tbl_offence_records o
SET enrollment_term_id = (
    SELECT et.id
    FROM tbl_enrollment_terms et
    WHERE et.enrollment_id = o.enrollment_id
    ORDER BY et.registered_at DESC
    LIMIT 1
)
WHERE o.enrollment_term_id IS NULL;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM tbl_offence_records WHERE enrollment_term_id IS NULL) THEN
        RAISE EXCEPTION 'Cannot migrate tbl_offence_records: some rows have no enrollment term for their enrollment';
    END IF;
END $$;

ALTER TABLE tbl_offence_records
    ALTER COLUMN enrollment_term_id SET NOT NULL;

ALTER TABLE tbl_offence_records
    DROP CONSTRAINT IF EXISTS fk_offence_records_enrollment_term_id;

ALTER TABLE tbl_offence_records
    ADD CONSTRAINT fk_offence_records_enrollment_term_id
        FOREIGN KEY (enrollment_term_id) REFERENCES tbl_enrollment_terms (id);

DROP INDEX IF EXISTS idx_offence_records_enrollment_id;

ALTER TABLE tbl_offence_records
    DROP COLUMN IF EXISTS enrollment_id;

CREATE INDEX IF NOT EXISTS idx_offence_records_enrollment_term_id ON tbl_offence_records (enrollment_term_id);
