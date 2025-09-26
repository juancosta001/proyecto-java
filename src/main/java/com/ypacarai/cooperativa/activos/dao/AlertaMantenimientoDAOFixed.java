package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.NivelUrgencia;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.TipoAlerta;

/**
 * DAO para la gestión de alertas de mantenimiento preventivo
 */
public class AlertaMantenimientoDAOFixed {
    
    /**
     * Guarda una nueva alerta de mantenimiento
     */
    public boolean save(AlertaMantenimiento alerta) {
        String sql = "INSERT INTO alertas_mantenimiento (activo_id, tipo_alerta, nivel_urgencia, " +
                    "titulo, mensaje, fecha_vencimiento, dias_restantes, leida, activa, " +
                    "usuario_asignado_id, fecha_creacion, fecha_lectura) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, alerta.getActivoId());
            stmt.setString(2, alerta.getTipoAlerta().name());
            stmt.setString(3, alerta.getNivelUrgencia().name());
            stmt.setString(4, alerta.getTitulo());
            stmt.setString(5, alerta.getMensaje());
            stmt.setDate(6, Date.valueOf(alerta.getFechaVencimiento()));
            stmt.setInt(7, alerta.getDiasRestantes());
            stmt.setBoolean(8, alerta.getLeida());
            stmt.setBoolean(9, alerta.getActiva());
            stmt.setObject(10, alerta.getUsuarioAsignadoId());
            stmt.setTimestamp(11, Timestamp.valueOf(alerta.getFechaCreacion()));
            stmt.setObject(12, alerta.getFechaLectura() != null ? Timestamp.valueOf(alerta.getFechaLectura()) : null);
            
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
     * Obtiene alertas activas no leídas
     */
    public List<AlertaMantenimiento> findActivasNoLeidas() {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT am.*, a.act_numero_activo, ta.tip_nombre " +
                    "FROM alertas_mantenimiento am " +
                    "JOIN activos a ON am.activo_id = a.act_id " +
                    "JOIN tipo_activos ta ON a.tip_act_id = ta.tip_act_id " +
                    "WHERE am.activa = true AND am.leida = false " +
                    "ORDER BY am.nivel_urgencia DESC, am.dias_restantes ASC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                alertas.add(mapResultSetToAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas activas: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Obtiene alertas por usuario asignado
     */
    public List<AlertaMantenimiento> findByUsuarioAsignado(Integer usuarioId, boolean soloActivas) {
        List<AlertaMantenimiento> alertas = new ArrayList<>();
        String sql = "SELECT am.*, a.act_numero_activo, ta.tip_nombre " +
                    "FROM alertas_mantenimiento am " +
                    "JOIN activos a ON am.activo_id = a.act_id " +
                    "JOIN tipo_activos ta ON a.tip_act_id = ta.tip_act_id " +
                    "WHERE am.usuario_asignado_id = ? " +
                    (soloActivas ? " AND am.activa = true" : "") +
                    " ORDER BY am.fecha_creacion DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapResultSetToAlerta(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener alertas por usuario: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Marca una alerta como leída
     */
    public boolean marcarComoLeida(Integer alertaId, Integer usuarioId) {
        String sql = "UPDATE alertas_mantenimiento SET leida = true, fecha_lectura = ? " +
                    "WHERE alerta_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, alertaId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar alerta como leída: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Desactiva alertas relacionadas con un activo
     */
    public boolean desactivarAlertasDeActivo(Integer activoId) {
        String sql = "UPDATE alertas_mantenimiento SET activa = false WHERE activo_id = ? AND activa = true";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activoId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al desactivar alertas del activo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mapea un ResultSet a AlertaMantenimiento
     */
    private AlertaMantenimiento mapResultSetToAlerta(ResultSet rs) throws SQLException {
        AlertaMantenimiento alerta = new AlertaMantenimiento();
        
        alerta.setAlertaId(rs.getInt("alerta_id"));
        alerta.setActivoId(rs.getInt("activo_id"));
        alerta.setTipoAlerta(TipoAlerta.valueOf(rs.getString("tipo_alerta")));
        alerta.setNivelUrgencia(NivelUrgencia.valueOf(rs.getString("nivel_urgencia")));
        alerta.setTitulo(rs.getString("titulo"));
        alerta.setMensaje(rs.getString("mensaje"));
        alerta.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
        alerta.setDiasRestantes(rs.getInt("dias_restantes"));
        alerta.setLeida(rs.getBoolean("leida"));
        alerta.setActiva(rs.getBoolean("activa"));
        
        Integer usuarioAsignadoId = rs.getInt("usuario_asignado_id");
        if (!rs.wasNull()) {
            alerta.setUsuarioAsignadoId(usuarioAsignadoId);
        }
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            alerta.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        Timestamp fechaLectura = rs.getTimestamp("fecha_lectura");
        if (fechaLectura != null) {
            alerta.setFechaLectura(fechaLectura.toLocalDateTime());
        }
        
        // Campos relacionados
        try {
            alerta.setActivoDescripcion(rs.getString("act_numero_activo"));
            alerta.setActivoTipo(rs.getString("tip_nombre"));
        } catch (SQLException e) {
            // Los campos relacionados pueden no estar presentes en todas las consultas
        }
        
        return alerta;
    }
}
