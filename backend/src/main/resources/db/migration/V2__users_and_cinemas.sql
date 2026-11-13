CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL CHECK (btrim(name) <> ''),
    email text NOT NULL CHECK (email <> '' AND email = btrim(email)),
    password_hash text NOT NULL CHECK (btrim(password_hash) <> ''),
    role text NOT NULL DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'CASHIER', 'CINEMA_ADMIN')),
    status text NOT NULL DEFAULT 'PENDING_VERIFICATION'
        CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'DISABLED')),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX users_email_lower_uq ON users (lower(email));

CREATE TABLE cinemas (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL CHECK (btrim(name) <> ''),
    address text NOT NULL CHECK (btrim(address) <> ''),
    timezone text NOT NULL CHECK (btrim(timezone) <> ''),
    status text NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE user_cinemas (
    user_id uuid NOT NULL REFERENCES users (id),
    cinema_id uuid NOT NULL REFERENCES cinemas (id),
    PRIMARY KEY (user_id, cinema_id)
);

CREATE INDEX user_cinemas_cinema_idx ON user_cinemas (cinema_id);
