-- Task 7.1b: workflow admin UI moved from farm step/outcome management (/farm-workflows)
-- to platform-wide enum transition management (/workflows, workflow-service).
-- Rename the V16 FARM_WORKFLOWS resource/permission in place so existing
-- permission_scopes / permission_policies grants (V16, V18) carry over unchanged.
-- Backend gate: workflow-service WorkflowController @RequiresPermission(resource = "WORKFLOWS").
-- Frontend gate: /workflows route with permissionResource WORKFLOWS.

UPDATE resources
SET name        = 'WORKFLOWS',
    description = 'Workflow definitions and transition management (all request types)',
    uri_pattern = '/workflows'
WHERE id = 'b2c3d4e5-f6a7-4890-b1c2-d3e4f5a6b7c8'
  AND name = 'FARM_WORKFLOWS';

UPDATE permissions
SET name        = 'WORKFLOWS',
    description = 'Access: workflow transition management'
WHERE id = 'c3d4e5f6-a7b8-4901-c2d3-e4f5a6b7c8d9'
  AND name = 'FARM_WORKFLOWS';

-- Menu 19 (V17): promote from Farm submenu to top-level entry; workflows now cover
-- farm and license request types alike.
UPDATE menu
SET parent_id  = NULL,
    label      = 'Workflows',
    route      = '/workflows',
    icon       = 'apartment',
    sort_order = 4
WHERE id = 19;

-- Policy grants unchanged: ADMIN_ALL_FEATURES (V16/V18) manages transitions; OPERATOR_POLICY (V16)
-- retains READ — user-request detail pages still fetch workflow definitions to label transitions.
