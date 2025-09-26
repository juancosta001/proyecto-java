package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento.TipoActivo;

/**
 * DAO para la gestión de configuraciones de mantenimiento preventivo
 */
public class ConfiguracionMantenimientoDAO {
    
    /**
     * Guarda una nueva configuración de mantenimiento
     */
    public boolean save(ConfiguracionMantenimiento config) {
        String sql = "INSERT INTO PLAN_MANTENIMIENTO (tip_act_id, plan_nombre, plan_descripcion, " +
                    "plan_frecuencia_dias, plan_dias_alerta, plan_procedimiento, " +
                    "plan_activo, creado_por, creado_en, actualizado_en) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, convertTipoActivoToId(config.getTipoActivo()));
            stmt.setString(2, "Mantenimiento " + config.getTipoActivo().name());
            stmt.setString(3, "Configuración automática para " + config.getTipoActivo().name());
            stmt.setInt(4, config.getDiasMantenimiento());
            stmt.setInt(5, config.getDiasAnticipoAlerta());
            stmt.setString(6, config.getActividadesPredefinidas());
            stmt.setBoolean(7, config.getActivo());
            stmt.setInt(8, 1); // Usuario sistema
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        config.setConfigId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca configuración por ID
     */
    public ConfiguracionMantenimiento findById(Integer configId) {
        String sql = "SELECT * FROM PLAN_MANTENIMIENTO WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, configId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfig(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar configuración por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca configuración por tipo de activo
     */
    public ConfiguracionMantenimiento findByTipoActivo(TipoActivo tipoActivo) {
        String sql = "SELECT * FROM PLAN_MANTENIMIENTO WHERE tip_act_id = ? AND plan_activo = true";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, convertTipoActivoToId(tipoActivo));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfig(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar configuración por tipo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las configuraciones
     */
    public List<ConfiguracionMantenimiento> findAll() {
        List<ConfiguracionMantenimiento> configuraciones = new ArrayList<>();
        String sql = "SELECT * FROM PLAN_MANTENIMIENTO ORDER BY tip_act_id";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                configuraciones.add(mapResultSetToConfig(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Obtiene configuraciones activas
     */
    public List<ConfiguracionMantenimiento> findAllActive() {
        List<ConfiguracionMantenimiento> configuraciones = new ArrayList<>();
        String sql = "SELECT * FROM PLAN_MANTENIMIENTO WHERE plan_activo = true ORDER BY tip_act_id";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                configuraciones.add(mapResultSetToConfig(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones activas: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Actualiza una configuración existente
     */
    public boolean update(ConfiguracionMantenimiento config) {
        String sql = "UPDATE PLAN_MANTENIMIENTO SET " +
                    "plan_frecuencia_dias = ?, plan_dias_alerta = ?, " +
                    "plan_procedimiento = ?, plan_activo = ?, actualizado_en = ? " +
                    "WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, config.getDiasMantenimiento());
            stmt.setInt(2, config.getDiasAnticipoAlerta());
            stmt.setString(3, config.getActividadesPredefinidas());
            stmt.setBoolean(4, config.getActivo());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, config.getConfigId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina una configuración (soft delete)
     */
    public boolean delete(Integer configId) {
        String sql = "UPDATE PLAN_MANTENIMIENTO SET plan_activo = false, actualizado_en = ? WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, configId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar configuración: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Convierte TipoActivo enum a ID de base de datos
     */
    private int convertTipoActivoToId(TipoActivo tipoActivo) {
        // Según tu esquema: 1=PC, 2=Impresora
        switch (tipoActivo) {
            case PC_Escritorio:
            case Laptop:
            case Servidor:
                return 1; // PC
            case Impresora_Laser:
            case Impresora_Inyeccion:
                return 2; // Impresora
            default:
                return 1; // Por defecto PC
        }
    }
    
    /**
     * Convierte ID de base de datos a TipoActivo enum
     */
    private TipoActivo convertIdToTipoActivo(int tipActId) {
        switch (tipActId) {
            case 1:
                return TipoActivo.PC_Escritorio;
            case 2:
                return TipoActivo.Impresora_Laser;
            default:
                return TipoActivo.PC_Escritorio;
        }
    }
    
    /**
     * Crea configuraciones por defecto si no existen
     */
    public void crearConfiguracionesPorDefecto() {
        // Verificar si ya existen configuraciones
        if (!findAll().isEmpty()) {
            System.out.println("⚠️  Ya existen configuraciones, saltando creación por defecto");
            return;
        }
        
        // Usar los datos que ya están en la BD según tu esquema
        System.out.println("✅ Utilizando configuraciones existentes en PLAN_MANTENIMIENTO");
    }
    
    /**
     * Mapea un ResultSet a ConfiguracionMantenimiento
     */
    private ConfiguracionMantenimiento mapResultSetToConfig(ResultSet rs) throws SQLException {
        ConfiguracionMantenimiento config = new ConfiguracionMantenimiento();
        
        config.setConfigId(rs.getInt("plan_id"));
        config.setTipoActivo(convertIdToTipoActivo(rs.getInt("tip_act_id")));
        config.setDiasMantenimiento(rs.getInt("plan_frecuencia_dias"));
        config.setDiasAnticipoAlerta(rs.getInt("plan_dias_alerta"));
        config.setActividadesPredefinidas(rs.getString("plan_procedimiento"));
        config.setDescripcionProcedimiento(rs.getString("plan_descripcion"));
        config.setActivo(rs.getBoolean("plan_activo"));
        
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        if (creadoEn != null) {
            config.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            config.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return config;
    }
}
