-- T-012: funciones programadas y estado independiente de cada asiento por función.

-- btree_gist permite combinar igualdad (room_id) y solapamiento de rangos en una restricción EXCLUDE.
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE screenings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_id uuid NOT NULL REFERENCES movies (id),
    room_id uuid NOT NULL REFERENCES rooms (id),
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    price numeric(12, 2) NOT NULL CHECK (price >= 0),
    currency char(3) NOT NULL DEFAULT 'COP' CHECK (currency ~ '^[A-Z]{3}$'),
    status text NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'CANCELLED', 'FINISHED')),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT screenings_time_range_ck CHECK (ends_at > starts_at),
    -- Dos funciones programadas no pueden ocupar la misma sala al mismo tiempo.
    -- El rango es semiabierto: una función puede empezar justo cuando termina la anterior.
    -- Las funciones canceladas o finalizadas no bloquean el horario.
    CONSTRAINT screenings_room_no_overlap EXCLUDE USING gist (
        room_id WITH =,
        tstzrange(starts_at, ends_at, '[)') WITH &&
    ) WHERE (status = 'SCHEDULED')
);

CREATE INDEX screenings_movie_starts_status_idx ON screenings (movie_id, starts_at, status);
CREATE INDEX screenings_room_starts_idx ON screenings (room_id, starts_at);

CREATE TABLE screening_seats (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    screening_id uuid NOT NULL REFERENCES screenings (id),
    seat_id uuid NOT NULL REFERENCES seats (id),
    status text NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'HELD', 'SOLD')),
    hold_expires_at timestamptz,
    -- Control optimista para JPA (@Version); el bloqueo de compra usa SELECT ... FOR UPDATE.
    version bigint NOT NULL DEFAULT 0 CHECK (version >= 0),
    CONSTRAINT screening_seats_screening_seat_uq UNIQUE (screening_id, seat_id),
    -- Solo un asiento bloqueado tiene vencimiento; disponible y vendido no lo tienen.
    CONSTRAINT screening_seats_hold_expiry_ck CHECK ((status = 'HELD') = (hold_expires_at IS NOT NULL))
);

CREATE INDEX screening_seats_screening_status_idx ON screening_seats (screening_id, status);
CREATE INDEX screening_seats_seat_idx ON screening_seats (seat_id);
-- Permite al job de expiración encontrar rápido los bloqueos vencidos.
CREATE INDEX screening_seats_held_expiry_idx ON screening_seats (hold_expires_at) WHERE status = 'HELD';

-- El asiento debe pertenecer a la sala donde se proyecta la función.
-- Si la función o el asiento no existen, la llave foránea reporta el error (23503).
CREATE FUNCTION screening_seats_check_room() RETURNS trigger
    LANGUAGE plpgsql AS
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM screenings sc, seats se
        WHERE sc.id = NEW.screening_id
          AND se.id = NEW.seat_id
          AND se.room_id <> sc.room_id
    ) THEN
        RAISE EXCEPTION 'El asiento % no pertenece a la sala de la función %', NEW.seat_id, NEW.screening_id
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER screening_seats_room_trg
    BEFORE INSERT OR UPDATE OF screening_id, seat_id ON screening_seats
    FOR EACH ROW EXECUTE FUNCTION screening_seats_check_room();
