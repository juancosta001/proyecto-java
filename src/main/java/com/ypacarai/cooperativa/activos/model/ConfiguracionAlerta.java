package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para configuración de alertas del sistema
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionAlerta {
    
    public enum TipoAlerta {
        MANTENIMIENTO_PREVENTIVO("Mantenimiento Preventivo"),
        MANTENIMIENTO_CORRECTIVO("Mantenimiento Correctivo"),
        TRASLADO_VENCIDO("Traslado Vencido"),
        ACTIVO_FUERA_SERVICIO("Activo Fuera de Servicio"),
        TICKET_VENCIDO("Ticket Vencido"),
        SISTEMA_GENERAL("Alerta del Sistema");
        
        private final String descripcion;
        
        TipoAlerta(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum FrecuenciaRevision {
        DIARIA("Diaria"),
        SEMANAL("Semanal"),
        CADA_3_DIAS("Cada 3 días"),
        CADA_2_HORAS("Cada 2 horas"),
        PERSONALIZADA("Personalizada");
        
        private final String descripcion;
        
        FrecuenciaRevision(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum NivelPrioridad {
        BAJA("Baja", "#28a745", "#ffffff"),
        MEDIA("Media", "#ffc107", "#212529"),
        ALTA("Alta", "#fd7e14", "#ffffff"),
        CRITICA("Crítica", "#dc3545", "#ffffff");
        
        private final String nombre;
        private final String colorFondo;
        private final String colorTexto;
        
        NivelPrioridad(String nombre, String colorFondo, String colorTexto) {
            this.nombre = nombre;
            this.colorFondo = colorFondo;
            this.colorTexto = colorTexto;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public String getColorFondo() {
            return colorFondo;
        }
        
        public String getColorTexto() {
            return colorTexto;
        }
    }
    
    private Integer alertaConfigId;
    private TipoAlerta tipoAlerta;
    private Boolean activa;
    private Integer diasAnticipacion;
    private FrecuenciaRevision frecuenciaRevision;
    private Integer intervaloPeriodoMinutos; // Para frecuencia personalizada
    private NivelPrioridad prioridadPorDefecto;
    private String colorIndicador;
    private Boolean sonidoHabilitado;
    private String archivoSonido;
    private String mensajePersonalizado;
    private Boolean enviarEmail;
    private String destinatariosEmail;
    private String plantillaEmail;
    private Boolean mostrarEnDashboard;
    private Boolean habilitarNotificacionPush;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public ConfiguracionAlerta() {
        this.activa = true;
        this.diasAnticipacion = 7;
        this.frecuenciaRevision = FrecuenciaRevision.DIARIA;
        this.prioridadPorDefecto = NivelPrioridad.MEDIA;
        this.sonidoHabilitado = false;
        this.enviarEmail = true;
        this.mostrarEnDashboard = true;
        this.habilitarNotificacionPush = false;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public ConfiguracionAlerta(TipoAlerta tipo) {
        this();
        this.tipoAlerta = tipo;
        this.configurarPorDefectoPorTipo(tipo);
    }
    
    /**
     * Configura valores por defecto según el tipo de alerta
     */
    private void configurarPorDefectoPorTipo(TipoAlerta tipo) {
        switch (tipo) {
            case MANTENIMIENTO_PREVENTIVO:
                this.diasAnticipacion = 7;
                this.prioridadPorDefecto = NivelPrioridad.MEDIA;
                this.colorIndicador = "#ffc107";
                this.mensajePersonalizado = "Mantenimiento preventivo programado en {DIAS} días";
                break;
                
            case MANTENIMIENTO_CORRECTIVO:
                this.diasAnticipacion = 3;
                this.prioridadPorDefecto = NivelPrioridad.ALTA;
                this.colorIndicador = "#fd7e14";
                this.sonidoHabilitado = true;
                this.mensajePersonalizado = "Mantenimiento correctivo requerido urgente";
                break;
                
            case TRASLADO_VENCIDO:
                this.diasAnticipacion = 1;
                this.prioridadPorDefecto = NivelPrioridad.CRITICA;
                this.colorIndicador = "#dc3545";
                this.sonidoHabilitado = true;
                this.mensajePersonalizado = "Traslado vencido - Devolución pendiente";
                break;
                
            case ACTIVO_FUERA_SERVICIO:
                this.diasAnticipacion = 0;
                this.prioridadPorDefecto = NivelPrioridad.ALTA;
                this.colorIndicador = "#dc3545";
                this.frecuenciaRevision = FrecuenciaRevision.CADA_2_HORAS;
                this.mensajePersonalizado = "Activo fuera de servicio requiere atención";
                break;
                
            case TICKET_VENCIDO:
                this.diasAnticipacion = 1;
                this.prioridadPorDefecto = NivelPrioridad.ALTA;
                this.colorIndicador = "#fd7e14";
                this.mensajePersonalizado = "Ticket vencido sin resolver";
                break;
                
            case SISTEMA_GENERAL:
                this.diasAnticipacion = 0;
                this.prioridadPorDefecto = NivelPrioridad.BAJA;
                this.colorIndicador = "#17a2b8";
                this.mensajePersonalizado = "Notificación del sistema";
                break;
        }
    }
    
    // Getters y Setters
    public Integer getAlertaConfigId() {
        return alertaConfigId;
    }
    
    public void setAlertaConfigId(Integer alertaConfigId) {
        this.alertaConfigId = alertaConfigId;
    }
    
    public TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }
    
    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }
    
    public Boolean getActiva() {
        return activa;
    }
    
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
    
    public Integer getDiasAnticipacion() {
        return diasAnticipacion;
    }
    
    public void setDiasAnticipacion(Integer diasAnticipacion) {
        this.diasAnticipacion = diasAnticipacion;
    }
    
    public FrecuenciaRevision getFrecuenciaRevision() {
        return frecuenciaRevision;
    }
    
    public void setFrecuenciaRevision(FrecuenciaRevision frecuenciaRevision) {
        this.frecuenciaRevision = frecuenciaRevision;
    }
    
    public Integer getIntervaloPeriodoMinutos() {
        return intervaloPeriodoMinutos;
    }
    
    public void setIntervaloPeriodoMinutos(Integer intervaloPeriodoMinutos) {
        this.intervaloPeriodoMinutos = intervaloPeriodoMinutos;
    }
    
    public NivelPrioridad getPrioridadPorDefecto() {
        return prioridadPorDefecto;
    }
    
    public void setPrioridadPorDefecto(NivelPrioridad prioridadPorDefecto) {
        this.prioridadPorDefecto = prioridadPorDefecto;
    }
    
    public String getColorIndicador() {
        return colorIndicador;
    }
    
    public void setColorIndicador(String colorIndicador) {
        this.colorIndicador = colorIndicador;
    }
    
    public Boolean getSonidoHabilitado() {
        return sonidoHabilitado;
    }
    
    public void setSonidoHabilitado(Boolean sonidoHabilitado) {
        this.sonidoHabilitado = sonidoHabilitado;
    }
    
    public String getArchivoSonido() {
        return archivoSonido;
    }
    
    public void setArchivoSonido(String archivoSonido) {
        this.archivoSonido = archivoSonido;
    }
    
    public String getMensajePersonalizado() {
        return mensajePersonalizado;
    }
    
    public void setMensajePersonalizado(String mensajePersonalizado) {
        this.mensajePersonalizado = mensajePersonalizado;
    }
    
    public Boolean getEnviarEmail() {
        return enviarEmail;
    }
    
    public void setEnviarEmail(Boolean enviarEmail) {
        this.enviarEmail = enviarEmail;
    }
    
    public String getDestinatariosEmail() {
        return destinatariosEmail;
    }
    
    public void setDestinatariosEmail(String destinatariosEmail) {
        this.destinatariosEmail = destinatariosEmail;
    }
    
    public String getPlantillaEmail() {
        return plantillaEmail;
    }
    
    public void setPlantillaEmail(String plantillaEmail) {
        this.plantillaEmail = plantillaEmail;
    }
    
    public Boolean getMostrarEnDashboard() {
        return mostrarEnDashboard;
    }
    
    public void setMostrarEnDashboard(Boolean mostrarEnDashboard) {
        this.mostrarEnDashboard = mostrarEnDashboard;
    }
    
    public Boolean getHabilitarNotificacionPush() {
        return habilitarNotificacionPush;
    }
    
    public void setHabilitarNotificacionPush(Boolean habilitarNotificacionPush) {
        this.habilitarNotificacionPush = habilitarNotificacionPush;
    }
    
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
    
    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
    
    // Métodos de utilidad
    
    /**
     * Obtiene el intervalo en minutos según la frecuencia
     */
    public int getIntervaloEnMinutos() {
        if (frecuenciaRevision == FrecuenciaRevision.PERSONALIZADA) {
            return intervaloPeriodoMinutos != null ? intervaloPeriodoMinutos : 1440; // Default: 1 día
        }
        
        switch (frecuenciaRevision) {
            case CADA_2_HORAS:
                return 120;
            case CADA_3_DIAS:
                return 4320; // 3 días * 24 horas * 60 minutos
            case SEMANAL:
                return 10080; // 7 días * 24 horas * 60 minutos
            case DIARIA:
            default:
                return 1440; // 24 horas * 60 minutos
        }
    }
    
    /**
     * Valida si la configuración es válida
     */
    public boolean esConfiguracionValida() {
        if (tipoAlerta == null) return false;
        if (diasAnticipacion == null || diasAnticipacion < 0) return false;
        if (frecuenciaRevision == FrecuenciaRevision.PERSONALIZADA && 
            (intervaloPeriodoMinutos == null || intervaloPeriodoMinutos <= 0)) return false;
        
        return true;
    }
    
    /**
     * Genera descripción resumida de la configuración
     */
    public String getDescripcionResumida() {
        StringBuilder desc = new StringBuilder();
        desc.append(tipoAlerta.getDescripcion());
        
        if (diasAnticipacion > 0) {
            desc.append(" (").append(diasAnticipacion).append(" días antes)");
        }
        
        desc.append(" - ").append(frecuenciaRevision.getDescripcion());
        desc.append(" - ").append(prioridadPorDefecto.getNombre());
        
        return desc.toString();
    }
    
    @Override
    public String toString() {
        return tipoAlerta.getDescripcion() + " - " + (activa ? "Activa" : "Inactiva");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ConfiguracionAlerta that = (ConfiguracionAlerta) obj;
        return tipoAlerta == that.tipoAlerta;
    }
    
    @Override
    public int hashCode() {
        return tipoAlerta != null ? tipoAlerta.hashCode() : 0;
    }
}
