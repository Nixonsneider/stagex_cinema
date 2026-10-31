# Pruebas y calidad V1.0

## Pirámide

### Backend unitario

Probar servicios puros: búsqueda de películas, reglas de función, cálculo de total, permisos, expiración de reservas, transición de estados y validación de QR.

### Backend de integración

Usar PostgreSQL real o Testcontainers para comprobar:

- Unicidad de asientos por función.
- Bloqueo pesimista/concurrencia.
- Rollback si falla el pago.
- Webhook repetido sin duplicar entradas.
- Migraciones Flyway.

### API

Probar cada endpoint con MockMvc/WebTestClient: códigos HTTP, DTOs, errores, roles y no filtración de datos de otro cliente/cine.

### Frontend

Probar rutas principales, formularios, selección de asientos, contador de expiración, errores de pago, guardas de rol y accesibilidad básica.

## Casos críticos mínimos

| Caso | Resultado esperado |
|---|---|
| Registro con correo duplicado | Error de validación sin revelar información innecesaria |
| Login de cuenta desactivada | `401` o respuesta de negocio definida; nunca crea sesión |
| Dos clientes toman el mismo asiento | Solo uno obtiene reserva válida |
| Reserva vencida | El asiento vuelve a estar disponible |
| Compra aprobada | Una orden pagada y una entrada por asiento |
| Webhook repetido | Misma respuesta, cero entradas duplicadas |
| Pago rechazado | Orden rechazada y asientos liberados |
| QR correcto | Entrada pasa a `USED` |
| QR usado otra vez | Conflicto y no cambia el estado |
| Taquillero modifica película | `403` |
| Administrador de otro cine consulta función | `403` o `404` según política de ocultamiento |
| XSS en título/sinopsis | Se muestra como texto, nunca como HTML ejecutable |

## Rendimiento objetivo

- Cartelera y búsqueda: menos de 3 segundos en condiciones estándar.
- Reserva de asientos: menos de 2 segundos.
- Prueba de carga inicial: 500 usuarios concurrentes como objetivo del documento de requisitos.

## Seguridad y operación

- Revisar dependencias vulnerables antes de liberar.
- Validar que el secreto JWT y credenciales externas provengan del entorno.
- Ejecutar pruebas de autorización con cada rol.
- Enmascarar correo y referencias de pago en logs.
- Respaldar PostgreSQL diariamente y comprobar restauración; objetivo de recuperación máximo: 4 horas.
- Verificar que las acciones críticas creen `audit_events`.
