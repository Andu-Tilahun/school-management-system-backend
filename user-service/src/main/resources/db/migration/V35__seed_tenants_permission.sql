INSERT INTO permissions (name, description, created_at, id, resource_id) VALUES ('TENANTS', 'Access: Tenants', '2026-05-02 11:09:26.275589', 'e3f4a5b6-c7d8-4f90-a123-23456789f012', 'e2f3a4b5-c6d7-4e8f-b012-12345678ef01');

INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', '8fe9c10f-5f7a-467d-b479-a44dde150cc7');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', '56fc119f-9788-4743-b115-1558d6bddebf');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', 'cf5afa07-7915-42e0-ade1-03abb0523972');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', '84543f6e-8a3a-44bc-b390-02049ba0035b');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', 'e1da5d22-aa98-4849-b178-64d945475730');

INSERT INTO permission_policies (permission_id, policy_id) VALUES ('e3f4a5b6-c7d8-4f90-a123-23456789f012', '9cd6da93-36f0-49b7-a923-857c06f78b58');

-- Tenant Manager: TENANTS:READ only (API read, no Tenants menu / CRUD)
INSERT INTO permissions (name, description, created_at, id, resource_id) VALUES ('TENANTS_READ', 'API read: Tenants (no menu)', '2026-05-02 11:09:26.275589', '8d9e0f1a-2b3c-4d4e-9f60-7b8c9d0e1f20', 'e2f3a4b5-c6d7-4e8f-b012-12345678ef01');
INSERT INTO permission_scopes (permission_id, scope_id) VALUES ('8d9e0f1a-2b3c-4d4e-9f60-7b8c9d0e1f20', 'e1da5d22-aa98-4849-b178-64d945475730');
INSERT INTO permission_policies (permission_id, policy_id) VALUES ('8d9e0f1a-2b3c-4d4e-9f60-7b8c9d0e1f20', '09cb27d2-c7a8-4bd3-9d17-aa5b93786333');
