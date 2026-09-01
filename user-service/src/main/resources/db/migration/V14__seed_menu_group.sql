INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (2, null, 'Users', null, 'team', 1, null);
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (4, 2, 'Access Control', null, null, 1, null);
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (1, null, 'Home', '/home', 'home', 0, '6f6de57b-5efb-4880-96f1-fa3e94f31bef');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (3, 2, 'All Users', '/users', null, 0, '96900bfc-3f56-4030-b950-85b9e3dcf9c2');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (5, 4, 'Scopes', '/scopes', null, 0, 'cfae8b07-41b1-4a29-9ce1-bc817762db5d');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (6, 4, 'Resources', '/resources', null, 1, '1541dc74-1d7c-4ac0-a195-2269bc5402b6');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (7, 4, 'Permissions', '/permissions', null, 2, 'c3fc2cbc-dcd5-433c-8feb-dfc459db78ca');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (8, 4, 'Policies', '/policies', null, 3, '4fb75048-64ac-4314-a5d0-04e698d75a0d');
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id) VALUES (9, 4, 'Groups', '/groups', null, 4, 'b49f9ea7-39a2-487b-a234-70cdbb8e44e1');


INSERT INTO groups (name, description, created_at, id) VALUES ('ADMIN_FULL_ACCESS', 'Full UI and API access for administrators', '2026-05-02 11:09:26.275589', '0aa037da-fe8a-4133-a971-536196f202d8');
INSERT INTO group_policies (group_id, policy_id) VALUES ('0aa037da-fe8a-4133-a971-536196f202d8', '9cd6da93-36f0-49b7-a923-857c06f78b58');
INSERT INTO user_groups (user_id, group_id) VALUES ('a1cec8bb-1cf1-40c2-9ab1-2a392af385e4', '0aa037da-fe8a-4133-a971-536196f202d8');
INSERT INTO user_policies (user_id, policy_id) VALUES ('a1cec8bb-1cf1-40c1-9ab1-2a492af385e4', '63887a2b-7671-4bcf-aa1b-523785c3109f');


