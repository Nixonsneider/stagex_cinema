# Stage X

Plataforma web para la gestión de cines y la venta de entradas en línea.

## Objetivo de la V1.0

Entregar un flujo completo y demostrable:

1. El administrador configura películas, salas y funciones.
2. El cliente consulta la cartelera y la ficha de una película.
3. El cliente selecciona una función y asientos disponibles.
4. El sistema bloquea temporalmente los asientos, procesa el pago y genera las entradas con QR.
5. El cliente consulta sus entradas y el personal valida el QR en el acceso.
6. El administrador consulta ventas básicas.

## Stack acordado

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 4.1.2, Spring Web MVC, Spring Data JPA, Spring Security |
| Frontend | React, TypeScript, Vite, React Router |
| Base de datos | PostgreSQL |
| Migraciones | Flyway |
| API | REST/JSON sobre HTTPS |
| Autenticación | JWT de corta duración y refresh token |
| Pruebas | JUnit/MockMvc, Mockito y Vitest/Testing Library |
| Integración externa | Adaptadores para pagos y correo; sandbox durante desarrollo |

## Documentación del proyecto

- [Alcance y backlog V1.0](./docs/V1.0_ALCANCE_Y_BACKLOG.md): qué se construye, qué se aplaza y criterios de aceptación.
- [Arquitectura](./docs/ARQUITECTURA.md): módulos, responsabilidades, seguridad y decisiones técnicas.
- [Modelo de datos](./docs/MODELO_DATOS.md): entidades, relaciones, estados y reglas de consistencia.
- [Contrato de API](./docs/API.md): endpoints, payloads, errores y permisos.
- [Guía del frontend](./docs/FRONTEND.md): rutas, componentes, estados y experiencia de usuario.
- [Plan de implementación](./docs/PLAN_IMPLEMENTACION.md): orden de trabajo, entregables y definición de terminado.
- [Pruebas y calidad](./docs/PRUEBAS_Y_CALIDAD.md): estrategia, casos críticos y criterios de salida.

## Fuentes funcionales

La documentación se consolidó a partir de:

- `StageX_Documento_de_requerimientos.md`
- `StageX_Casos_de_Uso.md`

Los identificadores originales `RF`, `REQ`, `RN` y `CU` se conservan para trazabilidad.

## Inicio rápido previsto

```bash
# Backend
cd backend
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

Variables mínimas:

```text
DATABASE_URL=jdbc:postgresql://localhost:5433/stagex
DATABASE_USERNAME=stagex
DATABASE_PASSWORD=change-me
JWT_SECRET=change-me-in-development-min-32-bytes
PAYMENT_PROVIDER_URL=
PAYMENT_PROVIDER_SECRET=
MAIL_PROVIDER_URL=
MAIL_PROVIDER_SECRET=
FRONTEND_URL=http://localhost:5173
```

Nunca se deben registrar contraseñas, tokens, secretos de proveedores ni datos completos de tarjetas en logs o repositorio.

El backend actual se generó con Spring Boot 4.1.2. Para ejecutarlo desde Windows:

```powershell
# Desde la raíz del repositorio
docker compose up -d postgres

# En otra terminal
cd backend
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

El endpoint de salud de Actuator queda disponible en `/actuator/health`.
Con el perfil `local` se cargan datos demo y cuentas de prueba (contraseña `StageX2026`);
la lista está en [docs/MODELO_DATOS.md](./docs/MODELO_DATOS.md#datos-demo-t-019).
`JWT_SECRET` debe tener al menos 32 bytes; sin variable se usa un secreto solo para desarrollo. Para
iniciar la aplicación se necesita una instancia PostgreSQL accesible mediante
las variables `DATABASE_*`. El perfil `local` usa los valores de desarrollo
definidos en `docker-compose.yml`; no reutilizar esos valores en producción.
El puerto publicado para Stage X es `5433` para evitar conflictos con otras
instalaciones locales de PostgreSQL.

Para detener PostgreSQL:

```powershell
docker compose stop postgres
```

Para eliminar también los datos locales:

```powershell
docker compose down -v
```

La eliminación del volumen borra la base local y debe usarse solamente cuando
se quiera reiniciar el entorno desde cero.

### Sin Docker (PostgreSQL instalado en Windows)

Con un PostgreSQL 14 o superior local, crea el usuario y las bases una sola vez
(como superusuario `postgres`):

```powershell
& "C:\Program Files\PostgreSQL\14\bin\psql.exe" -U postgres -h localhost -c "CREATE ROLE stagex LOGIN PASSWORD 'stagex_local_dev';" -c "CREATE DATABASE stagex OWNER stagex;" -c "CREATE DATABASE stagex_test OWNER stagex;"
```

Luego, desde `backend/`, apunta a esa instancia (puerto `5432`):

```powershell
# Pruebas, incluidas las de base de datos y concurrencia (usa la base desechable stagex_test)
$env:STAGEX_TEST_DATABASE_URL="jdbc:postgresql://localhost:5432/stagex_test"; .\mvnw.cmd test

# Aplicación con datos demo
$env:SPRING_PROFILES_ACTIVE="local"; $env:DATABASE_URL="jdbc:postgresql://localhost:5432/stagex"; .\mvnw.cmd spring-boot:run
```

Si PowerShell bloquea `npm` por la política de ejecución, usa `npm.cmd` (por ejemplo `npm.cmd run dev`).

Las funciones demo cubren los tres días siguientes a la primera carga. Cuando pasen, la cartelera
queda vacía: borra y vuelve a crear la base `stagex` (o `docker compose down -v`) para regenerarlas.

## Estado actual

| Área | Tareas | Qué funciona |
|---|---|---|
| Base de datos | T-010 a T-013, T-019 | Esquema completo con Flyway y datos demo en el perfil `local` |
| Acceso | T-014 a T-018, T-020 | Registro, verificación, login JWT, refresh, recuperación y permisos por rol |
| Cartelera | T-033 | `GET /movies`, ficha y funciones desde PostgreSQL |
| Asientos | T-040 a T-042, T-044 | Mapa por función, reserva de 10 minutos sin doble bloqueo y vencimiento automático |
| Pendiente | T-030 a T-032, T-045 en adelante | Administración del catálogo, checkout, pago, entradas QR y ventas |

El detalle de cada tarea está en [TASKS.md](./TASKS.md).
