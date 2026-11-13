# Tareas de Stage X V1.0

Este archivo es el tablero operativo del equipo. El alcance funcional se define en
[docs/V1.0_ALCANCE_Y_BACKLOG.md](./docs/V1.0_ALCANCE_Y_BACKLOG.md) y el orden general en
[docs/PLAN_IMPLEMENTACION.md](./docs/PLAN_IMPLEMENTACION.md).

## Integrantes y responsabilidades

| Integrante | Responsabilidad principal | Responsabilidades compartidas |
|---|---|---|
| **Nixon Esneider Villamizar Sarmiento** | Backend SpringBoot, Base de datos, PostgreSQL, Flyway, modelo relacional y consistencia transaccional | Arquitectura, revisión de reglas críticas y decisiones técnicas |
| **Santiago Caro Barbosa** | Autenticación, autorización y servicios de dominio | Contrato de API, integración con frontend y revisión de código |
| **Kevin Linares Castañeda** | Frontend React/TypeScript, catálogo, cartelera y panel administrativo | UX responsive, accesibilidad y pruebas de interfaz |
| **Luis López** | Reservas, pagos, entradas QR, validación y calidad de integración | Pruebas de flujo completo, integración de correo y preparación de entrega |

La responsabilidad indica quién coordina y entrega la tarea; no impide que otro
integrante ayude. Cada tarea debe tener un único responsable principal.

## Estados

- `TODO`: pendiente.
- `IN_PROGRESS`: en desarrollo.
- `BLOCKED`: bloqueada; se debe explicar el motivo.
- `REVIEW`: implementada y esperando revisión.
- `DONE`: implementada, probada y revisada.

## Reglas de trabajo

1. Actualizar el estado y el responsable antes de comenzar una tarea.
2. No comenzar una tarea mientras sus dependencias estén incompletas.
3. Una tarea debe producir un cambio verificable, no una actividad indefinida.
4. Toda tarea que modifique API, datos o reglas de negocio debe actualizar la documentación relacionada.
5. Toda tarea marcada como `DONE` debe tener pruebas y pasar las validaciones del proyecto.
6. Cada pull request debe ser revisado por un integrante distinto al responsable.
7. Si una tarea tarda más de tres días, dividirla en subtareas.
8. No mezclar tareas de dominios diferentes en el mismo pull request salvo que sea necesario para integrar un flujo.

## Fase 0 — Organización y base del repositorio

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-001 | Confirmar alcance V1.0 y reglas de negocio | Nixon Esneider Villamizar Sarmiento | DONE | — | El alcance de `V1.0_ALCANCE_Y_BACKLOG.md` y sus reglas de negocio quedan confirmados para iniciar desarrollo |
| T-002 | Configurar estructura de backend Spring Boot 4.1.2 | Nixon Esneider Villamizar Sarmiento | DONE | — | El backend generado en `backend/` compila, la prueba de contexto pasa sin PostgreSQL local y Actuator expone `/actuator/health` |
| T-003 | Configurar estructura de frontend React/TypeScript | Kevin Linares Castañeda | REVIEW | — | El frontend inicia, navega y tiene una página base |
| T-004 | Configurar PostgreSQL local y perfiles de entorno | Nixon Esneider Villamizar Sarmiento | DONE | — | Docker Compose levanta PostgreSQL en `localhost:5433`, el perfil `local` conecta y el backend responde en `/actuator/health` |
| T-005 | Configurar Flyway y migración inicial vacía | Nixon Esneider Villamizar Sarmiento | TODO | T-002, T-004 | Las migraciones se ejecutan en una base PostgreSQL limpia |
| T-006 | Configurar lint, pruebas y build de backend y frontend | Luis López | TODO | T-002, T-003 | Las validaciones se ejecutan con comandos documentados |
| T-007 | Revisar contrato inicial de API entre frontend y backend | Santiago Caro Barbosa | TODO | T-002, T-003 | `docs/API.md` refleja las rutas y payloads acordados |

## Fase 1 — Modelo de datos y seguridad

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-010 | Crear migraciones de usuarios, cines y relación usuario-cine | Nixon Esneider Villamizar Sarmiento | DONE | T-005 | Tablas, claves, índices y restricciones están migradas |
| T-011 | Crear migraciones de películas, salas y asientos | Nixon Esneider Villamizar Sarmiento | DONE | T-005 | El modelo permite configurar salas con filas y columnas |
| T-012 | Crear migraciones de funciones y asientos por función | Nixon Esneider Villamizar Sarmiento | TODO | T-011 | Cada función tiene un estado independiente por asiento |
| T-013 | Crear migraciones de órdenes, entradas y auditoría | Nixon Esneider Villamizar Sarmiento | TODO | T-012 | El esquema soporta compra, QR, validación y trazabilidad |
| T-014 | Implementar entidades y repositorios de autenticación | Santiago Caro Barbosa | TODO | T-010 | Usuario, rol y estado se persisten con validación |
| T-015 | Implementar registro y verificación de clientes | Santiago Caro Barbosa | TODO | T-014 | El cliente queda pendiente hasta confirmar su enlace |
| T-016 | Implementar inicio de sesión, JWT y cierre de sesión | Santiago Caro Barbosa | TODO | T-014 | La sesión expira y no se devuelven credenciales inválidas de forma sensible |
| T-017 | Implementar recuperación de contraseña | Santiago Caro Barbosa | TODO | T-015, T-016 | El enlace es temporal, de un solo uso y no revela si el correo existe |
| T-018 | Implementar autorización por rol y alcance de cine | Santiago Caro Barbosa | TODO | T-016 | `CUSTOMER`, `CASHIER` y `CINEMA_ADMIN` solo acceden a sus rutas |
| T-019 | Crear datos demo y usuarios iniciales seguros | Nixon Esneider Villamizar Sarmiento | TODO | T-010, T-013 | Una instalación limpia tiene datos mínimos para probar el flujo |
| T-020 | Crear login, registro y protección de rutas en React | Kevin Linares Castañeda | TODO | T-016, T-018 | Las rutas públicas y privadas se comportan según el rol |

## Fase 2 — Catálogo y programación

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-030 | Implementar CRUD de películas en backend | Santiago Caro Barbosa | TODO | T-011, T-018 | Se pueden crear, editar, activar y desactivar películas |
| T-031 | Implementar CRUD de salas y mapa básico | Santiago Caro Barbosa | TODO | T-011, T-018 | Una sala tiene asientos configurables y estado operativo |
| T-032 | Implementar CRUD de funciones y precios | Santiago Caro Barbosa | TODO | T-012, T-030, T-031 | Se rechazan funciones solapadas o asociadas a recursos inactivos |
| T-033 | Implementar cartelera, búsqueda y ficha pública | Kevin Linares Castañeda | IN_PROGRESS | T-030, T-032 | Solo se muestran películas con funciones disponibles |
| T-034 | Implementar panel de películas en React | Kevin Linares Castañeda | TODO | T-030 | El administrador puede gestionar el catálogo desde la interfaz |
| T-035 | Implementar panel de salas y asientos en React | Kevin Linares Castañeda | TODO | T-031 | El administrador puede crear sala y configurar filas/columnas |
| T-036 | Implementar panel de funciones en React | Kevin Linares Castañeda | TODO | T-032 | El administrador puede programar, editar y cancelar una función |
| T-037 | Probar reglas de catálogo y programación | Luis López | TODO | T-030, T-031, T-032 | Existen pruebas para estados, permisos, precios y solapamientos |

## Fase 3 — Asientos, reservas y compra

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-040 | Implementar consulta de disponibilidad por función | Santiago Caro Barbosa | TODO | T-012, T-032 | La API devuelve estados actuales de los asientos |
| T-041 | Implementar bloqueo temporal transaccional | Nixon Esneider Villamizar Sarmiento | TODO | T-012, T-040 | Dos clientes no pueden bloquear el mismo asiento |
| T-042 | Implementar expiración y liberación de reservas | Nixon Esneider Villamizar Sarmiento | TODO | T-041 | Los asientos vencidos vuelven a estar disponibles |
| T-043 | Implementar creación de órdenes y cálculo de total | Santiago Caro Barbosa | TODO | T-041 | El total se calcula en backend y la orden limita la cantidad de asientos |
| T-044 | Implementar mapa y selección de asientos en React | Kevin Linares Castañeda | TODO | T-040, T-041 | La interfaz distingue disponible, seleccionado, bloqueado y vendido |
| T-045 | Implementar checkout para cliente registrado e invitado | Kevin Linares Castañeda | TODO | T-043 | El checkout valida correo y muestra vencimiento de la reserva |
| T-046 | Implementar adaptador de pago sandbox | Luis López | TODO | T-043 | El proveedor se puede sustituir por un fake en pruebas |
| T-047 | Implementar webhook idempotente de pago | Luis López | TODO | T-046 | Un webhook repetido no duplica órdenes ni entradas |
| T-048 | Integrar flujo aprobado y rechazado de pago | Santiago Caro Barbosa | TODO | T-046, T-047 | Pago aprobado vende asientos; pago rechazado los libera |
| T-049 | Ejecutar pruebas de concurrencia de asientos | Luis López | TODO | T-041, T-048 | Las pruebas demuestran que no existe doble venta |

## Fase 4 — Entradas, acceso y ventas

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-050 | Generar entradas y token QR seguro | Luis López | TODO | T-048 | Se crea una entrada por asiento con token no predecible |
| T-051 | Implementar correo de confirmación | Luis López | TODO | T-050 | El correo contiene la información necesaria sin datos sensibles de pago |
| T-052 | Implementar “Mis entradas” y descarga/visualización QR | Kevin Linares Castañeda | TODO | T-050 | El cliente solo ve sus propias entradas |
| T-053 | Implementar validación de QR para taquillero | Luis López | TODO | T-050, T-018 | Un QR válido pasa a `USED` y un segundo uso es rechazado |
| T-054 | Implementar consulta básica de ventas | Santiago Caro Barbosa | TODO | T-048, T-018 | Taquillero y administrador ven solo ventas autorizadas |
| T-055 | Implementar vistas de acceso y ventas en React | Kevin Linares Castañeda | TODO | T-053, T-054 | Las pantallas manejan estados válido, usado, inválido y error |
| T-056 | Registrar acciones críticas en auditoría | Nixon Esneider Villamizar Sarmiento | TODO | T-013, T-048, T-053 | Compra, cancelación, validación y cambios administrativos dejan registro |

## Fase 5 — Integración y entrega

| ID | Tarea | Responsable | Estado | Dependencias | Criterio de aceptación |
|---|---|---|---|---|---|
| T-060 | Ejecutar pruebas de API y autorización | Luis López | TODO | T-018, T-030, T-054 | Se verifican códigos HTTP, roles y aislamiento por cine |
| T-061 | Ejecutar pruebas responsive y accesibilidad | Kevin Linares Castañeda | TODO | T-033, T-044, T-052, T-055 | Se valida desde 360 px, teclado, foco, labels y contraste básico |
| T-062 | Ejecutar pruebas de integración del flujo completo | Luis López | TODO | T-048, T-053, T-055 | Se completa compra aprobada, compra rechazada y validación de QR |
| T-063 | Revisar índices, transacciones y respaldo PostgreSQL | Nixon Esneider Villamizar Sarmiento | TODO | T-041, T-048, T-056 | Se prueba restauración de respaldo y no hay consultas críticas sin índice |
| T-064 | Actualizar documentación técnica y variables de entorno | Santiago Caro Barbosa | TODO | T-007, T-062 | README, API, arquitectura y modelo reflejan la implementación real |
| T-065 | Preparar datos demo y guía de ejecución | Nixon Esneider Villamizar Sarmiento | TODO | T-019, T-063 | El equipo puede levantar el proyecto desde una base limpia |
| T-066 | Revisión final cruzada y corrección de bloqueadores | Todos | TODO | T-060, T-061, T-062, T-064, T-065 | No quedan defectos críticos y todas las validaciones pasan |

## Definición de terminado

Una tarea puede pasar a `DONE` solamente cuando:

- el código está implementado en la capa que corresponda;
- se agregaron o actualizaron pruebas;
- se validaron permisos, errores y estados relevantes;
- la documentación relacionada está actualizada;
- no se incorporaron secretos ni datos sensibles;
- pasa build, lint y pruebas;
- fue revisada por otro integrante mediante pull request.

## Dependencias críticas

```text
Base de datos
  T-004 -> T-005 -> T-010..T-013

Seguridad
  T-010 -> T-014 -> T-016 -> T-018

Catálogo
  T-011 -> T-030/T-031 -> T-032 -> T-033

Compra
  T-012 -> T-040 -> T-041 -> T-043 -> T-046/T-047 -> T-048

Entradas
  T-048 -> T-050 -> T-053

Entrega
  T-048/T-053/T-055 -> T-060..T-066
```

## Política para tareas no contempladas

Si aparece una tarea nueva:

1. Verificar que pertenezca al alcance V1.0.
2. Crear un nuevo ID consecutivo.
3. Asignar un único responsable.
4. Registrar dependencias y criterio de aceptación.
5. No iniciar la tarea hasta que el equipo confirme su prioridad.
