package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.Ticket;

/**
 * DAO para la gestión de TICKET de mantenimiento
 */
public class TicketDAO {
    
    /**
     * Obtiene todos los TICKET con información de activos y usuarios
     */
    public List<Ticket> obtenerTodos() throws SQLException {
        List<Ticket> TICKET = new ArrayList<>();
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "ORDER BY t.tick_fecha_apertura DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Ticket ticket = mapearDesdeResultSet(rs);
                TICKET.add(ticket);
            }
        }
        
        return TICKET;
    }
    
    /**
     * Obtiene TICKET por estado
     */
    public List<Ticket> obtenerPorEstado(Ticket.Estado estado) throws SQLException {
        List<Ticket> TICKET = new ArrayList<>();
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_estado = ? " +
                    "ORDER BY t.tick_fecha_apertura DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = mapearDesdeResultSet(rs);
                    TICKET.add(ticket);
                }
            }
        }
        
        return TICKET;
    }
    
    /**
     * Obtiene TICKET asignados a un técnico específico
     */
    public List<Ticket> obtenerPorTecnico(Integer tecnicoId) throws SQLException {
        List<Ticket> TICKET = new ArrayList<>();
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_asignado_a = ? " +
                    "ORDER BY t.tick_fecha_apertura DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tecnicoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = mapearDesdeResultSet(rs);
                    TICKET.add(ticket);
                }
            }
        }
        
        return TICKET;
    }
    
    /**
     * Obtiene TICKET vencidos
     */
    public List<Ticket> obtenerVencidos() throws SQLException {
        List<Ticket> TICKET = new ArrayList<>();
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_fecha_vencimiento < NOW() AND t.tick_estado IN ('Abierto', 'En_Proceso') " +
                    "ORDER BY t.tick_fecha_vencimiento ASC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Ticket ticket = mapearDesdeResultSet(rs);
                TICKET.add(ticket);
            }
        }
        
        return TICKET;
    }
    
    /**
     * Busca un ticket por ID
     */
    public Optional<Ticket> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDesdeResultSet(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca un ticket por número
     */
    public Optional<Ticket> buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_numero = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numero);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDesdeResultSet(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Guarda un nuevo ticket
     */
    public Ticket guardar(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO TICKET (act_id, tick_tipo, tick_prioridad, tick_titulo, " +
                    "tick_descripcion, tick_estado, tick_fecha_apertura, tick_fecha_vencimiento, " +
                    "tick_asignado_a, tick_reportado_por) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, ticket.getActId());
            pstmt.setString(2, ticket.getTickTipo().name());
            pstmt.setString(3, ticket.getTickPrioridad().name());
            pstmt.setString(4, ticket.getTickTitulo());
            pstmt.setString(5, ticket.getTickDescripcion());
            pstmt.setString(6, ticket.getTickEstado().name());
            
            if (ticket.getTickFechaApertura() != null) {
                pstmt.setTimestamp(7, Timestamp.valueOf(ticket.getTickFechaApertura()));
            } else {
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            if (ticket.getTickFechaVencimiento() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(ticket.getTickFechaVencimiento()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }
            
            if (ticket.getTickAsignadoA() != null) {
                pstmt.setInt(9, ticket.getTickAsignadoA());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
            pstmt.setInt(10, ticket.getTickReportadoPor());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear ticket, no se afectaron filas.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setTickId(generatedKeys.getInt(1));
                    // Note: El tick_numero se genera automáticamente por el trigger
                    return ticket;
                } else {
                    throw new SQLException("Error al crear ticket, no se obtuvo el ID.");
                }
            }
        }
    }
    
    /**
     * Actualiza un ticket existente
     */
    public void actualizar(Ticket ticket) throws SQLException {
        String sql = "UPDATE TICKET SET tick_tipo = ?, tick_prioridad = ?, tick_titulo = ?, " +
                    "tick_descripcion = ?, tick_estado = ?, tick_fecha_vencimiento = ?, " +
                    "tick_fecha_cierre = ?, tick_asignado_a = ?, tick_solucion = ?, " +
                    "tick_tiempo_resolucion = ?, tick_notificacion_enviada = ?, " +
                    "actualizado_en = CURRENT_TIMESTAMP WHERE tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticket.getTickTipo().name());
            pstmt.setString(2, ticket.getTickPrioridad().name());
            pstmt.setString(3, ticket.getTickTitulo());
            pstmt.setString(4, ticket.getTickDescripcion());
            pstmt.setString(5, ticket.getTickEstado().name());
            
            if (ticket.getTickFechaVencimiento() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(ticket.getTickFechaVencimiento()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            
            if (ticket.getTickFechaCierre() != null) {
                pstmt.setTimestamp(7, Timestamp.valueOf(ticket.getTickFechaCierre()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP);
            }
            
            if (ticket.getTickAsignadoA() != null) {
                pstmt.setInt(8, ticket.getTickAsignadoA());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setString(9, ticket.getTickSolucion());
            
            if (ticket.getTickTiempoResolucion() != null) {
                pstmt.setInt(10, ticket.getTickTiempoResolucion());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }
            
            pstmt.setBoolean(11, ticket.isTickNotificacionEnviada());
            pstmt.setInt(12, ticket.getTickId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró el ticket con ID: " + ticket.getTickId());
            }
        }
    }
    
    /**
     * Cambia el estado de un ticket
     */
    public void cambiarEstado(Integer ticketId, Ticket.Estado nuevoEstado) throws SQLException {
        String sql = "UPDATE TICKET SET tick_estado = ?, actualizado_en = CURRENT_TIMESTAMP";
        
        if (nuevoEstado == Ticket.Estado.Resuelto || nuevoEstado == Ticket.Estado.Cerrado) {
            sql += ", tick_fecha_cierre = CURRENT_TIMESTAMP";
        }
        
        sql += " WHERE tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado.name());
            pstmt.setInt(2, ticketId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró el ticket con ID: " + ticketId);
            }
        }
    }
    
    /**
     * Asigna un ticket a un técnico
     */
    public void asignarTecnico(Integer ticketId, Integer tecnicoId) throws SQLException {
        String sql = "UPDATE TICKET SET tick_asignado_a = ?, tick_estado = 'En_Proceso', " +
                    "actualizado_en = CURRENT_TIMESTAMP WHERE tick_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tecnicoId);
            pstmt.setInt(2, ticketId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró el ticket con ID: " + ticketId);
            }
        }
    }
    
    /**
     * Mapea un ResultSet a una entidad Ticket
     */
    private Ticket mapearDesdeResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        
        ticket.setTickId(rs.getInt("tick_id"));
        ticket.setActId(rs.getInt("act_id"));
        ticket.setTickNumero(rs.getString("tick_numero"));
        ticket.setTickTipo(Ticket.Tipo.valueOf(rs.getString("tick_tipo")));
        ticket.setTickPrioridad(Ticket.Prioridad.valueOf(rs.getString("tick_prioridad")));
        ticket.setTickTitulo(rs.getString("tick_titulo"));
        ticket.setTickDescripcion(rs.getString("tick_descripcion"));
        ticket.setTickEstado(Ticket.Estado.valueOf(rs.getString("tick_estado")));
        
        Timestamp fechaApertura = rs.getTimestamp("tick_fecha_apertura");
        if (fechaApertura != null) {
            ticket.setTickFechaApertura(fechaApertura.toLocalDateTime());
        }
        
        Timestamp fechaVencimiento = rs.getTimestamp("tick_fecha_vencimiento");
        if (fechaVencimiento != null) {
            ticket.setTickFechaVencimiento(fechaVencimiento.toLocalDateTime());
        }
        
        Timestamp fechaCierre = rs.getTimestamp("tick_fecha_cierre");
        if (fechaCierre != null) {
            ticket.setTickFechaCierre(fechaCierre.toLocalDateTime());
        }
        
        // Campos nullable
        Integer asignadoA = rs.getInt("tick_asignado_a");
        if (!rs.wasNull()) {
            ticket.setTickAsignadoA(asignadoA);
        }
        
        ticket.setTickReportadoPor(rs.getInt("tick_reportado_por"));
        ticket.setTickSolucion(rs.getString("tick_solucion"));
        
        Integer tiempoResolucion = rs.getInt("tick_tiempo_resolucion");
        if (!rs.wasNull()) {
            ticket.setTickTiempoResolucion(tiempoResolucion);
        }
        
        ticket.setTickNotificacionEnviada(rs.getBoolean("tick_notificacion_enviada"));
        
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        if (creadoEn != null) {
            ticket.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            ticket.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        // Campos relacionados
        ticket.setActivoNumero(rs.getString("act_numero_activo"));
        ticket.setTecnicoAsignado(rs.getString("tecnico_asignado"));
        ticket.setUsuarioReporta(rs.getString("reportado_por"));
        ticket.setUbicacionNombre(rs.getString("ubicacion_nombre"));
        
        return ticket;
    }
    
    /**
     * Guarda un ticket y retorna si la operación fue exitosa
     */
    public boolean save(Ticket ticket) {
        try {
            guardar(ticket);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar ticket: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca TICKET por activo
     */
    public List<Ticket> findByActivo(Integer activoId) {
        List<Ticket> TICKET = new ArrayList<>();
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "ua.usu_nombre AS tecnico_asignado, " +
                    "ur.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "LEFT JOIN ACTIVO a ON t.act_id = a.act_id " +
                    "LEFT JOIN USUARIO ua ON t.tick_asignado_a = ua.usu_id " +
                    "LEFT JOIN USUARIO ur ON t.tick_reportado_por = ur.usu_id " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.act_id = ?";

        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, activoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TICKET.add(mapearDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar TICKET por activo: " + e.getMessage());
        }

        return TICKET;
    }
    
    /**
     * Obtiene tickets por una lista de IDs específicos
     */
    public List<Ticket> obtenerPorIds(List<Integer> ticketIds) throws SQLException {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Ticket> tickets = new ArrayList<>();
        
        // Crear placeholders para la consulta IN
        String placeholders = String.join(",", ticketIds.stream().map(id -> "?").toArray(String[]::new));
        
        String sql = "SELECT t.tick_id, t.act_id, t.tick_numero, t.tick_tipo, t.tick_prioridad, " +
                    "t.tick_titulo, t.tick_descripcion, t.tick_estado, t.tick_fecha_apertura, " +
                    "t.tick_fecha_vencimiento, t.tick_fecha_cierre, t.tick_asignado_a, " +
                    "t.tick_reportado_por, t.tick_solucion, t.tick_tiempo_resolucion, " +
                    "t.tick_notificacion_enviada, t.creado_en, t.actualizado_en, " +
                    "a.act_numero_activo, a.act_marca, a.act_modelo, " +
                    "u_asignado.usu_nombre AS tecnico_asignado, " +
                    "u_reporta.usu_nombre AS reportado_por, " +
                    "ub.ubi_nombre AS ubicacion_nombre " +
                    "FROM TICKET t " +
                    "INNER JOIN ACTIVO a ON a.act_id = t.act_id " +
                    "LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a " +
                    "LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por " +
                    "LEFT JOIN UBICACION ub ON ub.ubi_id = a.act_ubicacion_actual " +
                    "WHERE t.tick_id IN (" + placeholders + ") " +
                    "ORDER BY t.tick_fecha_apertura DESC";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Establecer los parámetros
            for (int i = 0; i < ticketIds.size(); i++) {
                pstmt.setInt(i + 1, ticketIds.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = mapearDesdeResultSet(rs);
                    tickets.add(ticket);
                }
            }
        }
        
        return tickets;
    }
}
