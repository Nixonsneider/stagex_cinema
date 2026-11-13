CREATE TABLE movies (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cinema_id uuid NOT NULL REFERENCES cinemas (id),
    title text NOT NULL CHECK (btrim(title) <> ''),
    synopsis text NOT NULL CHECK (btrim(synopsis) <> ''),
    duration_minutes integer NOT NULL CHECK (duration_minutes > 0),
    genre text NOT NULL CHECK (btrim(genre) <> ''),
    age_rating text NOT NULL CHECK (btrim(age_rating) <> ''),
    language text NOT NULL CHECK (btrim(language) <> ''),
    format text NOT NULL CHECK (btrim(format) <> ''),
    cast_members text,
    poster_url text NOT NULL CHECK (btrim(poster_url) <> ''),
    trailer_url text,
    status text NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX movies_cinema_idx ON movies (cinema_id);
CREATE INDEX movies_title_idx ON movies (title);
CREATE INDEX movies_status_idx ON movies (status);

CREATE TABLE rooms (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cinema_id uuid NOT NULL REFERENCES cinemas (id),
    name text NOT NULL CHECK (name <> '' AND name = btrim(name)),
    room_type text NOT NULL CHECK (btrim(room_type) <> ''),
    status text NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE UNIQUE INDEX rooms_cinema_name_uq ON rooms (cinema_id, lower(name));

CREATE TABLE seats (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id uuid NOT NULL REFERENCES rooms (id),
    row_label text NOT NULL CHECK (row_label <> '' AND row_label = upper(btrim(row_label))),
    seat_number integer NOT NULL CHECK (seat_number > 0),
    seat_type text NOT NULL DEFAULT 'STANDARD' CHECK (btrim(seat_type) <> ''),
    status text NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    UNIQUE (room_id, row_label, seat_number)
);
