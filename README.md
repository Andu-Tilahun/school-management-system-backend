# zion-ng-backend

Spring Boot microservices for Zion-ng (gateway, workflow, farm, license, user, and shared commons).

## Workflow (current architecture)

User-request workflows use an **enum transition** model:

- **Identity:** `UserRequestType` (e.g. `PRODUCT_DEMO`)
- **Position:** `WorkflowStatuses` (e.g. `DRAFT`, `SUBMITTED`)
- **Edges:** `currentStatus` → `nextStatus` with `requiredPolicyName` (JWT policy gate)
- **Process:** `PUT …/user-requests/process` with `{ "status": "SUBMITTED", … }` — not `selectedOutcomeId`

**Read first:** [`docs/Workflow_Architecture_Enum_Model.md`](docs/Workflow_Architecture_Enum_Model.md)  
**Decision log (ADR):** [`docs/ADR-workflow-enum.md`](docs/ADR-workflow-enum.md)  
**Architecture sign-off:** [`docs/Signoff_11.3_Architecture_Review_Enum_Workflows.md`](docs/Signoff_11.3_Architecture_Review_Enum_Workflows.md)  
**Docs index:** [`docs/README.md`](docs/README.md)

Quick examples:

```http
GET  /api/farm/user-requests/id/{id}/transitions
PUT  /api/farm/user-requests/process
GET  /api/workflows/userRequestType/{type}/workflowStatuses/{status}
POST /api/workflows
```

Farm vs license differ by base path (`/api/farm/user-requests` vs `/api/license/user-requests`).

## Modules (high level)

| Path | Role |
|------|------|
| `api-gateway` | HTTP entry |
| `workflow-service` | Workflow admin + internal Feign APIs |
| `farm-service` / `license-service` | Domain + embedded user-request APIs |
| `common/common-userrequest` | Shared user-request workflow engine |
| `common/common-application` | Shared enums and DTOs |
| `user-service` | Auth / policies / users |

Each service is built from its own directory (`mvn -DskipTests package`).

## Migration from step/outcome

Legacy step UUID / outcome graph was cut over to enums (big-bang; no dual-read). See:

- [`docs/API_Changelog_Workflow_Enum_Migration.md`](docs/API_Changelog_Workflow_Enum_Migration.md)
- [`docs/Migration_Order_10.2_Enum_Workflow_Cutover.md`](docs/Migration_Order_10.2_Enum_Workflow_Cutover.md)
- [`docs/Workflow_StepOutcome_to_Enum_Conversion_Plan.csv`](docs/Workflow_StepOutcome_to_Enum_Conversion_Plan.csv)

## Frontend

Angular app: [`../zion-ng-frontend`](../zion-ng-frontend) — see its README for UI, QA, and `npm run e2e`.
