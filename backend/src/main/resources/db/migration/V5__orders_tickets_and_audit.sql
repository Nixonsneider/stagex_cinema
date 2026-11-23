-- T-013: órdenes de compra, asientos comprados, entradas con QR y bitácora de acciones críticas.

CREATE TABLE orders (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES users (id),
    guest_email text CHECK (guest_email <> '' AND guest_email = lower(btrim(guest_email))),
    screening_id uuid NOT NULL REFERENCES screenings (id),
    status text NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PAID', 'REJECTED', 'EXPIRED', 'CANCELLED')),
    total numeric(12, 2) NOT NULL CHECK (total >= 0),
    currency char(3) NOT NULL DEFAULT 'COP' CHECK (currency ~ '^[A-Z]{3}$'),
    payment_reference text CHECK (btrim(payment_reference) <> ''),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- La orden es de un cliente registrado o de un invitado, nunca de ambos ni de ninguno.
    CONSTRAINT orders_buyer_ck CHECK ((user_id IS NULL) <> (guest_email IS NULL)),
    -- Una orden pagada siempre conserva la referencia del proveedor.
    CONSTRAINT orders_paid_reference_ck CHECK (status <> 'PAID' OR payment_reference IS NOT NULL)
);

-- Una referencia de pago no puede confirmar dos órdenes. Varias órdenes pueden no tener referencia aún.
CREATE UNIQUE INDEX orders_payment_reference_uq ON orders (payment_reference) WHERE payment_reference IS NOT NULL;
CREATE INDEX orders_created_status_screening_idx ON orders (created_at, status, screening_id);
CREATE INDEX orders_screening_idx ON orders (screening_id);
CREATE INDEX orders_user_idx ON orders (user_id) WHERE user_id IS NOT NULL;

CREATE TABLE order_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id uuid NOT NULL REFERENCES orders (id),
    screening_seat_id uuid NOT NULL REFERENCES screening_seats (id),
    unit_price numeric(12, 2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT order_items_order_seat_uq UNIQUE (order_id, screening_seat_id)
);

CREATE INDEX order_items_screening_seat_idx ON order_items (screening_seat_id);

-- El asiento comprado debe ser de la misma función que la orden.
-- Si la orden o el asiento no existen, la llave foránea reporta el error (23503).
CREATE FUNCTION order_items_check_screening() RETURNS trigger
    LANGUAGE plpgsql AS
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM orders o, screening_seats ss
        WHERE o.id = NEW.order_id
          AND ss.id = NEW.screening_seat_id
          AND ss.screening_id <> o.screening_id
    ) THEN
        RAISE EXCEPTION 'El asiento % no pertenece a la función de la orden %', NEW.screening_seat_id, NEW.order_id
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER order_items_screening_trg
    BEFORE INSERT OR UPDATE OF order_id, screening_seat_id ON order_items
    FOR EACH ROW EXECUTE FUNCTION order_items_check_screening();

CREATE TABLE tickets (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id uuid NOT NULL UNIQUE REFERENCES order_items (id),
    -- Hash SHA-256 en hexadecimal (QrTokenService). El token en claro nunca se guarda.
    qr_token_hash text NOT NULL CHECK (qr_token_hash ~ '^[0-9a-f]{64}$'),
    status text NOT NULL DEFAULT 'ISSUED' CHECK (status IN ('ISSUED', 'USED', 'CANCELLED', 'EXPIRED')),
    validated_at timestamptz,
    validated_by uuid REFERENCES users (id),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Solo una entrada usada tiene fecha y responsable de validación, y siempre los tiene.
    CONSTRAINT tickets_validation_ck CHECK (
        (status = 'USED') = (validated_at IS NOT NULL)
        AND (validated_at IS NULL) = (validated_by IS NULL)
    )
);

CREATE UNIQUE INDEX tickets_qr_token_hash_uq ON tickets (qr_token_hash);
CREATE INDEX tickets_validated_by_idx ON tickets (validated_by) WHERE validated_by IS NOT NULL;

CREATE TABLE audit_events (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Nulo cuando la acción la origina el sistema (job de expiración, webhook de pago).
    actor_id uuid REFERENCES users (id),
    action text NOT NULL CHECK (action ~ '^[A-Z][A-Z0-9_]*$'),
    entity_type text NOT NULL CHECK (btrim(entity_type) <> ''),
    entity_id uuid,
    metadata_json jsonb NOT NULL DEFAULT '{}'::jsonb CHECK (jsonb_typeof(metadata_json) = 'object'),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX audit_events_created_idx ON audit_events (created_at);
CREATE INDEX audit_events_entity_idx ON audit_events (entity_type, entity_id);
CREATE INDEX audit_events_actor_idx ON audit_events (actor_id) WHERE actor_id IS NOT NULL;

-- La bitácora solo admite inserciones: un evento registrado no se modifica ni se borra.
CREATE FUNCTION audit_events_append_only() RETURNS trigger
    LANGUAGE plpgsql AS
$$
BEGIN
    RAISE EXCEPTION 'audit_events solo admite inserciones'
        USING ERRCODE = 'restrict_violation';
END;
$$;

CREATE TRIGGER audit_events_append_only_trg
    BEFORE UPDATE OR DELETE ON audit_events
    FOR EACH ROW EXECUTE FUNCTION audit_events_append_only();
