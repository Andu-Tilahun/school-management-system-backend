INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (1, NULL, 'Home', '/home', 'home', 0, '6f6de57b-5efb-4880-96f1-fa3e94f31bef');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (2, NULL, 'Users', NULL, 'team', 1, NULL);
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (3, 2, 'All Users', '/users', NULL, 0, '96900bfc-3f56-4030-b950-85b9e3dcf9c2');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (4, 2, 'Access Control', NULL, NULL, 1, NULL);
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (5, 4, 'Scopes', '/scopes', NULL, 0, 'cfae8b07-41b1-4a29-9ce1-bc817762db5d');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (6, 4, 'Resources', '/resources', NULL, 1, '1541dc74-1d7c-4ac0-a195-2269bc5402b6');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (7, 4, 'Permissions', '/permissions', NULL, 2, 'c3fc2cbc-dcd5-433c-8feb-dfc459db78ca');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (8, 4, 'Policies', '/policies', NULL, 3, '4fb75048-64ac-4314-a5d0-04e698d75a0d');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (9, 4, 'Groups', '/groups', NULL, 4, 'b49f9ea7-39a2-487b-a234-70cdbb8e44e1');

INSERT INTO groups (name, description, created_at, id) VALUES ('SUPER_ADMIN_FEATURES', 'Full UI and API access for administrators', '2026-05-02 11:09:26.275589', '0aa037da-fe8a-4133-a971-536196f202d8');
INSERT INTO groups (name, description, created_at, id) VALUES ('TENANT_MANAGER_POLICY', 'UI and API access for tenant managers', '2026-05-02 11:09:26.275589', 'e9ccb946-2cac-44c6-84a5-74d2ceb2f690');
INSERT INTO groups (name, description, created_at, id) VALUES ('SCHOOL_ADMIN_POLICY', 'UI and API access for school administrators', '2026-05-02 11:09:26.275589', 'd5f18de5-f7e6-4aa0-96eb-16cbb5b76110');
INSERT INTO group_policies (group_id, policy_id) VALUES ('0aa037da-fe8a-4133-a971-536196f202d8', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO group_policies (group_id, policy_id) VALUES ('e9ccb946-2cac-44c6-84a5-74d2ceb2f690', '09cb27d2-c7a8-4bd3-9d17-aa5b93786333');
INSERT INTO group_policies (group_id, policy_id) VALUES ('d5f18de5-f7e6-4aa0-96eb-16cbb5b76110', 'cd29816f-ec74-4720-8833-18ce2e169102');
INSERT INTO user_groups (user_id, group_id) VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '0aa037da-fe8a-4133-a971-536196f202d8');
INSERT INTO user_policies (user_id, policy_id) VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '9cd6da93-36f0-49b7-a923-857c06f78b58');
