# MATRIZ DE ACCIONES POR ARCHIVO

**Sistema de Activos Ypacaraí - Alineación a Protocolo**

---

## ❌ ARCHIVOS A ELIMINAR (OUT-OF-SCOPE)

### Java Source Files

```bash
# Eliminar del paquete model
src/main/java/com/ypacarai/cooperativa/activos/model/MantenimientoTercerizado.java
src/main/java/com/ypacarai/cooperativa/activos/model/ProveedorServicio.java

# Eliminar del paquete dao
src/main/java/com/ypacarai/cooperativa/activos/dao/MantenimientoTercerizadoDAO.java
src/main/java/com/ypacarai/cooperativa/activos/dao/ProveedorServicioDAO.java

# Eliminar del paquete service
src/main/java/com/ypacarai/cooperativa/activos/service/MantenimientoTercerizadoService.java

# Eliminar del paquete view/gui
src/main/java/com/ypacarai/cooperativa/activos/view/MantenimientoTercerizadoPanel.java
src/main/java/com/ypacarai/cooperativa/activos/view/SolicitudMantenimientoTercerizadoWindow.java
src/main/java/com/ypacarai/cooperativa/activos/view/ProveedorServicioWindow.java
```

### SQL Scripts

```bash
# Schema y configuración de módulo tercerizado
src/main/resources/database/mantenimiento_tercerizado_schema.sql
corregir_tabla_mantenimiento.sql  # Si es del módulo tercerizado
```

### Batch Scripts

```bash
setup_mantenimiento_tercerizado.bat
```

### Documentación

```bash
MANTENIMIENTO_TERCERIZADO_MANUAL.md
```

### Compiled Classes (autogenerados, se eliminarán con clean)

```bash
target/classes/com/ypacarai/cooperativa/activos/model/MantenimientoTercerizado.class
target/classes/com/ypacarai/cooperativa/activos/model/ProveedorServicio.class
target/classes/com/ypacarai/cooperativa/activos/dao/MantenimientoTercerizadoDAO.class
target/classes/com/ypacarai/cooperativa/activos/dao/ProveedorServicioDAO.class
target/classes/com/ypacarai/cooperativa/activos/service/MantenimientoTercerizadoService.class
target/classes/com/ypacarai/cooperativa/activos/view/MantenimientoTercerizadoPanel.class
target/classes/com/ypacarai/cooperativa/activos/view/SolicitudMantenimientoTercerizadoWindow.class
target/classes/com/ypacarai/cooperativa/activos/view/ProveedorServicioWindow.class
```

### SQL Para Limpiar BD

```sql
-- Ejecutar en base de datos desarrollo/producción
DROP TABLE IF EXISTS mantenimiento_tercerizado;
DROP TABLE IF EXISTS proveedor_servicio;

-- Verificar que no queden vistas o procedures relacionados
SHOW TABLES LIKE '%tercerizado%';
SHOW TABLES LIKE '%proveedor%';
```

---

## ⚠️ ARCHIVOS A REFACTORIZAR

### A1. ActivoService.java

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/service/ActivoService.java`

**Cambios:**

```java
// AGREGAR al principio de la clase
public class ActivoService {
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("PC", "Impresora");

    // MODIFICAR método crearActivo() o similar
    public void crearActivo(Activo activo) throws Exception {
        // AGREGAR validación
        if (!TIPOS_PERMITIDOS.contains(activo.getTipoActivo().getNombre())) {
            throw new IllegalArgumentException(
                "Tipo de activo no permitido. Solo se permiten: " + TIPOS_PERMITIDOS
            );
        }

        // ...resto del código existente
    }
}
```

---

### A2. RegistroActivoPanel.java

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/view/RegistroActivoPanel.java`

**Cambios:**

```java
// MODIFICAR inicialización de combobox de tipos
private void initComponents() {
    // ...código existente...

    // REEMPLAZAR carga dinámica de tipos por hardcode
    comboTipoActivo.removeAllItems();
    comboTipoActivo.addItem(new TipoActivo(1, "PC"));
    comboTipoActivo.addItem(new TipoActivo(2, "Impresora"));

    // COMENTAR o ELIMINAR:
    // List<TipoActivo> tipos = tipoActivoDAO.listarTodos();
    // tipos.forEach(t -> comboTipoActivo.addItem(t));
}
```

---

### A3. MainWindowNew.java

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/view/MainWindowNew.java`

**Cambios:**

```java
// ELIMINAR imports del módulo tercerizado:
// import com.ypacarai.cooperativa.activos.view.MantenimientoTercerizadoPanel;
// import com.ypacarai.cooperativa.activos.view.SolicitudMantenimientoTercerizadoWindow;
// import com.ypacarai.cooperativa.activos.view.ProveedorServicioWindow;
// import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;

// ELIMINAR o COMENTAR inicialización del panel:
// private MantenimientoTercerizadoPanel panelTercerizado;

// ELIMINAR del menú/tab:
// tabbedPane.add("Tercerizado", panelTercerizado);

// ELIMINAR listeners/botones relacionados
```

---

### A4. DetallesMantenimientoWindow.java

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/view/DetallesMantenimientoWindow.java`

**Cambios:**

```java
// VERIFICAR que existan estos campos en el formulario:
// - txtDiagnostico ✅
// - txtSolucion ✅
// - txtComponentesReemplazados ✅
// - txtObservaciones ✅
// - panelFirmas o campos firma técnico/supervisor ⚠️

// AGREGAR si falta:
private JTextField txtFirmaTecnico;
private JTextField txtFirmaJefe;

// EN initComponents():
panelFirmas = new JPanel();
panelFirmas.add(new JLabel("Firma Técnico:"));
panelFirmas.add(txtFirmaTecnico);
panelFirmas.add(new JLabel("Firma Jefe:"));
panelFirmas.add(txtFirmaJefe);

// VERIFICAR integración con email:
// Debe llamar a NotificationService o EmailService al guardar
private void guardarFicha() {
    // ...guardar en BD...

    // AGREGAR si no existe:
    if (fichaReporte != null) {
        emailService.enviarFichaReporteAJefe(fichaReporte);
    }
}
```

---

### A5. ConsultaDinamica.java (si existe)

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/model/ConsultaDinamica.java`

**Opciones:**

**Opción 1 - Si es query builder genérico:**

```java
// ELIMINAR archivo completo
// Es scope creep (reportes deben ser predefinidos)
```

**Opción 2 - Si es para reportes fijos:**

```java
// MANTENER pero renombrar a:
// src/main/java/com/ypacarai/cooperativa/activos/model/ReportePredefinido.java

// AGREGAR validación:
public class ReportePredefinido {
    private enum TipoReporte {
        ESTADO_ACTIVOS,
        MANTENIMIENTOS,
        FALLAS,
        TRASLADOS
    }

    // No permitir queries arbitrarias
}
```

---

### A6. TicketService.java

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/service/TicketService.java`

**Cambios:**

```java
// AGREGAR máquina de estados explícita
public class TicketService {

    private static final Map<String, Set<String>> TRANSICIONES_VALIDAS = Map.of(
        "Pendiente", Set.of("Asignado", "Cancelado"),
        "Asignado", Set.of("En_Proceso", "Pendiente", "Cancelado"),
        "En_Proceso", Set.of("Completado", "Pausado", "Cancelado"),
        "Pausado", Set.of("En_Proceso", "Cancelado"),
        "Completado", Set.of(),  // Estado final
        "Cancelado", Set.of()    // Estado final
    );

    public void cambiarEstado(Long ticketId, String nuevoEstado) throws Exception {
        Ticket ticket = ticketDAO.buscarPorId(ticketId);
        String estadoActual = ticket.getEstado();

        if (!TRANSICIONES_VALIDAS.get(estadoActual).contains(nuevoEstado)) {
            throw new IllegalStateException(
                "Transición inválida: " + estadoActual + " -> " + nuevoEstado
            );
        }

        // ...actualizar estado...
    }
}
```

---

### A7. application.properties

**Ruta:** `src/main/resources/application.properties`

**Cambios:**

```properties
# VERIFICAR configuración Zimbra real
mail.smtp.host=mail.ypacarai.coop.py
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.from=informatica@ypacarai.coop.py

# AGREGAR feature flags para prevenir scope creep
feature.mantenimiento_tercerizado.enabled=false
feature.consultas_dinamicas.enabled=false

# AGREGAR restricción de tipos
activo.tipos.permitidos=PC,Impresora
```

---

## ✅ ARCHIVOS A CREAR (GAPS)

### B1. TrasladoDAO.java - CRÍTICO

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/dao/TrasladoDAO.java`

**Contenido completo:**

```java
package com.ypacarai.cooperativa.activos.dao;

import com.ypacarai.cooperativa.activos.model.Traslado;
import com.ypacarai.cooperativa.activos.config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrasladoDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void crear(Traslado traslado) throws SQLException {
        String sql = """
            INSERT INTO TRASLADO (
                activo_id, ubicacion_origen_id, ubicacion_destino_id,
                fecha_traslado, motivo, responsable, estado
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, traslado.getActivoId());
            stmt.setLong(2, traslado.getUbicacionOrigenId());
            stmt.setLong(3, traslado.getUbicacionDestinoId());
            stmt.setDate(4, Date.valueOf(traslado.getFechaTraslado()));
            stmt.setString(5, traslado.getMotivo());
            stmt.setString(6, traslado.getResponsable());
            stmt.setString(7, traslado.getEstado());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    traslado.setId(rs.getLong(1));
                }
            }
        }
    }

    public Optional<Traslado> buscarPorId(Long id) throws SQLException {
        String sql = """
            SELECT t.*,
                   a.descripcion as activo_desc,
                   uo.nombre as origen_nombre,
                   ud.nombre as destino_nombre
            FROM TRASLADO t
            JOIN ACTIVO a ON t.activo_id = a.id
            JOIN UBICACION uo ON t.ubicacion_origen_id = uo.id
            JOIN UBICACION ud ON t.ubicacion_destino_id = ud.id
            WHERE t.id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearTraslado(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Traslado> listarTodos() throws SQLException {
        String sql = """
            SELECT t.*,
                   a.descripcion as activo_desc,
                   uo.nombre as origen_nombre,
                   ud.nombre as destino_nombre
            FROM TRASLADO t
            JOIN ACTIVO a ON t.activo_id = a.id
            JOIN UBICACION uo ON t.ubicacion_origen_id = uo.id
            JOIN UBICACION ud ON t.ubicacion_destino_id = ud.id
            ORDER BY t.fecha_traslado DESC
        """;

        List<Traslado> traslados = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                traslados.add(mapearTraslado(rs));
            }
        }

        return traslados;
    }

    public List<Traslado> buscarPorActivo(Long activoId) throws SQLException {
        String sql = """
            SELECT t.*,
                   a.descripcion as activo_desc,
                   uo.nombre as origen_nombre,
                   ud.nombre as destino_nombre
            FROM TRASLADO t
            JOIN ACTIVO a ON t.activo_id = a.id
            JOIN UBICACION uo ON t.ubicacion_origen_id = uo.id
            JOIN UBICACION ud ON t.ubicacion_destino_id = ud.id
            WHERE t.activo_id = ?
            ORDER BY t.fecha_traslado DESC
        """;

        List<Traslado> traslados = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, activoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traslados.add(mapearTraslado(rs));
                }
            }
        }

        return traslados;
    }

    public void actualizar(Traslado traslado) throws SQLException {
        String sql = """
            UPDATE TRASLADO
            SET activo_id = ?,
                ubicacion_origen_id = ?,
                ubicacion_destino_id = ?,
                fecha_traslado = ?,
                motivo = ?,
                responsable = ?,
                estado = ?
            WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, traslado.getActivoId());
            stmt.setLong(2, traslado.getUbicacionOrigenId());
            stmt.setLong(3, traslado.getUbicacionDestinoId());
            stmt.setDate(4, Date.valueOf(traslado.getFechaTraslado()));
            stmt.setString(5, traslado.getMotivo());
            stmt.setString(6, traslado.getResponsable());
            stmt.setString(7, traslado.getEstado());
            stmt.setLong(8, traslado.getId());

            stmt.executeUpdate();
        }
    }

    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM TRASLADO WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Traslado mapearTraslado(ResultSet rs) throws SQLException {
        Traslado traslado = new Traslado();
        traslado.setId(rs.getLong("id"));
        traslado.setNumeroTraslado(rs.getString("numero_traslado"));
        traslado.setActivoId(rs.getLong("activo_id"));
        traslado.setUbicacionOrigenId(rs.getLong("ubicacion_origen_id"));
        traslado.setUbicacionDestinoId(rs.getLong("ubicacion_destino_id"));
        traslado.setFechaTraslado(rs.getDate("fecha_traslado").toLocalDate());
        traslado.setMotivo(rs.getString("motivo"));
        traslado.setResponsable(rs.getString("responsable"));
        traslado.setEstado(rs.getString("estado"));

        // Datos relacionados para display
        traslado.setActivoDescripcion(rs.getString("activo_desc"));
        traslado.setOrigenNombre(rs.getString("origen_nombre"));
        traslado.setDestinoNombre(rs.getString("destino_nombre"));

        return traslado;
    }
}
```

---

### B2. TrasladoService.java - CRÍTICO

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/service/TrasladoService.java`

**Contenido:**

```java
package com.ypacarai.cooperativa.activos.service;

import com.ypacarai.cooperativa.activos.dao.TrasladoDAO;
import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.model.Traslado;
import com.ypacarai.cooperativa.activos.model.Activo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TrasladoService {

    private final TrasladoDAO trasladoDAO;
    private final ActivoDAO activoDAO;
    private final NotificationService notificationService;

    public TrasladoService() {
        this.trasladoDAO = new TrasladoDAO();
        this.activoDAO = new ActivoDAO();
        this.notificationService = new NotificationService();
    }

    public void registrarTraslado(Traslado traslado) throws Exception {
        // Validar que el activo existe
        Optional<Activo> activo = activoDAO.buscarPorId(traslado.getActivoId());
        if (activo.isEmpty()) {
            throw new IllegalArgumentException("El activo no existe");
        }

        // Validar que origen != destino
        if (traslado.getUbicacionOrigenId().equals(traslado.getUbicacionDestinoId())) {
            throw new IllegalArgumentException("Origen y destino no pueden ser iguales");
        }

        // Validar que el activo está en la ubicación origen
        if (!activo.get().getUbicacionId().equals(traslado.getUbicacionOrigenId())) {
            throw new IllegalStateException("El activo no está en la ubicación origen especificada");
        }

        // Estado inicial
        traslado.setEstado("Registrado");

        // Registrar traslado
        trasladoDAO.crear(traslado);

        // Notificar (opcional)
        notificationService.notificarNuevoTraslado(traslado);
    }

    public void confirmarTraslado(Long trasladoId) throws Exception {
        Optional<Traslado> optTraslado = trasladoDAO.buscarPorId(trasladoId);
        if (optTraslado.isEmpty()) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }

        Traslado traslado = optTraslado.get();

        // Validar estado
        if (!"Registrado".equals(traslado.getEstado())) {
            throw new IllegalStateException("Solo se pueden confirmar traslados en estado Registrado");
        }

        // Actualizar estado
        traslado.setEstado("Confirmado");
        trasladoDAO.actualizar(traslado);

        // El trigger trg_traslado_actualizar_ubicacion se encarga de actualizar ACTIVO.ubicacion_id
    }

    public void cancelarTraslado(Long trasladoId, String motivo) throws Exception {
        Optional<Traslado> optTraslado = trasladoDAO.buscarPorId(trasladoId);
        if (optTraslado.isEmpty()) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }

        Traslado traslado = optTraslado.get();

        if ("Confirmado".equals(traslado.getEstado())) {
            throw new IllegalStateException("No se puede cancelar un traslado ya confirmado");
        }

        traslado.setEstado("Cancelado");
        traslado.setMotivo(traslado.getMotivo() + " | CANCELADO: " + motivo);
        trasladoDAO.actualizar(traslado);
    }

    public List<Traslado> listarTodos() throws SQLException {
        return trasladoDAO.listarTodos();
    }

    public List<Traslado> buscarPorActivo(Long activoId) throws SQLException {
        return trasladoDAO.buscarPorActivo(activoId);
    }

    public Optional<Traslado> buscarPorId(Long id) throws SQLException {
        return trasladoDAO.buscarPorId(id);
    }
}
```

---

### B3. TrasladosPanel.java - CRÍTICO

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/view/TrasladosPanel.java`

**Nota:** Este es un esqueleto extenso. Puedo generarlo completo si lo necesitas.

```java
package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.service.TrasladoService;
import com.ypacarai.cooperativa.activos.model.Traslado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TrasladosPanel extends JPanel {

    private TrasladoService trasladoService;
    private JTable tablaTraslados;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JButton btnRefrescar;

    public TrasladosPanel() {
        this.trasladoService = new TrasladoService();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior - Botones
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevo = new JButton("Nuevo Traslado");
        btnConfirmar = new JButton("Confirmar");
        btnCancelar = new JButton("Cancelar");
        btnRefrescar = new JButton("Refrescar");

        panelSuperior.add(btnNuevo);
        panelSuperior.add(btnConfirmar);
        panelSuperior.add(btnCancelar);
        panelSuperior.add(btnRefrescar);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {
            "ID", "Número", "Activo", "Origen", "Destino",
            "Fecha", "Responsable", "Estado"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTraslados = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaTraslados);

        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        btnNuevo.addActionListener(e -> abrirVentanaNuevoTraslado());
        btnConfirmar.addActionListener(e -> confirmarTrasladoSeleccionado());
        btnCancelar.addActionListener(e -> cancelarTrasladoSeleccionado());
        btnRefrescar.addActionListener(e -> cargarDatos());
    }

    private void cargarDatos() {
        try {
            List<Traslado> traslados = trasladoService.listarTodos();
            modeloTabla.setRowCount(0);

            for (Traslado t : traslados) {
                modeloTabla.addRow(new Object[]{
                    t.getId(),
                    t.getNumeroTraslado(),
                    t.getActivoDescripcion(),
                    t.getOrigenNombre(),
                    t.getDestinoNombre(),
                    t.getFechaTraslado(),
                    t.getResponsable(),
                    t.getEstado()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar traslados: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaNuevoTraslado() {
        // Implementar ventana de registro
        NuevoTrasladoWindow ventana = new NuevoTrasladoWindow(
            (JFrame) SwingUtilities.getWindowAncestor(this)
        );
        ventana.setVisible(true);

        if (ventana.isConfirmado()) {
            cargarDatos();
        }
    }

    private void confirmarTrasladoSeleccionado() {
        int fila = tablaTraslados.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un traslado", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 7);

        if (!"Registrado".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                "Solo se pueden confirmar traslados en estado Registrado",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
            "¿Confirmar traslado? Esto actualizará la ubicación del activo.",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                trasladoService.confirmarTraslado(id);
                JOptionPane.showMessageDialog(this, "Traslado confirmado");
                cargarDatos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelarTrasladoSeleccionado() {
        int fila = tablaTraslados.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un traslado", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 7);

        if ("Confirmado".equals(estado) || "Cancelado".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                "No se puede cancelar un traslado ya confirmado o cancelado",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String motivo = JOptionPane.showInputDialog(this,
            "Motivo de cancelación:");

        if (motivo != null && !motivo.trim().isEmpty()) {
            try {
                trasladoService.cancelarTraslado(id, motivo);
                JOptionPane.showMessageDialog(this, "Traslado cancelado");
                cargarDatos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
```

---

### B4. FichaReporteDAO.java - ALTA PRIORIDAD

**Ruta:** `src/main/java/com/ypacarai/cooperativa/activos/dao/FichaReporteDAO.java`

**Contenido:**

```java
package com.ypacarai.cooperativa.activos.dao;

import com.ypacarai.cooperativa.activos.model.FichaReporte;
import com.ypacarai.cooperativa.activos.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FichaReporteDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void crear(FichaReporte ficha) throws SQLException {
        String sql = """
            INSERT INTO FICHA_REPORTE (
                mantenimiento_id, diagnostico, solucion_aplicada,
                componentes_reemplazados, observaciones,
                firma_tecnico, firma_jefe
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, ficha.getMantenimientoId());
            stmt.setString(2, ficha.getDiagnostico());
            stmt.setString(3, ficha.getSolucionAplicada());
            stmt.setString(4, ficha.getComponentesReemplazados());
            stmt.setString(5, ficha.getObservaciones());
            stmt.setString(6, ficha.getFirmaTecnico());
            stmt.setString(7, ficha.getFirmaJefe());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ficha.setId(rs.getLong(1));
                }
            }
        }
    }

    public Optional<FichaReporte> buscarPorId(Long id) throws SQLException {
        String sql = """
            SELECT f.*,
                   m.activo_id,
                   a.descripcion as activo_desc
            FROM FICHA_REPORTE f
            JOIN MANTENIMIENTO m ON f.mantenimiento_id = m.id
            JOIN ACTIVO a ON m.activo_id = a.id
            WHERE f.id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFicha(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<FichaReporte> buscarPorMantenimiento(Long mantenimientoId) throws SQLException {
        String sql = """
            SELECT f.*,
                   m.activo_id,
                   a.descripcion as activo_desc
            FROM FICHA_REPORTE f
            JOIN MANTENIMIENTO m ON f.mantenimiento_id = m.id
            JOIN ACTIVO a ON m.activo_id = a.id
            WHERE f.mantenimiento_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, mantenimientoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFicha(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<FichaReporte> listarTodas() throws SQLException {
        String sql = """
            SELECT f.*,
                   m.activo_id,
                   a.descripcion as activo_desc
            FROM FICHA_REPORTE f
            JOIN MANTENIMIENTO m ON f.mantenimiento_id = m.id
            JOIN ACTIVO a ON m.activo_id = a.id
            ORDER BY f.fecha_creacion DESC
        """;

        List<FichaReporte> fichas = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fichas.add(mapearFicha(rs));
            }
        }

        return fichas;
    }

    public void actualizar(FichaReporte ficha) throws SQLException {
        String sql = """
            UPDATE FICHA_REPORTE
            SET diagnostico = ?,
                solucion_aplicada = ?,
                componentes_reemplazados = ?,
                observaciones = ?,
                firma_tecnico = ?,
                firma_jefe = ?
            WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ficha.getDiagnostico());
            stmt.setString(2, ficha.getSolucionAplicada());
            stmt.setString(3, ficha.getComponentesReemplazados());
            stmt.setString(4, ficha.getObservaciones());
            stmt.setString(5, ficha.getFirmaTecnico());
            stmt.setString(6, ficha.getFirmaJefe());
            stmt.setLong(7, ficha.getId());

            stmt.executeUpdate();
        }
    }

    private FichaReporte mapearFicha(ResultSet rs) throws SQLException {
        FichaReporte ficha = new FichaReporte();
        ficha.setId(rs.getLong("id"));
        ficha.setNumeroFicha(rs.getString("numero_ficha"));
        ficha.setMantenimientoId(rs.getLong("mantenimiento_id"));
        ficha.setDiagnostico(rs.getString("diagnostico"));
        ficha.setSolucionAplicada(rs.getString("solucion_aplicada"));
        ficha.setComponentesReemplazados(rs.getString("componentes_reemplazados"));
        ficha.setObservaciones(rs.getString("observaciones"));
        ficha.setFirmaTecnico(rs.getString("firma_tecnico"));
        ficha.setFirmaJefe(rs.getString("firma_jefe"));
        ficha.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());

        // Datos relacionados
        ficha.setActivoDescripcion(rs.getString("activo_desc"));

        return ficha;
    }
}
```

---

### B5. validate_scope.sh (Prevención)

**Ruta:** `validate_scope.sh`

**Contenido:**

```bash
#!/bin/bash
# Script de validación de alcance del sistema
# Ejecutar antes de commits o en CI/CD

echo "=== Validación de Alcance del Sistema ==="
echo ""

ERRORS=0

# 1. Verificar referencias a módulo tercerizado
echo "[1] Verificando referencias a módulo tercerizado..."
TERCERIZADO_REFS=$(grep -r "MantenimientoTercerizado\|ProveedorServicio" src/ --include="*.java" | grep -v "^$" | wc -l)
if [ $TERCERIZADO_REFS -gt 0 ]; then
    echo "❌ ERROR: Se encontraron $TERCERIZADO_REFS referencias al módulo tercerizado"
    grep -r "MantenimientoTercerizado\|ProveedorServicio" src/ --include="*.java"
    ERRORS=$((ERRORS+1))
else
    echo "✅ OK: No hay referencias al módulo tercerizado"
fi

# 2. Verificar tipos de activo en seeds
echo ""
echo "[2] Verificando tipos de activo..."
if [ -f "src/main/resources/database/seeds.sql" ]; then
    NON_ALLOWED_TYPES=$(grep -i "INSERT INTO TIPO_ACTIVO" src/main/resources/database/seeds.sql | grep -v -i "PC\|Impresora" | wc -l)
    if [ $NON_ALLOWED_TYPES -gt 0 ]; then
        echo "❌ ERROR: Se encontraron tipos de activo no permitidos en seeds"
        grep -i "INSERT INTO TIPO_ACTIVO" src/main/resources/database/seeds.sql | grep -v -i "PC\|Impresora"
        ERRORS=$((ERRORS+1))
    else
        echo "✅ OK: Solo tipos PC e Impresora en seeds"
    fi
else
    echo "⚠️  ADVERTENCIA: No existe seeds.sql"
fi

# 3. Verificar tablas en schema
echo ""
echo "[3] Verificando tablas en schema..."
if [ -f "src/main/resources/database/schema.sql" ]; then
    TERCERIZADO_TABLES=$(grep -i "CREATE TABLE.*tercerizado\|CREATE TABLE.*proveedor" src/main/resources/database/schema.sql | wc -l)
    if [ $TERCERIZADO_TABLES -gt 0 ]; then
        echo "❌ ERROR: Se encontraron tablas fuera de alcance en schema"
        grep -i "CREATE TABLE.*tercerizado\|CREATE TABLE.*proveedor" src/main/resources/database/schema.sql
        ERRORS=$((ERRORS+1))
    else
        echo "✅ OK: Schema sin tablas fuera de alcance"
    fi
else
    echo "⚠️  ADVERTENCIA: No existe schema.sql principal"
fi

# 4. Verificar packages no permitidos
echo ""
echo "[4] Verificando packages no permitidos..."
FORBIDDEN_PACKAGES=$(find src/ -type d -name "externo" -o -name "terceros" -o -name "proveedores" | wc -l)
if [ $FORBIDDEN_PACKAGES -gt 0 ]; then
    echo "❌ ERROR: Se encontraron packages fuera de alcance"
    find src/ -type d -name "externo" -o -name "terceros" -o -name "proveedores"
    ERRORS=$((ERRORS+1))
else
    echo "✅ OK: No hay packages prohibidos"
fi

# 5. Verificar feature flags
echo ""
echo "[5] Verificando feature flags en application.properties..."
if [ -f "src/main/resources/application.properties" ]; then
    TERCERIZADO_ENABLED=$(grep "feature.mantenimiento_tercerizado.enabled=true" src/main/resources/application.properties | wc -l)
    if [ $TERCERIZADO_ENABLED -gt 0 ]; then
        echo "❌ ERROR: Feature tercerizado está habilitado"
        ERRORS=$((ERRORS+1))
    else
        echo "✅ OK: Features out-of-scope deshabilitados"
    fi
else
    echo "⚠️  ADVERTENCIA: No existe application.properties"
fi

# Resultado final
echo ""
echo "===================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ VALIDACIÓN EXITOSA: Sistema dentro del alcance del protocolo"
    exit 0
else
    echo "❌ VALIDACIÓN FALLIDA: $ERRORS errores encontrados"
    echo "Revisar DELIMITACION.md y corregir antes de continuar"
    exit 1
fi
```

---

## 📝 CHECKLIST DE IMPLEMENTACIÓN

### Fase 1: Limpieza (2 días)

- [ ] Backup del repo: `git branch backup-before-cleanup`
- [ ] Eliminar archivos Java tercerizado (8 archivos)
- [ ] Eliminar scripts SQL/BAT (3 archivos)
- [ ] Eliminar documentación (2 archivos)
- [ ] DROP tablas en BD
- [ ] Refactorizar MainWindowNew.java (eliminar imports/referencias)
- [ ] `mvn clean compile` - verificar 0 errores
- [ ] Commit: "eliminar módulo fuera de alcance"

### Fase 2: Traslados (3 días)

- [ ] Crear TrasladoDAO.java
- [ ] Crear TrasladoService.java
- [ ] Crear TrasladosPanel.java
- [ ] Crear NuevoTrasladoWindow.java (ventana de registro)
- [ ] Integrar en MainWindowNew (menú/tab)
- [ ] Agregar en NotificationService si es necesario
- [ ] Testing manual básico
- [ ] Commit: "implementar módulo traslados completo"

### Fase 3: Refactor (2 días)

- [ ] ActivoService: validación restricción
- [ ] RegistroActivoPanel: hardcode tipos
- [ ] DetallesMantenimientoWindow: campos completos + email
- [ ] TicketService: máquina de estados
- [ ] application.properties: feature flags + config
- [ ] Commit: "refactor validaciones y feature flags"

### Fase 4: FichaReporte (1 día)

- [ ] Crear FichaReporteDAO.java
- [ ] Integrar en MantenimientoService/Panel
- [ ] Conectar envío email en DetallesMantenimientoWindow
- [ ] Testing
- [ ] Commit: "completar CRUD fichas reporte + email"

### Fase 5: Validación (2 días)

- [ ] Crear validate_scope.sh
- [ ] Ejecutar validación: `./validate_scope.sh`
- [ ] Corregir fallos
- [ ] Testing integral E2E
- [ ] Actualizar documentación
- [ ] Tag: `v1.0-mvp-protocolo`

---

## 📊 RESUMEN

| Categoría        | Cantidad    | Lista archivos                                                                                                                          |
| ---------------- | ----------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| **ELIMINAR**     | 13 archivos | Ver sección "ARCHIVOS A ELIMINAR"                                                                                                       |
| **REFACTORIZAR** | 7 archivos  | ActivoService, RegistroActivoPanel, MainWindowNew, DetallesMantenimientoWindow, ConsultaDinamica, TicketService, application.properties |
| **CREAR**        | 5 archivos  | TrasladoDAO, TrasladoService, TrasladosPanel, FichaReporteDAO, validate_scope.sh                                                        |

**TOTAL: 25 archivos modificados/creados para alcanzar 100% del protocolo**

---

**Fin de la matriz.**
