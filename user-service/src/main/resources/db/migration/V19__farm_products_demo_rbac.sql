-- RBAC resource + permission for farm product catalog / product-scoped user requests (UI route matches Angular).
INSERT INTO resources (id, name, description, type, uri_pattern, created_at)
VALUES (
           'c0010001-0001-4001-8001-000000000001',
           'FARM_PRODUCTS',
           'Farm products (demo catalog and workflow requests)',
           'UI',
           '/farm-products',
           NOW()
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, name, description, created_at, resource_id)
VALUES (
           'e0020001-0001-4001-8001-000000000001',
           'FARM_PRODUCTS',
           'Access: Farm products',
           NOW(),
           'c0010001-0001-4001-8001-000000000001'
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission_scopes (permission_id, scope_id)
VALUES ('e0020001-0001-4001-8001-000000000001', '8fe9c10f-5f7a-467d-b479-a44dde150cc7'),
       ('e0020001-0001-4001-8001-000000000001', '56fc119f-9788-4743-b115-1558d6bddebf'),
       ('e0020001-0001-4001-8001-000000000001', 'cf5afa07-7915-42e0-ade1-03abb0523972'),
       ('e0020001-0001-4001-8001-000000000001', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be'),
       ('e0020001-0001-4001-8001-000000000001', '84543f6e-8a3a-44bc-b390-02049ba0035b'),
       ('e0020001-0001-4001-8001-000000000001', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c'),
       ('e0020001-0001-4001-8001-000000000001', 'e1da5d22-aa98-4849-b178-64d945475730')
ON CONFLICT (permission_id, scope_id) DO NOTHING;

INSERT INTO policies (id, name, description, effect, created_at)
VALUES (
           'e0010001-0001-4001-8001-000000000099',
           'FARM_PRODUCT_DEMO_APPLICANT',
           'Demo applicant: farm products + home',
           'ALLOW',
           NOW()
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission_policies (permission_id, policy_id) VALUES
    ('e0020001-0001-4001-8001-000000000001', '9cd6da93-36f0-49b7-a923-857c06f78b58'),
    ('e0020001-0001-4001-8001-000000000001', '63887a2b-7671-4bcf-aa1b-523785c3109f'),
    ('e0020001-0001-4001-8001-000000000001', 'fadbe3a8-4388-4015-95be-37de382fdfbd'),
    ('e0020001-0001-4001-8001-000000000001', 'e0010001-0001-4001-8001-000000000099'),
    ('90399595-22ad-4e5c-bfa1-f6fb215c2a4e', 'e0010001-0001-4001-8001-000000000099')
ON CONFLICT (permission_id, policy_id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (20, 10, 'Farm products', '/farm-products', NULL, 2, 'c0010001-0001-4001-8001-000000000001')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (
    id, username, password, email, first_name, last_name, enabled,
    account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at
)
VALUES (
           'd0010001-0001-4001-8001-000000000001',
           'farm-product-applicant',
           '$2a$10$oLHhnCPoEeU127.T3EvssONiMdBl97lkuUIAV8CEK0CPHnCePbFRy',
           'farm-product-applicant@example.com',
           'Demo',
           'Applicant',
           TRUE,
           TRUE,
           TRUE,
           TRUE,
           NOW(),
           NOW()
       ),
       (
           'd0010001-0001-4001-8001-000000000002',
           'farm-product-officer',
           '$2a$10$oLHhnCPoEeU127.T3EvssONiMdBl97lkuUIAV8CEK0CPHnCePbFRy',
           'farm-product-officer@example.com',
           'Demo',
           'Officer',
           TRUE,
           TRUE,
           TRUE,
           TRUE,
           NOW(),
           NOW()
       ),
       (
           'd0010001-0001-4001-8001-000000000003',
           'farm-product-expert',
           '$2a$10$oLHhnCPoEeU127.T3EvssONiMdBl97lkuUIAV8CEK0CPHnCePbFRy',
           'farm-product-expert@example.com',
           'Demo',
           'Expert',
           TRUE,
           TRUE,
           TRUE,
           TRUE,
           NOW(),
           NOW()
       )
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_policies (user_id, policy_id) VALUES
    ('d0010001-0001-4001-8001-000000000001', 'e0010001-0001-4001-8001-000000000099'),
    ('d0010001-0001-4001-8001-000000000002', '63887a2b-7671-4bcf-aa1b-523785c3109f'),
    ('d0010001-0001-4001-8001-000000000003', 'fadbe3a8-4388-4015-95be-37de382fdfbd')
ON CONFLICT (user_id, policy_id) DO NOTHING;
