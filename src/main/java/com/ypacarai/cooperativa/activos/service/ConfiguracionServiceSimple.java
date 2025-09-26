package com.ypacarai.cooperativa.activos.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio simplificado para gestión de configuraciones del sistema
 * Cooperativa Ypacaraí LTDA
 * 
 * Esta versión simplificada maneja configuraciones en memoria hasta que
 * se implementen completamente las tablas de configuración.
 */
public class ConfiguracionServiceSimple {
    
    // Cache en memoria para configuraciones
    private Map<String, String> configuraciones;
    
    public ConfiguracionServiceSimple() {
        this.configuraciones = new HashMap<>();
        inicializarConfiguracionesPorDefecto();
    }
    
    /**
     * Obtiene valor de configuración como String
     */
    public String obtenerValorConfiguracion(String clave, String valorPorDefecto) {
        return configuraciones.getOrDefault(clave, valorPorDefecto);
    }
    
    /**
     * Obtiene valor de configuración como entero
     */
    public Integer obtenerValorConfiguracionEntero(String clave, Integer valorPorDefecto) {
        String valor = configuraciones.get(clave);
        if (valor != null) {
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                return valorPorDefecto;
            }
        }
        return valorPorDefecto;
    }
    
    /**
     * Obtiene valor de configuración como boolean
     */
    public Boolean obtenerValorConfiguracionBoolean(String clave, Boolean valorPorDefecto) {
        String valor = configuraciones.get(clave);
        if (valor != null) {
            return Boolean.parseBoolean(valor);
        }
        return valorPorDefecto;
    }
    
    /**
     * Obtiene valor de configuración como LocalTime
     */
    public LocalTime obtenerValorConfiguracionTiempo(String clave, LocalTime valorPorDefecto) {
        String valor = configuraciones.get(clave);
        if (valor != null) {
            try {
                return LocalTime.parse(valor);
            } catch (Exception e) {
                return valorPorDefecto;
            }
        }
        return valorPorDefecto;
    }
    
    /**
     * Actualiza valor de una configuración
     */
    public boolean actualizarValorConfiguracion(String clave, String nuevoValor) {
        configuraciones.put(clave, nuevoValor);
        return true;
    }
    
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
    
    /**
     * Obtiene todas las configuraciones como mapa
     */
    public Map<String, Object> obtenerTodasLasConfiguraciones() {
        Map<String, Object> todasConfig = new HashMap<>();
        
        // Configuraciones generales
        Map<String, String> generales = new HashMap<>();
        generales.put("sistema.nombre", obtenerValorConfiguracion("sistema.nombre", "Sistema de Gestión de Activos"));
        generales.put("sistema.version", obtenerValorConfiguracion("sistema.version", "1.0.0"));
        generales.put("sistema.organizacion", obtenerValorConfiguracion("sistema.organizacion", "Cooperativa Ypacaraí LTDA"));
        todasConfig.put("generales", generales);
        
        // Configuraciones de mantenimiento
        Map<String, String> mantenimiento = new HashMap<>();
        mantenimiento.put("dias_anticipacion_default", getDiasAnticipacionMantenimiento().toString());
        mantenimiento.put("periodicidad_pc", getPeriodicidadMantenimientoPorTipo("PC").toString());
        mantenimiento.put("periodicidad_impresora", getPeriodicidadMantenimientoPorTipo("Impresora").toString());
        todasConfig.put("mantenimiento", mantenimiento);
        
        // Configuraciones de horarios
        Map<String, LocalTime> horarios = getHorariosLaborales();
        todasConfig.put("horarios", horarios);
        
        // Configuraciones de alertas
        Map<String, String> alertas = getColoresAlertas();
        alertas.put("sonido_habilitado", sonidosAlertaHabilitados() ? "true" : "false");
        todasConfig.put("alertas", alertas);
        
        // Configuraciones de email
        todasConfig.put("email", getConfiguracionEmail());
        
        return todasConfig;
    }
    
    /**
     * Obtiene estadísticas de configuración
     */
    public Map<String, Object> obtenerEstadisticasConfiguracion() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("total_configuraciones", configuraciones.size());
        estadisticas.put("configuraciones_sistema", 3);
        estadisticas.put("configuraciones_mantenimiento", 4);
        estadisticas.put("configuraciones_alertas", 5);
        estadisticas.put("configuraciones_email", 4);
        estadisticas.put("configuraciones_horarios", 2);
        
        return estadisticas;
    }
    
    /**
     * Guarda configuración (simulado en memoria)
     */
    public boolean guardarConfiguracion(String clave, String valor, String descripcion, String tipo, String categoria) {
        configuraciones.put(clave, valor);
        return true;
    }
    
    /**
     * Simula configuraciones de alertas
     */
    public List<Map<String, Object>> obtenerConfiguracionesAlertas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        
        // Mantenimiento Preventivo
        Map<String, Object> alerta1 = new HashMap<>();
        alerta1.put("tipo", "MANTENIMIENTO_PREVENTIVO");
        alerta1.put("activa", true);
        alerta1.put("dias_anticipacion", 7);
        alerta1.put("frecuencia", "DIARIA");
        alerta1.put("prioridad", "MEDIA");
        alerta1.put("sonido", false);
        alerta1.put("email", true);
        alertas.add(alerta1);
        
        // Mantenimiento Correctivo
        Map<String, Object> alerta2 = new HashMap<>();
        alerta2.put("tipo", "MANTENIMIENTO_CORRECTIVO");
        alerta2.put("activa", true);
        alerta2.put("dias_anticipacion", 3);
        alerta2.put("frecuencia", "CADA_2_HORAS");
        alerta2.put("prioridad", "ALTA");
        alerta2.put("sonido", true);
        alerta2.put("email", true);
        alertas.add(alerta2);
        
        // Traslado Vencido
        Map<String, Object> alerta3 = new HashMap<>();
        alerta3.put("tipo", "TRASLADO_VENCIDO");
        alerta3.put("activa", true);
        alerta3.put("dias_anticipacion", 1);
        alerta3.put("frecuencia", "DIARIA");
        alerta3.put("prioridad", "CRITICA");
        alerta3.put("sonido", true);
        alerta3.put("email", true);
        alertas.add(alerta3);
        
        return alertas;
    }
    
    /**
     * Inicializa configuraciones por defecto en memoria
     */
    private void inicializarConfiguracionesPorDefecto() {
        // Configuraciones generales
        configuraciones.put("sistema.nombre", "Sistema de Gestión de Activos");
        configuraciones.put("sistema.version", "1.0.0");
        configuraciones.put("sistema.organizacion", "Cooperativa Ypacaraí LTDA");
        configuraciones.put("sistema.zona_horaria", "America/Asuncion");
        configuraciones.put("sistema.idioma", "es");
        
        // Configuraciones de mantenimiento
        configuraciones.put("mantenimiento.dias_anticipacion_default", "7");
        configuraciones.put("mantenimiento.periodicidad_pc", "90");
        configuraciones.put("mantenimiento.periodicidad_impresora", "30");
        configuraciones.put("mantenimiento.periodicidad_servidores", "60");
        configuraciones.put("mantenimiento.periodicidad_ups", "120");
        configuraciones.put("mantenimiento.periodicidad_proyectores", "180");
        
        // Configuraciones de horarios
        configuraciones.put("horarios.inicio_laboral", "08:00");
        configuraciones.put("horarios.fin_laboral", "17:00");
        configuraciones.put("horarios.almuerzo_inicio", "12:00");
        configuraciones.put("horarios.almuerzo_fin", "13:00");
        configuraciones.put("horarios.sabado_laboral", "false");
        
        // Configuraciones de alertas
        configuraciones.put("alertas.sonido_habilitado", "false");
        configuraciones.put("alertas.color_critica", "#dc3545");
        configuraciones.put("alertas.color_advertencia", "#ffc107");
        configuraciones.put("alertas.color_info", "#17a2b8");
        configuraciones.put("alertas.color_exito", "#28a745");
        configuraciones.put("alertas.frecuencia_revision", "diaria");
        configuraciones.put("alertas.max_por_dashboard", "10");
        
        // Configuraciones de email
        configuraciones.put("email.servidor_smtp", "mail.ypacarai.coop.py");
        configuraciones.put("email.puerto_smtp", "587");
        configuraciones.put("email.usuario_sistema", "sistema.activos@ypacarai.coop.py");
        configuraciones.put("email.jefe_informatica", "jefe.informatica@ypacarai.coop.py");
        configuraciones.put("email.usar_ssl", "true");
        configuraciones.put("email.timeout_segundos", "30");
        
        // Configuraciones de seguridad
        configuraciones.put("seguridad.intentos_login_max", "3");
        configuraciones.put("seguridad.tiempo_bloqueo_minutos", "15");
        configuraciones.put("seguridad.caducidad_sesion_horas", "8");
        configuraciones.put("seguridad.requiere_cambio_password", "false");
        
        // Configuraciones de reportes
        configuraciones.put("reportes.max_registros_excel", "10000");
        configuraciones.put("reportes.formato_fecha_defecto", "dd/MM/yyyy");
        configuraciones.put("reportes.incluir_logo", "true");
        configuraciones.put("reportes.ruta_temporal", "temp/reportes/");
    }
}
