CREATE TABLE IF NOT EXISTS tbl_student_emergency_contacts (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES tbl_students (id),
    emergency_contact_id UUID NOT NULL REFERENCES tbl_emergency_contacts (id),
    relationship VARCHAR(20) NOT NULL CHECK (relationship IN ('MOTHER', 'FATHER', 'GUARDIAN', 'SIBLING', 'OTHER')),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    school_id UUID NOT NULL REFERENCES tbl_schools (id),
    CONSTRAINT uk_student_emergency_contacts_pair UNIQUE (student_id, emergency_contact_id)
);

CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_student_id
    ON tbl_student_emergency_contacts (student_id);
CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_contact_id
    ON tbl_student_emergency_contacts (emergency_contact_id);
CREATE INDEX IF NOT EXISTS idx_student_emergency_contacts_school_id
    ON tbl_student_emergency_contacts (school_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_student_emergency_contacts_one_primary
    ON tbl_student_emergency_contacts (student_id)
    WHERE is_primary IS TRUE AND active IS TRUE;

INSERT INTO tbl_student_emergency_contacts (
    id,
    student_id,
    emergency_contact_id,
    relationship,
    is_primary,
    active,
    created_at,
    updated_at,
    created_by,
    updated_by,
    created_by_name,
    updated_by_name,
    school_id
)
SELECT
    gen_random_uuid(),
    student_id,
    id,
    'OTHER',
    FALSE,
    active,
    created_at,
    updated_at,
    created_by,
    updated_by,
    created_by_name,
    updated_by_name,
    school_id
FROM tbl_emergency_contacts
WHERE student_id IS NOT NULL
ON CONFLICT (student_id, emergency_contact_id) DO NOTHING;

UPDATE tbl_student_emergency_contacts sec
SET is_primary = TRUE
WHERE sec.id IN (
    SELECT DISTINCT ON (student_id) id
    FROM tbl_student_emergency_contacts
    WHERE active IS TRUE
    ORDER BY student_id, created_at NULLS LAST, id
);

UPDATE tbl_emergency_contacts
SET email = LOWER(TRIM(email))
WHERE email IS NOT NULL AND TRIM(email) <> '';

UPDATE tbl_emergency_contacts
SET email = 'migrated-' || id::text || '@unknown.local'
WHERE email IS NULL OR TRIM(email) = '';

WITH ranked AS (
    SELECT
        id,
        school_id,
        email,
        ROW_NUMBER() OVER (PARTITION BY school_id, email ORDER BY created_at NULLS LAST, id) AS rn
    FROM tbl_emergency_contacts
),
dupes AS (
    SELECT
        r.id AS dupe_id,
        k.id AS keeper_id
    FROM ranked r
    JOIN ranked k
      ON k.school_id = r.school_id
     AND k.email = r.email
     AND k.rn = 1
    WHERE r.rn > 1
)
UPDATE tbl_student_emergency_contacts sec
SET emergency_contact_id = d.keeper_id
FROM dupes d
WHERE sec.emergency_contact_id = d.dupe_id
  AND NOT EXISTS (
      SELECT 1
      FROM tbl_student_emergency_contacts existing
      WHERE existing.student_id = sec.student_id
        AND existing.emergency_contact_id = d.keeper_id
  );

WITH ranked AS (
    SELECT
        id,
        school_id,
        email,
        ROW_NUMBER() OVER (PARTITION BY school_id, email ORDER BY created_at NULLS LAST, id) AS rn
    FROM tbl_emergency_contacts
),
dupes AS (
    SELECT r.id AS dupe_id
    FROM ranked r
    WHERE r.rn > 1
)
DELETE FROM tbl_student_emergency_contacts sec
USING dupes d
WHERE sec.emergency_contact_id = d.dupe_id;

WITH ranked AS (
    SELECT
        id,
        ROW_NUMBER() OVER (PARTITION BY school_id, email ORDER BY created_at NULLS LAST, id) AS rn
    FROM tbl_emergency_contacts
)
DELETE FROM tbl_emergency_contacts
WHERE id IN (SELECT id FROM ranked WHERE rn > 1);

ALTER TABLE tbl_emergency_contacts DROP COLUMN IF EXISTS student_id;

DROP INDEX IF EXISTS idx_emergency_contacts_student_id;

ALTER TABLE tbl_emergency_contacts
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE tbl_emergency_contacts
    ADD CONSTRAINT uk_emergency_contacts_school_email UNIQUE (school_id, email);
