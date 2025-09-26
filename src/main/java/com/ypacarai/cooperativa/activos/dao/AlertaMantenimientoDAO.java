package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.NivelUrgencia;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.TipoAlerta;

/**
 * DAO para la gestión de alertas de mantenimiento preventivo
 * Adaptado para usar la tabla ALERTA del esquema real
 */
public class AlertaMantenimientoDAO {
    
    /**
     * Guarda una nueva alerta de mantenimiento
     */
    public boolean save(AlertaMantenimiento alerta) {
        String sql = "INSERT INTO ALERTA (act_id, ale_tipo, ale_titulo, ale_mensaje, " +
                    "ale_fecha_objetivo, ale_fecha_alerta, ale_prioridad, ale_estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, alerta.getActivoId());
            stmt.setString(2, convertTipoAlertaToBD(alerta.getTipoAlerta()));
            stmt.setString(3, alerta.getTitulo());
            stmt.setString(4, alerta.getMensaje());
            stmt.setObject(5, alerta.getFechaVencimiento() != null ? 
                           Date.valueOf(alerta.getFechaVencimiento()) : Date.valueOf(LocalDate.now()));
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(7, convertNivelUrgenciaToBD(alerta.getNivelUrgencia()));
            stmt.setString(8, "Pendiente");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        alerta.setAlertaId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar alerta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Convierte TipoAlerta enum a valor de BD
     */
    private String convertTipoAlertaToBD(TipoAlerta tipoAlerta) {
        // Según tu esquema: 'Mantenimiento_Proximo','Mantenimiento_Vencido','Traslado_Vencido'
        switch (tipoAlerta) {
            case PREVENTIVO_VENCIDO:
                return "Mantenimiento_Vencido";
            case PREVENTIVO_PROXIMO:
            default:
                return "Mantenimiento_Proximo";
        }
    }
    
    /**
     * Convierte NivelUrgencia enum a valor de BD
     */
    private String convertNivelUrgenciaToBD(NivelUrgencia nivelUrgencia) {
        // Según tu esquema: 'Info','Advertencia','Critica'
        switch (nivelUrgencia) {
            case CRITICO:
            case URGENTE:
                return "Critica";
            case ADVERTENCIA:
                return "Advertencia";
            case INFO:
            default:
                return "Info";
        }
    }
    
    /**
     * Convierte valor de BD a TipoAlerta enum
     */
    private TipoAlerta convertBDToTipoAlerta(String tipoBD) {
        switch (tipoBD) {
            case "Mantenimiento_Vencido":
                return TipoAlerta.PREVENTIVO_VENCIDO;
            case "Mantenimiento_Proximo":
            default:
                return TipoAlerta.PREVENTIVO_PROXIMO;
        }
    }
    
    /**
     * Convierte valor de BD a NivelUrgencia enum
     */
    private NivelUrgencia convertBDToNivelUrgencia(String prioridadBD) {
        switch (prioridadBD) {
            case "Critica":
                return NivelUrgencia.CRITICO;
            case "Advertencia":
                return NivelUrgencia.ADVERTENCIA;
            case "Info":
            default:
                return NivelUrgencia.INFO;
        }
    }
    
    /**
     * Busca alerta por ID
     */
    public AlertaMantenimiento findById(Integer alertaId) {
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "WHERE a.ale_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alertaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAlerta(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar alerta por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene alertas activas no leídas ordenadas por prioridad
     */
    public List<AlertaMantenimiento> findAlertasActivasNoLeidas() {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "WHERE a.ale_estado IN ('Pendiente', 'Enviada') " +
                    "ORDER BY CASE a.ale_prioridad " +
                    "         WHEN 'Critica' THEN 1 " +
                    "         WHEN 'Advertencia' THEN 2 " +
                    "         WHEN 'Info' THEN 3 END, " +
                    "         a.ale_fecha_objetivo ASC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                alertas.add(mapResultSetToAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas activas no leídas: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Obtiene alertas por usuario asignado - método actualizado para la tabla ALERTA
     */
    public List<AlertaMantenimiento> findByUsuarioAsignado(Integer usuarioId, boolean soloActivas) {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id ";
        
        if (soloActivas) {
            sql += "WHERE a.ale_estado IN ('Pendiente', 'Enviada') ";
        }
        
        sql += "ORDER BY a.ale_fecha_alerta DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                alertas.add(mapResultSetToAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas por usuario: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Obtiene alertas por activo
     */
    public List<AlertaMantenimiento> findByActivo(Integer activoId) {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "WHERE a.act_id = ? " +
                    "ORDER BY a.ale_fecha_alerta DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapResultSetToAlerta(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas por activo: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Obtiene alertas críticas (vencidas o próximas a vencer)
     */
    public List<AlertaMantenimiento> findAlertasCriticas() {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "WHERE a.ale_estado IN ('Pendiente', 'Enviada') " +
                    "AND (a.ale_prioridad = 'Critica' OR a.ale_fecha_objetivo <= CURRENT_DATE) " +
                    "ORDER BY a.ale_fecha_objetivo ASC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                alertas.add(mapResultSetToAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas críticas: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Obtiene alertas que vencen en los próximos días
     */
    public List<AlertaMantenimiento> findAlertasProximasAVencer(int diasAnticipo) {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "WHERE a.ale_estado IN ('Pendiente', 'Enviada') " +
                    "AND a.ale_fecha_objetivo BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY a.ale_fecha_objetivo ASC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, diasAnticipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapResultSetToAlerta(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas próximas a vencer: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Marca una alerta como atendida
     */
    public boolean marcarComoLeida(Integer alertaId) {
        String sql = "UPDATE ALERTA SET ale_estado = 'Atendida' WHERE ale_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alertaId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar alerta como leída: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Desactiva una alerta
     */
    public boolean desactivar(Integer alertaId) {
        String sql = "UPDATE ALERTA SET ale_estado = 'Cancelada' WHERE ale_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alertaId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al desactivar alerta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina (desactiva) alertas duplicadas para el mismo activo y tipo
     */
    public int eliminarAlertasDuplicadas(Integer activoId, TipoAlerta tipoAlerta) {
        String sql = "UPDATE ALERTA SET ale_estado = 'Cancelada' " +
                    "WHERE act_id = ? AND ale_tipo = ? AND ale_estado IN ('Pendiente', 'Enviada')";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activoId);
            stmt.setString(2, convertTipoAlertaToBD(tipoAlerta));
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar alertas duplicadas: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Cuenta alertas activas no leídas
     */
    public int contarAlertasActivasPorUsuario(Integer usuarioId) {
        String sql = "SELECT COUNT(*) FROM ALERTA " +
                    "WHERE ale_estado IN ('Pendiente', 'Enviada')";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar alertas activas: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Obtiene estadísticas de alertas por tipo y nivel de urgencia
     */
    public List<String[]> getResumenAlertasPorTipo() {
        List<String[]> resumen = new ArrayList<>();
        String sql = "SELECT ale_tipo, ale_prioridad, COUNT(*) as cantidad " +
                    "FROM ALERTA " +
                    "WHERE ale_estado IN ('Pendiente', 'Enviada') " +
                    "GROUP BY ale_tipo, ale_prioridad " +
                    "ORDER BY ale_prioridad, ale_tipo";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String[] fila = {
                    rs.getString("ale_tipo"),
                    rs.getString("ale_prioridad"),
                    String.valueOf(rs.getInt("cantidad"))
                };
                resumen.add(fila);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener resumen de alertas: " + e.getMessage());
        }
        
        return resumen;
    }
    
    /**
     * Actualiza los días restantes de todas las alertas activas - adaptado para tabla ALERTA
     */
    public int actualizarDiasRestantes() {
        // La tabla ALERTA no tiene campo días_restantes, se calcula en tiempo real
        // Este método mantiene compatibilidad pero no hace nada
        return 0;
    }
    
    /**
     * Obtiene todas las alertas (con paginación opcional)
     */
    public List<AlertaMantenimiento> findAll(int limit, int offset) {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT a.*, act.act_numero_activo, act.act_marca, act.act_modelo " +
                    "FROM ALERTA a " +
                    "LEFT JOIN ACTIVO act ON a.act_id = act.act_id " +
                    "ORDER BY a.ale_fecha_alerta DESC";
        
        if (limit > 0) {
            sql += " LIMIT ? OFFSET ?";
        }
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (limit > 0) {
                stmt.setInt(1, limit);
                stmt.setInt(2, offset);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapResultSetToAlerta(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las alertas: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Mapea un ResultSet a AlertaMantenimiento - adaptado para tabla ALERTA
     */
    private AlertaMantenimiento mapResultSetToAlerta(ResultSet rs) throws SQLException {
        AlertaMantenimiento alerta = new AlertaMantenimiento();
        
        alerta.setAlertaId(rs.getInt("ale_id"));
        alerta.setActivoId(rs.getInt("act_id"));
        alerta.setTipoAlerta(convertBDToTipoAlerta(rs.getString("ale_tipo")));
        alerta.setNivelUrgencia(convertBDToNivelUrgencia(rs.getString("ale_prioridad")));
        alerta.setTitulo(rs.getString("ale_titulo"));
        alerta.setMensaje(rs.getString("ale_mensaje"));
        
        Date fechaObjetivo = rs.getDate("ale_fecha_objetivo");
        if (fechaObjetivo != null) {
            alerta.setFechaVencimiento(fechaObjetivo.toLocalDate());
            // Calcular días restantes en tiempo real
            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), fechaObjetivo.toLocalDate());
            alerta.setDiasRestantes((int) diasRestantes);
        }
        
        alerta.setLeida("Atendida".equals(rs.getString("ale_estado")));
        alerta.setActiva("Pendiente".equals(rs.getString("ale_estado")) || "Enviada".equals(rs.getString("ale_estado")));
        
        Timestamp fechaAlerta = rs.getTimestamp("ale_fecha_alerta");
        if (fechaAlerta != null) {
            alerta.setFechaCreacion(fechaAlerta.toLocalDateTime());
        } else {
            alerta.setFechaCreacion(LocalDateTime.now());
        }
        
        // Campos adicionales del JOIN para información complementaria
        alerta.setActivoDescripcion(rs.getString("act_numero_activo"));
        String marca = rs.getString("act_marca");
        String modelo = rs.getString("act_modelo");
        alerta.setActivoTipo((marca != null ? marca : "") + " " + (modelo != null ? modelo : ""));
        
        return alerta;
    }
}
