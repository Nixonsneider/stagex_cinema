# Plan de implementación V1.0

## Orden de trabajo

### Fase 1: base técnica

- [x] Crear proyecto backend Spring Boot 4.1.2. El frontend queda pendiente de T-003.
- Configurar PostgreSQL, perfiles local/test y Flyway.
- Añadir manejo de errores, validación, CORS y formato de logs.
- Crear pipeline con compilación y pruebas.

### Fase 2: seguridad y datos

- Implementar usuarios, roles, BCrypt/Argon2 y JWT.
- Crear migración inicial y datos de prueba.
- Aplicar autorización por rol y por cine.

### Fase 3: catálogo y programación

- CRUD de películas.
- CRUD de salas y generador de mapa por filas/columnas.
- CRUD de funciones, validación de solapamiento y precios.
- Pantallas públicas de cartelera, búsqueda y ficha.

### Fase 4: asientos y compra

- Proyección de `screening_seats`.
- Reserva transaccional con expiración.
- Orden y cálculo de total en backend.
- Adaptador de pago sandbox y webhook idempotente.
- Emisión de QR y correo de confirmación.

### Fase 5: operación

- Mis entradas.
- Validación de QR y estados `ISSUED`/`USED`.
- Ventas básicas y filtros.
- Logs de acciones críticas.

### Fase 6: estabilización

- Pruebas de concurrencia y seguridad.
- Pruebas responsive y accesibilidad.
- Datos demo, documentación de despliegue y checklist de aceptación.

## Dependencias

1. No implementar checkout antes de tener migraciones y asientos por función.
2. No implementar pago real antes de tener orden idempotente y webhook verificable.
3. No implementar panel administrativo antes de cerrar autorización por rol.
4. Las integraciones externas deben poder sustituirse por fake adapters en pruebas.

## Definición de terminado

Una historia está terminada cuando:

- Está implementada en backend y frontend donde aplique.
- Tiene validación de entrada, autorización y manejo de errores.
- Tiene pruebas automatizadas de la regla de negocio.
- Tiene migración/documentación actualizada.
- No deja secretos ni datos sensibles en el código.
- Pasa build, lint y pruebas.
- Está verificada en una base PostgreSQL limpia.

## Criterio de entrega

- `mvn test` y `npm test` pasan.
- La aplicación inicia con variables documentadas.
- Se puede ejecutar una compra aprobada con proveedor sandbox.
- Se puede ejecutar una compra rechazada sin vender asientos.
- Se puede validar una entrada y rechazar un segundo uso.
- Las rutas de personal rechazan permisos incorrectos.
- Existe respaldo de base de datos y procedimiento de restauración probado.
