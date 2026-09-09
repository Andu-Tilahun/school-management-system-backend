  CREATE TABLE IF NOT EXISTS tenants (
       id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       tenant_code  VARCHAR(20) NOT NULL UNIQUE,
       tenant_name  VARCHAR(200) NOT NULL UNIQUE,
       website      VARCHAR(200)
  );