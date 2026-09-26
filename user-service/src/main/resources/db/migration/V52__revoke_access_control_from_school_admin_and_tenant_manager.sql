-- Access Control (Scopes, Resources, Permissions, Policies, Groups) is Super Admin only.
-- School Admin and Tenant Manager keep operational access; they do not manage RBAC.

DELETE FROM permission_policies
WHERE permission_id IN (
    '0d050ab8-93b6-4674-8b84-0465506383b6', -- SCOPES
    'bfa0b6ff-29dc-4a5c-9e29-1a08936538a7', -- RBAC_RESOURCES
    'a1445bb6-6e43-4537-9042-7ac8f519bd88', -- PERMISSIONS
    '1dcd71af-35d1-4575-8ba3-2718b841efa2', -- POLICIES
    '971b52a8-da22-4452-8993-8eddf1b8d510'  -- GROUPS
)
AND policy_id IN (
    'cd29816f-ec74-4720-8833-18ce2e169102', -- SCHOOL_ADMIN_POLICY
    '09cb27d2-c7a8-4bd3-9d17-aa5b93786333'  -- TENANT_MANAGER_POLICY
);

INSERT INTO permission_policies (permission_id, policy_id) VALUES ('0d050ab8-93b6-4674-8b84-0465506383b6', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('bfa0b6ff-29dc-4a5c-9e29-1a08936538a7', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('a1445bb6-6e43-4537-9042-7ac8f519bd88', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('1dcd71af-35d1-4575-8ba3-2718b841efa2', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('971b52a8-da22-4452-8993-8eddf1b8d510', '9cd6da93-36f0-49b7-a923-857c06f78b58');
