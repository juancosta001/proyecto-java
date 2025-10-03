package com.ypacarai.cooperativa.activos.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.TicketAsignacion;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * DAO para gestionar asignaciones múltiples de técnicos a tickets
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TicketAsignacionDAO {
    
    public TicketAsignacionDAO() {
    }
    
    /**
     * Asignar múltiples técnicos a un ticket
     */
    public boolean asignarTecnicos(int ticketId, List<TicketAsignacion> asignaciones) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfigComplete.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Desactivar asignaciones existentes
            String sqlDesactivar = "UPDATE ticket_asignaciones SET tas_activo = FALSE, actualizado_en = NOW() WHERE tick_id = ?";
            try (PreparedStatement stmtDesactivar = conn.prepareStatement(sqlDesactivar)) {
                stmtDesactivar.setInt(1, ticketId);
                stmtDesactivar.executeUpdate();
            }
            
            // 2. Insertar nuevas asignaciones
            String sqlInsertar = "INSERT INTO ticket_asignaciones (tick_id, usu_id, tas_rol_asignacion, tas_observaciones, tas_activo) " +
                               "VALUES (?, ?, ?, ?, TRUE) " +
                               "ON DUPLICATE KEY UPDATE " +
                               "tas_activo = TRUE, " +
                               "tas_rol_asignacion = VALUES(tas_rol_asignacion), " +
                               "tas_observaciones = VALUES(tas_observaciones), " +
                               "actualizado_en = NOW()";
            
            try (PreparedStatement stmtInsertar = conn.prepareStatement(sqlInsertar)) {
                for (TicketAsignacion asignacion : asignaciones) {
                    stmtInsertar.setInt(1, ticketId);
                    stmtInsertar.setInt(2, asignacion.getUsuId());
                    stmtInsertar.setString(3, asignacion.getTasRolAsignacion().toString());
                    stmtInsertar.setString(4, asignacion.getTasObservaciones());
                    stmtInsertar.addBatch();
                }
                stmtInsertar.executeBatch();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error en rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error al asignar técnicos: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar conexión: " + closeEx.getMessage());
                }
            }
        }
    }
    
    /**
     * Obtener todos los técnicos asignados a un ticket
     */
    public List<TicketAsignacion> obtenerTecnicosAsignados(int ticketId) throws SQLException {
        String sql = "SELECT " +
                    "ta.tas_id, ta.tick_id, ta.usu_id, " +
                    "u.usu_nombre, u.usu_email, " +
                    "ta.tas_rol_asignacion, ta.tas_fecha_asignacion, ta.tas_observaciones " +
                    "FROM ticket_asignaciones ta " +
                    "INNER JOIN usuario u ON ta.usu_id = u.usu_id " +
                    "WHERE ta.tick_id = ? AND ta.tas_activo = TRUE " +
                    "ORDER BY " +
                    "CASE ta.tas_rol_asignacion " +
                    "    WHEN 'Responsable' THEN 1 " +
                    "    WHEN 'Supervisor' THEN 2 " +
                    "    WHEN 'Colaborador' THEN 3 " +
                    "    ELSE 4 " +
                    "END, u.usu_nombre";
        
        List<TicketAsignacion> asignaciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TicketAsignacion asignacion = new TicketAsignacion();
                    asignacion.setTasId(rs.getInt("tas_id"));
                    asignacion.setTickId(rs.getInt("tick_id"));
                    asignacion.setUsuId(rs.getInt("usu_id"));
                    asignacion.setUsuarioNombre(rs.getString("usu_nombre"));
                    asignacion.setUsuarioEmail(rs.getString("usu_email"));
                    asignacion.setTasRolAsignacion(TicketAsignacion.RolAsignacion.valueOf(rs.getString("tas_rol_asignacion")));
                    
                    Timestamp fechaAsignacion = rs.getTimestamp("tas_fecha_asignacion");
                    if (fechaAsignacion != null) {
                        asignacion.setTasFechaAsignacion(fechaAsignacion.toLocalDateTime());
                    }
                    
                    asignacion.setTasObservaciones(rs.getString("tas_observaciones"));
                    asignaciones.add(asignacion);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener técnicos asignados: " + e.getMessage());
            throw e;
        }
        
        return asignaciones;
    }
    
    /**
     * Obtener técnico responsable principal de un ticket (para compatibilidad)
     */
    public TicketAsignacion obtenerResponsablePrincipal(int ticketId) throws SQLException {
        List<TicketAsignacion> asignaciones = obtenerTecnicosAsignados(ticketId);
        
        return asignaciones.stream()
            .filter(TicketAsignacion::esResponsable)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Asignar un solo técnico (método de conveniencia)
     */
    public boolean asignarTecnico(int ticketId, int usuarioId, TicketAsignacion.RolAsignacion rol, String observaciones) throws SQLException {
        List<TicketAsignacion> asignaciones = new ArrayList<>();
        asignaciones.add(new TicketAsignacion(ticketId, usuarioId, rol, observaciones));
        return asignarTecnicos(ticketId, asignaciones);
    }
    
    /**
     * Remover todas las asignaciones de un ticket
     */
    public boolean removerTodasAsignaciones(int ticketId) throws SQLException {
        String sql = "UPDATE ticket_asignaciones SET tas_activo = FALSE, actualizado_en = NOW() WHERE tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al remover asignaciones: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Remover asignación específica de un técnico
     */
    public boolean removerAsignacion(int ticketId, int usuarioId) throws SQLException {
        String sql = "UPDATE ticket_asignaciones SET tas_activo = FALSE, actualizado_en = NOW() WHERE tick_id = ? AND usu_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            stmt.setInt(2, usuarioId);
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al remover asignación específica: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener estadísticas de asignaciones por técnico
     */
    public Map<String, Integer> obtenerEstadisticasPorTecnico() throws SQLException {
        String sql = "SELECT u.usu_nombre, COUNT(ta.tas_id) as total_asignaciones " +
                    "FROM usuario u " +
                    "INNER JOIN ticket_asignaciones ta ON u.usu_id = ta.usu_id " +
                    "INNER JOIN ticket t ON ta.tick_id = t.tick_id " +
                    "WHERE ta.tas_activo = TRUE AND t.tick_estado IN ('Abierto', 'En_Proceso') " +
                    "GROUP BY u.usu_id, u.usu_nombre " +
                    "ORDER BY total_asignaciones DESC";
        
        Map<String, Integer> estadisticas = new HashMap<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                estadisticas.put(rs.getString("usu_nombre"), rs.getInt("total_asignaciones"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            throw e;
        }
        
        return estadisticas;
    }
    
    /**
     * Obtener tickets asignados a un técnico específico
     */
    public List<Integer> obtenerTicketsAsignados(int usuarioId) throws SQLException {
        String sql = "SELECT DISTINCT ta.tick_id " +
                    "FROM ticket_asignaciones ta " +
                    "INNER JOIN ticket t ON ta.tick_id = t.tick_id " +
                    "WHERE ta.usu_id = ? AND ta.tas_activo = TRUE " +
                    "AND t.tick_estado IN ('Abierto', 'En_Proceso') " +
                    "ORDER BY ta.tick_id DESC";
        
        List<Integer> ticketIds = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketIds.add(rs.getInt("tick_id"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener tickets asignados: " + e.getMessage());
            throw e;
        }
        
        return ticketIds;
    }
    
    /**
     * Verificar si un usuario está asignado a un ticket
     */
    public boolean estaAsignado(int ticketId, int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ticket_asignaciones WHERE tick_id = ? AND usu_id = ? AND tas_activo = TRUE";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ticketId);
            stmt.setInt(2, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar asignación: " + e.getMessage());
            throw e;
        }
        
        return false;
    }
    
    /**
     * Cambiar el rol de un técnico asignado
     */
    public boolean cambiarRolAsignacion(int ticketId, int usuarioId, TicketAsignacion.RolAsignacion nuevoRol) throws SQLException {
        String sql = "UPDATE ticket_asignaciones SET tas_rol_asignacion = ?, actualizado_en = NOW() WHERE tick_id = ? AND usu_id = ? AND tas_activo = TRUE";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoRol.name());
            stmt.setInt(2, ticketId);
            stmt.setInt(3, usuarioId);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cambiar rol de asignación: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener resumen de asignaciones para un ticket (método de conveniencia)
     */
    public String obtenerResumenAsignaciones(int ticketId) throws SQLException {
        List<TicketAsignacion> asignaciones = obtenerTecnicosAsignados(ticketId);
        
        if (asignaciones.isEmpty()) {
            return "Sin asignar";
        }
        
        StringBuilder resumen = new StringBuilder();
        
        // Agrupar por rol
        Map<TicketAsignacion.RolAsignacion, List<String>> porRol = new HashMap<>();
        for (TicketAsignacion asig : asignaciones) {
            porRol.computeIfAbsent(asig.getTasRolAsignacion(), k -> new ArrayList<>())
                  .add(asig.getUsuarioNombre());
        }
        
        // Construir resumen
        boolean primero = true;
        for (TicketAsignacion.RolAsignacion rol : TicketAsignacion.RolAsignacion.values()) {
            if (porRol.containsKey(rol)) {
                if (!primero) resumen.append(", ");
                resumen.append(rol).append(": ").append(String.join(", ", porRol.get(rol)));
                primero = false;
            }
        }
        
        return resumen.toString();
    }
}