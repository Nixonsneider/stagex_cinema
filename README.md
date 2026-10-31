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
JWT_SECRET=change-me-in-development
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

El endpoint de salud de Actuator queda disponible en `/actuator/health`. Para
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
