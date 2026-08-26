-- License service: training program workflow-backed requests.
INSERT INTO resources (id, name, description, type, uri_pattern, created_at)
VALUES (
    'c0010001-0001-4001-8001-000000000022',
    'TRAINING_PROGRAMS',
    'License training programs and workflow requests',
    'UI',
    '/licenses/training-programs',
    NOW()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, name, description, created_at, resource_id)
VALUES (
    'e0020001-0001-4001-8001-000000000022',
    'TRAINING_PROGRAMS',
    'Access: license training programs',
    NOW(),
    'c0010001-0001-4001-8001-000000000022'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission_scopes (permission_id, scope_id)
VALUES ('e0020001-0001-4001-8001-000000000022', '8fe9c10f-5f7a-467d-b479-a44dde150cc7'),
       ('e0020001-0001-4001-8001-000000000022', '56fc119f-9788-4743-b115-1558d6bddebf'),
       ('e0020001-0001-4001-8001-000000000022', 'cf5afa07-7915-42e0-ade1-03abb0523972'),
       ('e0020001-0001-4001-8001-000000000022', 'f6e8bf5f-423b-4cf4-80eb-e2ba9adae2be'),
       ('e0020001-0001-4001-8001-000000000022', '84543f6e-8a3a-44bc-b390-02049ba0035b'),
       ('e0020001-0001-4001-8001-000000000022', 'b37caad4-8ae8-4aff-aeff-6b93f8c2560c'),
       ('e0020001-0001-4001-8001-000000000022', 'e1da5d22-aa98-4849-b178-64d945475730')
ON CONFLICT (permission_id, scope_id) DO NOTHING;

INSERT INTO policies (id, name, description, effect, created_at)
VALUES (
    'e0010001-0001-4001-8001-000000000100',
    'TRAINING_PROGRAMS',
    'Applicant: create and submit training programs',
    'ALLOW',
    NOW()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission_policies (permission_id, policy_id)
VALUES ('e0020001-0001-4001-8001-000000000022', '9cd6da93-36f0-49b7-a923-857c06f78b58'),
       ('e0020001-0001-4001-8001-000000000022', '63887a2b-7671-4bcf-aa1b-523785c3109f'),
       ('e0020001-0001-4001-8001-000000000022', 'fadbe3a8-4388-4015-95be-37de382fdfbd'),
       ('e0020001-0001-4001-8001-000000000022', 'e0010001-0001-4001-8001-000000000100'),
       ('90399595-22ad-4e5c-bfa1-f6fb215c2a4e', 'e0010001-0001-4001-8001-000000000100')
ON CONFLICT (permission_id, policy_id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (
    25,
    21,
    'Training programs',
    '/licenses/training-programs',
    NULL,
    2,
    'c0010001-0001-4001-8001-000000000022'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (
    26,
    21,
    'Review training requests',
    '/licenses/training-programs/requests',
    NULL,
    3,
    'c0010001-0001-4001-8001-000000000022'
)
ON CONFLICT (id) DO NOTHING;
