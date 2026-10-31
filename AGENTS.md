# Instrucciones para trabajar en Stage X

## Contexto

Stage X es una plataforma web para gestión de cines y venta de entradas.

La versión V1.0 está definida en:
- docs/V1.0_ALCANCE_Y_BACKLOG.md
- docs/ARQUITECTURA.md
- docs/MODELO_DATOS.md
- docs/API.md

## Stack

- Backend: Java 21 y Spring Boot 4.1.2
- Frontend: React con TypeScript
- Base de datos: PostgreSQL
- Migraciones: Flyway

## Reglas obligatorias

- No implementar funcionalidades fuera de V1.0 sin aprobación del equipo.
- No guardar secretos, contraseñas ni datos de tarjeta en el repositorio.
- El backend es la fuente de verdad para permisos, precios, disponibilidad y pagos.
- No modificar directamente la base de datos sin una migración Flyway.
- Toda funcionalidad debe incluir validaciones y manejo explícito de errores.
- Las reglas críticas de asientos, pagos, roles y QR deben tener pruebas automatizadas.
- Mantener la separación entre controller, service, domain y repository.
- Respetar el contrato definido en docs/API.md.
- Antes de modificar un archivo, revisar los cambios existentes y no sobrescribir trabajo ajeno.

## Validación

Backend:

```bash
./mvnw test