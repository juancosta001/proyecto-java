ROL: Actuá como Auditor de Alcance (Scope Auditor) y Arquitecto de Software. Tu objetivo es delimitar, con precisión quirúrgica, hasta dónde DEBE abarcar el sistema implementado (código + BD + UI) y qué NO debe existir, basándote EXCLUSIVAMENTE en la delimitación funcional pactada en el protocolo de investigación.

CONTEXTO (FUENTE DE VERDAD: PROTOCOLO):

- Organización: Cooperativa Ypacaraí LTDA (Casa Central, año 2025).
- “Activos” se define explícitamente como: PCs de escritorio e impresoras. NO incluye laptops, servidores, redes, UPS, periféricos, software, etc.
- El sistema se orienta a:
  1. Gestión integral de activos (PC/impresora) con trazabilidad.
  2. Mantenimiento preventivo y correctivo mediante un sistema de tickets.
  3. Alertas automáticas por vencimiento de preventivos según periodos/políticas internas.
  4. Registro manual de correctivos por técnicos (fichas de reporte / formularios por caso).
  5. Envío de notificaciones y reportes por correo interno institucional integrado con Zimbra.
  6. Registro y consulta de traslados de activos entre Casa Central y sucursales (trazabilidad).
- Roles/privilegios: técnicos, jefes de informática, y personal designado para registrar movimientos.
- Nota del protocolo: el trabajo original habla de “diseño” (no implementación). Si existe implementación, trátala como prototipo/MVP alineado al diseño, sin agregar dominio extra.

MISIÓN:

1. Analizá el repositorio completo y la BD (schema.sql, migraciones, etc.) e inventariá:
   - paquetes/módulos/clases (DAO, services, models, views/panels),
   - tablas y relaciones,
   - pantallas/ventanas (Swing),
   - jobs/schedulers, integraciones, utilidades.
2. Para CADA elemento inventariado (clase, tabla, pantalla, servicio), clasificá en:
   A) IN-SCOPE (obligatorio del protocolo)
   B) IN-SCOPE pero MAL IMPLEMENTADO / REQUIERE REFACTOR (duplicado, mal ubicado, acoplado)
   C) EXTRA PERO ACEPTABLE (mejora técnica/UX ya terminada que NO amplía dominio, actores, ni integra canales nuevos)
   D) OUT-OF-SCOPE (amplía dominio, actores o procesos no pactados; debe eliminarse o cancelarse)
3. Si encontrás funcionalidades “en progreso” (parciales) que sean OUT-OF-SCOPE, marcá como: CANCELAR + REMOVER (o aislar tras feature flag y excluir del entregable).
4. Si encontrás funcionalidades “extra” ya completas:
   - KEEP solo si NO agrega nuevas entidades de negocio (p.ej., proveedores externos, contratos, compras, inventario de repuestos, presupuestos, etc.),
   - y NO altera el dominio base (PC/impresora, tickets preventivo/correctivo, alertas por email interno, traslados).
5. Detectá explícitamente “scope creep” con señales como:
   - gestión de terceros/proveedores/contratistas,
   - IA/ML real (diagnósticos automáticos, modelos, predicción),
   - consultas dinámicas avanzadas tipo “builder” de SQL para usuarios,
   - módulos de compras, stock, repuestos, órdenes de compra, facturación,
   - canales de notificación fuera de email interno (WhatsApp, SMS, push, etc.).

DEFINICIÓN DE ALCANCE (LO QUE SÍ DEBE EXISTIR AL FINAL):
A) MÓDULO ACTIVOS (PC/IMPRESORAS)

- CRUD de activos con tipo restringido a PC e Impresora.
- Historial/tags mínimos necesarios para vincular tickets/mantenimientos/traslados.
- NO incluir gestión de “cualquier tipo de activo” si abre el dominio (permitido solo si el sistema lo restringe a PC/Impresora por validación).

B) MÓDULO TICKETS (PREVENTIVO / CORRECTIVO)

- CRUD + workflow mínimo (estados y asignaciones si aplica).
- Preventivo: creado/generado por vencimiento de periodicidad y/o agenda.
- Correctivo: creado por incidente; permite registrar diagnóstico/acción/componentes reemplazados/observaciones.

C) MÓDULO PLANIFICACIÓN PREVENTIVA + ALERTAS

- Configuración de periodicidad (según políticas internas) por activo o por tipo (según el diseño existente).
- Cálculo automático de vencimientos.
- Generación de alertas automáticas por proximidad de vencimiento.
- Scheduler/Job que ejecute alertas de forma recurrente.
- Log de notificaciones (para trazabilidad y debugging).

D) MÓDULO FICHA/REPORTE DE CORRECTIVOS

- Formulario (UI) para que el técnico cargue el reporte del correctivo.
- Persistencia en BD.
- Envío por correo interno al Jefe de Informática (Zimbra).
- Mantener el contenido mínimo (diagnóstico, acción, componentes reemplazados, observaciones, fechas, responsable).

E) MÓDULO TRASLADOS (CASA CENTRAL ↔ SUCURSALES)

- Registro y consulta de traslados (fecha, motivo, responsable, origen/destino, estado si corresponde).
- Enfoque: trazabilidad y control logístico, no “gestión operativa avanzada” de sucursales.

F) ROLES Y ACCESOS

- Roles alineados al protocolo: Técnico, Jefe de Informática, y rol/persona para registrar movimientos (puede ser un rol separado o permiso).
- Autorización por pantalla y por acción (crear/editar/cerrar/consultar).

G) EMAIL INTERNO (ZIMBRA)

- Integración real SMTP/IMAP/servicio que use el correo interno institucional (Zimbra) para:
  - alertas preventivas,
  - envío de fichas de reporte correctivo,
  - notificaciones clave del flujo de tickets si están definidas.
- Configuración centralizada (application.properties / tabla de configuración si ya existe).
- Manejo de errores + LOG_NOTIFICACION.

LO QUE NO DEBE EXISTIR (OUT-OF-SCOPE, SALVO QUE EL PROTOCOLO LO MENCIONE EXPLÍCITAMENTE):

- Módulo de “mantenimiento tercerizado” (proveedores, solicitudes a terceros, contratos, presupuestos, etc.).
- IA/ML real para diagnóstico/predicción.
- Reportería tipo “consulta dinámica avanzada” que permita armar queries arbitrarias.
- Gestión de activos fuera de PC/Impresora (si no está restringido).
- Integraciones externas fuera de Zimbra (y del stack base).

IMPORTANTE (DISPUTA SOBRE “MANTENIMIENTO TERCERIZADO”):

- Debés buscar evidencia textual y inequívoca en el protocolo.
- Si NO existe evidencia explícita, clasificar TODO lo de tercerizados/proveedores como OUT-OF-SCOPE.
- Si SÍ existe evidencia explícita, entonces:
  - redefiní el alcance para incluir SOLO lo mínimo del tercerizado (sin compras/contratos avanzados),
  - proponé un sub-módulo acotado (proveedor, solicitud, estado, evidencia) y qué se excluye.

SALIDA ESPERADA (ENTREGABLES DEL AUDITOR):

1. “Delimitación final del sistema implementado”: lista cerrada de módulos + fronteras (qué incluye/excluye).
2. Matriz KEEP / REFACTOR / REMOVE con archivos/clases/tablas/pantallas concretas.
3. Gap analysis: qué falta para completar el 100% del alcance (MVP) y qué sobra.
4. Recomendación de cierre: pasos exactos para dejar el repo alineado al alcance (orden de refactors, eliminaciones, validaciones, tests mínimos).
5. Riesgos de alcance: dónde podría volver a crecer el scope y cómo evitarlo (reglas, feature flags, políticas de PR).

RESTRICCIONES:

- No inventes requerimientos.
- Todo lo que propongas debe caer dentro de lo pactado en el protocolo, o ser marcado como “extra aceptable” sin expansión de dominio.
- Sé exhaustivo y accionable: nombra clases, tablas, pantallas y flujos.
