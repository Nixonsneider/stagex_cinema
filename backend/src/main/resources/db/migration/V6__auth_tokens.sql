-- T-014 a T-017: tokens de verificación de correo, recuperación de contraseña y sesión.
-- Ningún token se guarda en claro: solo su hash SHA-256 en hexadecimal minúscula.

CREATE TABLE account_tokens (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users (id),
    purpose text NOT NULL CHECK (purpose IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET')),
    token_hash text NOT NULL CHECK (token_hash ~ '^[0-9a-f]{64}$'),
    expires_at timestamptz NOT NULL,
    used_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT account_tokens_expiry_ck CHECK (expires_at > created_at)
);

CREATE UNIQUE INDEX account_tokens_hash_uq ON account_tokens (token_hash);
-- Permite invalidar los enlaces pendientes de un usuario cuando se emite uno nuevo.
CREATE INDEX account_tokens_user_purpose_idx ON account_tokens (user_id, purpose) WHERE used_at IS NULL;

CREATE TABLE refresh_tokens (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users (id),
    token_hash text NOT NULL CHECK (token_hash ~ '^[0-9a-f]{64}$'),
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT refresh_tokens_expiry_ck CHECK (expires_at > created_at)
);

CREATE UNIQUE INDEX refresh_tokens_hash_uq ON refresh_tokens (token_hash);
-- Permite revocar todas las sesiones activas de un usuario (cambio de contraseña).
CREATE INDEX refresh_tokens_user_active_idx ON refresh_tokens (user_id) WHERE revoked_at IS NULL;
