CREATE TABLE IF NOT EXISTS tbl_schools (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_type  VARCHAR(20) NOT NULL,
    school_name  VARCHAR(200) NOT NULL,
    website      VARCHAR(200),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    created_by_name VARCHAR(100),
    updated_by_name VARCHAR(100),
    tenant_id    UUID NOT NULL REFERENCES tbl_tenants(id)
);

CREATE INDEX IF NOT EXISTS idx_schools_tenant_id ON tbl_schools (tenant_id);
CREATE INDEX IF NOT EXISTS idx_schools_school_type ON tbl_schools (tenant_id, school_type);
CREATE INDEX IF NOT EXISTS idx_schools_school_name ON tbl_schools (tenant_id, school_name);

INSERT INTO tbl_schools (id, school_type, school_name, website, created_at, updated_at, tenant_id)
VALUES (
    '8b716506-a316-450b-9f64-d1c95e05c647',
    'PRIVATE',
    'Default School',
    NULL,
    '2026-05-02 11:09:26.275589',
    '2026-05-02 11:09:26.275589',
    'afe48348-1934-419f-9cba-ba0f41b14748'
)
ON CONFLICT (id) DO NOTHING;
