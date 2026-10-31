**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

# **Especificación de Casos de Uso** 

**_Stage X – Plataforma web para la gestión de cines Fecha: 07/10/2026_** 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 1 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

## **Tabla de contenido** 

|Historial de Versiones................................................................................................4|
|---|
|Información del Proyecto...........................................................................................4|
|Aprobaciones.............................................................................................................4|
|Resumen Ejecutivo ...................................................................................................5|
|Diagrama de Casos de Uso.......................................................................................8|
|Descripción de Actores............................................................................................14|
|Cliente registrado..................................................................................................15|
|Cliente invitado.....................................................................................................16|
|Taquillero..............................................................................................................17|
|Administrador de cine...........................................................................................18|
|Administrador general..........................................................................................19|
|Pasarela de pagos................................................................................................20|
|Servicio de correo.................................................................................................21|
|Especificación de Casos de Uso.............................................................................22|
|CU-01 Registrar cliente........................................................................................23|
|CU-02 Iniciar sesión.............................................................................................25|
|CU-03 Recuperar contraseña...............................................................................27|
|CU-04 Aplicar control de acceso por rol...............................................................29|
|CU-05 Consultar cartelera....................................................................................31|
|CU-06 Buscar y filtrar películas............................................................................32|
|CU-07 Consultar ficha de película........................................................................33|
|CU-08 Seleccionar función...................................................................................34|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 2 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|CU-09 Seleccionar asientos.................................................................................36|
|---|
|CU-10 Comprar entradas y pagar........................................................................38|
|CU-11 Comprar como invitado.............................................................................40|
|CU-12 Consultar mis entradas.............................................................................42|
|CU-13 Visualizar y descargar entrada con código QR........................................43|
|CU-14 Cancelar compra.......................................................................................44|
|CU-15 Registrar película......................................................................................46|
|CU-16 Editar película...........................................................................................48|
|CU-17 Activar o desactivar película.....................................................................50|
|CU-18 Registrar sala............................................................................................52|
|CU-19 Diseñar mapa de asientos de la sala........................................................54|
|CU-20 Habilitar o deshabilitar sala.......................................................................56|
|CU-21 Programar función.....................................................................................58|
|CU-22 Programar funciones de forma masiva.....................................................60|
|CU-23 Modificar función.......................................................................................62|
|CU-24 Cancelar función.......................................................................................64|
|CU-25 Consultar ventas.......................................................................................66|
|CU-26 Generar reportes de gestión.....................................................................68|
|CU-27 Exportar reporte a PDF o Excel................................................................69|
|CU-28 Visualizar dashboard del día.....................................................................70|
|CU-29 Validar entrada en el acceso....................................................................71|
|CU-30 Crear cuenta de personal y asignar rol.....................................................73|
|CU-31 Editar cuenta de personal y rol.................................................................75|
|CU-32 Desactivar cuenta de personal.................................................................77|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 3 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

CU-33 Consultar bitácora de acciones críticas....................................................79 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 4 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

## **Historial de Versiones** 

|**Fecha**|**Versi**<br>**ón**|**Autor**|**Organizaci**<br>**ón**|**Descripción**|
|---|---|---|---|---|
|07/10/20<br>26|1.0|Equipo<br>Stage X|StageX|Versión inicial de la especificación de casos de<br>uso de Stage X, elaborada a partir del<br>documento de requerimientos v1.0<br>(07/10/2026).|



## **Información del Proyecto** 

|Empresa / Organización|Stage X|
|---|---|
|Proyecto|Stage X–Plataforma web para la gestión de cines|
|Fecha de preparación|07/10/2026|
|Cliente|Cines que adopten la plataforma (por definir)|
|Patrocinador principal|FESC|
|Gerente / Líder de Proyecto|Nixon Esneider Villamizar Sarmiento|
|Gerente / Líder de<br>Desarrollo de Software|Por definir|



## **Aprobaciones** 

|**Nombre y Apellido**|**Cargo**|**Departamento u**<br>**Organización**|**Fecha**|**Firma**|
|---|---|---|---|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 5 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

## **Resumen Ejecutivo** 

Este documento especifica los casos de uso de Stage X, versión 1.0, plataforma web orientada a la gestión integral de cines y a la compra de entradas en línea. Se elaboró a partir del Documento de requerimientos de software de Stage X y traduce sus 11 funcionalidades (RF-01 a RF-11), 53 requerimientos funcionales (REQ-001 a REQ-053) y 11 reglas de negocio (RN-01 a RN-11) en 33 casos de uso, 7 actores y sus flujos de interacción. 

Procesos y subprocesos descritos: registro y autenticación de usuarios; consulta de la cartelera, selección de función y de asientos con bloqueo temporal; compra y pago en línea; gestión de entradas, códigos QR y cancelaciones por el cliente; administración de películas, salas y funciones; consulta de ventas, reportes y validación de entradas en el acceso; y administración de usuarios, roles y bitácora de acciones críticas. 

Áreas organizacionales involucradas: clientes (espectadores), taquilla y acceso a salas, administración de cada cine y administración general de la plataforma. Intervienen además dos sistemas externos: la pasarela de pagos y el servicio de correo electrónico. 

Los casos de uso se agrupan en cinco módulos del sistema, que se resumen en las tablas siguientes. Cada caso se identifica como CU-## y referencia los requerimientos (REQ), funcionalidades (RF) y reglas de negocio (RN) del documento de requerimientos. Tipos de caso de uso: Primario (flujo central del negocio), Secundario (de apoyo) y Opcional (extiende a otro caso). 

### **<u><mark>Acceso y seguridad</mark></u>** 

|**ID**|**Caso de uso**|**Tipo**|**Actores**|**Requerimientos**|
|---|---|---|---|---|
|CU-01|Registrar cliente|Primario|Cliente invitado, Servicio<br>de correo|RF-01 · REQ-001<br>a REQ-005|
|CU-02|Iniciar sesión|Primario|Cliente registrado,<br>Taquillero, Administrador<br>de cine, Administrador<br>general|RF-01 · REQ-003,<br>REQ-005|
|CU-03|Recuperar contraseña|Secundario|Cliente registrado,<br>Taquillero, Administrador<br>de cine, Administrador<br>general, Servicio de<br>correo|RF-01 · REQ-004,<br>REQ-005|
|CU-04|Aplicar control de<br>acceso por rol|Secundario|Cliente registrado,<br>Taquillero, Administrador<br>de cine, Administrador<br>general|RF-11 · REQ-052|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 6 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **<u><mark>Portal del cliente</mark></u>** 

|**ID**|**Caso de uso**|**Tipo**|**Actores**|**Requerimientos**|
|---|---|---|---|---|
|CU-05|Consultar cartelera|Primario|Cliente registrado, Cliente<br>invitado|RF-02 · REQ-007,<br>REQ-008|
|CU-06|Buscar y filtrar<br>películas|Primario|Cliente registrado, Cliente<br>invitado|RF-02 · REQ-009<br>a REQ-012|
|CU-07|Consultar ficha de<br>película|Primario|Cliente registrado, Cliente<br>invitado|RF-02 · REQ-011|
|CU-08|Seleccionar función|Primario|Cliente registrado, Cliente<br>invitado|RF-03 · REQ-013<br>a REQ-016|
|CU-09|Seleccionar asientos|Primario|Cliente registrado, Cliente<br>invitado|RF-04 · REQ-017<br>a REQ-022|
|CU-10|Comprar entradas y<br>pagar|Primario|Cliente registrado, Cliente<br>invitado, Pasarela de<br>pagos, Servicio de correo|RF-05 · REQ-023<br>a REQ-028|
|CU-11|Comprar como<br>invitado|Secundario|Cliente invitado, Servicio<br>de correo|RF-01, RF-05 ·<br>REQ-006|
|CU-12|Consultar mis<br>entradas|Primario|Cliente registrado|RF-06 · REQ-029|
|CU-13|Visualizar y descargar<br>entrada con código<br>QR|Secundario|Cliente registrado|RF-06 · REQ-030|
|CU-14|Cancelar compra|Primario|Cliente registrado,<br>Pasarela de pagos|RF-06 · REQ-031<br>a REQ-053|



### **<u><mark>Administración de catálogo y programación</mark></u>** 

|**ID**|**Caso de uso**|**Tipo**|**Actores**|**Requerimientos**|
|---|---|---|---|---|
|CU-15|Registrar película|Primario|Administrador de cine,<br>Administrador general|RF-07 · REQ-033,<br>REQ-035|
|CU-16|Editar película|Primario|Administrador de cine,<br>Administrador general|RF-07 · REQ-034,<br>REQ-035|
|CU-17|Activar o desactivar<br>película|Primario|Administrador de cine,<br>Administrador general|RF-07 · REQ-034,<br>REQ-036|
|CU-18|Registrar sala|Primario|Administrador de cine,<br>Administrador general|RF-08 · REQ-037|
|CU-19|Diseñar mapa de<br>asientos de la sala|Primario|Administrador de cine,<br>Administrador general|RF-08 · REQ-038,<br>REQ-040|
|CU-20|Habilitar o deshabilitar<br>sala|Secundario|Administrador de cine,<br>Administrador general|RF-08 · REQ-039|
|CU-21|Programar función|Primario|Administrador de cine,<br>Administrador general|RF-09 · REQ-041<br>a REQ-053|
|CU-22|Programar funciones<br>de forma masiva|Opcional|Administrador de cine,<br>Administrador general|RF-09 · REQ-044<br>a REQ-053|
|CU-23|Modificar función|Primario|Administrador de cine,<br>Administrador general|RF-09 · REQ-041<br>a REQ-053|
|CU-24|Cancelar función|Primario|Administrador de cine,<br>Administrador general,<br>Pasarela de pagos,<br>Servicio de correo|RF-09 · REQ-045,<br>REQ-053|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 7 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **<u><mark>Ventas, reportes y validación</mark></u>** 

|**ID**|**Caso de uso**|**Tipo**|**Actores**|**Requerimientos**|
|---|---|---|---|---|
|CU-25|Consultar ventas|Primario|Administrador de cine,<br>Administrador general,<br>Taquillero|RF-10 · REQ-046|
|CU-26|Generar reportes de<br>gestión|Primario|Administrador de cine,<br>Administrador general|RF-10 · REQ-047|
|CU-27|Exportar reporte a<br>PDF o Excel|Opcional|Administrador de cine,<br>Administrador general|RF-10 · REQ-048|
|CU-28|Visualizar dashboard<br>del día|Secundario|Administrador de cine,<br>Administrador general|RF-10 · REQ-050|
|CU-29|Validar entrada en el<br>acceso|Primario|Taquillero|RF-10 · REQ-049|



### **<u><mark>Usuarios y trazabilidad</mark></u>** 

|**ID**|**Caso de uso**|**Tipo**|**Actores**|**Requerimientos**|
|---|---|---|---|---|
|CU-30|Crear cuenta de<br>personal y asignar rol|Primario|Administrador general|RF-11 · REQ-051,<br>REQ-052|
|CU-31|Editar cuenta de<br>personal y rol|Secundario|Administrador general|RF-11 · REQ-051,<br>REQ-052|
|CU-32|Desactivar cuenta de<br>personal|Secundario|Administrador general|RF-11 · REQ-051,<br>REQ-053|
|CU-33|Consultar bitácora de<br>acciones críticas|Secundario|Administrador general|RF-11 · REQ-053|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 8 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

## **Diagrama de Casos de Uso** 

Los diagramas siguientes siguen la notación UML para casos de uso e incluyen los actores, los casos de uso y las relaciones entre ellos. Se presentan cuatro diagramas, uno por grupo de funcionalidad, para mantener la legibilidad: 

- <mark>Actores (humanos y sistemas externos, estos últimos marcados «externo»).</mark> 

- Casos de uso, identificados con el código CU-## que se usa en las fichas de especificación. 

- Relaciones: asociaciones (línea continua entre actor y caso de uso) y relaciones entre casos de uso «include» (uso) y «extend» (extensión), dibujadas con flecha discontinua. 

Las relaciones de los actores con los casos de uso se denominan «Asociaciones». Las relaciones entre casos de uso son de dos tipos: «include» (el caso de origen usa al de destino) y «extend» (el caso de origen extiende opcionalmente el comportamiento del de destino). 

El Cliente invitado y el Cliente registrado son especializaciones del espectador y comparten las funciones de consulta y compra; los sistemas externos (Pasarela de pagos y Servicio de correo) participan como actores secundarios. 

Los diagramas se organizan por módulo del sistema: (1) acceso y seguridad, (2) portal del cliente, (3) administración de catálogo y programación de funciones, y (4) ventas, reportes, validación de entradas, usuarios y trazabilidad. 

**<mark>Diagrama 1. Acceso y seguridad</mark>** 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 9 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



<!-- Start of picture text -->
Stage X — Acceso y seguridad<br>ri<br>Taquillero >A<br>5AD<br>“genera<br><!-- End of picture text -->

**<mark>Diagrama 2. Portal del cliente</mark>** 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 10 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



<!-- Start of picture text -->
Stage X — Portal del cliente<br>f i SOx Ter<br>y fltrar peliculas<br>ClienteNG; (CU-06 Buscar<br>‘f) CU-07 Consutarfoha de potcua<br> —<———<br>cores<br>TO GLa<br>‘con cago OR<br>pagos<br>eenendn <externo><br>eemexsenra<br>Cliente<br>registrado<br><!-- End of picture text -->

### **<mark>Diagrama 3. Administración de catálogo y programación de funciones</mark>** 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 11 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



<!-- Start of picture text -->
Stage X — Administracién de catdlogo y programacién de funciones<br>fa<br>Administrad we<br>pes jor N / ‘CU-16 Editar pelicula<br>i)<br>MfM4<br>WW)<br>i<br>XW)<br>Vy<br>Ki)<br>(\ }<br>{| Meextends<br>\k<br>general<br>Pasarela de<br>mn<br><!-- End of picture text -->

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 12 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **<mark>Diagrama 4. Ventas, reportes, validación de entradas, usuarios y trazabilidad</mark>** 



<!-- Start of picture text -->
‘Stage X — Ventas, reportes, validacién de entradas, usuarios y trazabilidad<br>Yi<br>cine<br>aa (CU-32 Desactivar cuenta de personal<br>Administrador cuz<br><!-- End of picture text -->

<mark>Leyenda:</mark> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 13 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



<!-- Start of picture text -->
Elementos del Diagrama de Casos de Uso<br>«uses»<br>«extends»<br>Actor<br>Asociación<br>Caso de Uso<br><!-- End of picture text -->

## **Descripción de Actores** 

<mark>Un actor es cualquier entidad externa al sistema modelado que interactúa con él.</mark> 

No necesariamente coincide con los usuario, pues un mismo usuario puede desempeñar distintos roles que correspondan con varios actores. Además, un mismo actor puede desempeñar varios papeles según el caso de uso con que interactúa. 

A continuación se presenta la ficha de cada uno de los actores que intervienen en el sistema y aparecen en los diagramas: cinco actores humanos (Cliente registrado, Cliente invitado, Taquillero, Administrador de cine y Administrador general) y dos sistemas externos (Pasarela de pagos y Servicio de correo electrónico). 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 14 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Cliente registrado** 

|Actor|Cliente registrado<br>Identificador:<br>ACT-01|
|---|---|
|Descripción|Espectador que compra entradas en línea con una cuenta<br>propia, verificada por correo electrónico.|
|Características|Nivel técnico básico; accede desde móvil o computador.<br>Mantiene historial de compras y puede descargar o cancelar<br>sus entradas. Frecuencia de uso ocasional (semanal o<br>mensual).|
|Relación|Es una especialización del espectador (Cliente). Comparte con<br>el Cliente invitado las funciones de consulta y compra; solo el<br>Cliente registrado gestiona su historial de entradas. Un Cliente<br>invitado se convierte en Cliente registrado al crear su cuenta.|
|Referencias|Casos de uso en los que interviene: CU-02, CU-03, CU-04,<br>CU-05, CU-06, CU-07, CU-08, CU-09, CU-10, CU-12, CU-13,<br>CU-14.<br>Requerimientos: RF-01, RF-02, RF-03, RF-04, RF-05, RF-06,<br>RF-11.|



|**Atributos**|||
|---|---|---|
|**Nombre**|**Descripción**|**Tipo**|
|Nombre|Nombre del cliente.|Texto|
|Correo<br>electrónico|Identificador único de la cuenta; se usa<br>para iniciar sesión y recibir<br>comprobantes.|Texto (único)|
|Contraseña|Credencial de acceso, almacenada con<br>hash seguro.|Texto (hash)|
|Estado de la<br>cuenta|Pendiente de verificación o Activa.|Enumerado|
|Historial de<br>compras|Compras realizadas con su estado:<br>vigente, usada, cancelada o vencida.|Colección|



### **<u><mark>Comentarios</mark></u>** 

Los datos personales del cliente se tratan conforme a la normativa de protección <u>de datos aplicable y con su consentimiento (RNF-06).</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 15 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Cliente invitado** 

|Actor|Cliente invitado<br>Identificador:<br>ACT-02|
|---|---|
|Descripción|Espectador que consulta la cartelera y compra entradas sin<br>crear una cuenta, identificándose únicamente con su correo<br>electrónico.|
|Características|Nivel técnico básico; compra de forma ocasional. No tiene<br>historial de compras en la plataforma: recibe sus entradas por<br>correo electrónico.|
|Relación|Comparte con el Cliente registrado las funciones de consulta de<br>cartelera, selección de función y asientos y compra. Puede<br>registrarse para convertirse en Cliente registrado.|
|Referencias|Casos de uso en los que interviene: CU-01, CU-05, CU-06,<br>CU-07, CU-08, CU-09, CU-10, CU-11.<br>Requerimientos: RF-01, RF-01, RF-05, RF-02, RF-03, RF-04,<br>RF-05.|



|**Atributos**|||
|---|---|---|
|**Nombre**|**Descripción**|**Tipo**|
|Correo|Dirección donde recibe las entradas y el|Texto|
|electrónico|comprobante de la compra.||
|Selección en|Función, asientos y bloqueo temporal|Objeto temporal|
|curso|asociados a su compra en proceso.||



**<u><mark>Comentarios</mark></u>** Solo participa en las funcionalidades RF-01 (registro y compra como invitado) y <u>RF-02 a RF-05.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 16 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Taquillero** 

|Actor|Taquillero<br>Identificador:<br>ACT-03|
|---|---|
|Descripción|Empleado del cine (personal de acceso) que valida las<br>entradas en el ingreso a la sala y apoya la consulta de ventas.|
|Características|Capacitación básica en el sistema; uso diario, generalmente<br>desde un dispositivo con cámara o lector de códigos QR.<br>Según RN-10, solo puede validar entradas y consultar ventas.|
|Relación|Es un usuario de personal creado y administrado por el<br>Administrador general. No programa películas, salas, funciones<br>ni precios, tarea reservada a los administradores.|
|Referencias|Casos de uso en los que interviene: CU-02, CU-03, CU-04,<br>CU-25, CU-29.<br>Requerimientos: RF-01, RF-10, RF-11.|



|**Atributos**|||
|---|---|---|
|**Nombre**|**Descripción**|**Tipo**|
|Nombre|Nombre del empleado.|Texto|
|Correo<br>electrónico|Identificador único de la cuenta de<br>personal.|Texto (único)|
|Rol|Taquillero / Personal de acceso.|Enumerado|
|Estado de la<br>cuenta|Activa o Desactivada.|Enumerado|



### **<u><mark>Comentarios</mark></u>** 

Sus acciones críticas quedan registradas en la bitácora con usuario, fecha y hora <u>(RNF-11).</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 17 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Administrador de cine** 

|Actor|Administrador de cine<br>Identificador:<br>ACT-04|
|---|---|
|Descripción|Responsable de la operación de uno o varios cines: administra<br>películas, salas, funciones y precios, y revisa ventas y reportes.|
|Características|Uso diario. Tiene acceso al panel administrativo de los cines a<br>su cargo y puede realizar las tareas operativas de RF-07 a<br>RF-10.|
|Relación|Es un usuario de personal creado por el Administrador general.<br>Supervisa la operación que ejecuta el Taquillero y, según<br>RN-10, es uno de los dos roles autorizados para crear o<br>modificar películas, salas, funciones y precios.|
|Referencias|Casos de uso en los que interviene: CU-02, CU-03, CU-04,<br>CU-15, CU-16, CU-17, CU-18, CU-19, CU-20, CU-21, CU-22,<br>CU-23, CU-24, CU-25, CU-26, CU-27, CU-28.<br>Requerimientos: RF-01, RF-07, RF-08, RF-09, RF-10, RF-11.|



|**Atributos**|||
|---|---|---|
|**Nombre**|**Descripción**|**Tipo**|
|Nombre|Nombre del administrador.|Texto|
|Correo<br>electrónico|Identificador único de la cuenta de<br>personal.|Texto (único)|
|Rol|Administrador de cine.|Enumerado|
|Cines a cargo|Cines que el administrador puede<br>gestionar (uno o varios).|Colección|
|Estado de la<br>cuenta|Activa o Desactivada.|Enumerado|



### **<u><mark>Comentarios</mark></u>** 

Es quien confirma la prioridad y los parámetros operativos del cine (plazos, <u>intervalos entre funciones, precios).</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 18 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Administrador general** 

|Actor|Administrador general<br>Identificador:<br>ACT-05|
|---|---|
|Descripción|Responsable de la configuración global de la plataforma:<br>gestiona usuarios y roles, y accede a la mayor parte del<br>sistema.|
|Características|Uso de frecuencia media. Tiene los permisos de los demás<br>roles de personal y, además, administra las cuentas del<br>personal (RF-11).|
|Relación|Crea, edita y desactiva las cuentas del Taquillero y del<br>Administrador de cine. Hereda las funciones de administración<br>de película, sala, función y reportes del Administrador de cine.|
|Referencias|Casos de uso en los que interviene: CU-02, CU-03, CU-04,<br>CU-15, CU-16, CU-17, CU-18, CU-19, CU-20, CU-21, CU-22,<br>CU-23, CU-24, CU-25, CU-26, CU-27, CU-28, CU-30, CU-31,<br>CU-32, CU-33.<br>Requerimientos: RF-01, RF-07, RF-08, RF-09, RF-10, RF-11.|



|**Atributos**|||
|---|---|---|
|**Nombre**|**Descripción**|**Tipo**|
|Nombre|Nombre del administrador general.|Texto|
|Correo<br>electrónico|Identificador único de la cuenta.|Texto (único)|
|Rol|Administrador general.|Enumerado|
|Estado de la<br>cuenta|Activa o Desactivada.|Enumerado|



**<u><mark>Comentarios</mark></u>** Es el único actor autorizado para administrar cuentas de personal y consultar la <u>bitácora de acciones críticas.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 19 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Pasarela de pagos** 

|Actor|Pasarela de pagos|Identificador:<br>ACT-06|
|---|---|---|
|Descripción|Sistema externo que procesa de forma se<br>electrónicos y los reembolsos de Stage X.|gura los pagos<br>|
|Características|Se comunica mediante API cifrada y aute<br>confirmaciones (webhooks). Stage X solo<br>identificador de transacción que entrega,<br>de tarjetas (REQ-025).|nticada; envía<br>conserva el<br>nunca datos sensibles|
|Relación|Es invocado por el sistema durante la com<br>las cancelaciones de compras o funciones|pra (pago) y durante<br>(reembolso).|
|Referencias|Casos de uso en los que interviene: CU-1<br>Requerimientos: RF-05, RF-06, RF-09.|0, CU-14, CU-24.|
|**Atributos**|||
|**Nombre**|**Descripción**|**Tipo**|
|Identificador<br>de transacción|Código único del pago o reembolso<br>entregado por la pasarela.|Texto|
|Estado del<br>pago|Aprobado, rechazado o fallido.|Enumerado|
|Monto|Valor cobrado o reembolsado.|Numérico|
|Medio de pago|Tarjeta u otro medio habilitado.|Texto|



**<u><mark>Comentarios</mark></u>** <u><mark>Actor secundario: no inicia casos de uso; responde a solicitudes del sistema.</mark></u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 20 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

### **Servicio de correo** 

|Actor|Servicio de correo<br>Identificador:<br>ACT-07|
|---|---|
|Descripción|Servicio externo de correo electrónico transaccional (SMTP o<br>API de proveedor) usado por Stage X para enviar mensajes a<br>clientes.|
|Características|Envía verificación de cuenta, recuperación de contraseña,<br>comprobantes de compra y notificaciones de cambios o<br>cancelaciones de funciones.|
|Relación|Es invocado por el sistema en los casos de uso de registro,<br>recuperación de contraseña, compra y modificación o<br>cancelación de funciones.|
|Referencias|Casos de uso en los que interviene: CU-01, CU-03, CU-10,<br>CU-11, CU-24.<br>Requerimientos: RF-01, RF-01, RF-05, RF-05, RF-09.|
|**Atributos**||
|**Nombre**|**Descripción**<br>**Tipo**|
|Destinatario|Dirección de correo del cliente.<br>Texto|
|Tipo de<br>mensaje|Verificación, recuperación, comprobante<br>o notificación de cambio.<br>Enumerado|
|Estado de<br>envío|Enviado o fallido.<br>Enumerado|
|**Comentarios**||
|Actor secundario<br>sistema.|: no inicia casos de uso; ejecuta los envíos solicitados por el|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 21 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



## **Especificación de Casos de Uso** 

A continuación se presenta la especificación de los 33 casos de uso del sistema, agrupados por módulo. Cada ficha describe la forma en que el actor interactúa con el sistema: actores, tipo, referencias a los requerimientos, precondición, postcondición, curso normal de eventos y cursos alternos. 

Convenciones: «Sistema» designa las acciones automáticas de Stage X; los valores numéricos mencionados (por ejemplo, bloqueo de 10 minutos, máximo de 10 asientos por compra o cancelación hasta 2 horas antes de la función) son los valores por defecto del documento de requerimientos y están sujetos a validación con el cliente. El número del curso alterno hace referencia al paso del curso normal en el que ocurre. 

Los casos de uso se identifican con el código CU-## y los requerimientos con los códigos REQ-###, RF-##, RN-## del documento de requerimientos. 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 22 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-01 Registrar cliente** 

|Caso de Uso|Registrar cliente<br>**Identificador:**<br>CU-01|
|---|---|
|Actores|Cliente invitado, Servicio de correo|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-01. Requerimientos: REQ-001, REQ-002,<br>REQ-005.<br>Casos de uso relacionados: CU-02 Iniciar sesión.|
|Precondición|El usuario no tiene sesión iniciada ni una cuenta registrada con<br>el correo que va a ingresar.|
|Postcondición|Se crea la cuenta del cliente en estado «Pendiente de<br>verificación» y se envía un correo de verificación. Al confirmar el<br>enlace, la cuenta queda activa.|
|Descripción|Permite a un espectador crear una cuenta en Stage X con<br>nombre, correo electrónico único y contraseña, y activarla<br>mediante un enlace enviado a su correo.|
|Resumen|El invitado diligencia el formulario de registro, el sistema valida<br>los datos, crea la cuenta pendiente y envía un correo; la cuenta<br>se activa al confirmar el enlace.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente invitado|Selecciona la opción«Registrarse».|
|2|Sistema|Muestra el formulario de registro<br>(nombre, correo electrónico y<br>contraseña).|
|3|Cliente invitado|Ingresa sus datos y confirma el<br>registro.|
|4|Sistema|Valida el formato de los datos, la<br>unicidad del correo y las reglas de la<br>contraseña.|
|5|Sistema|Crea la cuenta en estado «Pendiente<br>de verificación» y solicita el envío del<br>correo de verificación.|
|6|Servicio de correo|Entrega al cliente un correo con el<br>enlace de verificación.|
|7|Cliente invitado|Abre el enlace de verificación.|
|8|Sistema|Activa la cuenta, muestra un<br>mensaje de confirmación e invita al<br>cliente a iniciar sesión.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 23 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si algún dato es inválido o el correo ya está registrado, el sistema<br>muestra un mensaje claro sin revelar información sensible, conserva<br>los datos ingresados y regresa al paso 3.|
|6|Si el envío del correo falla, el sistema informa la situación y permite<br>solicitar el reenvío del correo de verificación.|
|7|Si el enlace es inválido o ha vencido, el sistema informa el motivo y<br>permite solicitar un nuevo enlace. La cuenta permanece sin activar.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 24 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-02 Iniciar sesión** 

|Caso de Uso|Iniciar sesión<br>**Identificador:**<br>CU-02|
|---|---|
|Actores|Cliente registrado, Taquillero, Administrador de cine,<br>Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-01. Requerimientos: REQ-003, REQ-005.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-01 Registrar cliente, CU-03<br>Recuperar contraseña, CU-04 Aplicar control de acceso por rol.|
|Precondición|El usuario cuenta con una cuenta activa y verificada.|
|Postcondición|Se crea una sesión segura y el usuario queda dirigido a la vista<br>que corresponde a su rol. La sesión expira por inactividad.|
|Descripción|Permite a los clientes y al personal del cine autenticarse con<br>correo y contraseña para acceder al sistema según su rol.|
|Resumen|El usuario ingresa sus credenciales, el sistema las valida, abre<br>una sesión segura y lo redirige a la vista de su rol.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Usuario|Accede a la opción«Iniciar sesión».|
|2|Sistema|Muestra el formulario de ingreso<br>(correo y contraseña).|
|3|Usuario|Ingresa su correo y contraseña y<br>confirma.|
|4|Sistema|Valida las credenciales y el estado<br>de la cuenta.|
|5|Sistema|Crea una sesión segura con<br>expiración por inactividad.|
|6|Sistema|Aplica el control de acceso por rol<br>(ver CU-04) y redirige al usuario a la<br>vista correspondiente: portal del<br>cliente, validación de entradas o<br>panel administrativo.|



### **Cursos Alternos** 

|**Nro.**<br>**Descripción de acciones alternas**|
|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 25 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|4|Si las credenciales son inválidas, el sistema muestra un mensaje<br>genérico que no revela cuál dato falló y regresa al paso 3.|
|---|---|
|4|Si la cuenta no ha sido verificada, el sistema lo informa y ofrece<br>reenviar el correo de verificación.|
|4|Si la cuenta del personal está desactivada, el sistema niega el acceso<br>e informa que debe contactar al administrador.|
|5|Si la sesión permanece inactiva durante el tiempo configurado, el<br>sistema la cierra y solicita iniciar sesión nuevamente.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 26 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-03 Recuperar contraseña** 

|Caso de Uso|Recuperar contraseña<br>**Identificador:**<br>CU-03|
|---|---|
|Actores|Cliente registrado, Taquillero, Administrador de cine,<br>Administrador general, Servicio de correo|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-01. Requerimientos: REQ-004, REQ-005.<br>Casos de uso relacionados: CU-02 Iniciar sesión.|
|Precondición|El usuario tiene una cuenta registrada y no recuerda su<br>contraseña.|
|Postcondición|La contraseña del usuario queda actualizada con un hash seguro<br>y el enlace de recuperación queda invalidado.|
|Descripción|Permite al usuario restablecer su contraseña mediante un enlace<br>temporal enviado a su correo registrado.|
|Resumen|El usuario solicita la recuperación, recibe un enlace temporal por<br>correo y define una nueva contraseña.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Usuario|Selecciona la opción «¿Olvidó su<br>contraseña?».|
|2|Sistema|Solicita el correo electrónico de la<br>cuenta.|
|3|Usuario|Ingresa su correo electrónico y<br>confirma.|
|4|Sistema|Genera un enlace temporal de<br>recuperación y solicita su envío al<br>correo registrado.|
|5|Servicio de correo|Entrega el correo con el enlace de<br>recuperación.|
|6|Usuario|Abre el enlace e ingresa la nueva<br>contraseña.|
|7|Sistema|Valida que el enlace esté vigente y<br>que la contraseña cumpla las reglas;<br>actualiza la contraseña, invalida el<br>enlace y confirma el cambio.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 27 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si el correo no corresponde a una cuenta registrada, el sistema<br>muestra el mismo mensaje informativo neutro para no revelar qué<br>correos existen.|
|7|Si el enlace venció o ya fue usado, el sistema informa el motivo y<br>permite solicitar un nuevo enlace.|
|7|Si la nueva contraseña no cumple las reglas de seguridad, el sistema<br>indica los requisitos y regresa al paso 6.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 28 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-04 Aplicar control de acceso por rol** 

|Caso de Uso|Aplicar control de acceso por rol<br>**Identificador:**<br>CU-04|
|---|---|
|Actores|Cliente registrado, Taquillero, Administrador de cine,<br>Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-11. Requerimientos: REQ-052. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-02 Iniciar sesión.|
|Precondición|El usuario tiene una sesión iniciada y un rol asignado (cliente,<br>taquillero, administrador de cine o administrador general).|
|Postcondición|El usuario solo visualiza y ejecuta las opciones y operaciones<br>permitidas para su rol.|
|Descripción|Restringe las opciones visibles y las operaciones permitidas en<br>función del rol del usuario autenticado. Se ejecuta de forma<br>automática después del inicio de sesión y en cada solicitud.|
|Resumen|El sistema identifica el rol del usuario y habilita únicamente las<br>opciones y operaciones que le corresponden.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Sistema|Identifica el rol del usuario<br>autenticado.|
|2|Sistema|Obtiene los permisos asociados al<br>rol (solo los administradores crean o<br>modifican películas, salas, funciones<br>y precios; el taquillero valida<br>entradas y consulta ventas).|
|3|Sistema|Construye la vista mostrando<br>únicamente las opciones permitidas.|
|4|Usuario|Solicita una operación del sistema.|
|5|Sistema|Verifica que el rol tenga permiso<br>para la operación y la ejecuta.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 29 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|5<br>Si el rol no tiene permiso para la operación (por ejemplo, un taquillero|
|---|
|intenta modificar una función), el sistema la rechaza y muestra un<br>mensaje de acceso no autorizado.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 30 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-05 Consultar cartelera** 

|Caso de Uso|Consultar cartelera<br>**Identificador:**<br>CU-05|
|---|---|
|Actores|Cliente registrado, Cliente invitado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-02. Requerimientos: REQ-007, REQ-008.<br>Casos de uso relacionados: CU-06 Buscar y filtrar películas,<br>CU-07 Consultar ficha de película.|
|Precondición|Ninguna. La cartelera es pública y no requiere iniciar sesión.|
|Postcondición|No se modifica el estado del sistema.|
|Descripción|Permite al cliente visualizar las películas en cartelera y los<br>próximos estrenos.|
|Resumen|El cliente ingresa a la página principal y el sistema presenta las<br>películas con funciones programadas y los próximos estrenos.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado / Cliente<br>invitado|Ingresa a la página principal de<br>Stage X.|
|2|Sistema|Muestra las películas en cartelera:<br>solo las activas con al menos una<br>función programada.|
|3|Sistema|Muestra la sección de próximos<br>estrenos con su fecha de<br>lanzamiento.|
|4|Cliente registrado / Cliente<br>invitado|Selecciona una película para ver su<br>detalle (ver CU-07) o utiliza la<br>búsqueda (ver CU-06).|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si no hay películas con funciones programadas, el sistema muestra|
||un mensaje informativo indicando que no hay cartelera disponible.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 31 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-06 Buscar y filtrar películas** 

|Caso de Uso|Buscar y filtrar películas<br>**Identificador:**<br>CU-06|
|---|---|
|Actores|Cliente registrado, Cliente invitado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-02. Requerimientos: REQ-009, REQ-010,<br>REQ-012.<br>Casos de uso relacionados: CU-05 Consultar cartelera.|
|Precondición|El cliente se encuentra en la cartelera o en la página principal.|
|Postcondición|El listado de películas queda actualizado según el criterio de<br>búsqueda y los filtros aplicados.|
|Descripción|Permite buscar películas por título y filtrarlas por género,<br>clasificación por edad, idioma (doblada o subtitulada) y formato<br>(2D o 3D).|
|Resumen|El cliente escribe un título o aplica filtros y el sistema actualiza el<br>listado de resultados.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado / Cliente<br>invitado|Escribe el título de una película y/o<br>aplica filtros de género, clasificación,<br>idioma o formato.|
|2|Sistema|Busca coincidencias parciales,<br>ignorando mayúsculas y tildes, y<br>aplica los filtros seleccionados.|
|3|Sistema|Actualiza el listado con las películas<br>que cumplen los criterios.|
|4|Cliente registrado / Cliente<br>invitado|Selecciona una película del listado<br>para ver su detalle (ver CU-07).|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|3|Si la búsqueda no arroja resultados, el sistema muestra un mensaje<br>informativo y sugiere modificar o limpiar los filtros.|
|4|Si el cliente limpia la búsqueda y los filtros, el sistema vuelve a<br>mostrar la cartelera completa.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 32 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-07 Consultar ficha de película** 

|Caso de Uso|Consultar ficha de película<br>**Identificador:**<br>CU-07|
|---|---|
|Actores|Cliente registrado, Cliente invitado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-02. Requerimientos: REQ-011. Reglas de<br>negocio: RN-11.<br>Casos de uso relacionados: CU-05 Consultar cartelera, CU-06<br>Buscar y filtrar películas, CU-08 Seleccionar función.|
|Precondición|La película existe, está activa y es visible en la cartelera o en<br>próximos estrenos.|
|Postcondición|No se modifica el estado del sistema.|
|Descripción|Permite al cliente consultar la información detallada de una<br>película y las funciones disponibles.|
|Resumen|El cliente selecciona una película y el sistema muestra su ficha<br>con sinopsis, duración, clasificación, reparto, póster, tráiler y<br>funciones disponibles.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado / Cliente<br>invitado|Selecciona una película.|
|2|Sistema|Muestra la ficha con sinopsis,<br>duración, reparto, póster y tráiler.|
|3|Sistema|Muestra de forma visible la<br>clasificación por edad de la película.|
|4|Sistema|Muestra las funciones disponibles de<br>la película.|
|5|Cliente registrado / Cliente<br>invitado|Reproduce el tráiler o continúa a la<br>selección de función (ver CU-08).|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la película no tiene tráiler registrado, el sistema omite esa sección y<br>muestra el resto de la ficha.|
|4|Si la película es un próximo estreno y aún no tiene funciones, el<br>sistema muestra la fecha de lanzamiento en lugar de las funciones.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 33 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-08 Seleccionar función** 

|Caso de Uso|Seleccionar función<br>**Identificador:**<br>CU-08|
|---|---|
|Actores|Cliente registrado, Cliente invitado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-03. Requerimientos: REQ-013, REQ-014,<br>REQ-015, REQ-016. Reglas de negocio: RN-05.<br>Casos de uso relacionados: CU-07 Consultar ficha de película,<br>CU-09 Seleccionar asientos.|
|Precondición|El cliente está en la ficha de una película activa con funciones<br>programadas.|
|Postcondición|El cliente tiene seleccionada una función vigente y el sistema lo<br>dirige al mapa de asientos.|
|Descripción|Permite al cliente elegir fecha, hora, sala y formato de la función<br>a la que desea asistir.|
|Resumen|Desde la ficha de la película el cliente elige fecha y función,<br>revisa precios y disponibilidad y continúa al mapa de asientos.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado / Cliente<br>invitado|Elige una fecha desde la ficha de la<br>película.|
|2|Sistema|Lista las funciones de la fecha<br>agrupadas por cine, sala y formato,<br>ocultando o deshabilitando las que<br>ya iniciaron o están agotadas.|
|3|Cliente registrado / Cliente<br>invitado|Selecciona una función.|
|4|Sistema|Muestra el precio por tipo de<br>entrada, el idioma, el formato y la<br>cantidad de asientos disponibles.|
|5|Cliente registrado / Cliente<br>invitado|Confirma la función elegida.|
|6|Sistema|Continúa al mapa de asientos de la<br>función (ver CU-09).|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 34 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si no hay funciones para la fecha elegida, el sistema informa que no<br>hay funciones disponibles y sugiere otra fecha.|
|3|Si la función ya inició o está agotada, el sistema no permite<br>seleccionarla (no se venden entradas para funciones iniciadas).|
|5|Si el cliente desea cambiar de función antes del pago, el sistema<br>regresa al paso 3 sin perder la sesión del usuario.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 35 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-09 Seleccionar asientos** 

|Caso de Uso|Seleccionar asientos<br>**Identificador:**<br>CU-09|
|---|---|
|Actores|Cliente registrado, Cliente invitado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-04. Requerimientos: REQ-017, REQ-018,<br>REQ-019, REQ-020, REQ-021, REQ-022. Reglas de negocio:<br>RN-01, RN-02.<br>Casos de uso relacionados: CU-08 Seleccionar función, CU-10<br>Comprar entradas y pagar.|
|Precondición|El cliente seleccionó una función vigente con asientos<br>disponibles.|
|Postcondición|Los asientos elegidos quedan bloqueados temporalmente a<br>nombre del cliente (10 minutos por defecto) y se inicia un<br>contador visible. Si el cliente cancela o el tiempo vence, los<br>asientos se liberan.|
|Descripción|Permite al cliente visualizar el mapa de la sala y escoger sus<br>asientos, con bloqueo temporal para evitar ventas duplicadas.|
|Resumen|El sistema muestra el plano de la sala, el cliente elige hasta 10<br>asientos, el sistema los bloquea temporalmente y continúa al<br>pago.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Sistema|Muestra el plano de la sala indicando<br>asientos libres, ocupados,<br>bloqueados, seleccionados y de<br>accesibilidad, con leyenda de íconos<br>y colores.|
|2|Cliente registrado / Cliente<br>invitado|Selecciona uno o varios asientos,<br>hasta un máximo de 10 por<br>transacción.|
|3|Sistema|Verifica la disponibilidad de cada<br>asiento y los bloquea<br>temporalmente.|
|4|Sistema|Muestra los asientos elegidos y un<br>contador visible con el tiempo<br>restante del bloqueo.|
|5|Cliente registrado / Cliente<br>invitado|Confirma la selección.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 36 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|6<br>Sistema|Continúa al resumen del pedido y|
|---|---|
||pago (ver CU-10).|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el cliente intenta seleccionar más de 10 asientos, el sistema lo<br>impide e informa el máximo permitido.|
|3|Si un asiento ya está ocupado o bloqueado por otro usuario, el<br>sistema rechaza la selección de ese asiento, actualiza el mapa e<br>informa al cliente. Dos usuarios nunca pueden bloquear el mismo<br>asiento.|
|4|Si el tiempo de bloqueo vence sin completar el pago, el sistema libera<br>automáticamente los asientos, notifica al cliente y regresa al paso 1.|
|5|Si el cliente cancela la selección, el sistema libera inmediatamente los<br>asientos bloqueados.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 37 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-10 Comprar entradas y pagar** 

|Caso de Uso|Comprar entradas y pagar<br>**Identificador:**<br>CU-10|
|---|---|
|Actores|Cliente registrado, Cliente invitado, Pasarela de pagos, Servicio<br>de correo|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-05. Requerimientos: REQ-023, REQ-024,<br>REQ-025, REQ-026, REQ-027, REQ-028. Reglas de negocio:<br>RN-01, RN-02, RN-03, RN-05, RN-11.<br>Casos de uso relacionados: CU-09 Seleccionar asientos, CU-11<br>Comprar como invitado, CU-12 Consultar mis entradas.|
|Precondición|El cliente tiene asientos bloqueados con el bloqueo temporal<br>vigente y la función no ha iniciado.|
|Postcondición|La venta queda registrada con pago aprobado, los asientos<br>pasan a estado ocupado, se generan las entradas con código<br>QR único y el comprobante se envía por correo.|
|Descripción|Permite al cliente revisar su pedido, pagar en línea a través de la<br>pasarela de pagos y recibir sus entradas digitales.|
|Resumen|El sistema muestra el resumen del pedido, el cliente paga<br>mediante la pasarela y, al aprobarse el pago, el sistema registra<br>la venta, genera las entradas con QR y envía el comprobante.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Sistema|Muestra el resumen del pedido:<br>película, función, asientos, tipo de<br>entrada, clasificación por edad y total<br>a pagar.|
|2|Cliente registrado / Cliente<br>invitado|Revisa el resumen y elige el medio<br>de pago.|
|3|Cliente registrado / Cliente<br>invitado|Confirma el pago.|
|4|Sistema|Envía la solicitud de pago a la<br>pasarela, sin almacenar datos<br>sensibles de tarjeta.|
|5|Pasarela de pagos|Procesa el pago y responde con su<br>aprobación y el identificador de<br>transacción.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 38 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|6|Sistema|Registra la venta y marca los<br>asientos como ocupados,<br>únicamente tras la confirmación del<br>pago.|
|---|---|---|
|7|Sistema|Genera una entrada con código QR<br>único por cada asiento.|
|8|Servicio de correo|Envía el comprobante y las entradas<br>al correo del cliente.|
|9|Sistema|Muestra la pantalla de confirmación<br>de la compra.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el cliente no ha iniciado sesión, puede continuar como invitado (ver<br>CU-11) o iniciar sesión (ver CU-02).|
|3|Si el bloqueo temporal venció, el sistema libera los asientos, informa<br>al cliente y regresa a la selección de asientos (ver CU-09).|
|5|Si el pago es rechazado o falla, el sistema informa el motivo y permite<br>reintentar o cambiar el medio de pago mientras el bloqueo siga<br>vigente.|
|5|Si la pasarela no responde, el sistema consulta el estado de la<br>transacción antes de permitir un nuevo intento, evitando cobros<br>duplicados y ventas sin cobro.|
|8|Si falla el envío del correo, la venta no se revierte; el sistema muestra<br>las entradas en pantalla y permite reintentar el envío o consultarlas en<br>CU-12.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 39 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-11 Comprar como invitado** 

|Caso de Uso|Comprar como invitado<br>**Identificador:**<br>CU-11|
|---|---|
|Actores|Cliente invitado, Servicio de correo|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-01, RF-05. Requerimientos: REQ-006. Reglas<br>de negocio: RN-03.<br>Casos de uso relacionados: CU-10 Comprar entradas y pagar,<br>CU-01 Registrar cliente, CU-02 Iniciar sesión.|
|Precondición|El cliente no tiene sesión iniciada, tiene asientos bloqueados y<br>se encuentra en el resumen del pedido.|
|Postcondición|La compra queda asociada al correo electrónico del invitado, que<br>recibirá sus entradas y el comprobante.|
|Descripción|Permite completar la compra sin crear una cuenta,<br>proporcionando únicamente un correo electrónico para recibir las<br>entradas.|
|Resumen|El invitado ingresa su correo electrónico y continúa con el pago;<br>las entradas se envían a ese correo.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Sistema|Ofrece las opciones «Continuar<br>como invitado», «Iniciar sesión» o<br>«Registrarse».|
|2|Cliente invitado|Selecciona «Continuar como<br>invitado».|
|3|Sistema|Solicita el correo electrónico donde<br>recibirá las entradas.|
|4|Cliente invitado|Ingresa su correo electrónico.|
|5|Sistema|Valida el formato del correo y lo<br>asocia a la compra en curso.|
|6|Sistema|Continúa con el pago (ver CU-10).|
|7|Servicio de correo|Una vez aprobado el pago, envía las<br>entradas y el comprobante al correo<br>del invitado.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 40 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el cliente elige iniciar sesión o registrarse, el sistema ejecuta CU-02<br>o CU-01 y conserva la selección mientras el bloqueo temporal siga<br>vigente.|
|5|Si el correo tiene un formato inválido, el sistema muestra un mensaje<br>y regresa al paso 3.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 41 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-12 Consultar mis entradas** 

|Caso de Uso|Consultar mis entradas<br>**Identificador:**<br>CU-12|
|---|---|
|Actores|Cliente registrado|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-06. Requerimientos: REQ-029.<br>Casos de uso relacionados: CU-13 Visualizar y descargar<br>entrada con código QR, CU-14 Cancelar compra, CU-10<br>Comprar entradas y pagar.|
|Precondición|El cliente tiene una sesión iniciada.|
|Postcondición|No se modifica el estado del sistema.|
|Descripción|Permite al cliente consultar su historial de compras, próximas y<br>pasadas, con el estado de cada una.|
|Resumen|El cliente ingresa a «Mis entradas» y visualiza sus compras con<br>su estado: vigente, usada, cancelada o vencida.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado|Ingresa a la opción«Mis entradas».|
|2|Sistema|Lista las compras del cliente<br>separadas en próximas y pasadas,<br>con su estado (vigente, usada,<br>cancelada o vencida).|
|3|Cliente registrado|Selecciona una compra.|
|4|Sistema|Muestra el detalle: película, función,<br>asientos, valor total y estado de la<br>compra.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el cliente aún no tiene compras, el sistema muestra un mensaje|
||informativo con un acceso a la cartelera.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 42 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-13 Visualizar y descargar entrada con código QR** 

|Caso de Uso|Visualizar y descargar entrada con<br>código QR<br>**Identificador:**<br>CU-13|
|---|---|
|Actores|Cliente registrado|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-06. Requerimientos: REQ-030. Reglas de<br>negocio: RN-09.<br>Casos de uso relacionados: CU-12 Consultar mis entradas.|
|Precondición|El cliente tiene sesión iniciada y abrió una compra con estado<br>«vigente».|
|Postcondición|El cliente dispone de su entrada con código QR en pantalla o<br>descargada en su dispositivo.|
|Descripción|Permite visualizar y descargar las entradas con el código QR<br>que se presentará en el acceso a la sala.|
|Resumen|El cliente abre una compra vigente, visualiza el QR de cada<br>entrada y puede descargarlo.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado|Abre una compra vigente desde<br>CU-12.|
|2|Sistema|Muestra cada entrada con su<br>asiento, datos de la función y código<br>QR único.|
|3|Cliente registrado|Solicita descargar la entrada.|
|4|Sistema|Genera el archivo de la entrada y lo<br>entrega para su descarga.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la compra está cancelada, usada o vencida, el sistema no muestra<br>el código QR y solo presenta el estado de la compra.|
|4|Si la generación del archivo falla, el sistema informa el error y permite<br>reintentar; el QR sigue visible en pantalla.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 43 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-14 Cancelar compra** 

|Caso de Uso|Cancelar compra<br>**Identificador:**<br>CU-14|
|---|---|
|Actores|Cliente registrado, Pasarela de pagos|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-06. Requerimientos: REQ-031, REQ-032,<br>REQ-053. Reglas de negocio: RN-01, RN-07.<br>Casos de uso relacionados: CU-12 Consultar mis entradas.|
|Precondición|El cliente tiene sesión iniciada y una compra «vigente» cuya<br>función no ha iniciado y está dentro del plazo de cancelación<br>definido por el cine (por defecto, hasta 2 horas antes del inicio).|
|Postcondición|La compra queda cancelada, se solicita el reembolso a la<br>pasarela, los códigos QR quedan invalidados, los asientos se<br>liberan y la acción se registra en la bitácora.|
|Descripción|Permite al cliente solicitar la cancelación de una compra elegible<br>y gestionar su reembolso según las políticas del cine.|
|Resumen|El cliente solicita la cancelación, el sistema valida la política,<br>tramita el reembolso con la pasarela y libera los asientos.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Cliente registrado|Selecciona la opción «Cancelar<br>compra» en el detalle de una<br>compra.|
|2|Sistema|Valida que la compra sea elegible<br>según el plazo de cancelación<br>definido por el cine.|
|3|Sistema|Muestra las condiciones y el valor a<br>reembolsar y solicita confirmación.|
|4|Cliente registrado|Confirma la cancelación.|
|5|Sistema|Solicita el reembolso a la pasarela<br>de pagos.|
|6|Pasarela de pagos|Procesa el reembolso y confirma la<br>operación.|
|7|Sistema|Marca la compra como cancelada,<br>invalida los códigos QR y libera los<br>asientos para que queden<br>disponibles nuevamente.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 44 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|8|Sistema|Registra la cancelación y el|
|---|---|---|
|||reembolso en la bitácora e informa al|
|||cliente el resultado.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la compra está fuera del plazo de cancelación o la función ya inició,<br>el sistema no permite cancelar e informa el plazo vigente.|
|4|Si el cliente desiste, el sistema cierra la solicitud sin modificar la<br>compra.|
|6|Si el reembolso falla, el sistema informa que no fue posible<br>procesarlo, mantiene la compra vigente y permite reintentar<br>posteriormente.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 45 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-15 Registrar película** 

|Caso de Uso|Registrar película<br>**Identificador:**<br>CU-15|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-07. Requerimientos: REQ-033, REQ-035.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-16 Editar película, CU-17 Activar<br>o desactivar película.|
|Precondición|El administrador tiene sesión iniciada y permiso para administrar<br>películas.|
|Postcondición|La película queda registrada en el catálogo con sus datos<br>completos y será visible en la cartelera cuando esté activa y<br>tenga funciones programadas.|
|Descripción|Permite crear una película en el catálogo con sus datos<br>obligatorios: título, duración, género, clasificación, sinopsis y<br>póster.|
|Resumen|El administrador diligencia el formulario de la película, el sistema<br>valida los datos y la guarda en el catálogo.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Accede al módulo de películas.|
|2|Sistema|Muestra el listado de películas con<br>filtros.|
|3|Administrador de cine /<br>Administrador general|Selecciona «Nueva película».|
|4|Sistema|Muestra el formulario de registro.|
|5|Administrador de cine /<br>Administrador general|Completa título, sinopsis, duración,<br>género, clasificación, idioma, póster<br>y tráiler, y guarda.|
|6|Sistema|Valida los campos obligatorios, la<br>longitud de los campos y el formato<br>de las imágenes.|
|7|Sistema|Registra la película y muestra un<br>mensaje de confirmación.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 46 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|6|Si faltan datos obligatorios o alguno excede la longitud permitida, el<br>sistema indica los errores por campo y regresa al paso 5 conservando<br>lo ingresado.|
|6|Si el póster no cumple el formato de imagen admitido, el sistema lo<br>rechaza e informa los formatos válidos.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 47 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-16 Editar película** 

|Caso de Uso|Editar película<br>**Identificador:**<br>CU-16|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-07. Requerimientos: REQ-034, REQ-035.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-15 Registrar película, CU-17<br>Activar o desactivar película.|
|Precondición|El administrador tiene sesión iniciada y la película existe en el<br>catálogo.|
|Postcondición|Los datos de la película quedan actualizados sin afectar el<br>historial de funciones y ventas asociadas.|
|Descripción|Permite modificar los datos de una película existente.|
|Resumen|El administrador selecciona una película, modifica sus datos y el<br>sistema los valida y guarda conservando el historial.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Busca y selecciona la película en el<br>listado del módulo de películas.|
|2|Sistema|Muestra el formulario con los datos<br>actuales de la película.|
|3|Administrador de cine /<br>Administrador general|Modifica los datos requeridos y<br>guarda.|
|4|Sistema|Valida los campos, su longitud y el<br>formato de las imágenes.|
|5|Sistema|Actualiza la película conservando las<br>funciones y ventas asociadas y<br>muestra un mensaje de<br>confirmación.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|3|Si el administrador cancela la edición, el sistema descarta los cambios<br>y regresa al listado.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 48 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

- 4 Si algún dato es inválido, el sistema indica los errores y regresa al <u>paso 3.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 49 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-17 Activar o desactivar película** 

|Caso de Uso|Activar o desactivar película<br>**Identificador:**<br>CU-17|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-07. Requerimientos: REQ-034, REQ-036.<br>Reglas de negocio: RN-06, RN-10.<br>Casos de uso relacionados: CU-15 Registrar película, CU-16<br>Editar película.|
|Precondición|El administrador tiene sesión iniciada y la película existe en el<br>catálogo.|
|Postcondición|La película queda activa (visible en cartelera) o desactivada<br>(oculta), conservando su historial de funciones y ventas.|
|Descripción|Permite controlar la visibilidad de una película en la cartelera<br>mediante su activación o desactivación. Las películas con<br>funciones futuras o ventas no se eliminan, solo se desactivan.|
|Resumen|El administrador cambia el estado de una película y el sistema<br>actualiza la cartelera sin perder el historial.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Selecciona una película en el listado.|
|2|Administrador de cine /<br>Administrador general|Elige la acción «Activar» o<br>«Desactivar».|
|3|Sistema|Si va a desactivar, verifica si la<br>película tiene funciones futuras y<br>advierte al administrador.|
|4|Sistema|Solicita confirmación del cambio.|
|5|Administrador de cine /<br>Administrador general|Confirma el cambio de estado.|
|6|Sistema|Actualiza el estado de la película<br>conservando su historial de<br>funciones y ventas.|
|7|Sistema|Actualiza la cartelera y muestra un<br>mensaje de confirmación.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 50 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el administrador intenta eliminar definitivamente una película con<br>funciones futuras o ventas asociadas, el sistema lo impide y solo<br>permite su desactivación.|
|5|Si el administrador no confirma, el sistema mantiene el estado actual<br>de la película.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 51 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-18 Registrar sala** 

|Caso de Uso|Registrar sala<br>**Identificador:**<br>CU-18|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-08. Requerimientos: REQ-037. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-19 Diseñar mapa de asientos de<br>la sala, CU-20 Habilitar o deshabilitar sala.|
|Precondición|El administrador tiene sesión iniciada y permiso para administrar<br>salas.|
|Postcondición|La sala queda registrada con nombre, tipo y capacidad, y puede<br>configurarse su mapa de asientos.|
|Descripción|Permite registrar las salas del cine indicando nombre único por<br>cine, tipo (2D, 3D, VIP, etc.) y capacidad.|
|Resumen|El administrador ingresa los datos de la sala y el sistema los<br>valida y la registra.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Accede al módulo de salas.|
|2|Sistema|Muestra el listado de salas del cine.|
|3|Administrador de cine /<br>Administrador general|Selecciona «Nueva sala».|
|4|Administrador de cine /<br>Administrador general|Ingresa el nombre, el tipo (2D, 3D,<br>VIP, etc.) y la capacidad, y guarda.|
|5|Sistema|Valida los datos y que el nombre sea<br>único dentro del cine.|
|6|Sistema|Registra la sala y ofrece configurar<br>su mapa de asientos (ver CU-19).|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|5|Si ya existe una sala con el mismo nombre en el cine, el sistema|
||rechaza el registro e indica que el nombre debe ser único.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 52 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

- 5 Si algún dato es inválido (por ejemplo, capacidad no numérica), el <u>sistema indica el error y regresa al paso 4.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 53 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-19 Diseñar mapa de asientos de la sala** 

|Caso de Uso|Diseñar mapa de asientos de la sala<br>**Identificador:**<br>CU-19|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-08. Requerimientos: REQ-038, REQ-040.<br>Reglas de negocio: RN-01, RN-10.<br>Casos de uso relacionados: CU-18 Registrar sala.|
|Precondición|La sala existe y no tiene funciones con entradas vendidas.|
|Postcondición|La sala queda con su distribución de filas, columnas, pasillos y<br>asientos especiales (accesibilidad) definida.|
|Descripción|Permite diseñar la distribución de asientos de una sala: filas,<br>columnas, pasillos y asientos de accesibilidad.|
|Resumen|El administrador define en el editor la distribución de la sala y el<br>sistema la guarda.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Selecciona una sala y la opción<br>«Editar distribución».|
|2|Sistema|Verifica que la sala no tenga<br>funciones con entradas vendidas.|
|3|Sistema|Muestra el editor de asientos.|
|4|Administrador de cine /<br>Administrador general|Define el número de filas y<br>columnas.|
|5|Administrador de cine /<br>Administrador general|Marca los pasillos y los asientos de<br>accesibilidad (movilidad reducida) o<br>especiales.|
|6|Sistema|Valida la consistencia de la<br>distribución con la capacidad de la<br>sala.|
|7|Sistema|Guarda el mapa de asientos y<br>muestra un mensaje de<br>confirmación.|



### **Cursos Alternos** 

|**Nro.**<br>**Descripción de acciones alternas**|
|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 54 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

- 2 Si la sala tiene funciones con entradas vendidas, el sistema impide <u>modificar su distribución e informa el motivo.</u> 

- 6 Si la cantidad de asientos no coincide con la capacidad registrada, el <u>sistema indica la diferencia y permite ajustar el diseño o la capacidad.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 55 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-20 Habilitar o deshabilitar sala** 

|Caso de Uso|Habilitar o deshabilitar sala<br>**Identificador:**<br>CU-20|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-08. Requerimientos: REQ-039. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-18 Registrar sala, CU-21<br>Programar función.|
|Precondición|El administrador tiene sesión iniciada y la sala existe.|
|Postcondición|La sala queda habilitada o deshabilitada (por ejemplo, por<br>mantenimiento). Una sala deshabilitada no puede usarse para<br>programar nuevas funciones.|
|Descripción|Permite habilitar o deshabilitar una sala para mantenimiento,<br>advirtiendo si existen funciones futuras programadas en ella.|
|Resumen|El administrador cambia el estado de una sala y el sistema<br>advierte si tiene funciones futuras.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Selecciona una sala en el listado.|
|2|Administrador de cine /<br>Administrador general|Elige la acción «Deshabilitar» (por<br>mantenimiento).|
|3|Sistema|Verifica si la sala tiene funciones<br>futuras programadas.|
|4|Sistema|Si las tiene, advierte al administrador<br>y las lista; solicita confirmación.|
|5|Administrador de cine /<br>Administrador general|Confirma la acción.|
|6|Sistema|Cambia el estado de la sala a<br>deshabilitada y muestra un mensaje<br>de confirmación.|



### **Cursos Alternos** 

|**Nro.**<br>**Descripción de acciones alternas**|
|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 56 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|2|Si el administrador elige «Habilitar», el sistema cambia el estado de la<br>sala a habilitada y continúa en el paso 6.|
|---|---|
|3|Si la sala no tiene funciones futuras, el sistema continúa directamente<br>en el paso 5.|
|5|Si el administrador no confirma, el sistema mantiene el estado actual<br>de la sala.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 57 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-21 Programar función** 

|Caso de Uso|Programar función<br>**Identificador:**<br>CU-21|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-09. Requerimientos: REQ-041, REQ-042,<br>REQ-043, REQ-053. Reglas de negocio: RN-04, RN-05, RN-10.<br>Casos de uso relacionados: CU-22 Programar funciones de<br>forma masiva, CU-23 Modificar función, CU-24 Cancelar función.|
|Precondición|El administrador tiene sesión iniciada; existe al menos una<br>película activa y una sala habilitada con su mapa de asientos<br>definido.|
|Postcondición|La función queda programada y publicada con sus precios,<br>visible para los clientes, y la acción queda registrada en la<br>bitácora.|
|Descripción|Permite programar una función asociando película, sala, fecha,<br>hora, idioma y formato, y definiendo los precios por tipo de<br>entrada.|
|Resumen|El administrador selecciona película, sala, fecha y hora; el<br>sistema valida que no haya cruces; el administrador define<br>precios y publica.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Accede al calendario de funciones y<br>selecciona«Nueva función».|
|2|Administrador de cine /<br>Administrador general|Selecciona la película, la sala, la<br>fecha, la hora de inicio, el idioma y el<br>formato.|
|3|Sistema|Calcula la hora de finalización según<br>la duración de la película.|
|4|Sistema|Valida que la función no se solape<br>con otras de la misma sala,<br>respetando el intervalo mínimo<br>configurable de aseo y preparación.|
|5|Administrador de cine /<br>Administrador general|Define los precios por tipo de<br>entrada (general, niño, adulto mayor,<br>VIP).|
|6|Administrador de cine /<br>Administrador general|Publica la función.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 58 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|7|Sistema|Registra la función, la hace visible en|
|---|---|---|
|||la cartelera y la registra en la<br>bitácora.|
|8|Sistema|Muestra un mensaje de|
|||confirmación.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la película está desactivada o la sala deshabilitada, el sistema no<br>permite seleccionarla.|
|4|Si la función se solapa con otra de la misma sala o no respeta el<br>intervalo mínimo, el sistema rechaza la programación, indica la<br>función en conflicto y regresa al paso 2.|
|5|Si falta algún precio o es inválido, el sistema indica el error y no<br>permite publicar la función.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 59 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-22 Programar funciones de forma masiva** 

|Caso de Uso|Programar funciones de forma<br>masiva<br>**Identificador:**<br>CU-22|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Opcional|
|Referencias|Funcionalidad: RF-09. Requerimientos: REQ-044, REQ-042,<br>REQ-053. Reglas de negocio: RN-04, RN-10.<br>Casos de uso relacionados: CU-21 Programar función.|
|Precondición|El administrador tiene sesión iniciada y está en la programación<br>de funciones; existen película activa y sala habilitada.|
|Postcondición|Se publican las funciones del rango programado que no<br>presentan conflictos, con sus precios, y la acción queda<br>registrada en la bitácora.|
|Descripción|Permite programar de forma masiva o repetitiva varias funciones<br>(por ejemplo, para toda una semana) con una sola configuración.|
|Resumen|El administrador define película, sala, horarios y un rango de<br>fechas; el sistema valida cada función y publica las que no<br>presentan cruces.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|En CU-21, elige la opción de<br>programación repetitiva.|
|2|Administrador de cine /<br>Administrador general|Define la película, la sala, el idioma,<br>el formato, los horarios, el rango de<br>fechas y los días de la semana.|
|3|Administrador de cine /<br>Administrador general|Define los precios por tipo de<br>entrada.|
|4|Sistema|Genera las funciones resultantes y<br>valida cada una contra las existentes<br>en la sala, respetando el intervalo<br>mínimo.|
|5|Sistema|Presenta un resumen de las<br>funciones válidas y de las que<br>presentan conflicto.|
|6|Administrador de cine /<br>Administrador general|Confirma la publicación.|
|7|Sistema|Publica las funciones sin conflicto,<br>las registra en la bitácora e informa<br>el resultado.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 60 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|5|Si alguna función presenta solapamiento, el sistema no la publica; el<br>administrador puede excluirla o modificar los parámetros y volver al<br>paso 2.|
|6|Si el administrador cancela, el sistema descarta la programación sin<br>crear funciones.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 61 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-23 Modificar función** 

|Caso de Uso|Modificar función<br>**Identificador:**<br>CU-23|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-09. Requerimientos: REQ-041, REQ-042,<br>REQ-043, REQ-045, REQ-053. Reglas de negocio: RN-04,<br>RN-05, RN-10.<br>Casos de uso relacionados: CU-21 Programar función, CU-24<br>Cancelar función.|
|Precondición|El administrador tiene sesión iniciada y existe una función<br>programada que no ha iniciado.|
|Postcondición|La función queda actualizada, la acción queda registrada en la<br>bitácora y, si tenía entradas vendidas, los clientes afectados son<br>notificados por correo.|
|Descripción|Permite modificar fecha, hora, sala, idioma, formato o precios de<br>una función programada.|
|Resumen|El administrador edita una función, el sistema valida los cruces y,<br>si había entradas vendidas, notifica por correo a los clientes<br>afectados.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Selecciona una función en el<br>calendario de funciones.|
|2|Sistema|Muestra los datos de la función e<br>indica si tiene entradas vendidas.|
|3|Administrador de cine /<br>Administrador general|Modifica los datos requeridos y<br>guarda.|
|4|Sistema|Valida que la función no se solape<br>con otras de la misma sala,<br>respetando el intervalo mínimo.|
|5|Sistema|Si hay entradas vendidas, advierte<br>que se notificará a los clientes<br>afectados y solicita confirmación.|
|6|Administrador de cine /<br>Administrador general|Confirma la modificación.|
|7|Sistema|Actualiza la función y registra el<br>cambio en la bitácora.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 62 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|8|Servicio de correo|Envía la notificación del cambio a los|
|---|---|---|
|||clientes con entradas vendidas.|
|9|Sistema|Muestra un mensaje de|
|||confirmación.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la función ya inició, el sistema no permite modificarla.|
|4|Si la modificación genera solapamiento en la sala, el sistema la<br>rechaza, indica la función en conflicto y regresa al paso 3.|
|5|Si la función no tiene entradas vendidas, el sistema continúa en el<br>paso 7 sin enviar notificaciones.|
|6|Si el administrador no confirma, el sistema descarta los cambios.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 63 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-24 Cancelar función** 

|Caso de Uso|Cancelar función<br>**Identificador:**<br>CU-24|
|---|---|
|Actores|Administrador de cine, Administrador general, Pasarela de<br>pagos, Servicio de correo|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-09. Requerimientos: REQ-045, REQ-053.<br>Reglas de negocio: RN-08, RN-10.<br>Casos de uso relacionados: CU-23 Modificar función, CU-10<br>Comprar entradas y pagar.|
|Precondición|El administrador tiene sesión iniciada y existe una función<br>programada que no ha iniciado.|
|Postcondición|La función queda cancelada y deja de mostrarse; las entradas<br>vendidas se reembolsan íntegramente, los clientes son<br>notificados y la acción queda registrada en la bitácora.|
|Descripción|Permite cancelar una función; si tiene ventas, el sistema notifica<br>a los clientes y gestiona el reembolso íntegro de sus entradas.|
|Resumen|El administrador cancela una función, el sistema reembolsa las<br>entradas vendidas mediante la pasarela y notifica a los clientes<br>por correo.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Selecciona una función en el<br>calendario y elige «Cancelar<br>función».|
|2|Sistema|Muestra el número de entradas<br>vendidas y el valor total a<br>reembolsar, y solicita confirmación.|
|3|Administrador de cine /<br>Administrador general|Confirma la cancelación.|
|4|Sistema|Marca la función como cancelada, la<br>retira de la cartelera y libera sus<br>asientos.|
|5|Sistema|Solicita a la pasarela el reembolso<br>íntegro de cada compra asociada.|
|6|Pasarela de pagos|Procesa los reembolsos y confirma<br>cada operación.|
|7|Sistema|Marca las entradas como canceladas<br>e invalida sus códigos QR.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 64 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|8|Servicio de correo|Envía la notificación de cancelación<br>a los clientes afectados.|
|---|---|---|
|9|Sistema|Registra la cancelación y los<br>reembolsos en la bitácora y muestra<br>un mensaje de confirmación.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si la función no tiene entradas vendidas, el sistema omite la gestión<br>de reembolsos y continúa en el paso 3.|
|3|Si el administrador no confirma, el sistema mantiene la función sin<br>cambios.|
|6|Si la pasarela rechaza o no procesa un reembolso, el sistema deja<br>esa compra marcada como pendiente de reembolso, registra el hecho<br>en la bitácora y alerta al administrador para su reintento.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 65 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-25 Consultar ventas** 

|Caso de Uso|Consultar ventas<br>**Identificador:**<br>CU-25|
|---|---|
|Actores|Administrador de cine, Administrador general, Taquillero|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-10. Requerimientos: REQ-046. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-26 Generar reportes de gestión.|
|Precondición|El usuario tiene sesión iniciada con rol administrador o taquillero.|
|Postcondición|No se modifica el estado del sistema.|
|Descripción|Permite al personal consultar las ventas registradas y filtrarlas<br>por rango de fechas, película, sala o método de pago.|
|Resumen|El usuario accede al módulo de ventas, aplica filtros y consulta el<br>detalle de cada venta.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general /<br>Taquillero|Accede al módulo de ventas.|
|2|Sistema|Muestra el listado de ventas<br>recientes.|
|3|Administrador de cine /<br>Administrador general /<br>Taquillero|Aplica filtros por rango de fechas,<br>película, sala o método de pago.|
|4|Sistema|Muestra las ventas que cumplen los<br>filtros con fecha, usuario, función,<br>asientos, valor y estado del pago.|
|5|Administrador de cine /<br>Administrador general /<br>Taquillero|Selecciona una venta para ver su<br>detalle.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si no hay ventas para los filtros aplicados, el sistema muestra un<br>mensaje informativo.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 66 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

- 3 Si el rango de fechas es inválido (fecha inicial posterior a la final), el <u>sistema lo informa y solicita corregirlo.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 67 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-26 Generar reportes de gestión** 

|Caso de Uso|Generar reportes de gestión<br>**Identificador:**<br>CU-26|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-10. Requerimientos: REQ-047. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-25 Consultar ventas, CU-27<br>Exportar reporte a PDF o Excel, CU-28 Visualizar dashboard del<br>día.|
|Precondición|El administrador tiene sesión iniciada y existen ventas<br>registradas.|
|Postcondición|El reporte solicitado queda generado y visible en pantalla.|
|Descripción|Permite generar reportes de ventas, ocupación por función o<br>sala e ingresos por película y por periodo.|
|Resumen|El administrador elige el tipo de reporte y el periodo y el sistema<br>lo genera y presenta.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Accede al módulo de reportes.|
|2|Administrador de cine /<br>Administrador general|Selecciona el tipo de reporte (ventas,<br>ocupación por función o sala,<br>ingresos por película o por periodo) y<br>el periodo.|
|3|Sistema|Consolida la información de ventas y<br>funciones del periodo.|
|4|Sistema|Presenta el reporte en pantalla.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|3|Si no hay información para el periodo seleccionado, el sistema<br>muestra un mensaje informativo.|
|2|Si el periodo es inválido, el sistema lo informa y solicita corregirlo.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 68 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-27 Exportar reporte a PDF o Excel** 

|Caso de Uso|Exportar reporte a PDF o Excel<br>**Identificador:**<br>CU-27|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Opcional|
|Referencias|Funcionalidad: RF-10. Requerimientos: REQ-048. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-26 Generar reportes de gestión.|
|Precondición|El administrador tiene un reporte generado en pantalla.|
|Postcondición|Se entrega al administrador un archivo PDF o Excel con el<br>contenido del reporte.|
|Descripción|Permite exportar los reportes de gestión a formato PDF o Excel.|
|Resumen|Desde un reporte generado, el administrador elige el formato y el<br>sistema genera y entrega el archivo.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Solicita exportar el reporte y elige el<br>formato (PDF o Excel).|
|2|Sistema|Genera el archivo con el contenido y<br>los filtros del reporte.|
|3|Sistema|Entrega el archivo para su descarga.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si falla la generación del archivo, el sistema informa el error y permite<br>reintentar la exportación.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 69 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-28 Visualizar dashboard del día** 

|Caso de Uso|Visualizar dashboard del día<br>**Identificador:**<br>CU-28|
|---|---|
|Actores|Administrador de cine, Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-10. Requerimientos: REQ-050. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-26 Generar reportes de gestión.|
|Precondición|El administrador tiene sesión iniciada.|
|Postcondición|No se modifica el estado del sistema.|
|Descripción|Permite visualizar un panel con los indicadores clave del día:<br>ventas, ocupación y funciones activas.|
|Resumen|Al ingresar al panel administrativo, el sistema muestra los<br>indicadores clave de la operación del día.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador de cine /<br>Administrador general|Accede al dashboard del panel<br>administrativo.|
|2|Sistema|Calcula los indicadores del día:<br>ventas, ocupación y funciones<br>activas.|
|3|Sistema|Presenta los indicadores en el<br>dashboard.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si aún no hay ventas o funciones en el día, el sistema muestra los<br>indicadores en cero con un mensaje informativo.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 70 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-29 Validar entrada en el acceso** 

|Caso de Uso|Validar entrada en el acceso<br>**Identificador:**<br>CU-29|
|---|---|
|Actores|Taquillero|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-10. Requerimientos: REQ-049. Reglas de<br>negocio: RN-09, RN-10.<br>Casos de uso relacionados: CU-13 Visualizar y descargar<br>entrada con código QR.|
|Precondición|El taquillero tiene sesión iniciada y dispone de un dispositivo con<br>cámara o lector de códigos QR. El cliente presenta una entrada<br>con código QR.|
|Postcondición|La entrada válida queda marcada como «usada» con la fecha y<br>hora de validación y no puede reutilizarse.|
|Descripción|Permite validar en el ingreso a la sala el código QR de una<br>entrada y marcarla como utilizada, impidiendo su reutilización.|
|Resumen|El taquillero escanea el QR, el sistema verifica la entrada y, si es<br>válida, la marca como utilizada.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Taquillero|Accede al módulo de validación de<br>entradas.|
|2|Taquillero|Escanea el código QR de la entrada<br>con la cámara o el lector.|
|3|Sistema|Decodifica el código y consulta la<br>entrada.|
|4|Sistema|Verifica que la entrada exista, esté<br>vigente, corresponda a la función y<br>no haya sido usada.|
|5|Sistema|Marca la entrada como utilizada con<br>la fecha y hora de la validación.|
|6|Sistema|Muestra el resultado de validación<br>exitosa con el asiento y los datos de<br>la función.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 71 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|2|Si el código no puede leerse, el sistema lo informa y permite repetir el<br>escaneo.|
|4|Si la entrada ya fue utilizada, el sistema la rechaza e indica la fecha y<br>hora de su uso anterior.|
|4|Si el código no existe, la compra fue cancelada o corresponde a otra<br>función, el sistema rechaza la entrada e informa el motivo.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 72 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-30 Crear cuenta de personal y asignar rol** 

|Caso de Uso|Crear cuenta de personal y asignar<br>rol<br>**Identificador:**<br>CU-30|
|---|---|
|Actores|Administrador general|
|Tipo|Primario|
|Referencias|Funcionalidad: RF-11. Requerimientos: REQ-051, REQ-052.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-31 Editar cuenta de personal y<br>rol, CU-32 Desactivar cuenta de personal, CU-04 Aplicar control<br>de acceso por rol.|
|Precondición|El administrador general tiene sesión iniciada.|
|Postcondición|Se crea la cuenta del empleado con el rol asignado y queda<br>activa.|
|Descripción|Permite crear la cuenta de un empleado del cine y asignarle un<br>rol (administrador de cine o taquillero).|
|Resumen|El administrador general registra los datos del empleado y le<br>asigna un rol, y el sistema crea la cuenta.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador general|Accede al módulo de gestión de<br>usuarios.|
|2|Sistema|Muestra el listado de cuentas de<br>personal.|
|3|Administrador general|Selecciona«Nueva cuenta».|
|4|Administrador general|Ingresa el nombre y el correo<br>electrónico del empleado y le asigna<br>un rol.|
|5|Sistema|Valida los datos y que el correo<br>electrónico sea único.|
|6|Sistema|Crea la cuenta activa con el rol<br>asignado y habilita su acceso inicial.|
|7|Sistema|Registra la creación y muestra un<br>mensaje de confirmación.|



### **Cursos Alternos** 

|**Nro.**<br>**Descripción de acciones alternas**|
|---|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 73 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

|5|Si el correo ya está registrado o algún dato es inválido, el sistema lo<br>informa y regresa al paso 4.|
|---|---|
|4|Si el administrador cancela, el sistema no crea la cuenta.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 74 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-31 Editar cuenta de personal y rol** 

|Caso de Uso|Editar cuenta de personal y rol<br>**Identificador:**<br>CU-31|
|---|---|
|Actores|Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-11. Requerimientos: REQ-051, REQ-052.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-30 Crear cuenta de personal y<br>asignar rol, CU-32 Desactivar cuenta de personal, CU-04 Aplicar<br>control de acceso por rol.|
|Precondición|El administrador general tiene sesión iniciada y la cuenta de<br>personal existe.|
|Postcondición|Los datos o el rol de la cuenta quedan actualizados y los nuevos<br>permisos aplican desde la siguiente sesión o solicitud.|
|Descripción|Permite modificar los datos de una cuenta de personal y cambiar<br>su rol.|
|Resumen|El administrador general modifica los datos o el rol de un<br>empleado y el sistema aplica los nuevos permisos.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador general|Selecciona una cuenta de personal<br>en el listado.|
|2|Sistema|Muestra los datos y el rol actual de la<br>cuenta.|
|3|Administrador general|Modifica los datos o el rol y guarda.|
|4|Sistema|Valida los datos y que el correo, si<br>cambió, siga siendo único.|
|5|Sistema|Actualiza la cuenta y los permisos<br>asociados al rol, y muestra un<br>mensaje de confirmación.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si el correo ya está en uso o algún dato es inválido, el sistema lo<br>informa y regresa al paso 3.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 75 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 

- 3 Si el administrador cancela la edición, el sistema descarta los <u>cambios.</u> 

La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 76 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-32 Desactivar cuenta de personal** 

|Caso de Uso|Desactivar cuenta de personal<br>**Identificador:**<br>CU-32|
|---|---|
|Actores|Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-11. Requerimientos: REQ-051, REQ-053.<br>Reglas de negocio: RN-10.<br>Casos de uso relacionados: CU-30 Crear cuenta de personal y<br>asignar rol, CU-31 Editar cuenta de personal y rol, CU-02 Iniciar<br>sesión.|
|Precondición|El administrador general tiene sesión iniciada y la cuenta de<br>personal está activa.|
|Postcondición|La cuenta queda desactivada y no puede iniciar sesión, pero se<br>conserva la trazabilidad de las acciones realizadas por el<br>usuario.|
|Descripción|Permite desactivar la cuenta de un empleado sin perder la<br>trazabilidad de sus acciones.|
|Resumen|El administrador general desactiva una cuenta de personal y el<br>sistema bloquea su acceso conservando su historial.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador general|Selecciona una cuenta de personal<br>activa.|
|2|Administrador general|Elige la acción«Desactivar cuenta».|
|3|Sistema|Solicita confirmación e informa que<br>se conservará el historial de<br>acciones del usuario.|
|4|Administrador general|Confirma la desactivación.|
|5|Sistema|Cambia el estado de la cuenta a<br>desactivada, cierra sus sesiones<br>activas y conserva sus registros en<br>la bitácora.|
|6|Sistema|Muestra un mensaje de<br>confirmación.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 77 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si el administrador no confirma, la cuenta permanece activa.|
|1|Si la cuenta ya está desactivada, el sistema ofrece reactivarla en lugar<br>de desactivarla.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 78 

**La Oficina de Proyectos de Informática** 

**_www.pmoinformatica.com_** 



#### **CU-33 Consultar bitácora de acciones críticas** 

|Caso de Uso|Consultar bitácora de acciones<br>críticas<br>**Identificador:**<br>CU-33|
|---|---|
|Actores|Administrador general|
|Tipo|Secundario|
|Referencias|Funcionalidad: RF-11. Requerimientos: REQ-053. Reglas de<br>negocio: RN-10.<br>Casos de uso relacionados: CU-21 Programar función, CU-23<br>Modificar función, CU-24 Cancelar función, CU-14 Cancelar<br>compra.|
|Precondición|El administrador general tiene sesión iniciada.|
|Postcondición|No se modifica el estado del sistema; los registros de la bitácora<br>no pueden alterarse.|
|Descripción|Permite consultar la bitácora donde el sistema registra las<br>acciones críticas (creación y edición de funciones, precios,<br>cancelaciones y reembolsos) con usuario, fecha y hora.|
|Resumen|El administrador general consulta y filtra los registros de las<br>acciones críticas realizadas en el sistema.|



### **<mark>Curso Normal</mark>** 

|**Nro.**|**Ejecutor**|**Paso o Actividad**|
|---|---|---|
|1|Administrador general|Accede al módulo de bitácora.|
|2|Sistema|Muestra los registros más recientes<br>con usuario, fecha y hora, tipo de<br>acción y detalle.|
|3|Administrador general|Aplica filtros por usuario, rango de<br>fechas o tipo de acción.|
|4|Sistema|Muestra los registros que cumplen<br>los filtros.|



### **Cursos Alternos** 

|**Nro.**|**Descripción de acciones alternas**|
|---|---|
|4|Si no hay registros para los filtros aplicados, el sistema muestra un<br>mensaje informativo.|



La Oficina de Proyectos de Informática ( **<u>htp://www.pmoinformatt</u> ica.com** ) 

Página 79 

