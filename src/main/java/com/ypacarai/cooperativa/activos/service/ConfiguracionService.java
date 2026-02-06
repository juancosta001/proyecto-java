package com.ypacarai.cooperativa.activos.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ypacarai.cooperativa.activos.dao.ConfiguracionAlertaDAO;
import com.ypacarai.cooperativa.activos.dao.ConfiguracionSistemaDAO;
import com.ypacarai.cooperativa.activos.model.ConfiguracionAlerta;
import com.ypacarai.cooperativa.activos.model.ConfiguracionSistema;

/**
 * Servicio para gestión de configuraciones del sistema
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionService {
    
    private final ConfiguracionSistemaDAO configuracionSistemaDAO;
    private final ConfiguracionAlertaDAO configuracionAlertaDAO;
    
    // Cache para configuraciones frecuentemente accedidas
    private Map<String, ConfiguracionSistema> cacheConfiguraciones;
    private long ultimaActualizacionCache;
    private static final long TIEMPO_CACHE_MS = 300000; // 5 minutos
    
    public ConfiguracionService() {
        this.configuracionSistemaDAO = new ConfiguracionSistemaDAO();
        this.configuracionAlertaDAO = new ConfiguracionAlertaDAO();
        this.cacheConfiguraciones = new HashMap<>();
        this.ultimaActualizacionCache = 0;
        
        // Inicializar configuraciones por defecto si es necesario
        inicializarSiEsNecesario();
    }
    
    // ===== MÉTODOS PARA CONFIGURACIONES GENERALES =====
    
    /**
     * Guarda o actualiza una configuración del sistema
     */
    public boolean guardarConfiguracion(ConfiguracionSistema config) {
        boolean resultado = configuracionSistemaDAO.guardarConfiguracion(config);
        if (resultado) {
            invalidarCache();
        }
        return resultado;
    }
    
    /**
     * Obtiene configuración por clave con cache
     */
    public ConfiguracionSistema obtenerConfiguracion(String clave) {
        actualizarCacheSiEsNecesario();
        return cacheConfiguraciones.get(clave);
    }
    
    /**
     * Obtiene valor de configuración como String
     */
    public String obtenerValorConfiguracion(String clave, String valorPorDefecto) {
        ConfiguracionSistema config = obtenerConfiguracion(clave);
        return config != null ? config.getConfValor() : valorPorDefecto;
    }
    
    /**
     * Obtiene valor de configuración como entero
     */
    public Integer obtenerValorConfiguracionEntero(String clave, Integer valorPorDefecto) {
        ConfiguracionSistema config = obtenerConfiguracion(clave);
        if (config != null) {
            Integer valor = config.getValorComoEntero();
            return valor != null ? valor : valorPorDefecto;
        }
        return valorPorDefecto;
    }
    
    /**
     * Obtiene valor de configuración como boolean
     */
    public Boolean obtenerValorConfiguracionBoolean(String clave, Boolean valorPorDefecto) {
        ConfiguracionSistema config = obtenerConfiguracion(clave);
        return config != null ? config.getValorComoBoolean() : valorPorDefecto;
    }
    
    /**
     * Obtiene valor de configuración como LocalTime
     */
    public LocalTime obtenerValorConfiguracionTiempo(String clave, LocalTime valorPorDefecto) {
        ConfiguracionSistema config = obtenerConfiguracion(clave);
        if (config != null) {
            LocalTime valor = config.getValorComoTiempo();
            return valor != null ? valor : valorPorDefecto;
        }
        return valorPorDefecto;
    }
    
    /**
     * Actualiza solo el valor de una configuración
     */
    public boolean actualizarValorConfiguracion(String clave, String nuevoValor) {
        boolean resultado = configuracionSistemaDAO.actualizarValor(clave, nuevoValor);
        if (resultado) {
            invalidarCache();
        }
        return resultado;
    }
    
    /**
     * Obtiene configuraciones agrupadas por categoría
     */
    public Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> 
            obtenerConfiguracionesAgrupadasPorCategoria() {
        return configuracionSistemaDAO.obtenerAgrupadasPorCategoria();
    }
    
    /**
     * Obtiene configuraciones de una categoría específica
     */
    public List<ConfiguracionSistema> obtenerConfiguracionesPorCategoria(
            ConfiguracionSistema.CategoriaParametro categoria) {
        return configuracionSistemaDAO.obtenerPorCategoria(categoria);
    }
    
    /**
     * Restaura configuración a valor por defecto
     */
    public boolean restaurarConfiguracionPorDefecto(String clave) {
        boolean resultado = configuracionSistemaDAO.restaurarValorDefecto(clave);
        if (resultado) {
            invalidarCache();
        }
        return resultado;
    }
    
    // ===== MÉTODOS PARA CONFIGURACIONES DE ALERTAS =====
    
    /**
     * Guarda configuración de alerta
     */
    public boolean guardarConfiguracionAlerta(ConfiguracionAlerta config) {
        return configuracionAlertaDAO.guardarConfiguracionAlerta(config);
    }
    
    /**
     * Obtiene configuración de alerta por tipo
     */
    public ConfiguracionAlerta obtenerConfiguracionAlerta(ConfiguracionAlerta.TipoAlerta tipo) {
        return configuracionAlertaDAO.obtenerOCrearPorTipo(tipo);
    }
    
    /**
     * Obtiene todas las configuraciones de alertas
     */
    public List<ConfiguracionAlerta> obtenerTodasLasConfiguracionesAlertas() {
        return configuracionAlertaDAO.obtenerTodasLasConfiguraciones();
    }
    
    /**
     * Método alias para compatibilidad
     */
    public List<ConfiguracionAlerta> obtenerConfiguracionesAlerta() {
        return obtenerTodasLasConfiguracionesAlertas();
    }
    
    /**
     * Obtiene solo configuraciones de alertas activas
     */
    public List<ConfiguracionAlerta> obtenerConfiguracionesAlertasActivas() {
        return configuracionAlertaDAO.obtenerConfiguracionesActivas();
    }
    
    /**
     * Habilita o deshabilita una alerta
     */
    public boolean cambiarEstadoAlerta(ConfiguracionAlerta.TipoAlerta tipo, boolean activa) {
        return configuracionAlertaDAO.cambiarEstadoAlerta(tipo, activa);
    }
    
    // ===== MÉTODOS ESPECÍFICOS PARA CONFIGURACIONES COMUNES =====
    
    /**
     * Obtiene días de anticipación para mantenimiento por defecto
     */
    public Integer getDiasAnticipacionMantenimiento() {
        return obtenerValorConfiguracionEntero("mantenimiento.dias_anticipacion_default", 7);
    }
    
    /**
     * Obtiene periodicidad de mantenimiento por tipo de activo
     */
    public Integer getPeriodicidadMantenimientoPorTipo(String tipoActivo) {
        String clave = "mantenimiento.periodicidad_" + tipoActivo.toLowerCase();
        return obtenerValorConfiguracionEntero(clave, 90); // Default 90 días
    }
    
    /**
     * Obtiene horarios laborales
     */
    public Map<String, LocalTime> getHorariosLaborales() {
        Map<String, LocalTime> horarios = new HashMap<>();
        horarios.put("inicio", obtenerValorConfiguracionTiempo("horarios.inicio_laboral", LocalTime.of(8, 0)));
        horarios.put("fin", obtenerValorConfiguracionTiempo("horarios.fin_laboral", LocalTime.of(17, 0)));
        return horarios;
    }
    
    /**
     * Verifica si está en horario laboral
     */
    public boolean estaEnHorarioLaboral() {
        Map<String, LocalTime> horarios = getHorariosLaborales();
        LocalTime ahora = LocalTime.now();
        return !ahora.isBefore(horarios.get("inicio")) && !ahora.isAfter(horarios.get("fin"));
    }
    
    /**
     * Obtiene configuración de email del sistema
     */
    public Map<String, String> getConfiguracionEmail() {
        Map<String, String> emailConfig = new HashMap<>();
        emailConfig.put("servidor", obtenerValorConfiguracion("email.servidor_smtp", "mail.ypacarai.coop.py"));
        emailConfig.put("puerto", obtenerValorConfiguracion("email.puerto_smtp", "587"));
        emailConfig.put("usuario", obtenerValorConfiguracion("email.usuario_sistema", "sistema.activos@ypacarai.coop.py"));
        emailConfig.put("jefe", obtenerValorConfiguracion("email.jefe_informatica", "jefe.informatica@ypacarai.coop.py"));
        return emailConfig;
    }
    
    /**
     * Obtiene configuración de colores para alertas
     */
    public Map<String, String> getColoresAlertas() {
        Map<String, String> colores = new HashMap<>();
        colores.put("critica", obtenerValorConfiguracion("alertas.color_critica", "#dc3545"));
        colores.put("advertencia", obtenerValorConfiguracion("alertas.color_advertencia", "#ffc107"));
        colores.put("info", obtenerValorConfiguracion("alertas.color_info", "#17a2b8"));
        colores.put("exito", obtenerValorConfiguracion("alertas.color_exito", "#28a745"));
        return colores;
    }
    
    /**
     * Verifica si los sonidos de alerta están habilitados
     */
    public boolean sonidosAlertaHabilitados() {
        return obtenerValorConfiguracionBoolean("alertas.sonido_habilitado", false);
    }
    
    // ===== MÉTODOS DE UTILIDAD Y ESTADÍSTICAS =====
    
    /**
     * Obtiene estadísticas generales de configuración
     */
    public Map<String, Object> obtenerEstadisticasConfiguracion() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Estadísticas de configuraciones generales
        Map<String, Integer> statsGenerales = configuracionSistemaDAO.obtenerEstadisticas();
        estadisticas.put("configuraciones_generales", statsGenerales);
        
        // Estadísticas de configuraciones de alertas
        Map<String, Object> statsAlertas = configuracionAlertaDAO.obtenerEstadisticas();
        estadisticas.put("configuraciones_alertas", statsAlertas);
        
        // Total general
        int totalGeneral = statsGenerales.values().stream().mapToInt(Integer::intValue).sum();
        int totalAlertas = (Integer) statsAlertas.getOrDefault("total", 0);
        estadisticas.put("total_configuraciones", totalGeneral + totalAlertas);
        
        return estadisticas;
    }
    
    /**
     * Valida todas las configuraciones del sistema
     */
    public Map<String, List<String>> validarConfiguraciones() {
        Map<String, List<String>> errores = new HashMap<>();
        
        // Validar configuraciones generales
        List<ConfiguracionSistema> configuraciones = configuracionSistemaDAO.obtenerTodasActivas();
        for (ConfiguracionSistema config : configuraciones) {
            if (!config.esValorValido()) {
                errores.computeIfAbsent("configuraciones_generales", k -> new ArrayList<>())
                       .add("Configuración inválida: " + config.getConfClave() + " = " + config.getConfValor());
            }
        }
        
        // Validar configuraciones de alertas
        List<ConfiguracionAlerta> alertas = configuracionAlertaDAO.obtenerTodasLasConfiguraciones();
        for (ConfiguracionAlerta alerta : alertas) {
            if (!alerta.esConfiguracionValida()) {
                errores.computeIfAbsent("configuraciones_alertas", k -> new ArrayList<>())
                       .add("Configuración de alerta inválida: " + alerta.getTipoAlerta());
            }
        }
        
        return errores;
    }
    
    /**
     * Restaura todas las configuraciones a valores por defecto
     */
    public boolean restaurarTodasConfiguracionesPorDefecto() {
        try {
            // Reinicializar configuraciones generales
            configuracionSistemaDAO.inicializarConfiguracionesPorDefecto();
            
            // Reinicializar configuraciones de alertas
            configuracionAlertaDAO.inicializarConfiguracionesAlertasPorDefecto();
            
            invalidarCache();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al restaurar configuraciones por defecto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporta todas las configuraciones a un mapa para respaldo
     */
    public Map<String, Object> exportarConfiguraciones() {
        Map<String, Object> exportacion = new HashMap<>();
        
        // Exportar configuraciones generales
        Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> generales = 
            obtenerConfiguracionesAgrupadasPorCategoria();
        exportacion.put("configuraciones_generales", generales);
        
        // Exportar configuraciones de alertas
        List<ConfiguracionAlerta> alertas = obtenerTodasLasConfiguracionesAlertas();
        exportacion.put("configuraciones_alertas", alertas);
        
        // Metadatos
        exportacion.put("fecha_exportacion", new Date());
        exportacion.put("version_sistema", obtenerValorConfiguracion("sistema.version", "1.0.0"));
        
        return exportacion;
    }
    
    // ===== MÉTODOS PRIVADOS PARA GESTIÓN DE CACHE =====
    
    /**
     * Invalida el cache de configuraciones
     */
    private void invalidarCache() {
        cacheConfiguraciones.clear();
        ultimaActualizacionCache = 0;
    }
    
    /**
     * Actualiza el cache si es necesario
     */
    private void actualizarCacheSiEsNecesario() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimaActualizacionCache > TIEMPO_CACHE_MS || cacheConfiguraciones.isEmpty()) {
            actualizarCache();
            ultimaActualizacionCache = ahora;
        }
    }
    
    /**
     * Actualiza el cache con todas las configuraciones
     */
    private void actualizarCache() {
        List<ConfiguracionSistema> configuraciones = configuracionSistemaDAO.obtenerTodasActivas();
        cacheConfiguraciones.clear();
        
        for (ConfiguracionSistema config : configuraciones) {
            cacheConfiguraciones.put(config.getConfClave(), config);
        }
        
        System.out.println("Cache de configuraciones actualizado: " + cacheConfiguraciones.size() + " configuraciones");
    }
    
    /**
     * Fuerza la recarga de todas las configuraciones desde la base de datos
     * Útil para el SchedulerService y otros procesos automáticos
     */
    public void recargarConfiguraciones() {
        System.out.println("🔄 [SCHEDULER] Recargando configuraciones desde base de datos...");
        invalidarCache();
        actualizarCacheSiEsNecesario();
        System.out.println("✅ [SCHEDULER] Configuraciones recargadas exitosamente");
    }
    
    /**
     * Limpia configuraciones obsoletas que ya no se utilizan en el sistema
     */
    public boolean limpiarConfiguracionesObsoletas() {
        System.out.println("🧽 Iniciando limpieza de configuraciones obsoletas...");
        
        try {
            configuracionSistemaDAO.limpiarConfiguracionesObsoletas();
            invalidarCache(); // Forzar recarga del cache
            System.out.println("✅ Limpieza de configuraciones completada exitosamente");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error durante la limpieza de configuraciones: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicializa configuraciones por defecto si es necesario
     */
    private void inicializarSiEsNecesario() {
        try {
            // Verificar si existen configuraciones básicas
            if (obtenerConfiguracion("sistema.nombre") == null) {
                System.out.println("Inicializando configuraciones por defecto...");
                configuracionSistemaDAO.inicializarConfiguracionesPorDefecto();
                configuracionAlertaDAO.inicializarConfiguracionesAlertasPorDefecto();
                invalidarCache();
            }
        } catch (Exception e) {
            System.err.println("Error al verificar configuraciones iniciales: " + e.getMessage());
        }
    }
}
