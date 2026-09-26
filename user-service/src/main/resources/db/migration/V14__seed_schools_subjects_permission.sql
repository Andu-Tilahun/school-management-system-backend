INSERT INTO permissions (name, description, created_at, id, resource_id) VALUES ('SCHOOLS', 'Access: Schools', '2026-05-02 11:09:26.275589', 'a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', 'c4d5e6f7-a8b9-4c0d-9e2f-3a4b5c6d7e8f');
INSERT INTO permissions (name, description, created_at, id, resource_id) VALUES ('SUBJECTS', 'Access: Subjects', '2026-05-02 11:09:26.275589', 'b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'd5e6f7a8-b9c0-4d1e-8f3a-4b5c6d7e8f90');

INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', '8fe9c10f-5f7a-467d-b479-a44dde150cc7');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', '56fc119f-9788-4743-b115-1558d6bddebf');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', 'cf5afa07-7915-42e0-ade1-03abb0523972');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', '84543f6e-8a3a-44bc-b390-02049ba0035b');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', 'e1da5d22-aa98-4849-b178-64d945475730');

INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', '8fe9c10f-5f7a-467d-b479-a44dde150cc7');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', '56fc119f-9788-4743-b115-1558d6bddebf');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'cf5afa07-7915-42e0-ade1-03abb0523972');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', '84543f6e-8a3a-44bc-b390-02049ba0035b');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'e1da5d22-aa98-4849-b178-64d945475730');

INSERT INTO permission_policies (permission_id, policy_id) VALUES ('a8b9c0d1-e2f3-4a5b-9c7d-8e9f0a1b2c3d', '09cb27d2-c7a8-4bd3-9d17-aa5b93786333');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('b9c0d1e2-f3a4-4b5c-8d7e-8f9a0b1c2d3e', 'cd29816f-ec74-4720-8833-18ce2e169102');

-- School Admin: SCHOOLS:READ only (API read, no Schools menu / CRUD)
INSERT INTO permissions (name, description, created_at, id, resource_id) VALUES ('SCHOOLS_READ', 'API read: Schools (no menu)', '2026-05-02 11:09:26.275589', '7c8d9e0f-1a2b-4c3d-8e5f-6a7b8c9d0e1f', 'c4d5e6f7-a8b9-4c0d-9e2f-3a4b5c6d7e8f');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('7c8d9e0f-1a2b-4c3d-8e5f-6a7b8c9d0e1f', 'e1da5d22-aa98-4849-b178-64d945475730');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('7c8d9e0f-1a2b-4c3d-8e5f-6a7b8c9d0e1f', 'cd29816f-ec74-4720-8833-18ce2e169102');
