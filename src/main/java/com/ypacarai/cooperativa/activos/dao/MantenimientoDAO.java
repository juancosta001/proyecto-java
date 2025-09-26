package com.ypacarai.cooperativa.activos.dao;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Mantenimiento.TipoMantenimiento;
import com.ypacarai.cooperativa.activos.model.Mantenimiento.EstadoMantenimiento;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para el manejo de MANTENIMIENTO
 */
public class MantenimientoDAO {
    
    private DatabaseConfig databaseConfig;
    
    public MantenimientoDAO() {
        this.databaseConfig = new DatabaseConfig();
    }
    
    public MantenimientoDAO(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    /**
     * Guarda un nuevo mantenimiento en la base de datos
     */
    public boolean save(Mantenimiento mantenimiento) {
        String sql = "INSERT INTO MANTENIMIENTO (tick_id, act_id, plan_id, mant_fecha_inicio, " +
                    "mant_fecha_fin, mant_tipo, mant_descripcion_inicial, mant_diagnostico, " +
                    "mant_procedimiento, mant_resultado, mant_proxima_fecha, " +
                    "mant_tecnico_asignado, mant_estado, mant_observaciones, creado_en, actualizado_en) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, mantenimiento.getTickId());
            stmt.setObject(2, mantenimiento.getActId());
            stmt.setObject(3, mantenimiento.getPlanId());
            stmt.setObject(4, mantenimiento.getMantFechaInicio() != null ? 
                           Timestamp.valueOf(mantenimiento.getMantFechaInicio()) : null);
            stmt.setObject(5, mantenimiento.getMantFechaFin() != null ? 
                           Timestamp.valueOf(mantenimiento.getMantFechaFin()) : null);
            stmt.setString(6, mantenimiento.getMantTipo() != null ? 
                          mantenimiento.getMantTipo().name() : null);
            stmt.setString(7, mantenimiento.getMantDescripcionInicial());
            stmt.setString(8, mantenimiento.getMantDiagnostico());
            stmt.setString(9, mantenimiento.getMantProcedimiento());
            stmt.setString(10, mantenimiento.getMantResultado());
            stmt.setObject(11, mantenimiento.getMantProximaFecha() != null ? 
                           Date.valueOf(mantenimiento.getMantProximaFecha()) : null);
            stmt.setObject(12, mantenimiento.getMantTecnicoAsignado());
            stmt.setString(13, mantenimiento.getMantEstado() != null ? 
                          mantenimiento.getMantEstado().name() : EstadoMantenimiento.Programado.name());
            stmt.setString(14, mantenimiento.getMantObservaciones());
            stmt.setTimestamp(15, Timestamp.valueOf(mantenimiento.getCreadoEn() != null ? 
                             mantenimiento.getCreadoEn() : LocalDateTime.now()));
            stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mantenimiento.setMantId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Mantenimiento guardado exitosamente: " + mantenimiento.getMantId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar mantenimiento: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Busca un mantenimiento por ID
     */
    public Mantenimiento findById(Integer mantId) {
        String sql = "SELECT * FROM MANTENIMIENTO WHERE mant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mantId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMantenimiento(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimiento por ID " + mantId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los MANTENIMIENTO
     */
    public List<Mantenimiento> findAll() {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO ORDER BY mant_fecha_inicio DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los MANTENIMIENTO: " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
    
    /**
     * Obtiene MANTENIMIENTO por activo ID
     */
    public List<Mantenimiento> findByActivo(Integer actId) {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO WHERE act_id = ? ORDER BY mant_fecha_inicio DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, actId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener MANTENIMIENTO por activo " + actId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
    
    /**
     * Obtiene MANTENIMIENTO por ticket ID
     */
    public List<Mantenimiento> findByTicket(Integer tickId) {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO WHERE tick_id = ? ORDER BY mant_fecha_inicio DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tickId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener MANTENIMIENTO por ticket " + tickId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
    
    /**
     * Obtiene MANTENIMIENTO por técnico asignado
     */
    public List<Mantenimiento> findByTecnico(Integer tecnicoId) {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO WHERE mant_tecnico_asignado = ? ORDER BY mant_fecha_inicio DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tecnicoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener MANTENIMIENTO por técnico " + tecnicoId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
    
    /**
     * Obtiene MANTENIMIENTO por estado
     */
    public List<Mantenimiento> findByEstado(EstadoMantenimiento estado) {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO WHERE mant_estado = ? ORDER BY mant_fecha_inicio DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener MANTENIMIENTO por estado " + estado + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
    
    /**
     * Actualiza un mantenimiento existente
     */
    public boolean update(Mantenimiento mantenimiento) {
        String sql = "UPDATE MANTENIMIENTO SET tick_id = ?, act_id = ?, plan_id = ?, " +
                    "mant_fecha_inicio = ?, mant_fecha_fin = ?, mant_tipo = ?, " +
                    "mant_descripcion_inicial = ?, mant_diagnostico = ?, mant_procedimiento = ?, " +
                    "mant_resultado = ?, mant_proxima_fecha = ?, mant_tecnico_asignado = ?, " +
                    "mant_estado = ?, mant_observaciones = ?, actualizado_en = ? " +
                    "WHERE mant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, mantenimiento.getTickId());
            stmt.setObject(2, mantenimiento.getActId());
            stmt.setObject(3, mantenimiento.getPlanId());
            stmt.setObject(4, mantenimiento.getMantFechaInicio() != null ? 
                           Timestamp.valueOf(mantenimiento.getMantFechaInicio()) : null);
            stmt.setObject(5, mantenimiento.getMantFechaFin() != null ? 
                           Timestamp.valueOf(mantenimiento.getMantFechaFin()) : null);
            stmt.setString(6, mantenimiento.getMantTipo() != null ? 
                          mantenimiento.getMantTipo().name() : null);
            stmt.setString(7, mantenimiento.getMantDescripcionInicial());
            stmt.setString(8, mantenimiento.getMantDiagnostico());
            stmt.setString(9, mantenimiento.getMantProcedimiento());
            stmt.setString(10, mantenimiento.getMantResultado());
            stmt.setObject(11, mantenimiento.getMantProximaFecha() != null ? 
                           Date.valueOf(mantenimiento.getMantProximaFecha()) : null);
            stmt.setObject(12, mantenimiento.getMantTecnicoAsignado());
            stmt.setString(13, mantenimiento.getMantEstado() != null ? 
                          mantenimiento.getMantEstado().name() : null);
            stmt.setString(14, mantenimiento.getMantObservaciones());
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(16, mantenimiento.getMantId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento actualizado exitosamente: " + mantenimiento.getMantId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar mantenimiento " + mantenimiento.getMantId() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualiza solo el estado de un mantenimiento
     */
    public boolean updateEstado(Integer mantId, EstadoMantenimiento nuevoEstado) {
        String sql = "UPDATE MANTENIMIENTO SET mant_estado = ?, actualizado_en = ? WHERE mant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, mantId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Estado del mantenimiento actualizado: " + mantId + " -> " + nuevoEstado);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del mantenimiento " + mantId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Elimina un mantenimiento por ID
     */
    public boolean delete(Integer mantId) {
        String sql = "DELETE FROM MANTENIMIENTO WHERE mant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mantId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento eliminado exitosamente: " + mantId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar mantenimiento " + mantId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto Mantenimiento
     */
    private Mantenimiento mapResultSetToMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento mantenimiento = new Mantenimiento();
        
        mantenimiento.setMantId(rs.getInt("mant_id"));
        
        Integer tickId = rs.getObject("tick_id", Integer.class);
        mantenimiento.setTickId(tickId);
        
        Integer actId = rs.getObject("act_id", Integer.class);
        mantenimiento.setActId(actId);
        
        Integer planId = rs.getObject("plan_id", Integer.class);
        mantenimiento.setPlanId(planId);
        
        // Fechas
        Timestamp fechaInicio = rs.getTimestamp("mant_fecha_inicio");
        if (fechaInicio != null) {
            mantenimiento.setMantFechaInicio(fechaInicio.toLocalDateTime());
        }
        
        Timestamp fechaFin = rs.getTimestamp("mant_fecha_fin");
        if (fechaFin != null) {
            mantenimiento.setMantFechaFin(fechaFin.toLocalDateTime());
        }
        
        String tipoStr = rs.getString("mant_tipo");
        if (tipoStr != null) {
            mantenimiento.setMantTipo(TipoMantenimiento.valueOf(tipoStr));
        }
        
        mantenimiento.setMantDescripcionInicial(rs.getString("mant_descripcion_inicial"));
        mantenimiento.setMantDiagnostico(rs.getString("mant_diagnostico"));
        mantenimiento.setMantProcedimiento(rs.getString("mant_procedimiento"));
        mantenimiento.setMantResultado(rs.getString("mant_resultado"));
        
        Date proximaFecha = rs.getDate("mant_proxima_fecha");
        if (proximaFecha != null) {
            mantenimiento.setMantProximaFecha(proximaFecha.toLocalDate());
        }
        
        Integer tecnicoAsignado = rs.getObject("mant_tecnico_asignado", Integer.class);
        mantenimiento.setMantTecnicoAsignado(tecnicoAsignado);
        
        String estadoStr = rs.getString("mant_estado");
        if (estadoStr != null) {
            mantenimiento.setMantEstado(EstadoMantenimiento.valueOf(estadoStr));
        }
        
        mantenimiento.setMantObservaciones(rs.getString("mant_observaciones"));
        
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        if (creadoEn != null) {
            mantenimiento.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            mantenimiento.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return mantenimiento;
    }
    
    /**
     * Obtiene el total de MANTENIMIENTO
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM MANTENIMIENTO";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar MANTENIMIENTO: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Obtiene MANTENIMIENTO con paginación
     */
    public List<Mantenimiento> findPaginated(int offset, int limit) {
        List<Mantenimiento> MANTENIMIENTO = new ArrayList<>();
        String sql = "SELECT * FROM MANTENIMIENTO ORDER BY mant_fecha_inicio DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MANTENIMIENTO.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener MANTENIMIENTO paginados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return MANTENIMIENTO;
    }
}
