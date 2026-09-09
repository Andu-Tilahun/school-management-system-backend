CREATE TABLE schools (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID NOT NULL REFERENCES tenants(id),
    school_type  VARCHAR(20) NOT NULL,
    school_name  VARCHAR(200) NOT NULL,
    website      VARCHAR(200)
);