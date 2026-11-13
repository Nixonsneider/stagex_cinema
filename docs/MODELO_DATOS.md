# Modelo de datos V1.0

## Entidades principales

| Tabla | Propósito | Campos esenciales |
|---|---|---|
| `users` | Clientes y personal | `id`, `name`, `email`, `password_hash`, `role`, `status`, timestamps |
| `cinemas` | Cine operado | `id`, `name`, `address`, `timezone`, `status` |
| `user_cinemas` | Alcance del administrador | `user_id`, `cinema_id` |
| `movies` | Catálogo | `id`, `title`, `synopsis`, `duration_minutes`, `age_rating`, `language`, `format`, `poster_url`, `trailer_url`, `status` |
| `rooms` | Salas físicas | `id`, `cinema_id`, `name`, `status` |
| `seats` | Mapa base de la sala | `id`, `room_id`, `row_label`, `seat_number`, `seat_type`, `status` |
| `screenings` | Función programada | `id`, `movie_id`, `room_id`, `starts_at`, `ends_at`, `price`, `status` |
| `screening_seats` | Estado de cada asiento en una función | `id`, `screening_id`, `seat_id`, `status`, `hold_expires_at`, `version` |
| `orders` | Compra | `id`, `user_id` nullable, `guest_email` nullable, `screening_id`, `status`, `total`, `currency`, `payment_reference` |
| `order_items` | Asientos comprados | `id`, `order_id`, `screening_seat_id`, `unit_price` |
| `tickets` | Entrada emitida | `id`, `order_item_id`, `qr_token_hash`, `status`, `validated_at`, `validated_by` |
| `audit_events` | Acciones críticas | `id`, `actor_id`, `action`, `entity_type`, `entity_id`, `metadata_json`, `created_at` |

## Relaciones

```text
cinema 1---N rooms 1---N seats
movie 1---N screenings N---1 room
screening 1---N screening_seats N---1 seat
order 1---N order_items 1---1 ticket
user N---N cinema
```

## Estados

```text
user:          PENDING_VERIFICATION | ACTIVE | DISABLED
movie/room:    ACTIVE | INACTIVE
screening:    SCHEDULED | CANCELLED | FINISHED
seat:          AVAILABLE | HELD | SOLD
order:         PENDING | PAID | REJECTED | EXPIRED | CANCELLED
ticket:        ISSUED | USED | CANCELLED | EXPIRED
```

## Restricciones obligatorias

- `users.email` único e indexado, comparado en minúsculas.
- `screening_seats(screening_id, seat_id)` único.
- `order_items(order_id, screening_seat_id)` único.
- `payment_reference` único cuando no sea nulo.
- `ends_at > starts_at`.
- `price >= 0`.
- No se permite más de una función solapada en una sala. Validar en servicio y reforzar con exclusión temporal si la estrategia de PostgreSQL lo permite.
- Los asientos vendidos se protegen con `SELECT ... FOR UPDATE` o equivalente JPA pesimista.
- Los tokens de QR se almacenan hasheados.

## Migraciones

Al iniciar el backend con el perfil `local`, Flyway aplica los scripts de `backend/src/main/resources/db/migration/` en orden de versión. En una base limpia, `V1__initial_schema.sql` registra la versión 1 en `flyway_schema_history` sin crear tablas de negocio. Cada cambio posterior de esquema requiere una migración nueva. Hibernate valida el esquema con `ddl-auto=validate`.

Para repetir la prueba de integración, define `STAGEX_TEST_DATABASE_URL` con la URL JDBC de una base PostgreSQL vacía y desechable que acepte las credenciales del perfil `local`. Ejecuta `./mvnw test` desde `backend/`. Sin esa variable, la prueba de migración se omite.

## Desarrollo local

El repositorio incluye [docker-compose.yml](../docker-compose.yml) para crear
una instancia PostgreSQL local con la base `stagex`, el usuario `stagex` y el
puerto publicado `5433` (PostgreSQL escucha `5432` dentro del contenedor). El perfil Spring `local` está en
`backend/src/main/resources/application-local.properties` y obtiene la
conexión de `DATABASE_URL`, `DATABASE_USERNAME` y `DATABASE_PASSWORD`.

Los valores predeterminados del Compose son únicamente para desarrollo local y
no deben utilizarse en producción.

## Índices mínimos

- `movies(title)`, `movies(status)`.
- `screenings(movie_id, starts_at, status)`.
- `screening_seats(screening_id, status)`.
- `orders(created_at, status, screening_id)`.
- `tickets(qr_token_hash)` único.
