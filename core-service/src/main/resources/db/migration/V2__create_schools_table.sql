CREATE TABLE tbl_schools (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_type  VARCHAR(20) NOT NULL,
    school_name  VARCHAR(200) NOT NULL,
    website      VARCHAR(200),
    tenant_id    UUID NOT NULL REFERENCES tenants(id)
);

CREATE INDEX IF NOT EXISTS idx_schools_tenant_id ON schools (tenant_id);
CREATE INDEX IF NOT EXISTS idx_schools_school_type ON schools (tenant_id, school_type);
CREATE INDEX IF NOT EXISTS idx_schools_school_name ON schools (tenant_id, school_name);