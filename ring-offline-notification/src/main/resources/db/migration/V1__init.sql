CREATE TABLE users (
    user_id      VARCHAR(64)  PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE devices (
    device_id    VARCHAR(64)  PRIMARY KEY,
    user_id      VARCHAR(64)  NOT NULL REFERENCES users(user_id),
    device_name  VARCHAR(255) NOT NULL,
    state        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                 CHECK (state IN ('ACTIVE', 'DEPROVISIONED')),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_devices_user_id ON devices(user_id);
CREATE INDEX idx_devices_state ON devices(state);
