-- Navigation lives in `menu`; `resources` stays the RBAC catalog only (no menu columns).

CREATE TABLE menu (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT REFERENCES menu (id) ON DELETE CASCADE,
    label       VARCHAR(255) NOT NULL,
    route       VARCHAR(500),
    icon        VARCHAR(100),
    sort_order  INT         NOT NULL DEFAULT 0,
    resource_id UUID REFERENCES resources (id) ON DELETE SET NULL
);

CREATE INDEX idx_menu_parent ON menu (parent_id);
CREATE INDEX idx_menu_resource ON menu (resource_id);
