package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.ConfiguracionAlerta;

/**
 * DAO para la gestión de configuraciones de alertas
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionAlertaDAO {
    
    /**
     * Guarda o actualiza configuración de alerta
     */
    public boolean guardarConfiguracionAlerta(ConfiguracionAlerta config) {
        // Verificar si existe
        ConfiguracionAlerta existente = obtenerPorTipoAlerta(config.getTipoAlerta());
        
        if (existente != null) {
            return actualizarConfiguracionAlerta(config, existente.getAlertaConfigId());
        } else {
            return insertarConfiguracionAlerta(config);
        }
    }
    
    /**
     * Inserta nueva configuración de alerta
     */
    private boolean insertarConfiguracionAlerta(ConfiguracionAlerta config) {
        String sql = "INSERT INTO configuracion_alertas (tipo_alerta, activa, dias_anticipacion, " +
                    "frecuencia_revision, intervalo_periodo_minutos, prioridad_por_defecto, " +
                    "color_indicador, sonido_habilitado, archivo_sonido, mensaje_personalizado, " +
                    "enviar_email, destinatarios_email, plantilla_email, mostrar_en_dashboard, " +
                    "habilitar_notificacion_push) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, config.getTipoAlerta().name());
            stmt.setBoolean(2, config.getActiva());
            stmt.setInt(3, config.getDiasAnticipacion());
            stmt.setString(4, config.getFrecuenciaRevision().name());
            stmt.setObject(5, config.getIntervaloPeriodoMinutos());
            stmt.setString(6, config.getPrioridadPorDefecto().name());
            stmt.setString(7, config.getColorIndicador());
            stmt.setBoolean(8, config.getSonidoHabilitado());
            stmt.setString(9, config.getArchivoSonido());
            stmt.setString(10, config.getMensajePersonalizado());
            stmt.setBoolean(11, config.getEnviarEmail());
            stmt.setString(12, config.getDestinatariosEmail());
            stmt.setString(13, config.getPlantillaEmail());
            stmt.setBoolean(14, config.getMostrarEnDashboard());
            stmt.setBoolean(15, config.getHabilitarNotificacionPush());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        config.setAlertaConfigId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Configuración de alerta insertada: " + config.getTipoAlerta());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar configuración de alerta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualiza configuración de alerta existente
     */
    private boolean actualizarConfiguracionAlerta(ConfiguracionAlerta config, Integer id) {
        String sql = "UPDATE configuracion_alertas SET activa = ?, dias_anticipacion = ?, " +
                    "frecuencia_revision = ?, intervalo_periodo_minutos = ?, prioridad_por_defecto = ?, " +
                    "color_indicador = ?, sonido_habilitado = ?, archivo_sonido = ?, " +
                    "mensaje_personalizado = ?, enviar_email = ?, destinatarios_email = ?, " +
                    "plantilla_email = ?, mostrar_en_dashboard = ?, habilitar_notificacion_push = ?, " +
                    "actualizado_en = CURRENT_TIMESTAMP WHERE alerta_config_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, config.getActiva());
            stmt.setInt(2, config.getDiasAnticipacion());
            stmt.setString(3, config.getFrecuenciaRevision().name());
            stmt.setObject(4, config.getIntervaloPeriodoMinutos());
            stmt.setString(5, config.getPrioridadPorDefecto().name());
            stmt.setString(6, config.getColorIndicador());
            stmt.setBoolean(7, config.getSonidoHabilitado());
            stmt.setString(8, config.getArchivoSonido());
            stmt.setString(9, config.getMensajePersonalizado());
            stmt.setBoolean(10, config.getEnviarEmail());
            stmt.setString(11, config.getDestinatariosEmail());
            stmt.setString(12, config.getPlantillaEmail());
            stmt.setBoolean(13, config.getMostrarEnDashboard());
            stmt.setBoolean(14, config.getHabilitarNotificacionPush());
            stmt.setInt(15, id);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("Configuración de alerta actualizada: " + config.getTipoAlerta());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración de alerta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene configuración de alerta por tipo
     */
    public ConfiguracionAlerta obtenerPorTipoAlerta(ConfiguracionAlerta.TipoAlerta tipo) {
        String sql = "SELECT * FROM configuracion_alertas WHERE tipo_alerta = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfiguracionAlerta(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración de alerta por tipo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las configuraciones de alertas
     */
    public List<ConfiguracionAlerta> obtenerTodasLasConfiguraciones() {
        String sql = "SELECT * FROM configuracion_alertas ORDER BY tipo_alerta";
        
        List<ConfiguracionAlerta> configuraciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                configuraciones.add(mapResultSetToConfiguracionAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las configuraciones de alertas: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Obtiene solo las configuraciones activas
     */
    public List<ConfiguracionAlerta> obtenerConfiguracionesActivas() {
        String sql = "SELECT * FROM configuracion_alertas WHERE activa = true ORDER BY tipo_alerta";
        
        List<ConfiguracionAlerta> configuraciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                configuraciones.add(mapResultSetToConfiguracionAlerta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones activas: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Habilita o deshabilita una configuración de alerta
     */
    public boolean cambiarEstadoAlerta(ConfiguracionAlerta.TipoAlerta tipo, boolean activa) {
        String sql = "UPDATE configuracion_alertas SET activa = ?, actualizado_en = CURRENT_TIMESTAMP " +
                    "WHERE tipo_alerta = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, activa);
            stmt.setString(2, tipo.name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Estado de alerta " + tipo + " cambiado a: " + (activa ? "Activa" : "Inactiva"));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de alerta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Elimina configuración de alerta
     */
    public boolean eliminarConfiguracionAlerta(ConfiguracionAlerta.TipoAlerta tipo) {
        String sql = "DELETE FROM configuracion_alertas WHERE tipo_alerta = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Configuración de alerta eliminada: " + tipo);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar configuración de alerta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Inicializa configuraciones de alertas por defecto
     */
    public void inicializarConfiguracionesAlertasPorDefecto() {
        System.out.println("Inicializando configuraciones de alertas por defecto...");
        
        // Crear configuraciones para cada tipo de alerta
        for (ConfiguracionAlerta.TipoAlerta tipo : ConfiguracionAlerta.TipoAlerta.values()) {
            ConfiguracionAlerta existente = obtenerPorTipoAlerta(tipo);
            if (existente == null) {
                ConfiguracionAlerta nueva = new ConfiguracionAlerta(tipo);
                // Personalizar algunos valores según el tipo
                personalizarConfiguracionPorTipo(nueva, tipo);
                guardarConfiguracionAlerta(nueva);
            }
        }
        
        System.out.println("Configuraciones de alertas por defecto inicializadas correctamente");
    }
    
    /**
     * Personaliza la configuración según el tipo de alerta
     */
    private void personalizarConfiguracionPorTipo(ConfiguracionAlerta config, ConfiguracionAlerta.TipoAlerta tipo) {
        switch (tipo) {
            case MANTENIMIENTO_PREVENTIVO:
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py,tecnicos@ypacarai.coop.py");
                config.setPlantillaEmail("Mantenimiento preventivo programado para {ACTIVO} en {DIAS} días");
                break;
                
            case MANTENIMIENTO_CORRECTIVO:
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py");
                config.setPlantillaEmail("URGENTE: Mantenimiento correctivo requerido para {ACTIVO}");
                config.setSonidoHabilitado(true);
                break;
                
            case TRASLADO_VENCIDO:
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py,gerencia@ypacarai.coop.py");
                config.setPlantillaEmail("CRÍTICO: Traslado vencido - {ACTIVO} debe ser devuelto");
                config.setSonidoHabilitado(true);
                break;
                
            case ACTIVO_FUERA_SERVICIO:
                config.setFrecuenciaRevision(ConfiguracionAlerta.FrecuenciaRevision.CADA_2_HORAS);
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py");
                config.setPlantillaEmail("Activo fuera de servicio: {ACTIVO} requiere atención inmediata");
                break;
                
            case TICKET_VENCIDO:
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py");
                config.setPlantillaEmail("Ticket vencido sin resolver: {TICKET} para {ACTIVO}");
                break;
                
            case SISTEMA_GENERAL:
                config.setDestinatariosEmail("jefe.informatica@ypacarai.coop.py");
                config.setPlantillaEmail("Notificación del sistema: {MENSAJE}");
                config.setPrioridadPorDefecto(ConfiguracionAlerta.NivelPrioridad.BAJA);
                break;
        }
    }
    
    /**
     * Obtiene configuración de alerta con valores por defecto si no existe
     */
    public ConfiguracionAlerta obtenerOCrearPorTipo(ConfiguracionAlerta.TipoAlerta tipo) {
        ConfiguracionAlerta config = obtenerPorTipoAlerta(tipo);
        if (config == null) {
            config = new ConfiguracionAlerta(tipo);
            personalizarConfiguracionPorTipo(config, tipo);
            guardarConfiguracionAlerta(config);
        }
        return config;
    }
    
    /**
     * Obtiene estadísticas de configuraciones de alertas
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection()) {
            
            // Total de configuraciones
            String sqlTotal = "SELECT COUNT(*) as total FROM configuracion_alertas";
            try (PreparedStatement stmt = conn.prepareStatement(sqlTotal);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("total", rs.getInt("total"));
                }
            }
            
            // Configuraciones activas
            String sqlActivas = "SELECT COUNT(*) as activas FROM configuracion_alertas WHERE activa = true";
            try (PreparedStatement stmt = conn.prepareStatement(sqlActivas);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("activas", rs.getInt("activas"));
                }
            }
            
            // Con sonido habilitado
            String sqlSonido = "SELECT COUNT(*) as con_sonido FROM configuracion_alertas " +
                              "WHERE sonido_habilitado = true AND activa = true";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSonido);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("con_sonido", rs.getInt("con_sonido"));
                }
            }
            
            // Con envío de email
            String sqlEmail = "SELECT COUNT(*) as con_email FROM configuracion_alertas " +
                             "WHERE enviar_email = true AND activa = true";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEmail);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("con_email", rs.getInt("con_email"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas de alertas: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Mapea ResultSet a ConfiguracionAlerta
     */
    private ConfiguracionAlerta mapResultSetToConfiguracionAlerta(ResultSet rs) throws SQLException {
        ConfiguracionAlerta config = new ConfiguracionAlerta();
        
        config.setAlertaConfigId(rs.getInt("alerta_config_id"));
        config.setTipoAlerta(ConfiguracionAlerta.TipoAlerta.valueOf(rs.getString("tipo_alerta")));
        config.setActiva(rs.getBoolean("activa"));
        config.setDiasAnticipacion(rs.getInt("dias_anticipacion"));
        config.setFrecuenciaRevision(ConfiguracionAlerta.FrecuenciaRevision.valueOf(rs.getString("frecuencia_revision")));
        config.setIntervaloPeriodoMinutos(rs.getObject("intervalo_periodo_minutos", Integer.class));
        config.setPrioridadPorDefecto(ConfiguracionAlerta.NivelPrioridad.valueOf(rs.getString("prioridad_por_defecto")));
        config.setColorIndicador(rs.getString("color_indicador"));
        config.setSonidoHabilitado(rs.getBoolean("sonido_habilitado"));
        config.setArchivoSonido(rs.getString("archivo_sonido"));
        config.setMensajePersonalizado(rs.getString("mensaje_personalizado"));
        config.setEnviarEmail(rs.getBoolean("enviar_email"));
        config.setDestinatariosEmail(rs.getString("destinatarios_email"));
        config.setPlantillaEmail(rs.getString("plantilla_email"));
        config.setMostrarEnDashboard(rs.getBoolean("mostrar_en_dashboard"));
        config.setHabilitarNotificacionPush(rs.getBoolean("habilitar_notificacion_push"));
        
        // Timestamps
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
