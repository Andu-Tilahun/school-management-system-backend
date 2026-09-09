INSERT INTO scopes (name, description, created_at, id) VALUES ('VIEW', 'Read and view data', '2026-05-02 11:09:26.275589', '8fe9c10f-5f7a-467d-b479-a44dde150cc7');
INSERT INTO scopes (name, description, created_at, id) VALUES ('CREATE', 'Create new records', '2026-05-02 11:09:26.275589', '56fc119f-9788-4743-b115-1558d6bddebf');
INSERT INTO scopes (name, description, created_at, id) VALUES ('UPDATE', 'Update existing records', '2026-05-02 11:09:26.275589', 'cf5afa07-7915-42e0-ade1-03abb0523972');
INSERT INTO scopes (name, description, created_at, id) VALUES ('DELETE', 'Delete records', '2026-05-02 11:09:26.275589', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be');
INSERT INTO scopes (name, description, created_at, id) VALUES ('REFRESH', 'Refresh or reload lists', '2026-05-02 11:09:26.275589', '84543f6e-8a3a-44bc-b390-02049ba0035b');
INSERT INTO scopes (name, description, created_at, id) VALUES ('EXPORT', 'Export or download data', '2026-05-02 11:09:26.275589', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c');
INSERT INTO scopes (name, description, created_at, id) VALUES ('READ', 'Read data via API without menu (assign VIEW for menu + read).', '2026-05-02 11:09:26.396490', 'e1da5d22-aa98-4849-b178-64d945475730');


INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('HOME', 'Home page', 'UI', '/home', '2026-05-02 11:09:26.275589', '6f6de57b-5efb-4880-96f1-fa3e94f31bef');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('USERS', 'User accounts', 'UI', '/users', '2026-05-02 11:09:26.275589', '96900bfc-3f56-4030-b950-85b9e3dcf9c2');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('SCOPES', 'Scopes', 'UI', '/scopes', '2026-05-02 11:09:26.275589', 'cfae8b07-41b1-4a29-9ce1-bc817762db5d');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('RBAC_RESOURCES', 'RBAC resources', 'UI', '/resources', '2026-05-02 11:09:26.275589', '1541dc74-1d7c-4ac0-a195-2269bc5402b6');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('PERMISSIONS', 'Permissions', 'UI', '/permissions', '2026-05-02 11:09:26.275589', 'c3fc2cbc-dcd5-433c-8feb-dfc459db78ca');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('POLICIES', 'Policies', 'UI', '/policies', '2026-05-02 11:09:26.275589', '4fb75048-64ac-4314-a5d0-04e698d75a0d');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('GROUPS', 'Groups', 'UI', '/groups', '2026-05-02 11:09:26.275589', 'b49f9ea7-39a2-487b-a234-70cdbb8e44e1');

INSERT INTO policies (name, description, effect, created_at, id) VALUES ('SUPER_ADMIN_FEATURES', 'Full application features for administrators', 'ALLOW', '2026-05-02 11:09:26.275589', '9cd6da93-36f0-49b7-a923-857c06f78b58');
