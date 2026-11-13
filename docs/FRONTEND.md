# Guía del frontend React

## Estado actual

- **T-003:** proyecto en `frontend/` con React 19, TypeScript, Vite, React Router 7 y Vitest. Todas las rutas de esta guía existen; las que aún no están implementadas muestran una página base con la tarea pendiente. Comandos en `frontend/README.md`.
- **T-033:** cartelera (`/`) con búsqueda por título y filtros de género y clasificación guardados en la URL, y ficha (`/movies/:movieId`) con funciones agrupadas por día. Mientras el backend no exponga `GET /movies` y `GET /movies/{id}/screenings` (T-030, T-032), los datos salen de `src/services/api/mock`, que replica las reglas del servidor. Para usar el backend se define `VITE_API_URL` y `VITE_USE_MOCKS=false`.

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
