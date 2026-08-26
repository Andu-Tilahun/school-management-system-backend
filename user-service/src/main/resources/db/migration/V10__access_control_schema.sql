CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Scope rows are allowed actions on a resource (linked via permission_scopes).
CREATE TABLE scopes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    type        VARCHAR(100) NOT NULL,
    uri_pattern VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    resource_id UUID       NOT NULL
        REFERENCES resources(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE permission_scopes (
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    scope_id      UUID NOT NULL REFERENCES scopes(id)      ON DELETE RESTRICT,
    PRIMARY KEY (permission_id, scope_id)
);

CREATE TABLE policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    effect      VARCHAR(10)  NOT NULL DEFAULT 'ALLOW'
                    CHECK (effect IN ('ALLOW', 'DENY')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE permission_policies (
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    policy_id     UUID NOT NULL REFERENCES policies(id)    ON DELETE CASCADE,
    PRIMARY KEY (permission_id, policy_id)
);

CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE group_policies (
    group_id  UUID NOT NULL REFERENCES groups(id)   ON DELETE CASCADE,
    policy_id UUID NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, policy_id)
);

CREATE TABLE IF NOT EXISTS user_policies (
    user_id   UUID   NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    policy_id UUID NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, policy_id)
    );



CREATE TABLE user_groups (
    user_id  UUID NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_user_groups_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_groups_group
        FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

CREATE INDEX idx_permissions_resource_id    ON permissions(resource_id);
CREATE INDEX idx_permission_scopes_scope    ON permission_scopes(scope_id);
CREATE INDEX idx_permission_policies_policy ON permission_policies(policy_id);
CREATE INDEX idx_group_policies_policy      ON group_policies(policy_id);
CREATE INDEX idx_user_groups_group          ON user_groups(group_id);
CREATE INDEX idx_user_policies_policy       ON user_policies(policy_id);
