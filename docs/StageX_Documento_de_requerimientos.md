**Documento de requerimientos de software**

**_Stage X_**

Plataforma web para la gestión de cines: películas, funciones, salas, entradas y ventas

_Fecha: 07/10/2026_

_Nixon Esneider Villamizar Sarmiento_

_Santiago Caro Barbosa_

_Kevin Linares Castañeda_

_Luis López_

**Tabla de contenido**

[1\. Propósito 4](#1-propósito)

[2\. Alcance del producto / Software 4](#2-alcance-del-producto--software)

[2.1. Contexto y problema 4](#21-contexto-y-problema)

[2.2. Objetivo general 4](#22-objetivo-general)

[2.3. Beneficios y objetivos específicos 4](#23-beneficios-y-objetivos-específicos)

[2.4. Fuera del alcance de la versión 1.0 4](#24-fuera-del-alcance-de-la-versión-10)

[3\. Referencias 5](#3-referencias)

[4\. Funcionalidades del producto 5](#4-funcionalidades-del-producto)

[5\. Clases y características de usuarios 5](#5-clases-y-características-de-usuarios)

[6\. Entorno operativo 6](#6-entorno-operativo)

[7\. Requerimientos funcionales 6](#7-requerimientos-funcionales)

[7.1. RF-01 – Registro y autenticación de usuarios 6](#71-rf-01--registro-y-autenticación-de-usuarios)

[7.2. RF-02 – Búsqueda y consulta de películas 7](#72-rf-02--búsqueda-y-consulta-de-películas)

[7.3. RF-03 – Selección de función 7](#73-rf-03--selección-de-función)

[7.4. RF-04 – Selección de asientos 8](#74-rf-04--selección-de-asientos)

[7.5. RF-05 – Compra de entradas y pago 9](#75-rf-05--compra-de-entradas-y-pago)

[7.6. RF-06 – Gestión de entradas del cliente 9](#76-rf-06--gestión-de-entradas-del-cliente)

[7.7. RF-07 – Administración de películas 10](#77-rf-07--administración-de-películas)

[7.8. RF-08 – Administración de salas 10](#78-rf-08--administración-de-salas)

[7.9. RF-09 – Administración de funciones 11](#79-rf-09--administración-de-funciones)

[7.10. RF-10 – Gestión de ventas y reportes 11](#710-rf-10--gestión-de-ventas-y-reportes)

[7.11. RF-11 – Administración de usuarios y roles 12](#711-rf-11--administración-de-usuarios-y-roles)

[8\. Reglas de negocio 12](#8-reglas-de-negocio)

[9\. Requerimientos de interfaces externas 13](#9-requerimientos-de-interfaces-externas)

[9.1. Interfaces de usuario 13](#91-interfaces-de-usuario)

[9.2. Interfaces de hardware 14](#92-interfaces-de-hardware)

[9.3. Interfaces de software 14](#93-interfaces-de-software)

[9.4. Interfaces de comunicación 14](#94-interfaces-de-comunicación)

[10\. Requerimientos no funcionales 14](#10-requerimientos-no-funcionales)

[11\. Otros requerimientos 15](#11-otros-requerimientos)

[12\. Glosario 15](#12-glosario)

**Historial de Versiones**

| **Fecha**  | **Versión** | **Autor**        | **Organización** | **Descripción**                                             |
| ---------- | ----------- | ---------------- | ---------------- | ----------------------------------------------------------- |
| 07/10/2026 | 1.0         | Nixon Villamizar | StageX           | Versión inicial del documento de requerimientos de Stage X. |
|            |             |                  |                  |                                                             |

**Información del Proyecto**

| **Empresa / Organización**                                  | Stage X                                           |
| ----------------------------------------------------------- | ------------------------------------------------- |
| **Proyecto**                                                | Stage X – Plataforma web para la gestión de cines |
| **Fecha de preparación**                                    | 07/10/2026                                        |
| **Cliente**                                                 | Cines que adopten la plataforma (por definir)     |
| **Patrocinador principal**                                  | FESC                                              |
| **Gerente / Líder de Proyecto**                             | Nixon Esneider Villamizar Sarmiento               |
| **Gerente / Líder de Análisis de negocio y requerimientos** | Nixon Esneider Villamizar Sarmiento               |

**Aprobaciones**

| **Nombre y Apellido** | **Cargo** | **Departamento u Organización** | **Fecha** | **Firma** |
| --------------------- | --------- | ------------------------------- | --------- | --------- |
|                       |           |                                 |           |           |
|                       |           |                                 |           |           |
|                       |           |                                 |           |           |

# 1. Propósito

Este documento especifica los requerimientos de software de Stage X, versión 1.0 (Release inicial), una plataforma web orientada a la gestión integral de cines y a la compra de entradas en línea.

El documento cubre la totalidad del producto en su primera versión, incluyendo los dos grandes frentes del sistema:

- **Portal del cliente:** búsqueda de películas, selección de función, elección de asientos, compra y gestión de entradas.
- **Módulo de administración del cine:** gestión de películas, salas, funciones, precios, ventas, reportes y usuarios del personal.

# 2. Alcance del producto / Software

Stage X es una plataforma web que digitaliza el ciclo completo de operación de un cine, desde la programación de la cartelera hasta la venta y validación de las entradas.

## 2.1. Contexto y problema

**Desde el punto de vista del cliente,** se busca facilitar el proceso de encontrar una película, seleccionar una función, escoger un asiento y realizar la compra.

**Desde el punto de vista del cine,** se busca facilitar la administración de películas, funciones, salas y ventas.

## 2.2. Objetivo general

Ofrecer una plataforma única, sencilla y confiable que conecte la experiencia de compra del espectador con la operación diaria del cine.

## 2.3. Beneficios y objetivos específicos

- **Para el cliente:** reducir los pasos y el tiempo necesarios para comprar una entrada, con información clara y asientos garantizados.
- **Para el cine:** centralizar la programación y las ventas, evitar sobreventa de asientos y contar con información para decidir (ocupación e ingresos).
- **Para la organización:** disminuir la carga operativa en taquilla y errores manuales, y ampliar los canales de venta mediante el canal digital.

## 2.4. Fuera del alcance de la versión 1.0

- Venta de productos de confistería (alimentos y bebidas).
- Programas de fidelización, puntos y cupones de descuento.
- Aplicaciones móviles nativas (la versión 1.0 es web con diseño adaptable).
- Integración con sistemas contables o ERP externos.

# 3. Referencias

Se listan los documentos de referencia de este proyecto. Los campos entre corchetes deben completarse por el equipo.

| **Título**                                              | **Autor**      | **Versión** | **Fecha**      | **Ubicación**            |
| ------------------------------------------------------- | -------------- | ----------- | -------------- | ------------------------ |
| Plantilla de documento de requerimientos de software    | PMOInformática | —           | —              | <www.pmoinformatica.com> |
| Acta de constitución / definición de alcance de Stage X | \[Autor\]      | \[1.0\]     | \[dd/mm/aaaa\] | \[Ubicación\]            |
| Matriz de trazabilidad de requerimientos                | \[Autor\]      | \[1.0\]     | \[dd/mm/aaaa\] | \[Ubicación\]            |

# 4. Funcionalidades del producto

A continuación se enumeran las principales funcionalidades de Stage X. El detalle de cada una se documenta en la sección 7.

**Portal del cliente**

1. RF-01 – Registro y autenticación de usuarios
2. RF-02 – Búsqueda y consulta de películas
3. RF-03 – Selección de función
4. RF-04 – Selección de asientos
5. RF-05 – Compra de entradas y pago
6. RF-06 – Gestión de entradas del cliente

**Administración del cine**

7. RF-07 – Administración de películas
8. RF-08 – Administración de salas
9. RF-09 – Administración de funciones
10. RF-10 – Gestión de ventas y reportes
11. RF-11 – Administración de usuarios y roles

# 5. Clases y características de usuarios

| **Tipo de usuario**             | **Características**                                                                                                   | **Funcionalidades relevantes** | **Frecuencia de uso**         |
| ------------------------------- | --------------------------------------------------------------------------------------------------------------------- | ------------------------------ | ----------------------------- |
| Cliente registrado              | Espectador que compra en línea. Nivel técnico básico; accede desde móvil o computador. Mantiene historial de compras. | RF-01 a RF-06                  | Ocasional (semanal o mensual) |
| Cliente invitado                | Espectador que compra sin crear cuenta, solo con correo electrónico.                                                  | RF-02 a RF-05                  | Ocasional                     |
| Taquillero / Personal de acceso | Empleado que valida entradas en el ingreso y apoya ventas y consultas. Capacitación básica en el sistema.             | RF-06, RF-10 (validación)      | Alta (diaria)                 |
| Administrador de cine           | Responsable de la operación de uno o varios cines: programa funciones, precios y revisa reportes.                     | RF-07 a RF-10                  | Alta (diaria)                 |
| Administrador general           | Gestiona usuarios, roles y configuración global de la plataforma. Es quien accede a la mayor parte del sistema.       | RF-01, RF-07 a RF-11           | Media                         |

# 6. Entorno operativo

- **Plataforma de acceso:** aplicación web accesible desde navegadores modernos (Chrome, Firefox, Edge y Safari, en sus dos últimas versiones principales) en computadores, tabletas y teléfonos móviles.
- **Servidor:** despliegue en infraestructura en la nube con servidores Linux, servidor de aplicaciones web y base de datos relacional.
- **Servicios externos:** pasarela de pagos y servicio de envío de correos electrónicos transaccionales.
- **Coexistencia:** el sistema debe operar de forma independiente, sin requerir la instalación de software en los equipos de los clientes.

La pila tecnológica específica (lenguajes, frameworks y proveedor de nube) se definirá en la etapa de diseño de la solución.

# 7. Requerimientos funcionales

Los requerimientos se organizan por funcionalidad. Cada requerimiento tiene un identificador único con formato REQ-###. La prioridad (Alta, Media o Baja) debe ser confirmada por el área funcional.

## 7.1. RF-01 – Registro y autenticación de usuarios

| **Descripción**                                    | Permite a los clientes crear una cuenta e iniciar sesión, y al personal del cine acceder al sistema según su rol.                                                                                                                                                                                                                                                                                       |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El usuario selecciona "Registrarse" e ingresa nombre, correo y contraseña; el sistema valida los datos y envía un correo de verificación.<br><br>2\. El usuario inicia sesión con correo y contraseña; el sistema valida las credenciales y lo redirige a la vista correspondiente a su rol.<br><br>3\. Si el usuario olvida su contraseña, solicita un enlace de recuperación enviado a su correo. |

| **ID**  | **Requerimiento funcional**                                                                                                                         |
| ------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| REQ-001 | El sistema debe permitir el registro de clientes con nombre, correo electrónico único y contraseña.                                                 |
| REQ-002 | El sistema debe enviar un correo de verificación y activar la cuenta solo cuando el enlace sea confirmado.                                          |
| REQ-003 | El sistema debe autenticar usuarios mediante correo y contraseña y mantener una sesión segura con expiración por inactividad.                       |
| REQ-004 | El sistema debe permitir la recuperación de contraseña mediante un enlace temporal enviado al correo registrado.                                    |
| REQ-005 | El sistema debe mostrar mensajes claros ante credenciales inválidas, correo ya registrado o cuenta no verificada, sin revelar información sensible. |
| REQ-006 | El sistema debe permitir la compra como invitado únicamente proporcionando un correo electrónico para recibir las entradas.                         |

## 7.2. RF-02 – Búsqueda y consulta de películas

| **Descripción**                                    | Permite al cliente explorar la cartelera, buscar películas y consultar su información detallada.                                                                                                                                                                                                                                                                                            |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                                                                        |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El cliente ingresa a la página principal y visualiza las películas en cartelera y próximos estrenos.<br><br>2\. El cliente busca por título o aplica filtros (género, clasificación, idioma, formato); el sistema actualiza el listado.<br><br>3\. El cliente selecciona una película y el sistema muestra sinopsis, duración, clasificación, reparto, tráiler y funciones disponibles. |

| **ID**  | **Requerimiento funcional**                                                                                           |
| ------- | --------------------------------------------------------------------------------------------------------------------- |
| REQ-007 | El sistema debe mostrar la cartelera con las películas que tengan al menos una función programada.                    |
| REQ-008 | El sistema debe mostrar una sección de próximos estrenos con la fecha de lanzamiento.                                 |
| REQ-009 | El sistema debe permitir buscar películas por título, con coincidencias parciales e ignorando mayúsculas y tildes.    |
| REQ-010 | El sistema debe permitir filtrar por género, clasificación por edad, idioma (doblada/subtitulada) y formato (2D/3D).  |
| REQ-011 | El sistema debe presentar la ficha de cada película con sinopsis, duración, clasificación, reparto, póster y tráiler. |
| REQ-012 | El sistema debe mostrar un mensaje informativo cuando la búsqueda no arroje resultados.                               |

## 7.3. RF-03 – Selección de función

| **Descripción**                                    | Permite al cliente elegir la fecha, hora, sala y formato de la función a la que desea asistir.                                                                                                                                                                                                                                             |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                       |
| **Acciones iniciadoras y comportamiento esperado** | 1\. Desde la ficha de la película, el cliente elige una fecha; el sistema muestra los horarios disponibles por sala y formato.<br><br>2\. El cliente selecciona una función; el sistema muestra precio, idioma, formato y disponibilidad aproximada de asientos.<br><br>3\. El sistema continúa al mapa de asientos de la función elegida. |

| **ID**  | **Requerimiento funcional**                                                                                      |
| ------- | ---------------------------------------------------------------------------------------------------------------- |
| REQ-013 | El sistema debe listar las funciones de una película agrupadas por fecha, cine, sala y formato.                  |
| REQ-014 | El sistema debe ocultar o deshabilitar las funciones que ya hayan iniciado o que estén agotadas.                 |
| REQ-015 | El sistema debe mostrar, para cada función, el precio por tipo de entrada y la cantidad de asientos disponibles. |
| REQ-016 | El sistema debe permitir cambiar de función antes del pago sin perder la sesión del usuario.                     |

## 7.4. RF-04 – Selección de asientos

| **Descripción**                                    | Permite al cliente visualizar el mapa de la sala y escoger sus asientos con bloqueo temporal para evitar ventas duplicadas.                                                                                                                                                                                                                                            |
| -------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                                                   |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El sistema muestra el plano de la sala indicando asientos libres, ocupados, bloqueados y seleccionados.<br><br>2\. El cliente selecciona uno o varios asientos (hasta el máximo permitido por compra); el sistema los bloquea temporalmente.<br><br>3\. Si el tiempo de bloqueo vence sin completar el pago, el sistema libera los asientos y notifica al cliente. |

| **ID**  | **Requerimiento funcional**                                                                                                                |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| REQ-017 | El sistema debe mostrar el mapa de asientos de la sala con su estado actualizado en tiempo real o casi real.                               |
| REQ-018 | El sistema debe permitir seleccionar hasta 10 asientos por transacción.                                                                    |
| REQ-019 | El sistema debe bloquear los asientos seleccionados durante un tiempo configurable (por defecto 10 minutos) y mostrar un contador visible. |
| REQ-020 | El sistema debe impedir que un asiento ocupado o bloqueado por otro usuario sea seleccionado.                                              |
| REQ-021 | El sistema debe identificar asientos de accesibilidad (movilidad reducida) y diferenciarlos visualmente.                                   |
| REQ-022 | El sistema debe liberar automáticamente los asientos cuando el bloqueo expire o el usuario cancele la selección.                           |

## 7.5. RF-05 – Compra de entradas y pago

| **Descripción**                                    | Permite al cliente revisar su pedido, pagar en línea y recibir la confirmación con sus entradas digitales.                                                                                                                                                                                                                                                                                                                                                                                                         |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El sistema muestra el resumen: película, función, asientos, tipo de entrada y total a pagar.<br><br>2\. El cliente elige el medio de pago y confirma; el sistema procesa el pago a través de la pasarela externa.<br><br>3\. Si el pago es aprobado, el sistema registra la venta, confirma los asientos, genera las entradas con código QR y envía el comprobante por correo.<br><br>4\. Si el pago es rechazado o falla, el sistema informa el motivo y permite reintentar mientras el bloqueo siga vigente. |

| **ID**  | **Requerimiento funcional**                                                                                               |
| ------- | ------------------------------------------------------------------------------------------------------------------------- |
| REQ-023 | El sistema debe mostrar un resumen del pedido con el detalle y el total antes de confirmar el pago.                       |
| REQ-024 | El sistema debe integrarse con una pasarela de pagos externa para procesar tarjetas y otros medios de pago habilitados.   |
| REQ-025 | El sistema no debe almacenar datos sensibles de tarjetas; solo identificadores de transacción entregados por la pasarela. |
| REQ-026 | El sistema debe registrar la venta y marcar los asientos como ocupados únicamente tras la confirmación del pago.          |
| REQ-027 | El sistema debe generar un código único (QR) por entrada y enviar el comprobante al correo del cliente.                   |
| REQ-028 | El sistema debe manejar de forma consistente los fallos de pago, evitando cobros duplicados o ventas sin cobro.           |

## 7.6. RF-06 – Gestión de entradas del cliente

| **Descripción**                                    | Permite al cliente consultar su historial de compras, descargar sus entradas y solicitar cancelaciones según las políticas del cine.                                                                                                                                                                    |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Media                                                                                                                                                                                                                                                                                                   |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El cliente ingresa a "Mis entradas" y visualiza compras próximas y pasadas.<br><br>2\. El cliente abre una compra y descarga o visualiza el código QR de sus entradas.<br><br>3\. El cliente solicita la cancelación de una compra elegible; el sistema valida la política y gestiona el reembolso. |

| **ID**  | **Requerimiento funcional**                                                                                              |
| ------- | ------------------------------------------------------------------------------------------------------------------------ |
| REQ-029 | El sistema debe listar el historial de compras del cliente con su estado (vigente, usada, cancelada, vencida).           |
| REQ-030 | El sistema debe permitir visualizar y descargar la entrada con su código QR.                                             |
| REQ-031 | El sistema debe permitir cancelar compras dentro del plazo definido por el cine y reflejar el reembolso correspondiente. |
| REQ-032 | El sistema debe liberar los asientos de una compra cancelada para que estén disponibles nuevamente.                      |

## 7.7. RF-07 – Administración de películas

| **Descripción**                                    | Permite al personal administrativo crear, editar, activar y retirar películas del catálogo.                                                                                                                                                                                                                                                     |
| -------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                            |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El administrador accede al módulo de películas y visualiza el listado con filtros.<br><br>2\. El administrador crea o edita una película completando título, sinopsis, duración, género, clasificación, idioma, póster y tráiler.<br><br>3\. El administrador activa o desactiva la película para controlar su visibilidad en la cartelera. |

| **ID**  | **Requerimiento funcional**                                                                                                                 |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| REQ-033 | El sistema debe permitir crear películas con los datos obligatorios definidos (título, duración, género, clasificación, sinopsis y póster). |
| REQ-034 | El sistema debe permitir editar y desactivar películas sin eliminar su historial de funciones y ventas.                                     |
| REQ-035 | El sistema debe validar los formatos de imagen y la longitud de los campos antes de guardar.                                                |
| REQ-036 | El sistema debe impedir la eliminación definitiva de una película con funciones futuras o ventas asociadas.                                 |

## 7.8. RF-08 – Administración de salas

| **Descripción**                                    | Permite configurar las salas del cine, su capacidad, tipo y distribución de asientos.                                                                                                                                                                                                                  |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                   |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El administrador crea una sala indicando nombre, tipo (2D, 3D, VIP, etc.) y capacidad.<br><br>2\. El administrador define la distribución de filas y columnas, marca pasillos y asientos de accesibilidad.<br><br>3\. El administrador puede habilitar o deshabilitar una sala para mantenimiento. |

| **ID**  | **Requerimiento funcional**                                                                                  |
| ------- | ------------------------------------------------------------------------------------------------------------ |
| REQ-037 | El sistema debe permitir registrar salas con nombre único por cine, tipo y capacidad.                        |
| REQ-038 | El sistema debe permitir diseñar el mapa de asientos (filas, columnas, pasillos y asientos especiales).      |
| REQ-039 | El sistema debe permitir deshabilitar una sala y advertir si existen funciones futuras programadas en ella.  |
| REQ-040 | El sistema debe impedir modificar la distribución de una sala con funciones que ya tengan entradas vendidas. |

## 7.9. RF-09 – Administración de funciones

| **Descripción**                                    | Permite programar, modificar y cancelar funciones asignando película, sala, fecha, hora y precios.                                                                                                                                                                                                                                                                                                                                                                     |
| -------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Prioridad**                                      | Alta                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El administrador selecciona película, sala, fecha y hora de inicio; el sistema calcula la hora de fin según la duración.<br><br>2\. El sistema valida que no existan cruces con otras funciones de la misma sala (incluyendo tiempo de limpieza).<br><br>3\. El administrador define los precios por tipo de entrada y publica la función.<br><br>4\. Ante la cancelación de una función con ventas, el sistema notifica a los clientes y gestiona los reembolsos. |

| **ID**  | **Requerimiento funcional**                                                                                                          |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| REQ-041 | El sistema debe permitir programar funciones asociando película, sala, fecha, hora, idioma y formato.                                |
| REQ-042 | El sistema debe validar que dos funciones no se solapen en la misma sala, considerando un intervalo mínimo configurable entre ellas. |
| REQ-043 | El sistema debe permitir definir precios por tipo de entrada (general, niño, adulto mayor, VIP) por función.                         |
| REQ-044 | El sistema debe permitir la programación masiva o repetitiva de funciones (por ejemplo, para toda una semana).                       |
| REQ-045 | El sistema debe notificar por correo a los clientes afectados cuando una función con entradas vendidas sea modificada o cancelada.   |

## 7.10. RF-10 – Gestión de ventas y reportes

| **Descripción**                                    | Permite al personal del cine consultar las ventas, validar entradas en el acceso y obtener reportes de gestión.                                                                                                                                                                                                                                  |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Prioridad**                                      | Media                                                                                                                                                                                                                                                                                                                                            |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El administrador consulta las ventas filtrando por rango de fechas, película, sala o método de pago.<br><br>2\. El administrador genera reportes de ocupación e ingresos y los exporta en formato PDF o Excel.<br><br>3\. En el acceso a la sala, el personal escanea el código QR y el sistema valida la entrada y la marca como utilizada. |

| **ID**  | **Requerimiento funcional**                                                                                        |
| ------- | ------------------------------------------------------------------------------------------------------------------ |
| REQ-046 | El sistema debe registrar cada venta con fecha, usuario, función, asientos, valor y estado del pago.               |
| REQ-047 | El sistema debe ofrecer reportes de ventas, ocupación por función/sala e ingresos por película y por periodo.      |
| REQ-048 | El sistema debe permitir exportar los reportes a PDF y Excel.                                                      |
| REQ-049 | El sistema debe validar el código QR de una entrada en el acceso e impedir su reutilización.                       |
| REQ-050 | El sistema debe mostrar un panel (dashboard) con indicadores clave del día: ventas, ocupación y funciones activas. |

## 7.11. RF-11 – Administración de usuarios y roles

| **Descripción**                                    | Permite gestionar las cuentas del personal y asignar permisos según su rol.                                                                                                                                                                                                                |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Prioridad**                                      | Media                                                                                                                                                                                                                                                                                      |
| **Acciones iniciadoras y comportamiento esperado** | 1\. El administrador general crea una cuenta de personal y le asigna un rol.<br><br>2\. El sistema restringe las opciones visibles y las operaciones permitidas según el rol del usuario.<br><br>3\. El administrador puede desactivar cuentas sin perder la trazabilidad de sus acciones. |

| **ID**  | **Requerimiento funcional**                                                                                                           |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| REQ-051 | El sistema debe permitir crear, editar y desactivar cuentas del personal.                                                             |
| REQ-052 | El sistema debe implementar control de acceso por roles (administrador general, administrador de cine, taquillero, cliente).          |
| REQ-053 | El sistema debe registrar en una bitácora las acciones críticas (creación/edición de funciones, precios, cancelaciones y reembolsos). |

# 8. Reglas de negocio

| **ID** | **Regla de negocio**                                                                                                                                                                        |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RN-01  | Un asiento solo puede estar asociado a una única entrada vendida por función; no se permite la sobreventa.                                                                                  |
| RN-02  | Los asientos seleccionados se bloquean temporalmente (10 minutos por defecto) y se liberan si el pago no se completa.                                                                       |
| RN-03  | La venta se considera efectiva únicamente cuando la pasarela de pagos confirma el pago.                                                                                                     |
| RN-04  | Dos funciones no pueden solaparse en la misma sala; debe respetarse el tiempo mínimo configurable de aseo y preparación entre funciones.                                                    |
| RN-05  | No se pueden vender entradas para funciones que ya hayan iniciado.                                                                                                                          |
| RN-06  | Una película con funciones futuras o ventas asociadas solo puede desactivarse, no eliminarse.                                                                                               |
| RN-07  | La cancelación de compras por parte del cliente solo es posible hasta el plazo definido por el cine antes del inicio de la función (por defecto 2 horas).                                   |
| RN-08  | Si el cine cancela una función, todas las entradas vendidas se reembolsan íntegramente y se notifica a los clientes.                                                                        |
| RN-09  | Cada entrada tiene un código QR único que solo puede validarse una vez.                                                                                                                     |
| RN-10  | Los permisos dependen del rol: solo los administradores pueden crear o modificar películas, salas, funciones y precios; el taquillero únicamente puede validar entradas y consultar ventas. |
| RN-11  | El sistema debe respetar la clasificación por edad de la película e informarla visiblemente al cliente durante la compra.                                                                   |

Para hacer cumplir estas reglas, los requerimientos RF-04, RF-05, RF-09 y RF-11 incluyen validaciones aplicables a todo el sistema.

# 9. Requerimientos de interfaces externas

## 9.1. Interfaces de usuario

La interfaz se divide en dos áreas con vistas distintas: el portal público del cliente y el panel administrativo. Las pantallas principales previstas son:

- **Portal del cliente:** inicio / cartelera, resultados de búsqueda, ficha de película, selección de función, mapa de asientos, resumen y pago, confirmación, "Mis entradas", inicio de sesión y registro.
- **Panel administrativo:** dashboard, catálogo de películas, salas y editor de asientos, calendario de funciones, ventas, reportes, validación de entradas y gestión de usuarios.

Estándares de interfaz (GUI):

- Diseño adaptable (responsive) para móvil, tableta y escritorio.
- Mapa de asientos con leyenda de colores y no dependiente únicamente del color (ícono o patrón distinto por estado) para accesibilidad.
- Botones, menús y mensajes de error con estilo uniforme; acciones principales siempre visibles (por ejemplo, "Continuar").
- Cumplimiento de las pautas de accesibilidad WCAG 2.1 nivel AA en el portal del cliente.
- Interfaz en español, con posibilidad de extenderse a otros idiomas.

## 9.2. Interfaces de hardware

- Computadores, tabletas y teléfonos móviles con navegador web y conexión a Internet.
- Cámara del dispositivo o lector de códigos QR para la validación de entradas en el acceso.
- Impresora de tickets o de documentos (opcional) para el módulo de taquilla.
- No se requiere comunicación directa con dispositivos de hardware especializados en la versión 1.0.

## 9.3. Interfaces de software

| **Componente**                         | **Interacción**                                                                                                                              |
| -------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| Pasarela de pagos                      | Procesamiento de pagos y reembolsos mediante API; el sistema recibe confirmaciones (webhooks) y guarda solo el identificador de transacción. |
| Servicio de correo                     | Envío de verificación de cuenta, recuperación de contraseña, comprobantes de compra y notificaciones de cambios en funciones.                |
| Base de datos relacional               | Almacenamiento de películas, salas, funciones, asientos, usuarios, ventas y bitácora, con transacciones para garantizar la consistencia.     |
| Servicio de almacenamiento de archivos | Almacenamiento de pósters, imágenes y reportes generados.                                                                                    |

## 9.4. Interfaces de comunicación

- Comunicación cliente-servidor mediante HTTPS con TLS 1.2 o superior.
- API REST con intercambio de datos en formato JSON entre el frontend y el backend.
- Correo electrónico mediante SMTP o API de proveedor de correo transaccional.
- Actualización del estado de asientos en tiempo casi real mediante WebSockets o consultas periódicas.
- Todas las comunicaciones con la pasarela de pagos deben cifrarse y autenticarse.

# 10. Requerimientos no funcionales

| **ID** | **Categoría**           | **Requerimiento**                                                                                                                                                                 |
| ------ | ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RNF-01 | Rendimiento             | Las páginas de cartelera y búsqueda deben cargar en menos de 3 segundos con conexión estándar; las operaciones de selección de asientos deben responder en menos de 2 segundos.   |
| RNF-02 | Capacidad               | El sistema debe soportar al menos 500 usuarios concurrentes, con picos en días de estreno, sin degradación significativa.                                                         |
| RNF-03 | Concurrencia            | El sistema debe garantizar que dos usuarios no puedan comprar el mismo asiento simultáneamente.                                                                                   |
| RNF-04 | Disponibilidad          | El sistema debe tener una disponibilidad mínima del 99,5 % mensual, excluyendo mantenimientos programados.                                                                        |
| RNF-05 | Seguridad               | Las contraseñas deben almacenarse con hash seguro (por ejemplo, bcrypt o Argon2); el acceso a datos debe controlarse por roles; debe protegerse contra inyección SQL, XSS y CSRF. |
| RNF-06 | Privacidad              | El tratamiento de datos personales debe cumplir con la normativa de protección de datos aplicable y solicitar el consentimiento del usuario.                                      |
| RNF-07 | Usabilidad              | Un cliente nuevo debe poder completar una compra en menos de 2 minutos y en no más de 6 pasos, sin capacitación previa.                                                           |
| RNF-08 | Compatibilidad          | El sistema debe funcionar correctamente en las dos últimas versiones de Chrome, Firefox, Edge y Safari, y en pantallas desde 360 px de ancho.                                     |
| RNF-09 | Mantenibilidad          | El código debe seguir estándares de desarrollo, estar documentado y contar con pruebas automatizadas sobre las reglas de negocio críticas.                                        |
| RNF-10 | Respaldo y recuperación | Se deben realizar copias de seguridad diarias de la base de datos, con un tiempo máximo de recuperación de 4 horas.                                                               |
| RNF-11 | Trazabilidad            | Las acciones críticas deben quedar registradas con usuario, fecha y hora.                                                                                                         |

# 11. Otros requerimientos

- **Base de datos:** diseño relacional normalizado con integridad referencial; los datos de ventas deben conservarse al menos 5 años para efectos de reportes y auditoría.
- **Internacionalización:** la interfaz debe estar preparada para soportar varios idiomas y monedas; la versión 1.0 se entrega en español.
- **Legales:** cumplimiento de normas de protección de datos, protección al consumidor y facturación o comprobantes de venta vigentes en el país de operación \[por confirmar\].
- **Reutilización:** los componentes de autenticación, envío de correos y generación de reportes deben diseñarse como módulos reutilizables.

Supuestos: los valores numéricos indicados en este documento (tiempos de bloqueo, plazos de cancelación, cantidad de usuarios concurrentes, entre otros) son propuestas iniciales que deben ser validadas con el cliente.

# 12. Glosario

| **Término**         | **Definición**                                                                                            |
| ------------------- | --------------------------------------------------------------------------------------------------------- |
| Stage X             | Nombre de la plataforma web de gestión de cines y venta de entradas.                                      |
| Cartelera           | Conjunto de películas con funciones programadas en un periodo.                                            |
| Función             | Proyección de una película en una sala, fecha y hora específicas.                                         |
| Sala                | Espacio físico del cine donde se proyectan las funciones, con una distribución de asientos definida.      |
| Entrada             | Derecho de acceso a una función para un asiento determinado, identificado con un código QR.               |
| Bloqueo temporal    | Reserva transitoria de un asiento mientras el cliente completa el pago.                                   |
| Pasarela de pagos   | Servicio externo que procesa de forma segura los pagos electrónicos.                                      |
| QR                  | Código de respuesta rápida usado para validar entradas en el acceso.                                      |
| Dashboard           | Panel con indicadores clave de la operación.                                                              |
| RF / REQ / RN / RNF | Identificadores de funcionalidad, requerimiento funcional, regla de negocio y requerimiento no funcional. |
| API REST            | Interfaz de comunicación entre aplicaciones basada en HTTP.                                               |
| WCAG                | Pautas de accesibilidad para el contenido web.                                                            |