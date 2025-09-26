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
public class ConfiguracionMantenimientoDAOFixed {
    
    /**
     * Guarda una nueva configuración de mantenimiento
     */
    public boolean save(ConfiguracionMantenimiento config) {
        String sql = "INSERT INTO configuracion_mantenimiento (tipo_activo, dias_mantenimiento, " +
                    "dias_anticipo_alerta, tecnico_default_id, actividades_predefinidas, " +
                    "descripcion_procedimiento, activo, creado_en, actualizado_en) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, config.getTipoActivo().name());
            stmt.setInt(2, config.getDiasMantenimiento());
            stmt.setInt(3, config.getDiasAnticipoAlerta());
            stmt.setObject(4, config.getTecnicoDefaultId());
            stmt.setString(5, config.getActividadesPredefinidas());
            stmt.setString(6, config.getDescripcionProcedimiento());
            stmt.setBoolean(7, config.getActivo());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            
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
        String sql = "SELECT * FROM configuracion_mantenimiento WHERE config_id = ?";
        
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
        String sql = "SELECT * FROM configuracion_mantenimiento WHERE tipo_activo = ? AND activo = true";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipoActivo.name());
            
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
        String sql = "SELECT * FROM configuracion_mantenimiento ORDER BY tipo_activo";
        
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
     * Crea configuraciones por defecto si no existen
     */
    public void crearConfiguracionesPorDefecto() {
        // Verificar si ya existen configuraciones
        if (!findAll().isEmpty()) {
            return;
        }
        
        ConfiguracionMantenimiento[] configsDefault = {
            new ConfiguracionMantenimiento(TipoActivo.PC_Escritorio, 90, 7),
            new ConfiguracionMantenimiento(TipoActivo.Laptop, 60, 5),
            new ConfiguracionMantenimiento(TipoActivo.Impresora_Laser, 120, 10),
            new ConfiguracionMantenimiento(TipoActivo.Monitor, 180, 14),
            new ConfiguracionMantenimiento(TipoActivo.Switch_Red, 180, 14)
        };
        
        for (ConfiguracionMantenimiento config : configsDefault) {
            save(config);
        }
        
        System.out.println("✅ Configuraciones por defecto creadas");
    }
    
    /**
     * Mapea un ResultSet a ConfiguracionMantenimiento
     */
    private ConfiguracionMantenimiento mapResultSetToConfig(ResultSet rs) throws SQLException {
        ConfiguracionMantenimiento config = new ConfiguracionMantenimiento();
        
        config.setConfigId(rs.getInt("config_id"));
        config.setTipoActivo(TipoActivo.valueOf(rs.getString("tipo_activo")));
        config.setDiasMantenimiento(rs.getInt("dias_mantenimiento"));
        config.setDiasAnticipoAlerta(rs.getInt("dias_anticipo_alerta"));
        
        Integer tecnicoId = rs.getInt("tecnico_default_id");
        if (!rs.wasNull()) {
            config.setTecnicoDefaultId(tecnicoId);
        }
        
        config.setActividadesPredefinidas(rs.getString("actividades_predefinidas"));
        config.setDescripcionProcedimiento(rs.getString("descripcion_procedimiento"));
        config.setActivo(rs.getBoolean("activo"));
        
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
