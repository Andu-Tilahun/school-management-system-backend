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
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('FARM_COMPANY', 'Farm company', 'UI', '/farm-company', '2026-05-02 11:09:26.275589', '5cca2240-1a2a-4ab4-bafa-a40ececf5e91');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('FARM_PLOTS', 'Farm plots', 'UI', '/farm-plots', '2026-05-02 11:09:26.275589', '7ac03558-8da8-445f-bc32-d7941f93fb78');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('FARM_LEASES', 'Farm leases', 'UI', '/farm-leases', '2026-05-02 11:09:26.275589', 'f3ecba0c-3e9d-4868-bfb9-adb841ad80fd');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('FARM_FOLLOWUPS', 'Farm follow-ups', 'UI', '/farm-followups', '2026-05-02 11:09:26.275589', 'e36d8d8e-ddd3-4413-8e41-7740cf57dde2');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('CROWDFUNDING', 'Crowdfunding campaigns', 'UI', '/crowd-funding', '2026-05-02 11:09:26.275589', 'b695bd5a-2ec6-4756-ac3e-3cae0ca2f067');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('CROWDFUND_INVESTMENTS', 'Crowdfunding investments', 'UI', '/crowd-funding/investments', '2026-05-02 11:09:26.275589', 'c5606c4f-9e66-4f6b-befa-cc66d86f65fb');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('NOTIFICATIONS', 'Notifications', 'UI', '/notifications', '2026-05-02 11:09:26.413858', '3ac0d76a-9f17-48c7-acf3-f41157529f91');
INSERT INTO resources (name, description, type, uri_pattern, created_at, id) VALUES ('PAYMENTS', 'Payments', 'UI', '/payments', '2026-05-02 11:09:26.413858', 'cddaf231-18e9-4cc7-afef-94aa1fa20574');


INSERT INTO policies (name, description, effect, created_at, id) VALUES ('ADMIN_ALL_FEATURES', 'Full application features for administrators', 'ALLOW', '2026-05-02 11:09:26.275589', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO policies (name, description, effect, created_at, id) VALUES ('INVESTOR_POLICY', 'Farm leases and crowdfunding for investors', 'ALLOW', '2026-05-02 11:09:26.413858', '79ed0d57-7627-4e9d-bc37-41bdcde4d31b');
INSERT INTO policies (name, description, effect, created_at, id) VALUES ('OPERATOR_POLICY', 'Field operations: plots, payments, farm company', 'ALLOW', '2026-05-02 11:09:26.413858', '63887a2b-7671-4bcf-aa1b-523785c3109f');
INSERT INTO policies (name, description, effect, created_at, id) VALUES ('EXTENSION_WORKER_ASSIGNED', 'Farm follow-ups and related field work', 'ALLOW', '2026-05-02 11:09:26.413858', 'fadbe3a8-4388-4015-95be-37de382fdfbd');
INSERT INTO policies (name, description, effect, created_at, id) VALUES ('CCA_ACCESS', 'Clearing agent / CCA license workflow users', 'ALLOW', '2026-05-02 11:09:26.413858', 'ad40c3ad-4c42-450d-942f-96e7476efcd9');
