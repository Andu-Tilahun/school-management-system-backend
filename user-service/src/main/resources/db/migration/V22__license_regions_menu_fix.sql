-- V20 uses menu id 22 for Farm > "Review product requests". V21 reused id 22 for Licenses > Regions,
-- so the Regions INSERT conflicted and was skipped (ON CONFLICT DO NOTHING) while Organizations (id 23) succeeded.
-- Add Regions under the Licenses parent (id 21) with a free menu id.

INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (24, 21, 'Regions', '/licenses/regions', NULL, 0, 'c0010001-0001-4001-8001-000000000020')
ON CONFLICT (id) DO NOTHING;
