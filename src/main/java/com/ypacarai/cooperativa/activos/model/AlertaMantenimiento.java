package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Alerta de mantenimiento preventivo próximo a vencer
 */
public class AlertaMantenimiento {
    
    public enum TipoAlerta {
        PREVENTIVO_PROXIMO("Mantenimiento Preventivo Próximo"),
        PREVENTIVO_VENCIDO("Mantenimiento Preventivo Vencido"),
        CORRECTIVO_URGENTE("Mantenimiento Correctivo Urgente"),
        ACTIVO_FUERA_SERVICIO("Activo Fuera de Servicio");
        
        private final String descripcion;
        
        TipoAlerta(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum NivelUrgencia {
        INFO("Información", "#17a2b8"),      // Azul
        ADVERTENCIA("Advertencia", "#ffc107"), // Amarillo
        CRITICO("Crítico", "#dc3545"),        // Rojo
        URGENTE("Urgente", "#6f42c1");        // Púrpura
        
        private final String descripcion;
        private final String colorHex;
        
        NivelUrgencia(String descripcion, String colorHex) {
            this.descripcion = descripcion;
            this.colorHex = colorHex;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public String getColorHex() {
            return colorHex;
        }
    }
    
    private Integer alertaId;
    private Integer activoId;
    private TipoAlerta tipoAlerta;
    private NivelUrgencia nivelUrgencia;
    private String titulo;
    private String mensaje;
    private LocalDate fechaVencimiento;
    private Integer diasRestantes;
    private Boolean leida;
    private Boolean activa;
    private Integer usuarioAsignadoId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    
    // Campos adicionales para joins
    private String activoDescripcion;
    private String activoTipo;
    private String usuarioAsignado;
    
    // Constructores
    public AlertaMantenimiento() {
        this.leida = false;
        this.activa = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public AlertaMantenimiento(Integer activoId, TipoAlerta tipoAlerta, 
                              NivelUrgencia nivelUrgencia, String titulo, String mensaje) {
        this();
        this.activoId = activoId;
        this.tipoAlerta = tipoAlerta;
        this.nivelUrgencia = nivelUrgencia;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }
    
    // Getters y Setters
    public Integer getAlertaId() {
        return alertaId;
    }
    
    public void setAlertaId(Integer alertaId) {
        this.alertaId = alertaId;
    }
    
    public Integer getActivoId() {
        return activoId;
    }
    
    public void setActivoId(Integer activoId) {
        this.activoId = activoId;
    }
    
    public TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }
    
    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }
    
    public NivelUrgencia getNivelUrgencia() {
        return nivelUrgencia;
    }
    
    public void setNivelUrgencia(NivelUrgencia nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public Integer getDiasRestantes() {
        return diasRestantes;
    }
    
    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes;
    }
    
    public Boolean getLeida() {
        return leida;
    }
    
    public void setLeida(Boolean leida) {
        this.leida = leida;
        if (leida && fechaLectura == null) {
            this.fechaLectura = LocalDateTime.now();
        }
    }
    
    public Boolean getActiva() {
        return activa;
    }
    
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
    
    public Integer getUsuarioAsignadoId() {
        return usuarioAsignadoId;
    }
    
    public void setUsuarioAsignadoId(Integer usuarioAsignadoId) {
        this.usuarioAsignadoId = usuarioAsignadoId;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }
    
    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }
    
    // Campos adicionales
    public String getActivoDescripcion() {
        return activoDescripcion;
    }
    
    public void setActivoDescripcion(String activoDescripcion) {
        this.activoDescripcion = activoDescripcion;
    }
    
    public String getActivoTipo() {
        return activoTipo;
    }
    
    public void setActivoTipo(String activoTipo) {
        this.activoTipo = activoTipo;
    }
    
    public String getUsuarioAsignado() {
        return usuarioAsignado;
    }
    
    public void setUsuarioAsignado(String usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }
    
    /**
     * Determina si la alerta es crítica basado en días restantes
     */
    public boolean esCritica() {
        return diasRestantes != null && diasRestantes <= 0;
    }
    
    /**
     * Determina si la alerta es urgente (1-3 días)
     */
    public boolean esUrgente() {
        return diasRestantes != null && diasRestantes > 0 && diasRestantes <= 3;
    }
    
    /**
     * Obtiene texto de días restantes formateado
     */
    public String getDiasRestantesTexto() {
        if (diasRestantes == null) return "N/A";
        
        if (diasRestantes < 0) {
            return "Vencido hace " + Math.abs(diasRestantes) + " día(s)";
        } else if (diasRestantes == 0) {
            return "Vence hoy";
        } else {
            return "Vence en " + diasRestantes + " día(s)";
        }
    }
    
    @Override
    public String toString() {
        return titulo + " - " + getDiasRestantesTexto();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AlertaMantenimiento that = (AlertaMantenimiento) obj;
        return alertaId != null && alertaId.equals(that.alertaId);
    }
    
    @Override
    public int hashCode() {
        return alertaId != null ? alertaId.hashCode() : 0;
    }
}
