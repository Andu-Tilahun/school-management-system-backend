ALTER TABLE users
    ADD COLUMN user_scope_type VARCHAR(40) NOT NULL DEFAULT 'SYSTEM';

ALTER TABLE users
    ADD COLUMN external_id UUID;

COMMENT ON COLUMN users.user_scope_type IS 'SYSTEM: no external scope; REGION_REPRESENTATIVE / ORGANIZATION_REPRESENTATIVE: external_id is region or org UUID';
COMMENT ON COLUMN users.external_id IS 'Region or organization UUID when user_scope_type is not SYSTEM';
