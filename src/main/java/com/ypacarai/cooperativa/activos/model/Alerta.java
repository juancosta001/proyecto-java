package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa las alertas automáticas del sistema
 */
public class Alerta {
    
    public enum TipoAlerta {
        Mantenimiento_Proximo, Mantenimiento_Vencido, Traslado_Vencido
    }
    
    public enum PrioridadAlerta {
        Info, Advertencia, Critica
    }
    
    public enum EstadoAlerta {
        Pendiente, Enviada, Atendida, Cancelada
    }
    
    private Integer aleId;
    private Integer actId; // Activo relacionado
    private TipoAlerta aleTipo;
    private String aleTitulo;
    private String aleMensaje;
    private LocalDate aleFechaObjetivo; // Fecha del mantenimiento/devolución
    private LocalDateTime aleFechaAlerta; // Cuando se debe mostrar la alerta
    private PrioridadAlerta alePrioridad;
    private EstadoAlerta aleEstado;
    private Boolean aleEmailEnviado;
    private LocalDateTime aleFechaEnvio;
    private Integer referenciaId; // ID del mantenimiento o traslado relacionado
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public Alerta() {
        this.alePrioridad = PrioridadAlerta.Info;
        this.aleEstado = EstadoAlerta.Pendiente;
        this.aleEmailEnviado = false;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public Alerta(Integer actId, TipoAlerta tipo, String titulo, String mensaje, LocalDate fechaObjetivo) {
        this();
        this.actId = actId;
        this.aleTipo = tipo;
        this.aleTitulo = titulo;
        this.aleMensaje = mensaje;
        this.aleFechaObjetivo = fechaObjetivo;
        this.aleFechaAlerta = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getAleId() {
        return aleId;
    }
    
    public void setAleId(Integer aleId) {
        this.aleId = aleId;
    }
    
    public Integer getActId() {
        return actId;
    }
    
    public void setActId(Integer actId) {
        this.actId = actId;
    }
    
    public TipoAlerta getAleTipo() {
        return aleTipo;
    }
    
    public void setAleTipo(TipoAlerta aleTipo) {
        this.aleTipo = aleTipo;
    }
    
    public String getAleTitulo() {
        return aleTitulo;
    }
    
    public void setAleTitulo(String aleTitulo) {
        this.aleTitulo = aleTitulo;
    }
    
    public String getAleMensaje() {
        return aleMensaje;
    }
    
    public void setAleMensaje(String aleMensaje) {
        this.aleMensaje = aleMensaje;
    }
    
    public LocalDate getAleFechaObjetivo() {
        return aleFechaObjetivo;
    }
    
    public void setAleFechaObjetivo(LocalDate aleFechaObjetivo) {
        this.aleFechaObjetivo = aleFechaObjetivo;
    }
    
    public LocalDateTime getAleFechaAlerta() {
        return aleFechaAlerta;
    }
    
    public void setAleFechaAlerta(LocalDateTime aleFechaAlerta) {
        this.aleFechaAlerta = aleFechaAlerta;
    }
    
    public PrioridadAlerta getAlePrioridad() {
        return alePrioridad;
    }
    
    public void setAlePrioridad(PrioridadAlerta alePrioridad) {
        this.alePrioridad = alePrioridad;
    }
    
    public EstadoAlerta getAleEstado() {
        return aleEstado;
    }
    
    public void setAleEstado(EstadoAlerta aleEstado) {
        this.aleEstado = aleEstado;
    }
    
    public Boolean getAleEmailEnviado() {
        return aleEmailEnviado;
    }
    
    public void setAleEmailEnviado(Boolean aleEmailEnviado) {
        this.aleEmailEnviado = aleEmailEnviado;
    }
    
    public LocalDateTime getAleFechaEnvio() {
        return aleFechaEnvio;
    }
    
    public void setAleFechaEnvio(LocalDateTime aleFechaEnvio) {
        this.aleFechaEnvio = aleFechaEnvio;
    }
    
    public Integer getReferenciaId() {
        return referenciaId;
    }
    
    public void setReferenciaId(Integer referenciaId) {
        this.referenciaId = referenciaId;
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
    
    /**
     * Determina si la alerta está vencida
     */
    public boolean isVencida() {
        return aleFechaObjetivo != null && aleFechaObjetivo.isBefore(LocalDate.now());
    }
    
    /**
     * Determina si la alerta es crítica por proximidad
     */
    public boolean isCritica() {
        return alePrioridad == PrioridadAlerta.Critica || 
               (aleFechaObjetivo != null && aleFechaObjetivo.isBefore(LocalDate.now().plusDays(3)));
    }
    
    @Override
    public String toString() {
        return aleTitulo + " - " + alePrioridad;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Alerta that = (Alerta) obj;
        return aleId != null && aleId.equals(that.aleId);
    }
    
    @Override
    public int hashCode() {
        return aleId != null ? aleId.hashCode() : 0;
    }
}
