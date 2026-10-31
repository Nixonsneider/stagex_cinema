# Contrato de API REST V1.0

Base URL: `/api/v1`. Respuestas JSON. Fechas ISO-8601 con zona horaria. El contrato se implementa primero en backend y se consume desde React.

## Autenticación

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Crea cliente pendiente de verificación |
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
| `POST` | `/admin/movies` | `CINEMA_ADMIN` |
| `PUT` | `/admin/movies/{movieId}` | `CINEMA_ADMIN` |
| `PATCH` | `/admin/movies/{movieId}/status` | `CINEMA_ADMIN` |
| `POST` | `/admin/rooms` | `CINEMA_ADMIN` |
| `PUT` | `/admin/rooms/{roomId}/seats` | `CINEMA_ADMIN` |
| `PATCH` | `/admin/rooms/{roomId}/status` | `CINEMA_ADMIN` |
| `POST` | `/admin/screenings` | `CINEMA_ADMIN` |
| `PUT` | `/admin/screenings/{screeningId}` | `CINEMA_ADMIN` |
| `POST` | `/admin/screenings/{screeningId}/cancel` | `CINEMA_ADMIN` |

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
| `GET` | `/admin/sales?from=&to=&screeningId=` | `CASHIER`, `CINEMA_ADMIN` |

## Payloads críticos

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
