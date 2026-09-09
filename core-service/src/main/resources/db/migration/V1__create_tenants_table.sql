CREATE TABLE IF NOT EXISTS tbl_tenants (
     id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     tenant_code  VARCHAR(20) NOT NULL UNIQUE,
     tenant_name  VARCHAR(200) NOT NULL UNIQUE,
     website      VARCHAR(200)
);

CREATE INDEX IF NOT EXISTS idx_tenants_tenant_code ON tenants (tenant_code);
CREATE INDEX IF NOT EXISTS idx_tenants_tenant_name ON tenants (tenant_name);