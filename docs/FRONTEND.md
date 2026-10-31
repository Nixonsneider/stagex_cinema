# Guía del frontend React

## Rutas V1.0

### Públicas

| Ruta | Pantalla |
|---|---|
| `/` | Cartelera |
| `/movies/:movieId` | Ficha y funciones |
| `/screenings/:screeningId/seats` | Mapa de asientos |
| `/checkout/:orderId` | Resumen y pago |
| `/confirmation/:orderId` | Confirmación |
| `/login`, `/register`, `/forgot-password` | Acceso |

### Protegidas

| Ruta | Rol |
|---|---|
| `/my-tickets` | Cliente |
| `/my-tickets/:ticketId` | Cliente |
| `/admin/movies` | Administrador |
| `/admin/rooms` | Administrador |
| `/admin/screenings` | Administrador |
| `/admin/sales` | Taquillero/Administrador |
| `/access/validate` | Taquillero |

## Estructura sugerida

```text
src/
├── app/
├── components/
├── features/
│   ├── auth/
│   ├── movies/
│   ├── screenings/
│   ├── seats/
│   ├── checkout/
│   └── admin/
├── pages/
├── services/api/
├── hooks/
├── types/
└── styles/
```

## Reglas de UX

- El frontend nunca decide disponibilidad, precio, rol ni estado final de pago.
- Mostrar contador de vencimiento de reserva y advertir antes de que expire.
- Si una reserva vence, limpiar selección y solicitar refresco del mapa.
- Los asientos deben tener etiqueta y estado textual además del color.
- En errores de API mostrar mensaje accionable; no exponer detalles técnicos.
- Mantener el checkout en máximo seis pasos: función, asientos, datos, pago, confirmación.
- Diseñar desde 360 px y probar teclado, foco visible, labels y contraste WCAG 2.1 AA.
- Deshabilitar doble envío mientras una operación está pendiente.

## Estado

Separar:

- Estado remoto: películas, funciones, asientos, órdenes y tickets, administrado con un cliente de consultas/cache.
- Estado local: filtros, asiento seleccionado, formularios y modal.
- Sesión: access token en memoria cuando sea posible; refresh token en cookie `HttpOnly`, `Secure`, `SameSite`.

El mapa debe refrescarse después de crear/liberar una reserva y periódicamente durante el checkout. WebSockets pueden incorporarse después; no son necesarios para el primer flujo funcional si el bloqueo transaccional está correcto.
