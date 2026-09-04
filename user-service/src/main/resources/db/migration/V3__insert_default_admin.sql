INSERT INTO users (
    id,
    username,
    password,
    email,
    first_name,
    last_name,
    user_scope_type,
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
    'SYSTEM',
    TRUE,
    TRUE,
    TRUE,
    TRUE
)
ON CONFLICT (username) DO NOTHING;
