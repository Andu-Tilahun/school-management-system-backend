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
