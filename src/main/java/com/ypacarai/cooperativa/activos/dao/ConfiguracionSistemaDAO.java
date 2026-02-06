package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.ConfiguracionSistema;

/**
 * DAO para la gestión de configuraciones del sistema
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionSistemaDAO {
    
    /**
     * Crea o actualiza una configuración del sistema
     */
    public boolean guardarConfiguracion(ConfiguracionSistema config) {
        // Primero verificar si existe
        ConfiguracionSistema existente = obtenerPorClave(config.getConfClave());
        
        if (existente != null) {
            return actualizarConfiguracion(config);
        } else {
            return insertarConfiguracion(config);
        }
    }
    
    /**
     * Inserta nueva configuración
     */
    private boolean insertarConfiguracion(ConfiguracionSistema config) {
        String sql = "INSERT INTO configuracion_sistema (conf_clave, conf_valor, conf_descripcion, " +
                    "conf_tipo, conf_categoria, conf_valor_defecto, conf_obligatoria, conf_activa, " +
                    "conf_validacion, conf_opciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, config.getConfClave());
            stmt.setString(2, config.getConfValor());
            stmt.setString(3, config.getConfDescripcion());
            stmt.setString(4, config.getConfTipo().name());
            stmt.setString(5, config.getConfCategoria().name());
            stmt.setString(6, config.getConfValorDefecto());
            stmt.setBoolean(7, config.getConfObligatoria());
            stmt.setBoolean(8, config.getConfActiva());
            stmt.setString(9, config.getConfValidacion());
            stmt.setString(10, config.getConfOpciones());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        config.setConfId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Configuración insertada: " + config.getConfClave());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar configuración: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualiza configuración existente
     */
    private boolean actualizarConfiguracion(ConfiguracionSistema config) {
        String sql = "UPDATE configuracion_sistema SET conf_valor = ?, conf_descripcion = ?, " +
                    "conf_tipo = ?, conf_categoria = ?, conf_valor_defecto = ?, " +
                    "conf_obligatoria = ?, conf_activa = ?, conf_validacion = ?, " +
                    "conf_opciones = ?, actualizado_en = CURRENT_TIMESTAMP " +
                    "WHERE conf_clave = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, config.getConfValor());
            stmt.setString(2, config.getConfDescripcion());
            stmt.setString(3, config.getConfTipo().name());
            stmt.setString(4, config.getConfCategoria().name());
            stmt.setString(5, config.getConfValorDefecto());
            stmt.setBoolean(6, config.getConfObligatoria());
            stmt.setBoolean(7, config.getConfActiva());
            stmt.setString(8, config.getConfValidacion());
            stmt.setString(9, config.getConfOpciones());
            stmt.setString(10, config.getConfClave());
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("Configuración actualizada: " + config.getConfClave());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene configuración por clave
     */
    public ConfiguracionSistema obtenerPorClave(String clave) {
        String sql = "SELECT * FROM configuracion_sistema WHERE conf_clave = ? AND conf_activa = true";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, clave);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ConfiguracionSistema config = mapResultSetToConfiguracion(rs);
                    return config; // Puede ser null si la configuración es obsoleta
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración por clave: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las configuraciones de una categoría
     */
    public List<ConfiguracionSistema> obtenerPorCategoria(ConfiguracionSistema.CategoriaParametro categoria) {
        String sql = "SELECT * FROM configuracion_sistema WHERE conf_categoria = ? AND conf_activa = true " +
                    "ORDER BY conf_clave";
        
        List<ConfiguracionSistema> configuraciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConfiguracionSistema config = mapResultSetToConfiguracion(rs);
                    if (config != null) { // Ignorar configuraciones obsoletas
                        configuraciones.add(config);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones por categoría: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Obtiene todas las configuraciones activas
     */
    public List<ConfiguracionSistema> obtenerTodasActivas() {
        String sql = "SELECT * FROM configuracion_sistema WHERE conf_activa = true " +
                    "ORDER BY conf_categoria, conf_clave";
        
        List<ConfiguracionSistema> configuraciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ConfiguracionSistema config = mapResultSetToConfiguracion(rs);
                if (config != null) { // Ignorar configuraciones obsoletas
                    configuraciones.add(config);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las configuraciones: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Obtiene configuraciones agrupadas por categoría
     */
    public Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> obtenerAgrupadasPorCategoria() {
        Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> mapa = new LinkedHashMap<>();
        
        // Inicializar todas las categorías
        for (ConfiguracionSistema.CategoriaParametro categoria : ConfiguracionSistema.CategoriaParametro.values()) {
            mapa.put(categoria, new ArrayList<>());
        }
        
        List<ConfiguracionSistema> todas = obtenerTodasActivas();
        for (ConfiguracionSistema config : todas) {
            mapa.get(config.getConfCategoria()).add(config);
        }
        
        return mapa;
    }
    
    /**
     * Actualiza solo el valor de una configuración
     */
    public boolean actualizarValor(String clave, String nuevoValor) {
        String sql = "UPDATE configuracion_sistema SET conf_valor = ?, actualizado_en = CURRENT_TIMESTAMP " +
                    "WHERE conf_clave = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoValor);
            stmt.setString(2, clave);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Valor actualizado para configuración: " + clave + " = " + nuevoValor);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar valor de configuración: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Restaura una configuración a su valor por defecto
     */
    public boolean restaurarValorDefecto(String clave) {
        String sql = "UPDATE configuracion_sistema SET conf_valor = conf_valor_defecto, " +
                    "actualizado_en = CURRENT_TIMESTAMP WHERE conf_clave = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, clave);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Configuración restaurada a valor por defecto: " + clave);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al restaurar configuración: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Elimina una configuración (marca como inactiva)
     */
    public boolean eliminarConfiguracion(String clave) {
        String sql = "UPDATE configuracion_sistema SET conf_activa = false, " +
                    "actualizado_en = CURRENT_TIMESTAMP WHERE conf_clave = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, clave);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Configuración eliminada: " + clave);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar configuración: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Inicializa las configuraciones por defecto del sistema
     */
    public void inicializarConfiguracionesPorDefecto() {
        System.out.println("Inicializando configuraciones por defecto del sistema...");
        
        // Configuraciones Generales
        crearConfiguracionSiNoExiste("sistema.nombre", "Sistema de Gestión de Activos", 
            "Nombre del sistema", ConfiguracionSistema.TipoParametro.TEXTO, 
            ConfiguracionSistema.CategoriaParametro.GENERAL, true);
        
        crearConfiguracionSiNoExiste("sistema.version", "1.0.0", 
            "Versión actual del sistema", ConfiguracionSistema.TipoParametro.TEXTO, 
            ConfiguracionSistema.CategoriaParametro.GENERAL, false);
        
        crearConfiguracionSiNoExiste("sistema.organizacion", "Cooperativa Ypacaraí LTDA", 
            "Nombre de la organización", ConfiguracionSistema.TipoParametro.TEXTO, 
            ConfiguracionSistema.CategoriaParametro.GENERAL, true);
        
        // Configuraciones de Mantenimiento
        crearConfiguracionSiNoExiste("mantenimiento.dias_anticipacion_default", "7", 
            "Días de anticipación por defecto para alertas de mantenimiento", 
            ConfiguracionSistema.TipoParametro.NUMERO, 
            ConfiguracionSistema.CategoriaParametro.MANTENIMIENTO, true);
        
        crearConfiguracionSiNoExiste("mantenimiento.periodicidad_computadoras", "90", 
            "Días entre mantenimientos preventivos para computadoras", 
            ConfiguracionSistema.TipoParametro.NUMERO, 
            ConfiguracionSistema.CategoriaParametro.MANTENIMIENTO, true);
        
        crearConfiguracionSiNoExiste("mantenimiento.periodicidad_impresoras", "30", 
            "Días entre mantenimientos preventivos para impresoras", 
            ConfiguracionSistema.TipoParametro.NUMERO, 
            ConfiguracionSistema.CategoriaParametro.MANTENIMIENTO, true);
        
        crearConfiguracionSiNoExiste("mantenimiento.periodicidad_servidores", "60", 
            "Días entre mantenimientos preventivos para servidores", 
            ConfiguracionSistema.TipoParametro.NUMERO, 
            ConfiguracionSistema.CategoriaParametro.MANTENIMIENTO, true);
        
        // Configuraciones de Alertas
        crearConfiguracionSiNoExiste("alertas.sonido_habilitado", "false", 
            "Habilitar sonidos para alertas críticas", ConfiguracionSistema.TipoParametro.BOOLEAN, 
            ConfiguracionSistema.CategoriaParametro.ALERTAS, false);
        
        crearConfiguracionSiNoExiste("alertas.frecuencia_revision", "diaria", 
            "Frecuencia de revisión automática de alertas", ConfiguracionSistema.TipoParametro.TEXTO, 
            ConfiguracionSistema.CategoriaParametro.ALERTAS, true);
        
        // Configuraciones de Email
        crearConfiguracionSiNoExiste("email.servidor_smtp", "mail.ypacarai.coop.py", 
            "Servidor SMTP para envío de correos", ConfiguracionSistema.TipoParametro.TEXTO, 
            ConfiguracionSistema.CategoriaParametro.EMAIL, true);
        
        crearConfiguracionSiNoExiste("email.puerto_smtp", "587", 
            "Puerto SMTP", ConfiguracionSistema.TipoParametro.NUMERO, 
            ConfiguracionSistema.CategoriaParametro.EMAIL, true);
        
        crearConfiguracionSiNoExiste("email.usuario_sistema", "sistema.activos@ypacarai.coop.py", 
            "Usuario de correo del sistema", ConfiguracionSistema.TipoParametro.EMAIL, 
            ConfiguracionSistema.CategoriaParametro.EMAIL, true);
        
        crearConfiguracionSiNoExiste("email.jefe_informatica", "jefe.informatica@ypacarai.coop.py", 
            "Correo del jefe de informática", ConfiguracionSistema.TipoParametro.EMAIL, 
            ConfiguracionSistema.CategoriaParametro.EMAIL, true);
        
        System.out.println("Configuraciones por defecto inicializadas correctamente");
    }
    
    /**
     * Limpia configuraciones obsoletas que ya no se utilizan en el sistema
     */
    public void limpiarConfiguracionesObsoletas() {
        System.out.println("Limpiando configuraciones obsoletas...");
        
        // Configuraciones de horarios laborales (no utilizadas)
        eliminarConfiguracion("horarios.inicio_laboral");
        eliminarConfiguracion("horarios.fin_laboral");
        eliminarConfiguracion("horarios.sabado_laboral");
        
        // Configuraciones de colores no utilizadas
        eliminarConfiguracion("alertas.color_critica");
        eliminarConfiguracion("alertas.color_advertencia");
        
        // Configuraciones de sistema duplicadas o innecesarias
        eliminarConfiguracion("sistema.descripcion");
        eliminarConfiguracion("sistema.logo_path");
        
        System.out.println("Limpieza de configuraciones obsoletas completada");
    }
    
    /**
     * Crea una configuración si no existe
     */
    private void crearConfiguracionSiNoExiste(String clave, String valor, String descripcion, 
                                            ConfiguracionSistema.TipoParametro tipo, 
                                            ConfiguracionSistema.CategoriaParametro categoria, 
                                            boolean obligatoria) {
        ConfiguracionSistema existente = obtenerPorClave(clave);
        if (existente == null) {
            ConfiguracionSistema nueva = new ConfiguracionSistema(clave, valor, descripcion, tipo, categoria);
            nueva.setConfObligatoria(obligatoria);
            guardarConfiguracion(nueva);
        }
    }
    
    /**
     * Obtiene estadísticas de configuración
     */
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        
        String sql = "SELECT conf_categoria, COUNT(*) as total FROM configuracion_sistema " +
                    "WHERE conf_activa = true GROUP BY conf_categoria";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                stats.put(rs.getString("conf_categoria"), rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Mapea ResultSet a ConfiguracionSistema
     */
    private ConfiguracionSistema mapResultSetToConfiguracion(ResultSet rs) throws SQLException {
        ConfiguracionSistema config = new ConfiguracionSistema();
        
        config.setConfId(rs.getInt("conf_id"));
        config.setConfClave(rs.getString("conf_clave"));
        config.setConfValor(rs.getString("conf_valor"));
        config.setConfDescripcion(rs.getString("conf_descripcion"));
        
        try {
            config.setConfTipo(ConfiguracionSistema.TipoParametro.valueOf(rs.getString("conf_tipo")));
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo de parámetro obsoleto encontrado: " + rs.getString("conf_tipo"));
            return null; // Ignorar configuración con tipo obsoleto
        }
        
        try {
            config.setConfCategoria(ConfiguracionSistema.CategoriaParametro.valueOf(rs.getString("conf_categoria")));
        } catch (IllegalArgumentException e) {
            System.err.println("Categoría obsoleta encontrada: " + rs.getString("conf_categoria") + " - Ignorando configuración");
            return null; // Ignorar configuración con categoría obsoleta
        }
        
        config.setConfValorDefecto(rs.getString("conf_valor_defecto"));
        config.setConfObligatoria(rs.getBoolean("conf_obligatoria"));
        config.setConfActiva(rs.getBoolean("conf_activa"));
        config.setConfValidacion(rs.getString("conf_validacion"));
        config.setConfOpciones(rs.getString("conf_opciones"));
        
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
