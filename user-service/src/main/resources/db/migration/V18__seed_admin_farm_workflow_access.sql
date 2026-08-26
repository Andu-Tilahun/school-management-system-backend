-- Default admin user (see V3__insert_default_admin.sql)
-- ADMIN_ALL_FEATURES policy (V13), ADMIN_FULL_ACCESS group (V14), FARM_WORKFLOWS permission (V16).

-- Ensure admin is in the full-access group (inherits ADMIN_ALL_FEATURES and thus all permissions on that policy).
INSERT INTO user_groups (user_id, group_id)
VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '0aa037da-fe8a-4133-a971-536196f202d8')
ON CONFLICT DO NOTHING;

-- Direct policy on admin: guarantees ADMIN_ALL_FEATURES even if group membership is missing.
INSERT INTO user_policies (user_id, policy_id)
VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '9cd6da93-36f0-49b7-a923-857c06f78b58')
ON CONFLICT DO NOTHING;

-- Idempotent: farm workflows permission is part of the admin policy graph (menu + API + farm-service).
INSERT INTO permission_policies (permission_id, policy_id)
VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '9cd6da93-36f0-49b7-a923-857c06f78b58')
ON CONFLICT DO NOTHING;
