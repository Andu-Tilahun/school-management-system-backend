-- License service: regions & organizations admin UI (routes align with Angular /licenses/*).

INSERT INTO resources (id, name, description, type, uri_pattern, created_at)
VALUES (
           'c0010001-0001-4001-8001-000000000020',
           'REGIONS',
           'License regions',
           'UI',
           '/licenses/regions',
           NOW()
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO resources (id, name, description, type, uri_pattern, created_at)
VALUES (
           'c0010001-0001-4001-8001-000000000021',
           'ORGANIZATIONS',
           'License organizations',
           'UI',
           '/licenses/organizations',
           NOW()
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, name, description, created_at, resource_id)
VALUES (
           'e0020001-0001-4001-8001-000000000020',
           'REGIONS',
           'Access: license regions',
           NOW(),
           'c0010001-0001-4001-8001-000000000020'
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, name, description, created_at, resource_id)
VALUES (
           'e0020001-0001-4001-8001-000000000021',
           'ORGANIZATIONS',
           'Access: license organizations',
           NOW(),
           'c0010001-0001-4001-8001-000000000021'
       )
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission_scopes (permission_id, scope_id)
VALUES ('e0020001-0001-4001-8001-000000000020', '8fe9c10f-5f7a-467d-b479-a44dde150cc7'),
       ('e0020001-0001-4001-8001-000000000020', '56fc119f-9788-4743-b115-1558d6bddebf'),
       ('e0020001-0001-4001-8001-000000000020', 'cf5afa07-7915-42e0-ade1-03abb0523972'),
       ('e0020001-0001-4001-8001-000000000020', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be'),
       ('e0020001-0001-4001-8001-000000000020', '84543f6e-8a3a-44bc-b390-02049ba0035b'),
       ('e0020001-0001-4001-8001-000000000020', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c'),
       ('e0020001-0001-4001-8001-000000000020', 'e1da5d22-aa98-4849-b178-64d945475730'),
       ('e0020001-0001-4001-8001-000000000021', '8fe9c10f-5f7a-467d-b479-a44dde150cc7'),
       ('e0020001-0001-4001-8001-000000000021', '56fc119f-9788-4743-b115-1558d6bddebf'),
       ('e0020001-0001-4001-8001-000000000021', 'cf5afa07-7915-42e0-ade1-03abb0523972'),
       ('e0020001-0001-4001-8001-000000000021', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be'),
       ('e0020001-0001-4001-8001-000000000021', '84543f6e-8a3a-44bc-b390-02049ba0035b'),
       ('e0020001-0001-4001-8001-000000000021', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c'),
       ('e0020001-0001-4001-8001-000000000021', 'e1da5d22-aa98-4849-b178-64d945475730')
ON CONFLICT (permission_id, scope_id) DO NOTHING;

-- License region/org API access is enforced in license-service via @RequiresPermission on controllers.
INSERT INTO permission_policies (permission_id, policy_id) VALUES
    ('e0020001-0001-4001-8001-000000000020', '9cd6da93-36f0-49b7-a923-857c06f78b58'),
    ('e0020001-0001-4001-8001-000000000021', '9cd6da93-36f0-49b7-a923-857c06f78b58')
ON CONFLICT (permission_id, policy_id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (21, NULL, 'Licenses', NULL, 'safety', 3, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (22, 21, 'Regions', '/licenses/regions', NULL, 0, 'c0010001-0001-4001-8001-000000000020')
ON CONFLICT (id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (23, 21, 'Organizations', '/licenses/organizations', NULL, 1, 'c0010001-0001-4001-8001-000000000021')
ON CONFLICT (id) DO NOTHING;
