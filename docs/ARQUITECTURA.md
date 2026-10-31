# Arquitectura técnica

## Estilo

Aplicación web modular con backend Spring Boot 4.1.2 y frontend React separado. El backend expone una API REST versionada (`/api/v1`) y PostgreSQL es la fuente de verdad transaccional. No se introduce microservicios en V1.

```text
React + TypeScript
        |
 HTTPS / JSON
        v
Spring Boot REST
  ├─ auth
  ├─ catalog
  ├─ scheduling
  ├─ booking
  ├─ payment
  ├─ ticket
  └─ operations
        |
   PostgreSQL
        |
  Adapters: payment / mail
```

## Backend

Paquetes por dominio, no por tipo técnico:

```text
com.stagex
├── auth
├── catalog
├── cinema
├── scheduling
├── booking
├── payment
├── ticket
├── sales
└── shared
```

Cada módulo mantiene `controller`, `application/service`, `domain`, `repository` y `dto` cuando sean necesarios. Los controladores no contienen reglas de negocio ni acceden directamente a repositorios.

## Flujo de compra

1. `GET /functions/{id}/seats` lee el mapa y estados vigentes.
2. `POST /holds` valida función, cantidad y disponibilidad dentro de una transacción.
3. PostgreSQL bloquea las filas de asiento de función en un orden determinista.
4. Se crea una orden `PENDING` y reservas `HELD` con vencimiento.
5. El backend crea una sesión de pago usando un adaptador.
6. El webhook autenticado confirma o rechaza el pago de forma idempotente.
7. En confirmación, las reservas pasan a `SOLD`, se crean entradas con QR y se libera la orden.
8. El correo se envía fuera de la transacción; si falla, la compra no se deshace y queda un evento para reintento.

No se debe confiar en el precio enviado por React: el backend calcula el total desde la función y sus precios.

## Seguridad

- Contraseñas con BCrypt o Argon2.
- JWT firmado, con expiración corta; refresh token rotado y revocable.
- Autorización en backend mediante roles y alcance de cine.
- Validación de DTOs con Bean Validation.
- CORS limitado a `FRONTEND_URL`.
- HTTPS en entornos no locales.
- Webhook de pago con firma, idempotency key y protección contra repetición.
- QR con identificador opaco o token firmado; nunca incluir datos personales.
- Logs estructurados sin contraseñas, tokens ni datos de tarjeta.

## Integraciones

`PaymentGateway` y `MailService` son interfaces internas. Sus implementaciones concretas se seleccionan por configuración (`sandbox` en desarrollo). Así se puede probar la compra sin acoplar la lógica a un proveedor.

## Concurrencia y tareas

- La creación de reservas usa transacciones y restricciones de base de datos.
- Un job periódico marca como expiradas las reservas vencidas; la lectura de disponibilidad también ignora vencidas.
- Webhooks y reintentos de correo deben ser idempotentes.
- El despliegue inicial puede ser un proceso backend, un frontend estático y PostgreSQL administrado.
