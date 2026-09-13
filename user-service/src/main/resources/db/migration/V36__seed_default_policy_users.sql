-- Super Admin (admin) is seeded in V3/V7. Reuse that user and ensure policy/group links.
INSERT INTO users (
    id,
    username,
    password,
    email,
    first_name,
    last_name,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired
)
VALUES (
    'a1cec8bb-1cf1-40c2-9ab1-2a392af385e4',
    'admin',
    crypt('ChangeMe123!', gen_salt('bf', 10)),
    'admin@example.com',
    'System',
    'Administrator',
    TRUE,
    TRUE,
    TRUE,
    TRUE
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_groups (user_id, group_id)
VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '0aa037da-fe8a-4133-a971-536196f202d8')
ON CONFLICT DO NOTHING;

INSERT INTO user_policies (user_id, policy_id)
VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '9cd6da93-36f0-49b7-a923-857c06f78b58')
ON CONFLICT DO NOTHING;

-- Tenant Manager: external_id = default tenant (core V1 afe48348-1934-419f-9cba-ba0f41b14748)
INSERT INTO users (
    id,
    username,
    password,
    email,
    first_name,
    last_name,
    external_id,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired
)
VALUES (
    '8590d78e-08c3-4f8f-a811-d7fca9cabae2',
    'tenant_manager',
    crypt('ChangeMe123!', gen_salt('bf', 10)),
    'tenant_manager@example.com',
    'Default',
    'Tenant Manager',
    'afe48348-1934-419f-9cba-ba0f41b14748',
    TRUE,
    TRUE,
    TRUE,
    TRUE
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_groups (user_id, group_id)
VALUES ('8590d78e-08c3-4f8f-a811-d7fca9cabae2', 'e9ccb946-2cac-44c6-84a5-74d2ceb2f690')
ON CONFLICT DO NOTHING;

INSERT INTO user_policies (user_id, policy_id)
VALUES ('8590d78e-08c3-4f8f-a811-d7fca9cabae2', '09cb27d2-c7a8-4bd3-9d17-aa5b93786333')
ON CONFLICT DO NOTHING;

-- School Admin: external_id = default school (core V2 8b716506-a316-450b-9f64-d1c95e05c647)
INSERT INTO users (
    id,
    username,
    password,
    email,
    first_name,
    last_name,
    external_id,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired
)
VALUES (
    '36987d6d-a54c-4649-9d30-c7d54a93c6dc',
    'school_admin',
    crypt('ChangeMe123!', gen_salt('bf', 10)),
    'school_admin@example.com',
    'Default',
    'School Admin',
    '8b716506-a316-450b-9f64-d1c95e05c647',
    TRUE,
    TRUE,
    TRUE,
    TRUE
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_groups (user_id, group_id)
VALUES ('36987d6d-a54c-4649-9d30-c7d54a93c6dc', 'd5f18de5-f7e6-4aa0-96eb-16cbb5b76110')
ON CONFLICT DO NOTHING;

INSERT INTO user_policies (user_id, policy_id)
VALUES ('36987d6d-a54c-4649-9d30-c7d54a93c6dc', 'cd29816f-ec74-4720-8833-18ce2e169102')
ON CONFLICT DO NOTHING;
