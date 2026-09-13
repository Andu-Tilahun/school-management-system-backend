CREATE TABLE IF NOT EXISTS tbl_tenants (
     id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     tenant_code  VARCHAR(20) NOT NULL UNIQUE,
     tenant_name  VARCHAR(200) NOT NULL UNIQUE,
     website      VARCHAR(200),
     created_at TIMESTAMP,
     updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tenants_tenant_code ON tbl_tenants (tenant_code);
CREATE INDEX IF NOT EXISTS idx_tenants_tenant_name ON tbl_tenants (tenant_name);

INSERT INTO tbl_tenants (id, tenant_code, tenant_name, website, created_at, updated_at)
VALUES (
    'afe48348-1934-419f-9cba-ba0f41b14748',
    'DEFAULT',
    'Default Tenant',
    NULL,
    '2026-05-02 11:09:26.275589',
    '2026-05-02 11:09:26.275589'
)
ON CONFLICT (id) DO NOTHING;
