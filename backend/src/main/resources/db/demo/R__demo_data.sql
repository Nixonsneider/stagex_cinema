-- T-019: datos demo para desarrollo local. Solo se cargan con el perfil "local"
-- (spring.flyway.locations incluye classpath:db/demo). NUNCA usar en producción:
-- las contraseñas de estas cuentas son públicas.
--
-- Cuentas (contraseña de todas: StageX2026; la misma del mock del frontend):
--   cliente@stagex.test    CUSTOMER      activa
--   taquilla@stagex.test   CASHIER       activa, asignada al cine demo
--   admin@stagex.test      CINEMA_ADMIN  activa, asignada al cine demo
--   pendiente@stagex.test  CUSTOMER      pendiente de verificación
--
-- Es una migración repetible: Flyway la vuelve a ejecutar cuando cambia su contenido.
-- Todos los INSERT usan identificadores fijos y ON CONFLICT DO NOTHING, así que es idempotente.
-- Las funciones se programan desde el día siguiente a la primera carga; para regenerarlas
-- se reinicia la base local con "docker compose down -v".

INSERT INTO users (id, name, email, password_hash, role, status) VALUES
    ('00000000-0000-4000-8000-000000000001', 'Cliente Demo', 'cliente@stagex.test',
     '$2a$10$sz3I7CpMIT2trXF3hjM1ZuiPm6Ps1WhMI6VJT0kivxyx6Ajj1zxRa', 'CUSTOMER', 'ACTIVE'),
    ('00000000-0000-4000-8000-000000000002', 'Taquilla Demo', 'taquilla@stagex.test',
     '$2a$10$xtwH.xZt6weJrPoxU6uKEOKqUpfcSc.VHOQyoEfH3rwSGxVrQkjvq', 'CASHIER', 'ACTIVE'),
    ('00000000-0000-4000-8000-000000000003', 'Administrador Demo', 'admin@stagex.test',
     '$2a$10$fYfybSHgTgpZLy7pNapQE.lbV7QRCuH0H/.Um/qfW8TUMHu/Ggxu2', 'CINEMA_ADMIN', 'ACTIVE'),
    ('00000000-0000-4000-8000-000000000004', 'Cliente Pendiente', 'pendiente@stagex.test',
     '$2a$10$sfcesusaCcS4vDto7mFJ9uSzH.MshHyifoqWsDXSkxI/kcUPrYbMq', 'CUSTOMER', 'PENDING_VERIFICATION')
ON CONFLICT DO NOTHING;

INSERT INTO cinemas (id, name, address, timezone) VALUES
    ('00000000-0000-4000-8000-000000000101', 'Stage X Centro', 'Avenida 0 # 10-20, Cúcuta', 'America/Bogota')
ON CONFLICT DO NOTHING;

INSERT INTO user_cinemas (user_id, cinema_id) VALUES
    ('00000000-0000-4000-8000-000000000002', '00000000-0000-4000-8000-000000000101'),
    ('00000000-0000-4000-8000-000000000003', '00000000-0000-4000-8000-000000000101')
ON CONFLICT DO NOTHING;

INSERT INTO rooms (id, cinema_id, name, room_type) VALUES
    ('00000000-0000-4000-8000-000000000201', '00000000-0000-4000-8000-000000000101', 'Sala 1', '2D'),
    ('00000000-0000-4000-8000-000000000202', '00000000-0000-4000-8000-000000000101', 'Sala 2', '3D')
ON CONFLICT DO NOTHING;

-- Sala 1: filas A-H con 12 asientos. Sala 2: filas A-F con 10 asientos.
INSERT INTO seats (id, room_id, row_label, seat_number)
SELECT md5('stagex-demo-seat-' || r.id || '-' || chr(64 + fila) || '-' || numero)::uuid,
       r.id, chr(64 + fila), numero
FROM (VALUES ('00000000-0000-4000-8000-000000000201'::uuid, 8, 12),
             ('00000000-0000-4000-8000-000000000202'::uuid, 6, 10)) AS r (id, filas, columnas)
CROSS JOIN LATERAL generate_series(1, r.filas) AS fila
CROSS JOIN LATERAL generate_series(1, r.columnas) AS numero
ON CONFLICT DO NOTHING;

INSERT INTO movies (id, cinema_id, title, synopsis, duration_minutes, genre, age_rating, language, format,
                    cast_members, poster_url) VALUES
    ('00000000-0000-4000-8000-000000000301', '00000000-0000-4000-8000-000000000101', 'El Último Faro',
     'Una guardiana de faro en la costa Caribe descubre que las luces que ve cada noche no vienen del mar.',
     118, 'Drama', '12+', 'es', '2D', 'Reparto demo', '/posters/el-ultimo-faro.jpg'),
    ('00000000-0000-4000-8000-000000000302', '00000000-0000-4000-8000-000000000101', 'Órbita Cero',
     'La tripulación de una estación abandonada debe decidir a quién creerle cuando la Tierra deja de responder.',
     132, 'Ciencia ficción', '12+', 'es', '3D', 'Reparto demo', '/posters/orbita-cero.jpg'),
    ('00000000-0000-4000-8000-000000000303', '00000000-0000-4000-8000-000000000101', 'Risas en el Barrio',
     'Una familia organiza el torneo de microfútbol más caótico que ha visto su conjunto residencial.',
     95, 'Comedia', 'TODOS', 'es', '2D', 'Reparto demo', '/posters/risas-en-el-barrio.jpg'),
    ('00000000-0000-4000-8000-000000000304', '00000000-0000-4000-8000-000000000101', 'Sombras de Medianoche',
     'Cuatro amigos pasan la noche en una casona de Pamplona donde los relojes nunca marcan la misma hora.',
     104, 'Terror', '18+', 'es', '2D', 'Reparto demo', '/posters/sombras-de-medianoche.jpg')
ON CONFLICT DO NOTHING;

-- Tres días de funciones desde mañana (hora de Bogotá). Cada función reserva 15 minutos
-- de limpieza después de la película, así que nunca se solapan en la misma sala.
INSERT INTO screenings (id, movie_id, room_id, starts_at, ends_at, price, currency)
SELECT md5('stagex-demo-screening-' || p.movie_id || '-' || p.hora || '-' || dia)::uuid,
       p.movie_id, p.room_id, inicio, inicio + make_interval(mins => m.duration_minutes + 15), p.precio, 'COP'
FROM (VALUES
        ('00000000-0000-4000-8000-000000000301'::uuid, '00000000-0000-4000-8000-000000000201'::uuid, '15:00', 18000),
        ('00000000-0000-4000-8000-000000000302'::uuid, '00000000-0000-4000-8000-000000000202'::uuid, '18:00', 24000),
        ('00000000-0000-4000-8000-000000000303'::uuid, '00000000-0000-4000-8000-000000000202'::uuid, '15:30', 24000),
        ('00000000-0000-4000-8000-000000000304'::uuid, '00000000-0000-4000-8000-000000000201'::uuid, '21:00', 18000),
        ('00000000-0000-4000-8000-000000000303'::uuid, '00000000-0000-4000-8000-000000000201'::uuid, '18:00', 18000)
     ) AS p (movie_id, room_id, hora, precio)
JOIN movies m ON m.id = p.movie_id
CROSS JOIN generate_series(1, 3) AS dia
CROSS JOIN LATERAL (
    SELECT ((date_trunc('day', now() AT TIME ZONE 'America/Bogota') + make_interval(days => dia))::date
            + p.hora::time) AT TIME ZONE 'America/Bogota' AS inicio
) AS t
ON CONFLICT DO NOTHING;

-- Estado inicial de cada asiento en cada función.
INSERT INTO screening_seats (screening_id, seat_id)
SELECT sc.id, se.id
FROM screenings sc
JOIN seats se ON se.room_id = sc.room_id
WHERE sc.room_id IN ('00000000-0000-4000-8000-000000000201', '00000000-0000-4000-8000-000000000202')
ON CONFLICT DO NOTHING;
