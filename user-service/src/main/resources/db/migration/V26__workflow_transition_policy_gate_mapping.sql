-- Task 7.1: Map legacy workflow step policy_id gates to user-service policies.
-- Zion uses policy-based transition auth (required_policy_name / required_policy_id) instead of
-- license-service roleId. This reference table documents the legacy → policy mapping for admin UI
-- and cross-service alignment with workflow-service V14.

CREATE TABLE IF NOT EXISTS workflow_step_policy_gate (
    legacy_policy_id VARCHAR(128) NOT NULL,
    policy_id        UUID         NOT NULL,
    policy_name      VARCHAR(128) NOT NULL,
    description      TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_workflow_step_policy_gate PRIMARY KEY (legacy_policy_id),
    CONSTRAINT fk_workflow_step_policy_gate_policy
        FOREIGN KEY (policy_id) REFERENCES policies (id) ON DELETE RESTRICT,
    CONSTRAINT uq_workflow_step_policy_gate_policy_name UNIQUE (policy_name)
);

COMMENT ON TABLE workflow_step_policy_gate IS
    'Legacy workflow_steps.policy_id / assign_expert_policy_id → policies.id (zion roleId equivalent for transitions).';

INSERT INTO workflow_step_policy_gate (legacy_policy_id, policy_id, policy_name, description)
VALUES
    (
        'PRODUCT_DEMO_APPLICANT',
        'e0010001-0001-4001-8001-000000000099',
        'FARM_PRODUCT_DEMO_APPLICANT',
        'Farm product demo applicant submit gate (V5 DRAFT step)'
    ),
    (
        'TRAINING_PROGRAM_APPLICANT',
        'e0010001-0001-4001-8001-000000000100',
        'TRAINING_PROGRAMS',
        'Training program applicant submit gate (V7 DRAFT step)'
    ),
    (
        'CLEARING_AGENT_APPLICANT',
        'e0010001-0001-4001-8001-000000000101',
        'CLEARING_AGENT_APPLICANT',
        'Clearing agent applicant submit gate (V8 DRAFT step)'
    ),
    (
        'OPERATOR_POLICY',
        '63887a2b-7671-4bcf-aa1b-523785c3109f',
        'OPERATOR_POLICY',
        'Officer review / assign-expert transitions (V5/V7/V8 review steps)'
    ),
    (
        'EXTENSION_WORKER_ASSIGNED',
        'fadbe3a8-4388-4015-95be-37de382fdfbd',
        'EXTENSION_WORKER_ASSIGNED',
        'Assigned field expert approval (V5 EXPERT_DECISION step, assign_expert_policy_id)'
    )
ON CONFLICT (legacy_policy_id) DO UPDATE
SET policy_id   = EXCLUDED.policy_id,
    policy_name = EXCLUDED.policy_name,
    description = EXCLUDED.description;
