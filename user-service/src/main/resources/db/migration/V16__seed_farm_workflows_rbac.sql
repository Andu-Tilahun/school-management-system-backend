INSERT INTO resources (name, description, type, uri_pattern, created_at, id)
VALUES ('FARM_WORKFLOWS', 'Farm workflows', 'UI', '/farm-workflows', NOW(), 'b2c3d4e5-f6a7-4890-b1c2-d3e4f5a6b7c8');

INSERT INTO permissions (name, description, created_at, id, resource_id)
VALUES ('FARM_WORKFLOWS', 'Access: Farm workflows', NOW(), 'c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', 'b2c3d4e5-f6a7-4890-b1c2-d3e4f5a6b7c8');

INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '8fe9c10f-5f7a-467d-b479-a44dde150cc7');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '56fc119f-9788-4743-b115-1558d6bddebf');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', 'cf5afa07-7915-42e0-ade1-03abb0523972');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '84543f6e-8a3a-44bc-b390-02049ba0035b');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', 'e1da5d22-aa98-4849-b178-64d945475730');

INSERT INTO permission_policies (permission_id, policy_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9', '63887a2b-7671-4bcf-aa1b-523785c3109f');
