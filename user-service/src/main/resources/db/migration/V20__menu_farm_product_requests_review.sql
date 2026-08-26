-- Second entry under Farm for the same FARM_PRODUCTS resource: inbox / review queue (matches Angular /farm-products/requests).
INSERT INTO menu (id, parent_id, label, route, icon, sort_order, resource_id)
VALUES (
           22,
           10,
           'Review product requests',
           '/farm-products/requests',
           NULL,
           7,
           'c0010001-0001-4001-8001-000000000001'
       )
ON CONFLICT (id) DO NOTHING;
