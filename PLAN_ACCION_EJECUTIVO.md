# PLAN DE ACCIÓN EJECUTIVO

**Sistema Activos Ypacaraí - Cierre de Gaps con Protocolo**

---

## 🎯 OBJETIVO

Llevar el sistema del **70% al 100%** de cumplimiento del protocolo de delimitación en **10 días hábiles** (2 semanas).

---

## 📊 SITUACIÓN ACTUAL

### ✅ LO QUE FUNCIONA (70%)

- Gestión de activos PC/Impresora
- Tickets preventivos/correctivos
- Alertas automáticas
- Planificación mantenimiento
- Roles y accesos
- Reportería (4 reportes + dashboard)
- Email básico

### ❌ LO QUE FALTA (20%)

1. **Módulo Traslados incompleto** → falta DAO, Service, Panel
2. **FichaReporteDAO** → no confirmado
3. **Validación restricción activos** → no se valida en código
4. **Envío automático fichas por email** → no confirmado

### 🚨 SCOPE CREEP (10%)

- **Módulo Tercerizado completo** → eliminar (13 archivos)

---

## 📅 CRONOGRAMA DETALLADO

### SEMANA 1

#### **DÍA 1-2: Eliminación Scope Creep**

```bash
# BACKUPS
git checkout -b feature/align-to-protocol
git branch backup-$(date +%Y%m%d)

# ELIMINAR ARCHIVOS JAVA
rm src/main/java/com/ypacarai/cooperativa/activos/model/MantenimientoTercerizado.java
rm src/main/java/com/ypacarai/cooperativa/activos/model/ProveedorServicio.java
rm src/main/java/com/ypacarai/cooperativa/activos/dao/MantenimientoTercerizadoDAO.java
rm src/main/java/com/ypacarai/cooperativa/activos/dao/ProveedorServicioDAO.java
rm src/main/java/com/ypacarai/cooperativa/activos/service/MantenimientoTercerizadoService.java
rm src/main/java/com/ypacarai/cooperativa/activos/view/MantenimientoTercerizadoPanel.java
rm src/main/java/com/ypacarai/cooperativa/activos/view/SolicitudMantenimientoTercerizadoWindow.java
rm src/main/java/com/ypacarai/cooperativa/activos/view/ProveedorServicioWindow.java

# ELIMINAR SQL/SCRIPTS
rm src/main/resources/database/mantenimiento_tercerizado_schema.sql
rm setup_mantenimiento_tercerizado.bat
rm MANTENIMIENTO_TERCERIZADO_MANUAL.md

# LIMPIAR BD (conectar a MySQL primero)
mysql -u root -p sistema_activos <<EOF
DROP TABLE IF EXISTS mantenimiento_tercerizado;
DROP TABLE IF EXISTS proveedor_servicio;
SHOW TABLES;  # Verificar
EOF

# EDITAR MainWindowNew.java
# - Eliminar imports de clases tercerizado
# - Eliminar panel tercerizado del tabbedPane
# - Eliminar listeners/botones relacionados

# COMPILAR
mvn clean compile

# COMMIT
git add -A
git commit -m "eliminar módulo tercerizado (out-of-scope del protocolo)"
```

**Entregables:**

- ✅ 13 archivos eliminados
- ✅ 2 tablas BD eliminadas
- ✅ Compilación exitosa sin errores

---

#### **DÍA 3-5: Implementar Módulo Traslados Completo**

**Día 3 - DAO:**

```
1. Copiar código de MATRIZ_ARCHIVOS_ACCION.md sección B1
2. Crear: src/main/java/com/ypacarai/cooperativa/activos/dao/TrasladoDAO.java
3. Compilar: mvn compile
4. Test manual simple desde main() temporal
```

**Día 4 - Service:**

```
1. Copiar código de MATRIZ_ARCHIVOS_ACCION.md sección B2
2. Crear: src/main/java/com/ypacarai/cooperativa/activos/service/TrasladoService.java
3. Ajustar imports si es necesario
4. Compilar
```

**Día 5 - Panel UI:**

```
1. Copiar código de MATRIZ_ARCHIVOS_ACCION.md sección B3
2. Crear: src/main/java/com/ypacarai/cooperativa/activos/view/TrasladosPanel.java
3. Crear ventana auxiliar: NuevoTrasladoWindow.java (similar a CrearTicketWindow)
4. Integrar en MainWindowNew:
   - Import TrasladosPanel
   - Agregar tab: tabbedPane.add("Traslados", new TrasladosPanel())
5. Ejecutar aplicación y probar flujo completo
```

**Commit:**

```bash
git add src/main/java/com/ypacarai/cooperativa/activos/dao/TrasladoDAO.java
git add src/main/java/com/ypacarai/cooperativa/activos/service/TrasladoService.java
git add src/main/java/com/ypacarai/cooperativa/activos/view/TrasladosPanel.java
git add src/main/java/com/ypacarai/cooperativa/activos/view/NuevoTrasladoWindow.java
git add src/main/java/com/ypacarai/cooperativa/activos/view/MainWindowNew.java
git commit -m "implementar módulo traslados completo (CRUD + UI)"
```

**Entregables:**

- ✅ TrasladoDAO con 5 métodos CRUD
- ✅ TrasladoService con lógica de negocio
- ✅ TrasladosPanel funcional en UI
- ✅ Tab "Traslados" visible y operativo

---

### SEMANA 2

#### **DÍA 6-7: Refactorizar Validaciones**

**Día 6 - Restricción Activos:**

**A. ActivoService.java:**

```java
// AL INICIO DE LA CLASE
private static final Set<String> TIPOS_PERMITIDOS = Set.of("PC", "Impresora");

// EN método crearActivo() o equivalente
public void crearActivo(Activo activo) throws Exception {
    if (!TIPOS_PERMITIDOS.contains(activo.getTipoActivo().getNombre())) {
        throw new IllegalArgumentException(
            "Tipo de activo no permitido. Solo se permiten: " + TIPOS_PERMITIDOS
        );
    }
    // ...resto del código...
}
```

**B. RegistroActivoPanel.java:**

```java
// EN initComponents() donde se carga el combo de tipos
private void cargarTiposActivo() {
    comboTipoActivo.removeAllItems();
    // HARDCODEAR en lugar de cargar desde BD
    comboTipoActivo.addItem(new TipoActivo(1, "PC"));
    comboTipoActivo.addItem(new TipoActivo(2, "Impresora"));
}
```

**Día 7 - Máquina Estados Tickets:**

**TicketService.java:**

```java
// AGREGAR constante de transiciones válidas
private static final Map<String, Set<String>> TRANSICIONES_VALIDAS = Map.of(
    "Pendiente", Set.of("Asignado", "Cancelado"),
    "Asignado", Set.of("En_Proceso", "Pendiente", "Cancelado"),
    "En_Proceso", Set.of("Completado", "Pausado", "Cancelado"),
    "Pausado", Set.of("En_Proceso", "Cancelado"),
    "Completado", Set.of(),
    "Cancelado", Set.of()
);

// AGREGAR método de validación
public void cambiarEstado(Long ticketId, String nuevoEstado) throws Exception {
    Ticket ticket = ticketDAO.buscarPorId(ticketId);
    String estadoActual = ticket.getEstado();

    if (!TRANSICIONES_VALIDAS.get(estadoActual).contains(nuevoEstado)) {
        throw new IllegalStateException(
            "Transición inválida: " + estadoActual + " -> " + nuevoEstado
        );
    }

    ticket.setEstado(nuevoEstado);
    ticketDAO.actualizar(ticket);
}
```

**Commit:**

```bash
git add src/main/java/com/ypacarai/cooperativa/activos/service/ActivoService.java
git add src/main/java/com/ypacarai/cooperativa/activos/view/RegistroActivoPanel.java
git add src/main/java/com/ypacarai/cooperativa/activos/service/TicketService.java
git commit -m "refactor: agregar validaciones de negocio (restricción activos + workflow tickets)"
```

**Entregables:**

- ✅ Solo PC/Impresora permitidos
- ✅ Workflow tickets validado

---

#### **DÍA 8: FichaReporte + Email**

**A. Crear FichaReporteDAO:**

```
1. Copiar código de MATRIZ_ARCHIVOS_ACCION.md sección B4
2. Crear: src/main/java/com/ypacarai/cooperativa/activos/dao/FichaReporteDAO.java
3. Compilar
```

**B. Integrar envío email:**

**DetallesMantenimientoWindow.java:**

```java
// AL INICIO
private EmailService emailService;
private FichaReporteDAO fichaReporteDAO;

public DetallesMantenimientoWindow(...) {
    // ...código existente...
    this.emailService = new EmailService();
    this.fichaReporteDAO = new FichaReporteDAO();
}

// EN método guardar() o equivalente
private void guardarFicha() {
    try {
        // 1. Crear objeto FichaReporte con datos del formulario
        FichaReporte ficha = new FichaReporte();
        ficha.setMantenimientoId(mantenimiento.getId());
        ficha.setDiagnostico(txtDiagnostico.getText());
        ficha.setSolucionAplicada(txtSolucion.getText());
        ficha.setComponentesReemplazados(txtComponentes.getText());
        ficha.setObservaciones(txtObservaciones.getText());
        ficha.setFirmaTecnico(txtFirmaTecnico.getText());
        ficha.setFirmaJefe(txtFirmaJefe.getText());

        // 2. Guardar en BD
        fichaReporteDAO.crear(ficha);

        // 3. Enviar por email al Jefe
        emailService.enviarFichaReporteAJefe(ficha);

        JOptionPane.showMessageDialog(this,
            "Ficha guardada y enviada por email al Jefe de Informática");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
```

**EmailService.java:** (si no existe el método)

```java
public void enviarFichaReporteAJefe(FichaReporte ficha) throws Exception {
    // Obtener email del jefe desde configuración o BD
    String emailJefe = configuracionService.getEmailJefeInformatica();

    String asunto = "Ficha de Reporte Correctivo N° " + ficha.getNumeroFicha();

    String cuerpo = String.format("""
        Se ha completado el siguiente mantenimiento correctivo:

        Ficha N°: %s
        Activo: %s
        Diagnóstico: %s
        Solución aplicada: %s
        Componentes reemplazados: %s
        Observaciones: %s

        Técnico: %s
        Fecha: %s
        """,
        ficha.getNumeroFicha(),
        ficha.getActivoDescripcion(),
        ficha.getDiagnostico(),
        ficha.getSolucionAplicada(),
        ficha.getComponentesReemplazados(),
        ficha.getObservaciones(),
        ficha.getFirmaTecnico(),
        ficha.getFechaCreacion()
    );

    enviarEmail(emailJefe, asunto, cuerpo);
}
```

**Commit:**

```bash
git add src/main/java/com/ypacarai/cooperativa/activos/dao/FichaReporteDAO.java
git add src/main/java/com/ypacarai/cooperativa/activos/view/DetallesMantenimientoWindow.java
git add src/main/java/com/ypacarai/cooperativa/activos/service/EmailService.java
git commit -m "implementar CRUD fichas reporte + envío automático por email"
```

**Entregables:**

- ✅ FichaReporteDAO operativo
- ✅ Envío automático al Jefe

---

#### **DÍA 9: Feature Flags + Config**

**application.properties:**

```properties
# ===== CONFIGURACIÓN GENERAL =====
app.name=Sistema de Activos - Cooperativa Ypacaraí
app.version=1.0-MVP

# ===== CONFIGURACIÓN EMAIL ZIMBRA =====
mail.smtp.host=mail.ypacarai.coop.py
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.from=informatica@ypacarai.coop.py
mail.jefe.informatica=jefe.informatica@ypacarai.coop.py

# ===== RESTRICCIÓN DE ALCANCE =====
activo.tipos.permitidos=PC,Impresora

# ===== FEATURE FLAGS (Prevención Scope Creep) =====
feature.mantenimiento_tercerizado.enabled=false
feature.consultas_dinamicas.enabled=false
feature.reportes_avanzados.enabled=false

# ===== SCHEDULER ALERTAS =====
scheduler.alertas.enabled=true
scheduler.alertas.cron=0 0 8 * * ?  # Diario a las 8am
scheduler.tickets_preventivos.cron=0 0 0 * * ?  # Diario a medianoche
```

**ConsultaDinamica.java:** (si existe)

```java
// OPCIÓN: Agregar validación de feature flag
public class ConsultaDinamica {

    public ConsultaDinamica() {
        if (!ConfiguracionService.isFeatureEnabled("consultas_dinamicas")) {
            throw new UnsupportedOperationException(
                "Las consultas dinámicas están deshabilitadas"
            );
        }
    }
}

// O MEJOR: Eliminar archivo si no se usa
```

**Commit:**

```bash
git add src/main/resources/application.properties
git commit -m "configurar feature flags y restricciones de alcance"
```

**Entregables:**

- ✅ Config Zimbra real
- ✅ Feature flags activos

---

#### **DÍA 10: Testing + Validación**

**A. Script de validación:**

```bash
# Copiar validate_scope.sh de MATRIZ_ARCHIVOS_ACCION.md sección B5
# Crear en raíz del proyecto
nano validate_scope.sh
chmod +x validate_scope.sh

# EJECUTAR
./validate_scope.sh
```

**B. Testing manual E2E:**

```
1. Crear activo PC → ✅ OK
2. Intentar crear activo "Celular" → ⛔ Debe fallar
3. Crear ticket preventivo → ✅ OK
4. Cambiar estado Pendiente→Completado directamente → ⛔ Debe fallar
5. Cambiar Pendiente→Asignado→En_Proceso→Completado → ✅ OK
6. Registrar mantenimiento correctivo → ✅ OK
7. Crear ficha reporte → ✅ OK
8. Verificar email enviado al Jefe → ✅ OK
9. Registrar traslado Casa Central→Sucursal → ✅ OK
10. Confirmar traslado → ✅ Ubicación actualizada
11. Generar reporte Excel → ✅ 4 sheets
12. Verificar alertas automáticas → ✅ Scheduler funciona
```

**C. Verificar NO existen:**

```
❌ Referencias a "tercerizado"
❌ Referencias a "proveedor"
❌ Tablas mantenimiento_tercerizado
❌ Panel tercerizado en UI
```

**Commit final:**

```bash
git add validate_scope.sh
git commit -m "agregar script de validación de alcance"

# TAG RELEASE
git tag -a v1.0-mvp-protocolo -m "MVP 100% alineado al protocolo de delimitación"
git push origin feature/align-to-protocol
git push origin v1.0-mvp-protocolo
```

**Entregables:**

- ✅ Script validación
- ✅ Testing E2E completo
- ✅ Tag v1.0-mvp-protocolo

---

## 📋 CHECKLIST FINAL

### Eliminaciones

- [x] MantenimientoTercerizadoDAO.java
- [x] ProveedorServicioDAO.java
- [x] MantenimientoTercerizadoService.java
- [x] MantenimientoTercerizadoPanel.java
- [x] SolicitudMantenimientoTercerizadoWindow.java
- [x] ProveedorServicioWindow.java
- [x] MantenimientoTercerizado.java
- [x] ProveedorServicio.java
- [x] mantenimiento_tercerizado_schema.sql
- [x] setup_mantenimiento_tercerizado.bat
- [x] MANTENIMIENTO_TERCERIZADO_MANUAL.md
- [x] DROP TABLE mantenimiento_tercerizado
- [x] DROP TABLE proveedor_servicio

### Creaciones

- [ ] TrasladoDAO.java
- [ ] TrasladoService.java
- [ ] TrasladosPanel.java
- [ ] NuevoTrasladoWindow.java
- [ ] FichaReporteDAO.java
- [ ] validate_scope.sh

### Refactorizaciones

- [ ] ActivoService.java - restricción tipos
- [ ] RegistroActivoPanel.java - hardcode combo
- [ ] TicketService.java - máquina estados
- [ ] DetallesMantenimientoWindow.java - envío email
- [ ] EmailService.java - método enviarFichaReporteAJefe()
- [ ] MainWindowNew.java - integrar TrasladosPanel
- [ ] application.properties - feature flags

### Validaciones

- [ ] `./validate_scope.sh` → ✅ 0 errores
- [ ] `mvn clean compile` → ✅ SUCCESS
- [ ] Testing E2E completo → ✅ All pass
- [ ] No referencias tercerizado → ✅ Confirmed

---

## 🎯 MÉTRICAS DE ÉXITO

| Métrica                    | Antes         | Después |
| -------------------------- | ------------- | ------- |
| **Cumplimiento protocolo** | 70%           | 100%    |
| **Archivos out-of-scope**  | 13            | 0       |
| **Módulos incompletos**    | 1 (Traslados) | 0       |
| **Validaciones faltantes** | 3             | 0       |
| **Feature flags**          | 0             | 3       |
| **Tests E2E pasando**      | ?             | 12/12   |

---

## ⚠️ RIESGOS Y MITIGACIONES

| Riesgo                                    | Probabilidad | Impacto | Mitigación                                         |
| ----------------------------------------- | ------------ | ------- | -------------------------------------------------- |
| Referencias rotas al eliminar tercerizado | Media        | Alto    | Backup + branch específico + compile checks        |
| UI/UX de TrasladosPanel incompleta        | Alta         | Medio   | Basarse en Tickets/Mantenimiento panels existentes |
| SMTP Zimbra no configurado                | Alta         | Alto    | Probar con Gmail primero, migrar después           |
| Testing E2E largo                         | Media        | Bajo    | Priorizar casos críticos, automatizar después      |
| Scope creep vuelve a aparecer             | Baja         | Alto    | Feature flags + validate_scope.sh en CI/CD         |

---

## 📞 CONTACTO Y SOPORTE

**Si tienes dudas:**

1. Revisar ANALISIS_GAPS_DELIMITACION.md (análisis completo)
2. Revisar MATRIZ_ARCHIVOS_ACCION.md (código copy-paste)
3. Ejecutar `./validate_scope.sh` para diagnóstico

**Próximos pasos después del MVP:**

1. Testing automatizado con JUnit
2. Integración CI/CD con validación de alcance
3. Documentación de usuario final
4. Deploy en servidor producción
5. Capacitación usuarios

---

**FIN DEL PLAN DE ACCIÓN**

¡Listo para ejecutar! 🚀
