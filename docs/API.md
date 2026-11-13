# Contrato de API REST V1.0

Base URL: `/api/v1`. Respuestas JSON. Fechas ISO-8601 con zona horaria. El contrato se implementa primero en backend y se consume desde React.

## Autenticación

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Crea cliente pendiente de verificación |
| `POST` | `/auth/verify-email` | Público | Confirma el correo y activa la cuenta |
| `POST` | `/auth/login` | Público | Devuelve access y refresh token |
| `POST` | `/auth/refresh` | Público | Rota refresh token |
| `POST` | `/auth/forgot-password` | Público | Respuesta neutra y enlace temporal |
| `POST` | `/auth/reset-password` | Público | Cambia contraseña |
| `POST` | `/auth/logout` | Autenticado | Revoca refresh token |

## Catálogo y funciones

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/movies?query=&genre=&ageRating=&page=` | Público |
| `GET` | `/movies/{movieId}` | Público |
| `GET` | `/movies/{movieId}/screenings?date=` | Público |
| `GET` | `/screenings/{screeningId}/seats` | Público |
| `GET` | `/admin/movies` | `CINEMA_ADMIN` |
| `POST` | `/admin/movies` | `CINEMA_ADMIN` |
| `PUT` | `/admin/movies/{movieId}` | `CINEMA_ADMIN` |
| `PATCH` | `/admin/movies/{movieId}/status` | `CINEMA_ADMIN` |
| `GET` | `/admin/rooms` | `CINEMA_ADMIN` |
| `GET` | `/admin/rooms/{roomId}` | `CINEMA_ADMIN` |
| `POST` | `/admin/rooms` | `CINEMA_ADMIN` |
| `PUT` | `/admin/rooms/{roomId}/seats` | `CINEMA_ADMIN` |
| `PATCH` | `/admin/rooms/{roomId}/status` | `CINEMA_ADMIN` |
| `GET` | `/admin/screenings` | `CINEMA_ADMIN` |
| `POST` | `/admin/screenings` | `CINEMA_ADMIN` |
| `PUT` | `/admin/screenings/{screeningId}` | `CINEMA_ADMIN` |
| `POST` | `/admin/screenings/{screeningId}/cancel` | `CINEMA_ADMIN` |

Las rutas `GET /admin/*` listan solo recursos de los cines asignados al administrador. `GET /admin/rooms/{roomId}` devuelve la sala con su mapa de asientos. `POST /admin/rooms` recibe `cinemaId` y se rechaza con `403` si el cine no pertenece al administrador.

Los listados paginados (`GET /movies` y los `GET /admin/*`) responden:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

## Compra y entradas

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/holds` | Público/autenticado |
| `DELETE` | `/holds/{holdId}` | Dueño de la reserva |
| `POST` | `/orders` | Público/autenticado |
| `GET` | `/orders/{orderId}` | Dueño o personal autorizado |
| `POST` | `/payments/webhook` | Proveedor, firma obligatoria |
| `GET` | `/me/tickets` | Cliente |
| `GET` | `/me/tickets/{ticketId}` | Dueño |
| `POST` | `/access/tickets/validate` | `CASHIER` |
| `GET` | `/admin/sales?from=&to=&screeningId=&status=` | `CASHIER`, `CINEMA_ADMIN` |

## Payloads críticos

Registro (`POST /auth/register`), respuesta `201`:

```json
{ "name": "Ana Pérez", "email": "ana@example.com", "password": "********" }
```

```json
{ "userId": "uuid", "status": "PENDING_VERIFICATION" }
```

El correo se normaliza a minúsculas. Un correo ya registrado responde `409 EMAIL_ALREADY_REGISTERED`.

Verificar correo (`POST /auth/verify-email`), respuesta `204`:

```json
{ "token": "opaque-token" }
```

El token es temporal y de un solo uso. Si es inválido o venció responde `400 INVALID_OR_EXPIRED_TOKEN`.

Inicio de sesión (`POST /auth/login`):

```json
{ "email": "ana@example.com", "password": "********" }
```

Respuesta (`expiresIn` en segundos; el valor lo define la configuración del backend):

```json
{
  "accessToken": "jwt",
  "refreshToken": "opaque-token",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": { "id": "uuid", "name": "Ana Pérez", "role": "CUSTOMER" }
}
```

Credenciales incorrectas responden siempre `401 INVALID_CREDENTIALS`, sin indicar si falló el correo o la contraseña. Solo cuando las credenciales son correctas y la cuenta sigue pendiente se responde `403 ACCOUNT_PENDING_VERIFICATION`.

Renovar sesión (`POST /auth/refresh`) recibe `{ "refreshToken": "opaque-token" }` y devuelve el mismo formato que el inicio de sesión con un refresh token nuevo; el anterior queda revocado.

Cerrar sesión (`POST /auth/logout`) recibe `{ "refreshToken": "opaque-token" }`, revoca ese token y responde `204`.

Recuperar contraseña (`POST /auth/forgot-password`) recibe `{ "email": "ana@example.com" }` y responde siempre `202`, exista o no el correo.

Cambiar contraseña (`POST /auth/reset-password`), respuesta `204`:

```json
{ "token": "opaque-token", "newPassword": "********" }
```

Crear reserva:

```json
{
  "screeningId": "uuid",
  "screeningSeatIds": ["uuid", "uuid"]
}
```

Respuesta:

```json
{
  "holdId": "uuid",
  "orderId": "uuid",
  "expiresAt": "2026-10-31T16:00:00Z",
  "amount": 24000,
  "currency": "COP",
  "paymentUrl": "https://sandbox.example/checkout"
}
```

Validar entrada:

```json
{ "qrToken": "opaque-token" }
```

Respuesta exitosa:

```json
{
  "ticketId": "uuid",
  "status": "USED",
  "movie": "Película",
  "screeningStartsAt": "2026-11-01T20:00:00-05:00",
  "seat": "F-08",
  "validatedAt": "2026-10-31T18:00:00Z"
}
```

Un QR ya usado responde `409 TICKET_ALREADY_USED` y un QR inexistente `404 TICKET_NOT_FOUND`.

Ventas (`GET /admin/sales`): `from` y `to` filtran por fecha, `screeningId` por función y `status` por estado de la orden. La respuesta incluye el listado paginado y los totales:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "totals": { "orders": 0, "tickets": 0, "amount": 0, "currency": "COP" }
}
```

## Errores

Formato único:

```json
{
  "timestamp": "2026-10-31T18:00:00Z",
  "status": 409,
  "code": "SEAT_NOT_AVAILABLE",
  "message": "Uno o más asientos ya no están disponibles.",
  "fieldErrors": []
}
```

Usar `400` para validación, `401` sin autenticación, `403` sin permiso, `404` inexistente, `409` conflicto de estado y `422` rechazo funcional del proveedor de pagos. No devolver trazas al cliente.

Códigos mínimos (se pueden ampliar sin cambiar el formato):

| HTTP | `code` | Cuándo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Falla Bean Validation; el detalle va en `fieldErrors` |
| 400 | `INVALID_OR_EXPIRED_TOKEN` | Token de verificación o de recuperación inválido, usado o vencido |
| 401 | `UNAUTHORIZED` | Falta el token de acceso o no es válido |
| 401 | `INVALID_CREDENTIALS` | Correo o contraseña incorrectos |
| 401 | `INVALID_WEBHOOK_SIGNATURE` | Firma del webhook de pago incorrecta |
| 403 | `FORBIDDEN` | Rol o cine sin permiso para la ruta |
| 403 | `ACCOUNT_PENDING_VERIFICATION` | Cuenta sin verificar |
| 404 | `NOT_FOUND` | Recurso inexistente o fuera del alcance del usuario |
| 404 | `TICKET_NOT_FOUND` | QR inexistente |
| 409 | `EMAIL_ALREADY_REGISTERED` | Correo duplicado en el registro |
| 409 | `SEAT_NOT_AVAILABLE` | Asiento bloqueado o vendido |
| 409 | `HOLD_EXPIRED` | La reserva venció |
| 409 | `SCREENING_OVERLAP` | La función se solapa con otra en la misma sala |
| 409 | `TICKET_ALREADY_USED` | El QR ya fue validado |
| 422 | `PAYMENT_REJECTED` | El proveedor rechazó el pago |

## Decisiones pendientes

Puntos del contrato que el equipo debe definir antes de implementar la compra (T-043, T-045):

1. `POST /holds` ya devuelve `orderId`, por lo que crea la orden `PENDING`. Falta definir qué hace `POST /orders`: confirmar el checkout o recibir el correo del invitado.
2. `holdId` no tiene tabla propia en `MODELO_DATOS.md`: los bloqueos viven en `screening_seats.hold_expires_at`. Definir si `holdId` es el mismo `orderId` o un identificador aparte.
3. El correo del cliente invitado (`guestEmail`) debe viajar en `POST /holds` o en `POST /orders`; hoy ninguno de los dos lo incluye.
4. Tamaño de página por defecto (`size=20` es una propuesta) y máximo permitido.
